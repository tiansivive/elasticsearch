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
 */
final class EvalPrimOps {

    private EvalPrimOps() {}

    static void evaluate(Evaluator eval, CorePrimOp primOp, Value[] env, ActionListener<Value> listener) {
        var args = primOp.args();
        var op = primOp.op();

        switch (op) {
            case NOT -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l, operand) ->
                l.onResponse(new Value.BooleanVal(!requireBoolean(operand, op)))));

            case NEG -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l, operand) ->
                l.onResponse(new Value.IntegerVal(-requireInteger(operand, op)))));

            case ADD, SUB, MUL, DIV, MOD -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l1, leftVal) ->
                eval.evaluate(args.get(1), env, l1.delegateFailureAndWrap((l2, rightVal) ->
                    l2.onResponse(new Value.IntegerVal(intArithmetic(op, requireInteger(leftVal, op), requireInteger(rightVal, op))))))));

            case EQ, NEQ, LT, GT, LTE, GTE -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l1, leftVal) ->
                eval.evaluate(args.get(1), env, l1.delegateFailureAndWrap((l2, rightVal) ->
                    l2.onResponse(new Value.BooleanVal(intComparison(op, requireInteger(leftVal, op), requireInteger(rightVal, op))))))));

            case AND, OR -> eval.evaluate(args.get(0), env, listener.delegateFailureAndWrap((l1, leftVal) ->
                eval.evaluate(args.get(1), env, l1.delegateFailureAndWrap((l2, rightVal) ->
                    l2.onResponse(new Value.BooleanVal(
                        op == Op.AND ? requireBoolean(leftVal, op) && requireBoolean(rightVal, op)
                                     : requireBoolean(leftVal, op) || requireBoolean(rightVal, op)))))));
        }
    }

    static int intArithmetic(Op op, int left, int right) {
        try {
            return switch (op) {
                case ADD -> left + right;
                case SUB -> left - right;
                case MUL -> left * right;
                case DIV -> left / right;
                case MOD -> left % right;
                default -> throw new AssertionError("not an arithmetic op: " + op);
            };
        } catch (ArithmeticException e) {
            throw new EvaluationException("division by zero", e);
        }
    }

    static boolean intComparison(Op op, int left, int right) {
        return switch (op) {
            case EQ -> left == right;
            case NEQ -> left != right;
            case LT -> left < right;
            case GT -> left > right;
            case LTE -> left <= right;
            case GTE -> left >= right;
            default -> throw new AssertionError("not a comparison op: " + op);
        };
    }

    static int requireInteger(Value value, Op op) {
        return switch (value) {
            case Value.IntegerVal v -> v.value();
            case Value.NullVal ignored -> throw new EvaluationException("null value in " + op + " operation");
            default -> throw new AssertionError("type checker bug: expected Integer for " + op + ", got " + value);
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
