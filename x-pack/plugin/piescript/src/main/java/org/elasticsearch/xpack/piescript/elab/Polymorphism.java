/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.antlr.v4.runtime.tree.ParseTree;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreTypeApp;
import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Polymorphism machinery: generalization, instantiation, the ∀-CHECK rule,
 * and {@code CoreTypeAbs}/{@code CoreTypeApp} wrapping. Extracted from
 * {@link Elaborator} to keep the main dispatcher focused on form dispatch.
 */
final class Polymorphism {

    private Polymorphism() {}

    // ──── ∀-CHECK rule ────

    /**
     * The ∀-CHECK rule: when checking an expression against a polymorphic
     * {@link TypeScheme}, peel the quantifiers, recursively check against
     * the monomorphic body, and wrap the result in {@link CoreTypeAbs} nodes.
     */
    static CoreExpr forallCheck(Elaborator elab, ParseTree node, TypeScheme expected, ElaborationContext ctx, Elaborator.Src src) {
        CoreExpr inner = elab.check(node, TypeScheme.mono(expected.body()), ctx, src);
        return wrapTypeAbs(inner, expected, src.source());
    }

    // ──── Generalize ────

    /**
     * Generalize a monotype into a type scheme. Solves all pending constraints
     * first so that the zonker is populated, then collects unsolved metas at
     * the given binding level, converts each to a Rigid, and records the
     * solution in the zonker. The TypeScheme maps Rigid IDs to their kinds.
     */
    static TypeScheme generalize(Elaborator elab, MonoType type, int bindingLevel) {
        elab.solveConstraints();
        var metas = new HashMap<Integer, Kind>();
        TypeWalker.collectMetas(type, bindingLevel, elab.state, metas);
        if (metas.isEmpty()) {
            return TypeScheme.mono(type);
        }
        var rigidMap = new LinkedHashMap<Integer, Kind>();
        for (var entry : metas.entrySet()) {
            var rigid = elab.state.freshRigid(entry.getValue());
            elab.state.solve(entry.getKey(), rigid);
            rigidMap.put(rigid.id(), entry.getValue());
        }
        return new TypeScheme(rigidMap, type);
    }

    // ──── Instantiate ────

    /**
     * Instantiate a polymorphic type scheme: create fresh metas for each
     * quantified Rigid, walk the scheme body to substitute them, and wrap
     * in nested {@link CoreTypeApp} nodes. The {@code baseFactory} produces
     * the inner expression (e.g. {@code CoreVar} for local bindings,
     * {@code CoreFree} for module-level free variables) given the instantiated
     * monotype.
     */
    static CoreExpr instantiateAndWrap(
        Elaborator elab,
        Function<MonoType, CoreExpr> baseFactory,
        TypeScheme scheme,
        ElaborationContext ctx,
        Source source
    ) {
        var freshMetas = new LinkedHashMap<Integer, MonoType>();
        for (var entry : scheme.quantified().entrySet()) {
            var fresh = entry.getValue() == Kind.ROW ? elab.state.freshRow(ctx.bindingLevel()) : elab.state.freshType(ctx.bindingLevel());
            freshMetas.put(entry.getKey(), fresh);
        }
        var instantiated = instantiateBody(elab, scheme.body(), freshMetas);
        CoreExpr wrapped = baseFactory.apply(instantiated);
        for (var fresh : freshMetas.values()) {
            wrapped = new CoreTypeApp(source, wrapped, fresh, instantiated);
        }
        return wrapped;
    }

    private static MonoType instantiateBody(Elaborator elab, MonoType type, Map<Integer, MonoType> rigidSubst) {
        return switch (type) {
            case MonoType.Meta meta -> {
                var resolved = elab.state.zonkOrKeep(meta);
                if (resolved instanceof MonoType.Rigid r && rigidSubst.containsKey(r.id())) {
                    yield rigidSubst.get(r.id());
                }
                yield resolved instanceof MonoType.Meta ? resolved : instantiateBody(elab, resolved, rigidSubst);
            }
            case MonoType.Rigid r -> rigidSubst.getOrDefault(r.id(), r);
            case MonoType.TCon t -> t;
            case MonoType.Arrow(var p, var r) -> new MonoType.Arrow(
                instantiateBody(elab, p, rigidSubst),
                instantiateBody(elab, r, rigidSubst)
            );
            // Flatten the row through the zonker so fields hidden behind solved
            // row-variable tails are visited. Without this, rigids in the
            // continuation (e.g. from successive field projections) would be
            // silently skipped. A future cleanup could run resolveDeep on the
            // whole scheme body before walking, removing the need for ad-hoc
            // flattening here.
            case MonoType.RecordType(var row) -> {
                var flat = elab.state.resolveRow(row);
                var newFields = new LinkedHashMap<String, MonoType>();
                for (var entry : flat.fields().entrySet()) {
                    newFields.put(entry.getKey(), instantiateBody(elab, entry.getValue(), rigidSubst));
                }
                var newRowVar = flat.rowVar().map(rv -> {
                    var resolved = elab.state.zonkOrKeep(rv);
                    if (resolved instanceof MonoType.Rigid r && rigidSubst.containsKey(r.id())) {
                        var replacement = rigidSubst.get(r.id());
                        return replacement instanceof MonoType.Meta m ? m : rv;
                    }
                    return resolved instanceof MonoType.Meta m ? m : rv;
                });
                yield new MonoType.RecordType(new RowType(newFields, newRowVar));
            }
            case MonoType.AppType(var c, var a) -> new MonoType.AppType(
                instantiateBody(elab, c, rigidSubst),
                instantiateBody(elab, a, rigidSubst)
            );
        };
    }

    // ──── CoreTypeAbs wrapping ────

    /**
     * Wrap an expression in nested unary {@link CoreTypeAbs} nodes, one per
     * quantified variable in the scheme. Returns the expression unchanged if
     * the scheme is monomorphic.
     */
    static CoreExpr wrapTypeAbs(CoreExpr rhs, TypeScheme scheme, Source source) {
        if (scheme.quantified().isEmpty()) {
            return rhs;
        }
        var entries = new ArrayList<>(scheme.quantified().entrySet());
        CoreExpr wrapped = rhs;
        for (int i = entries.size() - 1; i >= 0; i--) {
            var entry = entries.get(i);
            wrapped = new CoreTypeAbs(source, entry.getKey(), entry.getValue(), wrapped, wrapped.type());
        }
        return wrapped;
    }
}
