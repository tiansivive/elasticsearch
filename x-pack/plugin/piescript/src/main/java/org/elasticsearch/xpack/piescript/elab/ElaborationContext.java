/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Immutable typing context (Γ) carried through recursive descent during
 * elaboration. Each recursive call receives its own context — the call stack
 * handles scope unwinding, so there is no push/pop and no risk of getting
 * out of sync.
 *
 * <p>The context has two scoping layers:
 * <ol>
 *   <li><b>Local bindings</b> — a de Bruijn-indexed list of named type schemes.
 *       Index 0 is the most recently bound variable (head of the list).</li>
 *   <li><b>Module bindings</b> — a name-keyed map of type schemes for free
 *       variables (built-ins, eventually imports). Module bindings are checked
 *       only when no local binding matches, so local variables shadow
 *       module-level names.</li>
 * </ol>
 *
 * <p>The context also carries the current binding level (let-nesting depth)
 * used for generalization.
 *
 * <p>Usage in a recursive elaborator:
 * <pre>{@code
 * CoreExpr elaborate(Expr expr, ElaborationContext ctx, ElaborationState state) {
 *     // Lambda: extend context for the body
 *     case Lambda(name, body) -> {
 *         var alpha = state.freshType(ctx.bindingLevel());
 *         var innerCtx = ctx.bind(name, TypeScheme.mono(alpha));
 *         var elaboratedBody = elaborate(body, innerCtx, state);
 *         ...
 *     }
 *     // Let: raise binding level for RHS, extend context for body
 *     case Let(name, rhs, body) -> {
 *         var letCtx = ctx.enterBindingLevel();
 *         var elaboratedRhs = elaborate(rhs, letCtx, state);
 *         // generalize...
 *         var bodyCtx = ctx.bind(name, scheme);
 *         var elaboratedBody = elaborate(body, bodyCtx, state);
 *         ...
 *     }
 * }
 * }</pre>
 */
public final class ElaborationContext {

    /** Empty context at binding level 0 with no module bindings. */
    public static final ElaborationContext EMPTY = new ElaborationContext(List.of(), Map.of(), Map.of(), 0);

    private final List<NamedScheme> bindings;
    private final Map<String, TypeScheme> module;
    private final Map<String, MonoType> kindModule;
    private final int bindingLevel;

    private ElaborationContext(
        List<NamedScheme> bindings,
        Map<String, TypeScheme> module,
        Map<String, MonoType> kindModule,
        int bindingLevel
    ) {
        this.bindings = bindings;
        this.module = module;
        this.kindModule = kindModule;
        this.bindingLevel = bindingLevel;
    }

    /**
     * Create a context pre-populated with module-level bindings (built-ins,
     * imports, etc.) and a kind context for type constructors.
     */
    public static ElaborationContext withModule(Map<String, TypeScheme> module, Map<String, MonoType> kindModule) {
        return new ElaborationContext(List.of(), module, kindModule, 0);
    }

    /** A binding in the context: surface name paired with its type scheme. */
    public record NamedScheme(String name, TypeScheme scheme) {}

    /** Result of a variable lookup: de Bruijn index + type scheme. */
    public record LookupResult(int index, TypeScheme scheme) {}

    /**
     * Return a new context with an additional binding at de Bruijn index 0.
     * The current bindings shift up by one index. Binding level and module
     * are preserved.
     */
    public ElaborationContext bind(String name, TypeScheme scheme) {
        var extended = new ArrayList<NamedScheme>(1 + bindings.size());
        extended.add(new NamedScheme(name, scheme));
        extended.addAll(bindings);
        return new ElaborationContext(Collections.unmodifiableList(extended), module, kindModule, bindingLevel);
    }

    /**
     * Look up a variable by surface name. Scans from index 0 (most recent
     * binding), returning the de Bruijn index and type scheme.
     *
     * @return the lookup result, or empty if the name is not bound
     */
    public Optional<LookupResult> lookup(String name) {
        return IntStream.range(0, bindings.size())
            .filter(i -> bindings.get(i).name().equals(name))
            .mapToObj(i -> new LookupResult(i, bindings.get(i).scheme()))
            .findFirst();
    }

    /**
     * Look up a name in the module-level bindings (built-ins, imports).
     * Only consulted when {@link #lookup} finds no local binding.
     *
     * @return the type scheme, or empty if the name is not in the module
     */
    public Optional<TypeScheme> lookupModule(String name) {
        return Optional.ofNullable(module.get(name));
    }

    /** Return a new context with binding level incremented (entering a let-RHS). */
    public ElaborationContext enterBindingLevel() {
        return new ElaborationContext(bindings, module, kindModule, bindingLevel + 1);
    }

    /** Return a new context with binding level decremented (exiting a let-RHS). */
    public ElaborationContext exitBindingLevel() {
        return new ElaborationContext(bindings, module, kindModule, bindingLevel - 1);
    }

    /**
     * Look up the kind of a type constructor by name.
     *
     * @return the kind (e.g., {@code Type}, {@code Type → Type}), or empty if unknown
     */
    public Optional<MonoType> lookupKind(String name) {
        return Optional.ofNullable(kindModule.get(name));
    }

    /** Current binding level (let-nesting depth). */
    public int bindingLevel() {
        return bindingLevel;
    }

    /** Number of bindings in the context. */
    public int depth() {
        return bindings.size();
    }
}
