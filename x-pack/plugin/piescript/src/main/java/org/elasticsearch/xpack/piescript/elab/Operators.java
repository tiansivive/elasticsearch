/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;

import java.util.List;

/**
 * Binary operator elaboration: arithmetic, comparison, and boolean operators as typed primops.
 */
final class Operators {

    private Operators() {}

    record PrimSig(MonoType param, MonoType result) {}

    static CoreExpr binary(Elaborator elab, ParserRuleContext node, ParseTree left, ParseTree right, Op op, ElaborationContext ctx) {
        var lhs = elab.elaborate(left, ctx);
        var rhs = elab.elaborate(right, ctx);
        var s = Elaborator.source(node);

        var sig = primOpSignature(op);
        elab.emitConstraint(lhs.type(), sig.param, s);
        elab.emitConstraint(rhs.type(), sig.param, s);
        return new CorePrimOp(s.source(), op, List.of(lhs, rhs), sig.result);
    }

    /**
     * Concrete primop signatures. Phase 1: arithmetic is {@code Integer -> Integer -> Integer},
     * comparison is {@code Integer -> Integer -> Boolean}, boolean ops are
     * {@code Boolean -> Boolean -> Boolean}. Long/Double support deferred to coercion phase.
     */
    static PrimSig primOpSignature(Op op) {
        return switch (op) {
            case ADD, SUB, MUL, DIV, MOD -> new PrimSig(Elaborator.INTEGER, Elaborator.INTEGER);
            case EQ, NEQ, LT, GT, LTE, GTE -> new PrimSig(Elaborator.INTEGER, Elaborator.BOOLEAN);
            case AND, OR -> new PrimSig(Elaborator.BOOLEAN, Elaborator.BOOLEAN);
            case NOT, NEG -> throw new IllegalStateException("unary ops should not use binary dispatch");
        };
    }
}
