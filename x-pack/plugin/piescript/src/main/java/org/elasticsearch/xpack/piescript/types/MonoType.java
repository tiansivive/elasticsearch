/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

/**
 * Monomorphic types in the Piescript type system. Every Core IR node carries a MonoType
 * (possibly containing unsolved {@link Meta} variables that are resolved via the zonker).
 *
 * <p>Design spec:
 * <pre>{@code
 * MonoType
 *   = TCon(name: String)                           -- "Integer", "Keyword", ...
 *   | Arrow(param: MonoType, result: MonoType)
 *   | RecordType(row: MonoType)                    -- row should be row-kinded
 *   | AppType(constructor: MonoType, argument: MonoType)
 *   | Meta(id: int, bindingLevel: int, kind: MonoType)  -- unsolved metavar
 *   | Rigid(id: int, kind: MonoType)                    -- bound/skolemized type variable (D-031)
 * }</pre>
 *
 * <p>Kinds are represented as MonoType values (F-omega-lite, D-05X): base kinds
 * {@code TCon("Type")} and {@code TCon("Row")}, arrow kinds via {@link Arrow}.
 * Kind correctness is enforced through unification constraints, not runtime assertions.
 *
 * <p>{@link RowType} is a MonoType variant representing row structure (field map + optional
 * tail). It appears as the argument to {@link RecordType} and as a zonker solution for
 * row-kinded metas. See D-050.
 */
public sealed interface MonoType permits MonoType.TCon, MonoType.Arrow, MonoType.RecordType, MonoType.AppType, MonoType.Meta,
    MonoType.Rigid, RowType {

    /** Type constructor: "Integer", "Long", "Double", "Keyword", "Boolean", "Null". */
    record TCon(String name) implements MonoType {}

    /** Function type: {@code param → result}. Also used for arrow kinds. */
    record Arrow(MonoType param, MonoType result) implements MonoType {}

    /**
     * Record type wrapping a row. The {@code row} should be row-kinded: a {@link RowType},
     * a {@link Meta} with kind {@code TCon("Row")}, or a {@link Rigid} with kind
     * {@code TCon("Row")}. Kind correctness is enforced via unification constraints
     * emitted by the elaborator, not by a construction-time assertion.
     */
    record RecordType(MonoType row) implements MonoType {}

    /** Type application: constructor applied to argument (e.g. {@code List Record}). */
    record AppType(MonoType constructor, MonoType argument) implements MonoType {}

    /**
     * Unsolved metavariable, allocated during elaboration. The {@code id} is unique,
     * {@code bindingLevel} records the let-nesting depth at allocation time (for
     * generalization), and {@code kind} is itself a {@link MonoType} representing
     * the kind of this variable (e.g. {@code TCon("Type")}, {@code TCon("Row")}).
     * Solutions are stored in the zonker, not on the meta itself.
     */
    record Meta(int id, int bindingLevel, MonoType kind) implements MonoType {}

    /**
     * Bound (skolemized) type variable introduced when elaborating a type annotation
     * or generalizing an unannotated definition. Rigids do NOT unify with anything
     * except themselves (same {@code id}). They represent universally quantified
     * variables in {@link TypeScheme} bodies. The {@code kind} field is a
     * {@link MonoType} representing the kind (e.g. {@code TCon("Type")}, {@code TCon("Row")}).
     */
    record Rigid(int id, MonoType kind) implements MonoType {}
}
