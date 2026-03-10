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
 *
 * <p>Uses {@code Map<String, MonoType>} rather than recursive {@code Empty | Extend(label, type, tail)}
 * because rows are commutative — a Map captures this naturally. The optional {@code rowVar}
 * makes the row open (for row polymorphism in Phase 2) or closed (Phase 1).
 *
 * <p>Phase 1 only uses closed rows. Open rows with Rémy-style unification are deferred to Phase 2.
 */
public record RowType(Map<String, MonoType> fields, Optional<MonoType.Meta> rowVar) {

    /** Closed row with no row variable — all fields are known. */
    public static RowType closed(Map<String, MonoType> fields) {
        return new RowType(fields, Optional.empty());
    }

    /** Open row with a row variable tail — supports row polymorphism. */
    public static RowType open(Map<String, MonoType> fields, MonoType.Meta var) {
        return new RowType(fields, Optional.of(var));
    }
}
