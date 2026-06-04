/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.core.Nullable;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;
import java.util.Map;

/**
 * A pattern in a {@code match} expression arm.
 *
 * <p>Phase 1 implements basic patterns (literals, variables, wildcards, records, lists).
 * Constructor patterns (for ADTs) are deferred to a future phase.
 */
public sealed interface Pattern permits Pattern.LitPat, Pattern.VarPat, Pattern.WildcardPat, Pattern.RecordPat, Pattern.ListPat,
    Pattern.ConsListPat {

    /** Matches a specific literal value (e.g., {@code 42}, {@code "hello"}, {@code true}). */
    record LitPat(LitVal value) implements Pattern {}

    /**
     * Matches any value and binds it to a variable in the de Bruijn environment.
     *
     * @param debugName the variable name from the source text (for debugging/errors)
     * @param type      the inferred type of the variable
     */
    record VarPat(@Nullable String debugName, MonoType type) implements Pattern {}

    /** Matches any value but binds nothing (the {@code _} pattern). */
    record WildcardPat() implements Pattern {}

    /**
     * Matches a record containing at least the specified fields.
     *
     * @param fields   the fields to match, each with its own sub-pattern
     * @param hasTail  true if the pattern includes a tail binding (e.g., {@code { a: 1 | rest }})
     * @param tailName the name of the tail variable, if {@code hasTail} is true
     */
    record RecordPat(Map<String, Pattern> fields, boolean hasTail, @Nullable String tailName) implements Pattern {}

    /**
     * Matches a list of an exact length.
     *
     * @param elements the patterns to match against each element
     */
    record ListPat(List<Pattern> elements) implements Pattern {}

    /**
     * Matches a non-empty list, decomposing it into a head element and a tail list.
     *
     * @param head the pattern to match against the first element
     * @param tail the pattern to match against the rest of the list
     */
    record ConsListPat(Pattern head, Pattern tail) implements Pattern {}
}
