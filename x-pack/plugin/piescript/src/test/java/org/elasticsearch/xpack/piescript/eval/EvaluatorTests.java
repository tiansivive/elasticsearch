/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.Elaborator;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;

import java.util.Map;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class EvaluatorTests extends ESTestCase {

    private Value evaluate(String source) {
        var parser = new PiescriptParser();
        var program = parser.parse(source);
        var state = new ElaborationState();
        var elaborator = new Elaborator(state);
        var coreExpr = elaborator.elaborateProgram(program);
        return new Evaluator().evaluate(coreExpr);
    }

    // ──── Literals ────

    public void testIntegerLiteral() {
        assertThat(evaluate("42"), is(new Value.IntegerVal(42)));
    }

    public void testLongLiteral() {
        assertThat(evaluate("3000000000"), is(new Value.LongVal(3_000_000_000L)));
    }

    public void testDoubleLiteral() {
        var result = evaluate("3.14");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertThat(((Value.DoubleVal) result).value(), closeTo(3.14, 0.001));
    }

    public void testStringLiteral() {
        assertThat(evaluate("\"hello\""), is(new Value.KeywordVal("hello")));
    }

    public void testTrueLiteral() {
        assertThat(evaluate("true"), is(new Value.BooleanVal(true)));
    }

    public void testFalseLiteral() {
        assertThat(evaluate("false"), is(new Value.BooleanVal(false)));
    }

    public void testNullLiteral() {
        assertThat(evaluate("null"), is(new Value.NullVal()));
    }

    // ──── Arithmetic ────

    public void testAddition() {
        assertThat(evaluate("1 + 2"), is(new Value.IntegerVal(3)));
    }

    public void testSubtraction() {
        assertThat(evaluate("10 - 3"), is(new Value.IntegerVal(7)));
    }

    public void testMultiplication() {
        assertThat(evaluate("4 * 5"), is(new Value.IntegerVal(20)));
    }

    public void testDivision() {
        assertThat(evaluate("10 / 3"), is(new Value.IntegerVal(3)));
    }

    public void testModulo() {
        assertThat(evaluate("10 % 3"), is(new Value.IntegerVal(1)));
    }

    public void testDivisionByZero() {
        var ex = expectThrows(EvaluationException.class, () -> evaluate("10 / 0"));
        assertThat(ex.getMessage(), containsString("division by zero"));
    }

    public void testModuloByZero() {
        var ex = expectThrows(EvaluationException.class, () -> evaluate("10 % 0"));
        assertThat(ex.getMessage(), containsString("division by zero"));
    }

    // ──── Comparison ────

    public void testLessThan() {
        assertThat(evaluate("1 < 2"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("2 < 1"), is(new Value.BooleanVal(false)));
    }

    public void testGreaterThan() {
        assertThat(evaluate("3 > 2"), is(new Value.BooleanVal(true)));
    }

    public void testLessThanOrEqual() {
        assertThat(evaluate("2 <= 2"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("3 <= 2"), is(new Value.BooleanVal(false)));
    }

    public void testGreaterThanOrEqual() {
        assertThat(evaluate("2 >= 2"), is(new Value.BooleanVal(true)));
    }

    public void testEqual() {
        assertThat(evaluate("3 == 3"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("3 == 4"), is(new Value.BooleanVal(false)));
    }

    public void testNotEqual() {
        assertThat(evaluate("3 != 4"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("3 != 3"), is(new Value.BooleanVal(false)));
    }

    // ──── Boolean operators ────

    public void testAnd() {
        assertThat(evaluate("true && true"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("true && false"), is(new Value.BooleanVal(false)));
    }

    public void testOr() {
        assertThat(evaluate("false || true"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("false || false"), is(new Value.BooleanVal(false)));
    }

    public void testNot() {
        assertThat(evaluate("!true"), is(new Value.BooleanVal(false)));
        assertThat(evaluate("!false"), is(new Value.BooleanVal(true)));
    }

    // ──── Unary negation ────

    public void testNeg() {
        assertThat(evaluate("-5"), is(new Value.IntegerVal(-5)));
    }

    public void testDoubleNeg() {
        assertThat(evaluate("- -5"), is(new Value.IntegerVal(5)));
    }

    // ──── Let-bindings ────

    public void testLetBinding() {
        assertThat(evaluate("let x = 1 in x"), is(new Value.IntegerVal(1)));
    }

    public void testLetBindingWithExpression() {
        assertThat(evaluate("let x = 1 + 2 in x"), is(new Value.IntegerVal(3)));
    }

    public void testNestedLet() {
        assertThat(evaluate("let x = 1 in let y = 2 in x + y"), is(new Value.IntegerVal(3)));
    }

    public void testLetShadowing() {
        assertThat(evaluate("let x = 1 in let x = 2 in x"), is(new Value.IntegerVal(2)));
    }

    public void testTopLevelBindings() {
        assertThat(evaluate("let x = 10; let y = 20; x + y"), is(new Value.IntegerVal(30)));
    }

    // ──── Lambdas + application ────

    public void testIdentityFunction() {
        assertThat(evaluate("(fn x -> x) 42"), is(new Value.IntegerVal(42)));
    }

    public void testIncrementFunction() {
        assertThat(evaluate("(fn x -> x + 1) 2"), is(new Value.IntegerVal(3)));
    }

    public void testMultiParamLambda() {
        assertThat(evaluate("(fn x y -> x + y) 3 4"), is(new Value.IntegerVal(7)));
    }

    public void testLambdaReturnsClosure() {
        var result = evaluate("fn x -> x");
        assertThat(result, instanceOf(Value.ClosureVal.class));
    }

    // ──── Let-polymorphism ────

    public void testLetPolymorphism() {
        var result = evaluate("let id = fn x -> x in let a = id 1 in id true");
        assertThat(result, is(new Value.BooleanVal(true)));
    }

    // ──── Records ────

    public void testEmptyRecord() {
        var result = evaluate("{}");
        assertThat(result, is(new Value.RecordVal(Map.of())));
    }

    public void testRecordLiteral() {
        var result = evaluate("{ x: 1, y: 2 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.IntegerVal(1), "y", new Value.IntegerVal(2)))));
    }

    public void testRecordProjection() {
        assertThat(evaluate("{ x: 1, y: 2 }.x"), is(new Value.IntegerVal(1)));
    }

    public void testRecordProjectionSecondField() {
        assertThat(evaluate("{ x: 1, y: 2 }.y"), is(new Value.IntegerVal(2)));
    }

    public void testRecordUpdate() {
        var result = evaluate("{ { x: 1, y: 2 } | x = 3 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.IntegerVal(3), "y", new Value.IntegerVal(2)))));
    }

    public void testRecordUpdateAddField() {
        var result = evaluate("{ { x: 1 } | y = 2 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.IntegerVal(1), "y", new Value.IntegerVal(2)))));
    }

    // ──── Pipe operator ────

    public void testPipeOperator() {
        assertThat(evaluate("1 |> (fn x -> x + 1)"), is(new Value.IntegerVal(2)));
    }

    public void testPipeChain() {
        assertThat(evaluate("1 |> (fn x -> x + 1) |> (fn y -> y * 3)"), is(new Value.IntegerVal(6)));
    }

    // ──── Blocks ────

    public void testBlock() {
        assertThat(evaluate("{ let x = 1; let y = 2; x + y }"), is(new Value.IntegerVal(3)));
    }

    // ──── Closures ────

    public void testClosure() {
        assertThat(evaluate("let add = fn x -> fn y -> x + y in add 1 2"), is(new Value.IntegerVal(3)));
    }

    public void testClosureCapture() {
        assertThat(evaluate("let f = let a = 10 in fn x -> x + a in f 5"), is(new Value.IntegerVal(15)));
    }

    // ──── Accessor sugar ────

    public void testAccessorSugar() {
        assertThat(evaluate("{ x: 42 } |> .x"), is(new Value.IntegerVal(42)));
    }

    // ──── Null in arithmetic ────

    public void testNullInArithmetic() {
        var ex = expectThrows(EvaluationException.class, () -> evaluate("let x : Integer = null in x + 1"));
        assertThat(ex.getMessage(), containsString("null"));
    }

    // ──── Complex expressions ────

    public void testFactorialLike() {
        var result = evaluate("let f = fn n -> n * (n - 1) * (n - 2) in f 5");
        assertThat(result, is(new Value.IntegerVal(60)));
    }

    public void testNestedRecordProjection() {
        assertThat(evaluate("{ inner: { x: 42 } }.inner.x"), is(new Value.IntegerVal(42)));
    }

    // ──── Open-row tests (Phase 1d) ────

    public void testRowPolymorphicFunction() {
        var result = evaluate("let getX = fn r -> r.x in let a = getX { x: 10, y: 20 } in a + getX { x: 5 }");
        assertThat(result, is(new Value.IntegerVal(15)));
    }

    public void testUpdateSugarPreservesExtraFields() {
        var result = evaluate("let upd = { _ | x = 99 } in upd { x: 1, y: 2 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.IntegerVal(99), "y", new Value.IntegerVal(2)))));
    }

    // ──── Annotated polymorphic functions (Phase 1d) ────

    public void testAnnotatedIdentityEndToEnd() {
        assertThat(evaluate("let id : a -> a = fn x -> x in id 42"), is(new Value.IntegerVal(42)));
    }

    public void testAnnotatedIdentityPolymorphicUse() {
        assertThat(evaluate("let id : a -> a = fn x -> x in let a = id 1 in id true"), is(new Value.BooleanVal(true)));
    }

    public void testAnnotatedMultiParamFunction() {
        assertThat(evaluate("let const : a -> b -> a = fn x y -> x in const 42 true"), is(new Value.IntegerVal(42)));
    }

    public void testAnnotatedMultiParamPolymorphicUse() {
        var result = evaluate("let const : a -> b -> a = fn x y -> x in let a = const 1 true in const true 42");
        assertThat(result, is(new Value.BooleanVal(true)));
    }

    // ──── Polytype ascription (D-036) ────

    @AwaitsFix(bugUrl = "D-038: MonoType needs Forall variant for polytype ascription")
    public void testPolytypeAscription() {
        var result = evaluate("let f = (fn x -> x : a -> a) in let a = f 1 in f true");
        assertThat(result, is(new Value.BooleanVal(true)));
    }

    // ──── Block body propagation ────

    public void testBlockBodyResult() {
        assertThat(evaluate("{ let x = 10; let y = 20; x + y }"), is(new Value.IntegerVal(30)));
    }
}
