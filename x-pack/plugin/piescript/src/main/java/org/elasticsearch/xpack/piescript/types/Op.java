/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

/**
 * Primitive operators used in {@code CoreExpr.PrimOp} nodes. The elaborator resolves
 * surface-syntax operators to these after type-checking operands (see Phase 1 plan D1.8
 * for the full resolution table including numeric widening rules).
 */
public enum Op {
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    EQ,
    NEQ,
    LT,
    GT,
    LTE,
    GTE,
    AND,
    OR,
    NOT,
    NEG
}
