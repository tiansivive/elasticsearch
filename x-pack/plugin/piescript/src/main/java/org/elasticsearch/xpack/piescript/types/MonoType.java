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
 * <p>Design spec (from Phase 1 plan D1.9):
 * <pre>{@code
 * MonoType
 *   = TCon(name: String)                           -- "Integer", "Keyword", ...
 *   | Arrow(param: MonoType, result: MonoType)
 *   | RecordType(row: RowType)
 *   | AppType(constructor: MonoType, argument: MonoType)
 *   | Meta(id: int, bindingLevel: int, kind: Kind)  -- unsolved metavar
 * }</pre>
 */
public sealed interface MonoType {

    /** Type constructor: "Integer", "Long", "Double", "Keyword", "Boolean", "Null". */
    record TCon(String name) implements MonoType {}

    /** Function type: {@code param → result}. */
    record Arrow(MonoType param, MonoType result) implements MonoType {}

    /** Record type with row structure (closed or open). */
    record RecordType(RowType row) implements MonoType {}

    /** Type application: constructor applied to argument (e.g. {@code Stream Record}). */
    record AppType(MonoType constructor, MonoType argument) implements MonoType {}

    /**
     * Unsolved metavariable, allocated during elaboration. The {@code id} is unique,
     * {@code bindingLevel} records the let-nesting depth at allocation time (for
     * generalization), and {@code kind} distinguishes type metas from row metas.
     * Solutions are stored in the zonker, not on the meta itself.
     */
    record Meta(int id, int bindingLevel, Kind kind) implements MonoType {}
}
