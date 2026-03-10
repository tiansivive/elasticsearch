/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

/**
 * Distinguishes type-level metavariables from row-level metavariables.
 * The elaborator uses this to allocate fresh metas of the correct kind,
 * and the zonker uses it to interpret solutions (MonoType vs RowType).
 */
public enum Kind {
    TYPE,
    ROW
}
