/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreTypeApp;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.Op;

import java.util.LinkedHashMap;

/**
 * Tree-walking evaluator for well-typed Core IR. Uses a de Bruijn environment
 * machine: the environment is a {@code Value[]} indexed by de Bruijn index,
 * with position 0 being the most recently bound variable (D-024).
 *
 * <p>The evaluator trusts the type checker (D-025): where a specific
 * {@link Value} variant is required (closure for application, record for
 * projection/update), a non-matching value is an internal invariant violation
 * ({@link AssertionError}), not a user error.
 *
 * <p>The only user-observable runtime errors are null in arithmetic (D-027)
 * and division by zero, both reported as {@link EvaluationException}.
 */
public final class Evaluator {

    private static final Value[] EMPTY_ENV = new Value[0];

    public Value evaluate(CoreExpr expr) {
        return evaluate(expr, EMPTY_ENV);
    }

    private Value evaluate(CoreExpr expr, Value[] env) {
        return switch (expr) {
            case CoreLit lit -> litToValue(lit.value());

            case CoreVar var -> env[var.index()];

            case CoreLam lam -> new Value.ClosureVal(lam.body(), env.clone());

            case CoreApp app -> {
                var fn = evaluate(app.fn(), env);
                var arg = evaluate(app.arg(), env);
                var closure = switch (fn) {
                    case Value.ClosureVal c -> c;
                    default -> throw new AssertionError("type checker bug: expected closure, got " + fn);
                };
                yield evaluate(closure.body(), prepend(arg, closure.env()));
            }

            case CoreLet let -> {
                var rhs = evaluate(let.rhs(), env);
                yield evaluate(let.body(), prepend(rhs, env));
            }

            case CoreRecord rec -> {
                var labels = rec.labels();
                var values = rec.children();
                var fields = new LinkedHashMap<String, Value>(labels.size());
                for (int i = 0; i < labels.size(); i++) {
                    fields.put(labels.get(i), evaluate(values.get(i), env));
                }
                yield new Value.RecordVal(fields);
            }

            case CoreProject proj -> {
                var record = evaluate(proj.expr(), env);
                var recVal = switch (record) {
                    case Value.RecordVal r -> r;
                    default -> throw new AssertionError("type checker bug: expected record, got " + record);
                };
                yield recVal.fields().get(proj.label());
            }

            case CoreUpdate upd -> {
                var base = evaluate(upd.expr(), env);
                var baseRec = switch (base) {
                    case Value.RecordVal r -> r;
                    default -> throw new AssertionError("type checker bug: expected record, got " + base);
                };
                var newFields = new LinkedHashMap<>(baseRec.fields());
                var updates = upd.updates();
                for (var field : updates) {
                    newFields.put(field.label(), evaluate(field.value(), env));
                }
                yield new Value.RecordVal(newFields);
            }

            case CoreTypeAbs typeAbs -> evaluate(typeAbs.body(), env);

            case CoreTypeApp typeApp -> evaluate(typeApp.polyExpr(), env);

            case CorePrimOp primOp -> evaluatePrimOp(primOp, env);
        };
    }

    private static Value litToValue(LitVal lit) {
        return switch (lit) {
            case LitVal.IntegerLit v -> new Value.IntegerVal(v.value());
            case LitVal.LongLit v -> new Value.LongVal(v.value());
            case LitVal.DoubleLit v -> new Value.DoubleVal(v.value());
            case LitVal.KeywordLit v -> new Value.KeywordVal(v.value().utf8ToString());
            case LitVal.BooleanLit v -> new Value.BooleanVal(v.value());
            case LitVal.NullLit v -> new Value.NullVal();
        };
    }

    private Value evaluatePrimOp(CorePrimOp primOp, Value[] env) {
        var args = primOp.args();
        var op = primOp.op();

        return switch (op) {
            case NOT -> {
                var operand = requireBoolean(evaluate(args.get(0), env), op);
                yield new Value.BooleanVal(!operand);
            }
            case NEG -> {
                var operand = requireInteger(evaluate(args.get(0), env), op);
                yield new Value.IntegerVal(-operand);
            }
            case ADD, SUB, MUL, DIV, MOD -> {
                var left = requireInteger(evaluate(args.get(0), env), op);
                var right = requireInteger(evaluate(args.get(1), env), op);
                yield new Value.IntegerVal(intArithmetic(op, left, right));
            }
            case EQ, NEQ, LT, GT, LTE, GTE -> {
                var left = requireInteger(evaluate(args.get(0), env), op);
                var right = requireInteger(evaluate(args.get(1), env), op);
                yield new Value.BooleanVal(intComparison(op, left, right));
            }
            case AND, OR -> {
                var left = requireBoolean(evaluate(args.get(0), env), op);
                var right = requireBoolean(evaluate(args.get(1), env), op);
                yield new Value.BooleanVal(op == Op.AND ? left && right : left || right);
            }
        };
    }

    private static int intArithmetic(Op op, int left, int right) {
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

    private static boolean intComparison(Op op, int left, int right) {
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

    private static int requireInteger(Value value, Op op) {
        return switch (value) {
            case Value.IntegerVal v -> v.value();
            case Value.NullVal ignored -> throw new EvaluationException("null value in " + op + " operation");
            default -> throw new AssertionError("type checker bug: expected Integer for " + op + ", got " + value);
        };
    }

    private static boolean requireBoolean(Value value, Op op) {
        return switch (value) {
            case Value.BooleanVal v -> v.value();
            case Value.NullVal ignored -> throw new EvaluationException("null value in " + op + " operation");
            default -> throw new AssertionError("type checker bug: expected Boolean for " + op + ", got " + value);
        };
    }

    private static Value[] prepend(Value value, Value[] env) {
        var newEnv = new Value[env.length + 1];
        newEnv[0] = value;
        System.arraycopy(env, 0, newEnv, 1, env.length);
        return newEnv;
    }
}
