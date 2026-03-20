/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import org.apache.lucene.util.BytesRef;

import java.util.Map;

/**
 * Literal values carried by {@code CoreExpr.Lit} nodes. Each variant aligns with
 * an ES DataType (see Phase 1 plan D1.3):
 *
 * <ul>
 *   <li>{@code 42} → IntegerLit (32-bit; LongLit if overflows)</li>
 *   <li>{@code 9999999999} → LongLit (64-bit)</li>
 *   <li>{@code 3.14} → DoubleLit (64-bit float)</li>
 *   <li>{@code "hello"} → KeywordLit (BytesRef, matches ESQL's Literal.keyword())</li>
 *   <li>{@code true}/{@code false} → BooleanLit</li>
 *   <li>{@code null} → NullLit</li>
 *   <li>{@code use "index" as idx} → IndexLit (name + field metadata, D-050)</li>
 * </ul>
 */
public sealed interface LitVal {

    record IntegerLit(int value) implements LitVal {}

    record LongLit(long value) implements LitVal {}

    record DoubleLit(double value) implements LitVal {}

    record KeywordLit(BytesRef value) implements LitVal {}

    record BooleanLit(boolean value) implements LitVal {}

    record NullLit() implements LitVal {}

    /**
     * Index literal produced by {@code use} declarations. Carries the index name
     * and field type metadata resolved by the index resolution pre-pass.
     * The UUID is a cluster-state concern resolved at evaluation time, not
     * baked into the Core IR.
     *
     * @param fieldTypes maps field names to ES type descriptors (e.g., "keyword", "long")
     */
    record IndexLit(String name, Map<String, String> fieldTypes) implements LitVal {}
}
