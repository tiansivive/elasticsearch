/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.Elaborator;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        var future = new PlainActionFuture<Value>();
        new Evaluator(null, EsExecutors.DIRECT_EXECUTOR_SERVICE).evaluate(coreExpr, future);
        return future.actionGet();
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

    // ──── Stream built-ins (Phase 2.8) ────
    //
    // These tests construct Core IR directly and inject a StreamVal into the
    // evaluator environment, bypassing parsing and elaboration. This validates
    // the built-in map/filter/reduce logic without requiring a real ES client.

    private static final Source SRC = Source.EMPTY;
    private static final MonoType INT = new MonoType.TCon("Integer");
    private static final MonoType BOOL = new MonoType.TCon("Boolean");

    private static Value.StreamVal testStream() {
        var row1 = new Value.RecordVal(linkedMap("name", new Value.KeywordVal("alice"), "age", new Value.IntegerVal(30)));
        var row2 = new Value.RecordVal(linkedMap("name", new Value.KeywordVal("bob"), "age", new Value.IntegerVal(25)));
        var row3 = new Value.RecordVal(linkedMap("name", new Value.KeywordVal("carol"), "age", new Value.IntegerVal(35)));
        return new Value.StreamVal(List.of(row1, row2, row3));
    }

    private static Map<String, Value> linkedMap(String k1, Value v1, String k2, Value v2) {
        var map = new LinkedHashMap<String, Value>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    /**
     * Evaluate a Core IR expression with a pre-populated environment.
     * Variable at de Bruijn index 0 = env[0], etc.
     */
    private Value evaluateWithEnv(CoreExpr expr, Value... env) {
        var future = new PlainActionFuture<Value>();
        new Evaluator(null, EsExecutors.DIRECT_EXECUTOR_SERVICE).evaluate(expr, env, future);
        return future.actionGet();
    }

    public void testMapProjectField() {
        // map (fn r -> r.age) stream — extracts .age from each record
        var stream = testStream();
        // fn r -> r.age : body is CoreProject(CoreVar(0), "age")
        var body = new CoreProject(SRC, new CoreVar(SRC, 0, "r", INT), "age", INT);
        var lambda = new CoreLam(SRC, "r", INT, body, INT);
        var mapFree = new CoreFree(SRC, "map", INT);
        // map lambda stream → apply map to lambda, then apply result to stream (var 0)
        var mapApplied = new CoreApp(SRC, mapFree, lambda, INT);
        var fullExpr = new CoreApp(SRC, mapApplied, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.StreamVal.class));
        var elements = ((Value.StreamVal) result).elements();
        assertThat(elements.size(), is(3));
        assertThat(elements.get(0), is(new Value.IntegerVal(30)));
        assertThat(elements.get(1), is(new Value.IntegerVal(25)));
        assertThat(elements.get(2), is(new Value.IntegerVal(35)));
    }

    public void testMapTransformField() {
        // map (fn r -> r.age + 1) stream — increments .age
        var stream = testStream();
        var proj = new CoreProject(SRC, new CoreVar(SRC, 0, "r", INT), "age", INT);
        var lit1 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.IntegerLit(1),
            INT
        );
        var add = new CorePrimOp(SRC, Op.ADD, List.of(proj, lit1), INT);
        var lambda = new CoreLam(SRC, "r", INT, add, INT);
        var mapFree = new CoreFree(SRC, "map", INT);
        var mapApplied = new CoreApp(SRC, mapFree, lambda, INT);
        var fullExpr = new CoreApp(SRC, mapApplied, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.StreamVal.class));
        var elements = ((Value.StreamVal) result).elements();
        assertThat(elements.size(), is(3));
        assertThat(elements.get(0), is(new Value.IntegerVal(31)));
        assertThat(elements.get(1), is(new Value.IntegerVal(26)));
        assertThat(elements.get(2), is(new Value.IntegerVal(36)));
    }

    public void testFilterByPredicate() {
        // filter (fn r -> r.age > 28) stream — keeps alice(30) and carol(35)
        var stream = testStream();
        var proj = new CoreProject(SRC, new CoreVar(SRC, 0, "r", INT), "age", INT);
        var lit28 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.IntegerLit(28),
            INT
        );
        var gt = new CorePrimOp(SRC, Op.GT, List.of(proj, lit28), BOOL);
        var lambda = new CoreLam(SRC, "r", INT, gt, INT);
        var filterFree = new CoreFree(SRC, "filter", INT);
        var filterApplied = new CoreApp(SRC, filterFree, lambda, INT);
        var fullExpr = new CoreApp(SRC, filterApplied, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.StreamVal.class));
        var elements = ((Value.StreamVal) result).elements();
        assertThat(elements.size(), is(2));
        assertThat(((Value.RecordVal) elements.get(0)).fields().get("name"), is(new Value.KeywordVal("alice")));
        assertThat(((Value.RecordVal) elements.get(1)).fields().get("name"), is(new Value.KeywordVal("carol")));
    }

    public void testFilterKeepsAll() {
        // filter (fn r -> true) stream — keeps everything
        var stream = testStream();
        var trueBody = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.BooleanLit(true),
            BOOL
        );
        var lambda = new CoreLam(SRC, "r", INT, trueBody, INT);
        var filterFree = new CoreFree(SRC, "filter", INT);
        var filterApplied = new CoreApp(SRC, filterFree, lambda, INT);
        var fullExpr = new CoreApp(SRC, filterApplied, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.StreamVal.class));
        assertThat(((Value.StreamVal) result).elements().size(), is(3));
    }

    public void testFilterRemovesAll() {
        // filter (fn r -> false) stream — removes everything
        var stream = testStream();
        var falseBody = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.BooleanLit(false),
            BOOL
        );
        var lambda = new CoreLam(SRC, "r", INT, falseBody, INT);
        var filterFree = new CoreFree(SRC, "filter", INT);
        var filterApplied = new CoreApp(SRC, filterFree, lambda, INT);
        var fullExpr = new CoreApp(SRC, filterApplied, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.StreamVal.class));
        assertThat(((Value.StreamVal) result).elements().size(), is(0));
    }

    public void testReduceSumAges() {
        // reduce (fn acc elem -> acc + elem.age) 0 stream — sums ages: 30+25+35=90
        var stream = testStream();
        // fn acc elem -> acc + elem.age
        // de Bruijn: elem=0, acc=1
        var accVar = new CoreVar(SRC, 1, "acc", INT);
        var elemProj = new CoreProject(SRC, new CoreVar(SRC, 0, "elem", INT), "age", INT);
        var addBody = new CorePrimOp(SRC, Op.ADD, List.of(accVar, elemProj), INT);
        var innerLam = new CoreLam(SRC, "elem", INT, addBody, INT);
        var outerLam = new CoreLam(SRC, "acc", INT, innerLam, INT);

        var reduceFree = new CoreFree(SRC, "reduce", INT);
        var lit0 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.IntegerLit(0),
            INT
        );
        // reduce outerLam 0 stream
        var reduceF = new CoreApp(SRC, reduceFree, outerLam, INT);
        var reduceInit = new CoreApp(SRC, reduceF, lit0, INT);
        var fullExpr = new CoreApp(SRC, reduceInit, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, is(new Value.IntegerVal(90)));
    }

    public void testReduceEmptyStream() {
        // reduce (fn acc elem -> acc + 1) 0 emptyStream — returns initial value
        var emptyStream = new Value.StreamVal(List.of());
        var accVar = new CoreVar(SRC, 1, "acc", INT);
        var lit1 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.IntegerLit(1),
            INT
        );
        var addBody = new CorePrimOp(SRC, Op.ADD, List.of(accVar, lit1), INT);
        var innerLam = new CoreLam(SRC, "elem", INT, addBody, INT);
        var outerLam = new CoreLam(SRC, "acc", INT, innerLam, INT);

        var reduceFree = new CoreFree(SRC, "reduce", INT);
        var lit0 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.IntegerLit(0),
            INT
        );
        var reduceF = new CoreApp(SRC, reduceFree, outerLam, INT);
        var reduceInit = new CoreApp(SRC, reduceF, lit0, INT);
        var fullExpr = new CoreApp(SRC, reduceInit, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, emptyStream);
        assertThat(result, is(new Value.IntegerVal(0)));
    }

    public void testMapOnEmptyStream() {
        var emptyStream = new Value.StreamVal(List.of());
        var body = new CoreProject(SRC, new CoreVar(SRC, 0, "r", INT), "age", INT);
        var lambda = new CoreLam(SRC, "r", INT, body, INT);
        var mapFree = new CoreFree(SRC, "map", INT);
        var mapApplied = new CoreApp(SRC, mapFree, lambda, INT);
        var fullExpr = new CoreApp(SRC, mapApplied, new CoreVar(SRC, 0, "stream", INT), INT);

        var result = evaluateWithEnv(fullExpr, emptyStream);
        assertThat(result, instanceOf(Value.StreamVal.class));
        assertThat(((Value.StreamVal) result).elements().size(), is(0));
    }

    // ──── Spawn / When (Block A) ────

    /**
     * Evaluate a piescript program using a real thread pool executor.
     * Required for spawn tests where the forked computation must run on a
     * separate thread to exercise the async path.
     */
    private Value evaluateAsync(String source) throws Exception {
        var parser = new PiescriptParser();
        var program = parser.parse(source);
        var state = new ElaborationState();
        var elaborator = new Elaborator(state);
        var coreExpr = elaborator.elaborateProgram(program);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            var future = new PlainActionFuture<Value>();
            new Evaluator(null, pool).evaluate(coreExpr, future);
            return future.actionGet(5, TimeUnit.SECONDS);
        } finally {
            pool.shutdown();
        }
    }

    public void testSpawnAndWhenPureValue() {
        assertThat(evaluate("let ch = spawn 42 in when (ch x) -> x"), is(new Value.IntegerVal(42)));
    }

    public void testSpawnAndWhenWithComputation() {
        assertThat(evaluate("let ch = spawn (1 + 2) in when (ch x) -> x + 10"), is(new Value.IntegerVal(13)));
    }

    public void testSpawnAndWhenBoolean() {
        assertThat(evaluate("let ch = spawn true in when (ch b) -> b"), is(new Value.BooleanVal(true)));
    }

    public void testSpawnAndWhenString() {
        assertThat(evaluate("let ch = spawn \"hello\" in when (ch s) -> s"), is(new Value.KeywordVal("hello")));
    }

    public void testSpawnAndWhenRecord() {
        var result = evaluate("let ch = spawn { x: 1, y: 2 } in when (ch r) -> r.x + r.y");
        assertThat(result, is(new Value.IntegerVal(3)));
    }

    public void testWhenMultipleBindings() {
        var result = evaluate("let a = spawn 10 in let b = spawn 20 in when (a x) & (b y) -> x + y");
        assertThat(result, is(new Value.IntegerVal(30)));
    }

    public void testWhenThreeBindings() {
        var result = evaluate("let a = spawn 1 in let b = spawn 2 in let c = spawn 3 in when (a x) & (b y) & (c z) -> x + y + z");
        assertThat(result, is(new Value.IntegerVal(6)));
    }

    public void testWhenMultipleBindingsProducesRecord() {
        var result = evaluate(
            "let a = spawn 42 in let b = spawn \"hello\" in when (a num) & (b greeting) -> { n: num, g: greeting }"
        );
        assertThat(result, instanceOf(Value.RecordVal.class));
        var fields = ((Value.RecordVal) result).fields();
        assertThat(fields.get("n"), is(new Value.IntegerVal(42)));
        assertThat(fields.get("g"), is(new Value.KeywordVal("hello")));
    }

    public void testSpawnNestedInWhenBody() {
        var result = evaluate(
            "let ch1 = spawn 10 in when (ch1 x) -> let ch2 = spawn (x + 5) in when (ch2 y) -> y"
        );
        assertThat(result, is(new Value.IntegerVal(15)));
    }

    public void testSpawnAndWhenAsyncWithThreadPool() throws Exception {
        assertThat(evaluateAsync("let ch = spawn 42 in when (ch x) -> x + 1"), is(new Value.IntegerVal(43)));
    }

    public void testMultiChannelWhenAsyncWithThreadPool() throws Exception {
        var result = evaluateAsync("let a = spawn 10 in let b = spawn 20 in when (a x) & (b y) -> x + y");
        assertThat(result, is(new Value.IntegerVal(30)));
    }

    public void testSpawnWithLambdaBody() {
        var result = evaluate("let ch = spawn (let f = fn x -> x * 2 in f 21) in when (ch x) -> x");
        assertThat(result, is(new Value.IntegerVal(42)));
    }

    public void testQueryWithoutClientThrows() {
        var query = new org.elasticsearch.xpack.piescript.core.CoreQuery(SRC, "FROM test", "test", INT);
        var future = new PlainActionFuture<Value>();
        new Evaluator(null, EsExecutors.DIRECT_EXECUTOR_SERVICE).evaluate(query, future);
        var ex = expectThrows(EvaluationException.class, future::actionGet);
        assertThat(ex.getMessage(), containsString("requires a client"));
    }
}
