/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.types.Op;

/**
 * Primitive operation evaluation: arithmetic, comparison, and boolean operators.
 *
 * <p>All numeric operations use {@code double}. Integer, Long, and Double runtime values
 * are all accepted and widened to {@code double} via {@link #requireNumeric}. Results
 * are always {@link Value.DoubleVal}.
 *
 * <p>EQ/NEQ use polymorphic structural equality via {@link Object#equals} on {@code Value}
 * records (D-049).
 */
final class EvalPrimOps {

    private EvalPrimOps() {}

    static void evaluate(Evaluator eval, CorePrimOp primOp, Value[] env, ActionListener<Value> listener) {
        var args = primOp.args();
        var op = primOp.op();

        switch (op) {
            case NOT -> eval.evaluate(
                args.get(0),
                env,
                listener.delegateFailureAndWrap((l, operand) -> l.onResponse(new Value.BooleanVal(!requireBoolean(operand, op))))
            );

            case NEG -> eval.evaluate(
                args.get(0),
                env,
                listener.delegateFailureAndWrap((l, operand) -> l.onResponse(new Value.DoubleVal(-requireNumeric(operand, op))))
            );

            case ADD, SUB, MUL, DIV, MOD -> eval.evaluate(
                args.get(0),
                env,
                listener.delegateFailureAndWrap(
                    (l1, leftVal) -> eval.evaluate(
                        args.get(1),
                        env,
                        l1.delegateFailureAndWrap(
                            (l2, rightVal) -> l2.onResponse(
                                new Value.DoubleVal(doubleArithmetic(op, requireNumeric(leftVal, op), requireNumeric(rightVal, op)))
                            )
                        )
                    )
                )
            );

            case EQ, NEQ -> eval.evaluate(
                args.get(0),
                env,
                listener.delegateFailureAndWrap(
                    (l1, leftVal) -> eval.evaluate(args.get(1), env, l1.delegateFailureAndWrap((l2, rightVal) -> {
                        boolean equal = leftVal.equals(rightVal);
                        l2.onResponse(new Value.BooleanVal(op == Op.EQ ? equal : !equal));
                    }))
                )
            );

            case LT, GT, LTE, GTE -> eval.evaluate(
                args.get(0),
                env,
                listener.delegateFailureAndWrap(
                    (l1, leftVal) -> eval.evaluate(
                        args.get(1),
                        env,
                        l1.delegateFailureAndWrap(
                            (l2, rightVal) -> l2.onResponse(
                                new Value.BooleanVal(doubleComparison(op, requireNumeric(leftVal, op), requireNumeric(rightVal, op)))
                            )
                        )
                    )
                )
            );

            case AND, OR -> eval.evaluate(
                args.get(0),
                env,
                listener.delegateFailureAndWrap(
                    (l1, leftVal) -> eval.evaluate(
                        args.get(1),
                        env,
                        l1.delegateFailureAndWrap(
                            (l2, rightVal) -> l2.onResponse(
                                new Value.BooleanVal(
                                    op == Op.AND
                                        ? requireBoolean(leftVal, op) && requireBoolean(rightVal, op)
                                        : requireBoolean(leftVal, op) || requireBoolean(rightVal, op)
                                )
                            )
                        )
                    )
                )
            );
        }
    }

    static double doubleArithmetic(Op op, double left, double right) {
        return switch (op) {
            case ADD -> left + right;
            case SUB -> left - right;
            case MUL -> left * right;
            case DIV -> {
                if (right == 0.0) throw new EvaluationException("division by zero");
                yield left / right;
            }
            case MOD -> {
                if (right == 0.0) throw new EvaluationException("division by zero");
                yield left % right;
            }
            default -> throw new AssertionError("not an arithmetic op: " + op);
        };
    }

    static boolean doubleComparison(Op op, double left, double right) {
        return switch (op) {
            case LT -> left < right;
            case GT -> left > right;
            case LTE -> left <= right;
            case GTE -> left >= right;
            default -> throw new AssertionError("not an ordering op: " + op);
        };
    }

    static double requireNumeric(Value value, Op op) {
        return switch (value) {
            case Value.DoubleVal v -> v.value();
            case Value.IntegerVal v -> (double) v.value();
            case Value.LongVal v -> (double) v.value();
            case Value.NullVal ignored -> throw new EvaluationException("null value in " + op + " operation");
            default -> throw new AssertionError("type checker bug: expected numeric for " + op + ", got " + value);
        };
    }

    static boolean requireBoolean(Value value, Op op) {
        return switch (value) {
            case Value.BooleanVal v -> v.value();
            case Value.NullVal ignored -> throw new EvaluationException("null value in " + op + " operation");
            default -> throw new AssertionError("type checker bug: expected Boolean for " + op + ", got " + value);
        };
    }
}
