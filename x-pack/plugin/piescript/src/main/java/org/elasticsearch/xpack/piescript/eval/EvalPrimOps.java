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
            case NOT -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l, operand) -> {
                if (operand instanceof Value.Symbol s) {
                    l.onResponse(new Value.Symbol("NOT " + s.esql()));
                } else {
                    l.onResponse(new Value.BooleanVal(!requireBoolean(operand, op)));
                }
            }));

            case NEG -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l, operand) -> {
                if (operand instanceof Value.Symbol s) {
                    l.onResponse(new Value.Symbol("-" + s.esql()));
                } else {
                    l.onResponse(new Value.DoubleVal(-requireNumeric(operand, op)));
                }
            }));

            case ADD, SUB, MUL, DIV, MOD, EQ, NEQ, LT, GT, LTE, GTE, AND, OR -> evalBinary(eval, args, op, env, listener);
        }
    }

    private static void evalBinary(
        Evaluator eval,
        java.util.List<org.elasticsearch.xpack.piescript.core.CoreExpr> args,
        Op op,
        Value[] env,
        ActionListener<Value> listener
    ) {
        eval.evaluate(
            args.get(0),
            env,
            listener.delegateFailureAndWrap((l1, leftVal) -> eval.evaluate(args.get(1), env, l1.delegateFailureAndWrap((l2, rightVal) -> {
                if (leftVal instanceof Value.Symbol || rightVal instanceof Value.Symbol) {
                    var left = EvalBuiltins.compileValueToEsql(leftVal);
                    var right = EvalBuiltins.compileValueToEsql(rightVal);
                    l2.onResponse(new Value.Symbol("(" + left + " " + esqlOp(op) + " " + right + ")"));
                } else {
                    l2.onResponse(evalConcreteBinary(op, leftVal, rightVal));
                }
            })))
        );
    }

    private static Value evalConcreteBinary(Op op, Value left, Value right) {
        return switch (op) {
            case ADD, SUB, MUL, DIV, MOD -> new Value.DoubleVal(doubleArithmetic(op, requireNumeric(left, op), requireNumeric(right, op)));
            case EQ -> new Value.BooleanVal(left.equals(right));
            case NEQ -> new Value.BooleanVal(!left.equals(right));
            case LT, GT, LTE, GTE -> new Value.BooleanVal(doubleComparison(op, requireNumeric(left, op), requireNumeric(right, op)));
            case AND -> new Value.BooleanVal(requireBoolean(left, op) && requireBoolean(right, op));
            case OR -> new Value.BooleanVal(requireBoolean(left, op) || requireBoolean(right, op));
            default -> throw new AssertionError("unhandled binary op: " + op);
        };
    }

    private static String esqlOp(Op op) {
        return switch (op) {
            case ADD -> "+";
            case SUB -> "-";
            case MUL -> "*";
            case DIV -> "/";
            case MOD -> "%";
            case EQ -> "==";
            case NEQ -> "!=";
            case LT -> "<";
            case GT -> ">";
            case LTE -> "<=";
            case GTE -> ">=";
            case AND -> "AND";
            case OR -> "OR";
            default -> throw new AssertionError("not a binary op: " + op);
        };
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
