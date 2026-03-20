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
 *
 * <p>All numeric types (integer literals, decimal literals, ESQL numeric fields) are unified
 * as {@code Double}. Arithmetic is {@code Double → Double → Double}, ordering comparison is
 * {@code Double → Double → Boolean}. No widening or coercion rules are needed.
 *
 * <p>{@code ==} and {@code !=} are polymorphic (D-049): both operands must share the same type
 * but that type is unconstrained. The evaluator uses {@link Object#equals} on {@code Value}
 * records, which means structural equality for all current value types. Proper
 * semantics (e.g. an {@code Eq} typeclass) are deferred to a future phase.
 */
final class Operators {

    private Operators() {}

    record PrimSig(MonoType param, MonoType result) {}

    static CoreExpr binary(Elaborator elab, ParserRuleContext node, ParseTree left, ParseTree right, Op op, ElaborationContext ctx) {
        var lhs = elab.elaborate(left, ctx);
        var rhs = elab.elaborate(right, ctx);
        var s = Elaborator.source(node);

        if (op == Op.EQ || op == Op.NEQ) {
            var operandType = elab.state.freshType(ctx.bindingLevel());
            elab.emitConstraint(lhs.type(), operandType, s);
            elab.emitConstraint(rhs.type(), operandType, s);
            return new CorePrimOp(s.source(), op, List.of(lhs, rhs), Elaborator.BOOLEAN);
        }

        var sig = primOpSignature(op);
        elab.emitConstraint(lhs.type(), sig.param, s);
        elab.emitConstraint(rhs.type(), sig.param, s);
        return new CorePrimOp(s.source(), op, List.of(lhs, rhs), sig.result);
    }

    /**
     * Concrete primop signatures. Arithmetic is {@code Double → Double → Double},
     * ordering comparison is {@code Double → Double → Boolean}, boolean ops are
     * {@code Boolean → Boolean → Boolean}. EQ/NEQ are handled inline in {@link #binary}
     * (polymorphic, D-049).
     */
    static PrimSig primOpSignature(Op op) {
        return switch (op) {
            case ADD, SUB, MUL, DIV, MOD -> new PrimSig(Elaborator.DOUBLE, Elaborator.DOUBLE);
            case LT, GT, LTE, GTE -> new PrimSig(Elaborator.DOUBLE, Elaborator.BOOLEAN);
            case EQ, NEQ -> throw new IllegalStateException("EQ/NEQ are polymorphic; handled in binary()");
            case AND, OR -> new PrimSig(Elaborator.BOOLEAN, Elaborator.BOOLEAN);
            case NOT, NEG -> throw new IllegalStateException("unary ops should not use binary dispatch");
        };
    }
}
