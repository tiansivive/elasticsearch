/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.Set;

/**
 * Type errors produced by unification. Not an exception — the unifier returns
 * {@code Optional<TypeError>} (empty = success, present = error).
 *
 * <p>Source locations are not included here; the elaborator (T1b.5) wraps
 * these with source info when calling the unifier.
 */
public sealed interface TypeError {

    /** Two types that should be equal are structurally incompatible. */
    record Mismatch(MonoType expected, MonoType actual) implements TypeError {}

    /** A metavariable occurs in its own solution, which would create an infinite type. */
    record InfiniteType(MonoType.Meta meta, MonoType type) implements TypeError {}

    /** A record field's type failed to unify; wraps the inner error with the field label. */
    record FieldMismatch(String label, TypeError cause) implements TypeError {}

    /** One record type has fields the other lacks. */
    record MissingFields(Set<String> missing, MonoType inType) implements TypeError {}
}
