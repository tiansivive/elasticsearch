/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Type-level traversal utilities used during elaboration: generalization,
 * instantiation, and deep resolution. All methods are static and take
 * the shared {@link ElaborationState} explicitly.
 */
public final class TypeWalker {

    private TypeWalker() {}

    /**
     * Generalize a monotype into a type scheme by quantifying over any unsolved
     * metas whose binding level is at or above {@code bindingLevel}.
     */
    static TypeScheme generalize(MonoType type, int bindingLevel, ElaborationState state) {
        var resolved = resolveDeep(type, state);
        var metas = new HashSet<Integer>();
        collectMetas(resolved, bindingLevel, state, metas);
        if (metas.isEmpty()) {
            return TypeScheme.mono(resolved);
        }
        return new TypeScheme(metas, resolved);
    }

    /**
     * Instantiate a type scheme by replacing each quantified variable with a
     * fresh meta at the given binding level.
     */
    static MonoType instantiate(TypeScheme scheme, int bindingLevel, ElaborationState state) {
        if (scheme.quantified().isEmpty()) {
            return scheme.body();
        }
        var freshening = new HashMap<Integer, MonoType>();
        for (var id : scheme.quantified()) {
            freshening.put(id, state.freshType(bindingLevel));
        }
        return walkType(scheme.body(), freshening);
    }

    /**
     * Recursively resolve all solved metas in a type, producing a fully
     * grounded type (or as grounded as the zonker allows).
     */
    public static MonoType resolveDeep(MonoType type, ElaborationState state) {
        return switch (type) {
            case MonoType.Meta meta -> {
                var resolved = state.resolveType(meta);
                yield resolved instanceof MonoType.Meta ? resolved : resolveDeep(resolved, state);
            }
            case MonoType.TCon t -> t;
            case MonoType.Arrow(var param, var result) -> new MonoType.Arrow(resolveDeep(param, state), resolveDeep(result, state));
            case MonoType.RecordType(var row) -> {
                var newFields = new LinkedHashMap<String, MonoType>();
                for (var entry : row.fields().entrySet()) {
                    newFields.put(entry.getKey(), resolveDeep(entry.getValue(), state));
                }
                yield new MonoType.RecordType(new RowType(newFields, row.rowVar()));
            }
            case MonoType.AppType(var ctor, var arg) -> new MonoType.AppType(resolveDeep(ctor, state), resolveDeep(arg, state));
        };
    }

    /**
     * Substitute metas according to a mapping. Used by instantiation
     * to replace quantified variables with fresh metas.
     */
    static MonoType walkType(MonoType type, Map<Integer, MonoType> subst) {
        return switch (type) {
            case MonoType.Meta(var id, var lvl, var kind) -> subst.getOrDefault(id, type);
            case MonoType.TCon t -> t;
            case MonoType.Arrow(var param, var result) -> new MonoType.Arrow(walkType(param, subst), walkType(result, subst));
            case MonoType.RecordType(var row) -> {
                var newFields = new LinkedHashMap<String, MonoType>();
                for (var entry : row.fields().entrySet()) {
                    newFields.put(entry.getKey(), walkType(entry.getValue(), subst));
                }
                var newRowVar = row.rowVar().map(rv -> {
                    var replacement = subst.get(rv.id());
                    return replacement instanceof MonoType.Meta m ? m : rv;
                });
                yield new MonoType.RecordType(new RowType(newFields, newRowVar));
            }
            case MonoType.AppType(var ctor, var arg) -> new MonoType.AppType(walkType(ctor, subst), walkType(arg, subst));
        };
    }

    private static void collectMetas(MonoType type, int bindingLevel, ElaborationState state, Set<Integer> acc) {
        switch (type) {
            case MonoType.Meta(var id, var lvl, var kind) -> {
                if (lvl >= bindingLevel && state.isSolved(id) == false) {
                    acc.add(id);
                }
            }
            case MonoType.TCon ignored -> {
            }
            case MonoType.Arrow(var param, var result) -> {
                collectMetas(param, bindingLevel, state, acc);
                collectMetas(result, bindingLevel, state, acc);
            }
            case MonoType.RecordType(var row) -> {
                for (var fieldType : row.fields().values()) {
                    collectMetas(fieldType, bindingLevel, state, acc);
                }
                row.rowVar().ifPresent(rv -> {
                    if (rv.bindingLevel() >= bindingLevel && state.isSolved(rv.id()) == false) {
                        acc.add(rv.id());
                    }
                });
            }
            case MonoType.AppType(var ctor, var arg) -> {
                collectMetas(ctor, bindingLevel, state, acc);
                collectMetas(arg, bindingLevel, state, acc);
            }
        }
    }
}
