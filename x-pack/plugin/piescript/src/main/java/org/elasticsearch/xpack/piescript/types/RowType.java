/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import java.util.Map;
import java.util.Optional;

/**
 * Row type: a set of labeled fields plus an optional row variable tail.
 * A first-class {@link MonoType} variant (D-050) — can appear as the argument
 * to {@link MonoType.RecordType} and as a zonker solution for row-kinded metas.
 *
 * <p>Uses {@code Map<String, MonoType>} rather than recursive {@code Empty | Extend(label, type, tail)}
 * because rows are commutative — a Map captures this naturally. The optional {@code tail}
 * makes the row open (for row polymorphism) or closed.
 *
 * <p>The tail, when present, must be row-kinded: a {@link MonoType.Meta} with {@link Kind#ROW},
 * a {@link MonoType.Rigid} with {@link Kind#ROW}, or another {@code RowType}.
 */
public record RowType(Map<String, MonoType> fields, Optional<MonoType> tail) implements MonoType {

    public RowType {
        tail.ifPresent(t -> { assert MonoType.isRowKinded(t) : "RowType tail must be row-kinded, got: " + t; });
    }

    /** Closed row with no row variable — all fields are known. */
    public static RowType closed(Map<String, MonoType> fields) {
        return new RowType(fields, Optional.empty());
    }

    /** Open row with a row variable tail — supports row polymorphism. */
    public static RowType open(Map<String, MonoType> fields, MonoType tail) {
        return new RowType(fields, Optional.of(tail));
    }
}
