/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Piescript prelude: built-in functions available as module-level free
 * variables. These are wired into the {@link ElaborationContext} module map
 * so they can be referenced without explicit import.
 *
 * <p>Type schemes use pre-allocated Rigid IDs that are disjoint from the
 * IDs produced by {@link ElaborationState#freshRigid}. Since instantiation
 * substitutes all quantified Rigids with fresh metas, the IDs only need
 * to be internally consistent within each scheme.
 *
 * <p>Signatures:
 * <pre>
 *   map    : ∀a b. (a → b) → Stream a → Stream b
 *   filter : ∀a.   (a → Boolean) → Stream a → Stream a
 *   reduce : ∀a b. (b → a → b) → b → Stream a → b
 * </pre>
 */
public final class Prelude {

    private Prelude() {}

    private static final MonoType.Rigid A0 = new MonoType.Rigid(-1, Kind.TYPE);
    private static final MonoType.Rigid B0 = new MonoType.Rigid(-2, Kind.TYPE);

    /**
     * The module map containing all built-in function type schemes.
     * Pass this to {@link ElaborationContext#withModule} for the standard
     * Piescript elaboration environment.
     */
    public static final Map<String, TypeScheme> MODULE = buildModule();

    /** Arity (number of term-level arguments) for each built-in function. */
    public static final Map<String, Integer> ARITY = Map.of("map", 2, "filter", 2, "reduce", 3);

    private static Map<String, TypeScheme> buildModule() {
        var module = new LinkedHashMap<String, TypeScheme>();
        module.put("map", mapScheme());
        module.put("filter", filterScheme());
        module.put("reduce", reduceScheme());
        return Map.copyOf(module);
    }

    // map : ∀(a:TYPE, b:TYPE). (a → b) → Stream a → Stream b
    private static TypeScheme mapScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        quantified.put(B0.id(), Kind.TYPE);

        var body = new MonoType.Arrow(new MonoType.Arrow(A0, B0), new MonoType.Arrow(stream(A0), stream(B0)));
        return new TypeScheme(quantified, body);
    }

    // filter : ∀(a:TYPE). (a → Boolean) → Stream a → Stream a
    private static TypeScheme filterScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);

        var body = new MonoType.Arrow(new MonoType.Arrow(A0, Elaborator.BOOLEAN), new MonoType.Arrow(stream(A0), stream(A0)));
        return new TypeScheme(quantified, body);
    }

    // reduce : ∀(a:TYPE, b:TYPE). (b → a → b) → b → Stream a → b
    private static TypeScheme reduceScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        quantified.put(B0.id(), Kind.TYPE);

        var body = new MonoType.Arrow(
            new MonoType.Arrow(B0, new MonoType.Arrow(A0, B0)),
            new MonoType.Arrow(B0, new MonoType.Arrow(stream(A0), B0))
        );
        return new TypeScheme(quantified, body);
    }

    private static MonoType.AppType stream(MonoType element) {
        return new MonoType.AppType(Elaborator.STREAM, element);
    }
}
