/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.TransportVersion;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.cluster.node.VersionInformation;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.test.transport.MockTransportService;
import org.elasticsearch.threadpool.TestThreadPool;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.Elaborator;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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

    private static TestThreadPool threadPool;
    private static TransportService transportService;

    @BeforeClass
    public static void startTransport() {
        threadPool = new TestThreadPool(EvaluatorTests.class.getSimpleName());
        transportService = MockTransportService.createNewService(
            Settings.EMPTY,
            VersionInformation.CURRENT,
            TransportVersion.current(),
            threadPool
        );
        transportService.start();
    }

    @AfterClass
    public static void stopTransport() {
        transportService.stop();
        transportService.close();
        transportService = null;
        threadPool.close();
        threadPool = null;
    }

    private Value evaluate(String source) {
        var parser = new PiescriptParser();
        var program = parser.parse(source);
        var state = new ElaborationState();
        var elaborator = new Elaborator(state);
        var coreExpr = elaborator.elaborateProgram(program);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(coreExpr, future);
        return future.actionGet();
    }

    // ──── Literals ────

    public void testIntegerLiteral() {
        assertThat(evaluate("42"), is(new Value.DoubleVal(42)));
    }

    public void testLongLiteral() {
        assertThat(evaluate("3000000000"), is(new Value.DoubleVal(3_000_000_000.0)));
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
        assertThat(evaluate("1 + 2"), is(new Value.DoubleVal(3.0)));
    }

    public void testSubtraction() {
        assertThat(evaluate("10 - 3"), is(new Value.DoubleVal(7.0)));
    }

    public void testMultiplication() {
        assertThat(evaluate("4 * 5"), is(new Value.DoubleVal(20.0)));
    }

    public void testDivision() {
        var result = evaluate("10 / 3");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertThat(((Value.DoubleVal) result).value(), closeTo(3.333, 0.01));
    }

    public void testModulo() {
        assertThat(evaluate("10 % 3"), is(new Value.DoubleVal(1.0)));
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

    public void testKeywordEqual() {
        assertThat(evaluate("\"hello\" == \"hello\""), is(new Value.BooleanVal(true)));
        assertThat(evaluate("\"hello\" == \"world\""), is(new Value.BooleanVal(false)));
    }

    public void testKeywordNotEqual() {
        assertThat(evaluate("\"hello\" != \"world\""), is(new Value.BooleanVal(true)));
        assertThat(evaluate("\"hello\" != \"hello\""), is(new Value.BooleanVal(false)));
    }

    public void testBooleanEqual() {
        assertThat(evaluate("true == true"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("true == false"), is(new Value.BooleanVal(false)));
    }

    public void testBooleanNotEqual() {
        assertThat(evaluate("true != false"), is(new Value.BooleanVal(true)));
        assertThat(evaluate("false != false"), is(new Value.BooleanVal(false)));
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
        assertThat(evaluate("-5"), is(new Value.DoubleVal(-5.0)));
    }

    public void testDoubleNeg() {
        assertThat(evaluate("- -5"), is(new Value.DoubleVal(5.0)));
    }

    // ──── Let-bindings ────

    public void testLetBinding() {
        assertThat(evaluate("let x = 1 in x"), is(new Value.DoubleVal(1.0)));
    }

    public void testLetBindingWithExpression() {
        assertThat(evaluate("let x = 1 + 2 in x"), is(new Value.DoubleVal(3.0)));
    }

    public void testNestedLet() {
        assertThat(evaluate("let x = 1 in let y = 2 in x + y"), is(new Value.DoubleVal(3.0)));
    }

    public void testLetShadowing() {
        assertThat(evaluate("let x = 1 in let x = 2 in x"), is(new Value.DoubleVal(2.0)));
    }

    public void testTopLevelBindings() {
        assertThat(evaluate("let x = 10; let y = 20; x + y"), is(new Value.DoubleVal(30.0)));
    }

    // ──── Lambdas + application ────

    public void testIdentityFunction() {
        assertThat(evaluate("(fn x -> x) 42"), is(new Value.DoubleVal(42.0)));
    }

    public void testIncrementFunction() {
        assertThat(evaluate("(fn x -> x + 1) 2"), is(new Value.DoubleVal(3.0)));
    }

    public void testMultiParamLambda() {
        assertThat(evaluate("(fn x y -> x + y) 3 4"), is(new Value.DoubleVal(7.0)));
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
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.DoubleVal(1.0), "y", new Value.DoubleVal(2.0)))));
    }

    public void testRecordProjection() {
        assertThat(evaluate("{ x: 1, y: 2 }.x"), is(new Value.DoubleVal(1.0)));
    }

    public void testRecordProjectionSecondField() {
        assertThat(evaluate("{ x: 1, y: 2 }.y"), is(new Value.DoubleVal(2.0)));
    }

    public void testRecordUpdate() {
        var result = evaluate("{ { x: 1, y: 2 } | x = 3 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.DoubleVal(3.0), "y", new Value.DoubleVal(2.0)))));
    }

    public void testRecordUpdateAddField() {
        var result = evaluate("{ { x: 1 } | y = 2 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.DoubleVal(1.0), "y", new Value.DoubleVal(2.0)))));
    }

    // ──── Pipe operator ────

    public void testPipeOperator() {
        assertThat(evaluate("1 |> (fn x -> x + 1)"), is(new Value.DoubleVal(2.0)));
    }

    public void testPipeChain() {
        assertThat(evaluate("1 |> (fn x -> x + 1) |> (fn y -> y * 3)"), is(new Value.DoubleVal(6.0)));
    }

    // ──── Blocks ────

    public void testBlock() {
        assertThat(evaluate("{ let x = 1; let y = 2; x + y }"), is(new Value.DoubleVal(3.0)));
    }

    // ──── Closures ────

    public void testClosure() {
        assertThat(evaluate("let add = fn x -> fn y -> x + y in add 1 2"), is(new Value.DoubleVal(3.0)));
    }

    public void testClosureCapture() {
        assertThat(evaluate("let f = let a = 10 in fn x -> x + a in f 5"), is(new Value.DoubleVal(15.0)));
    }

    // ──── Accessor sugar ────

    public void testAccessorSugar() {
        assertThat(evaluate("{ x: 42 } |> .x"), is(new Value.DoubleVal(42.0)));
    }

    // ──── Null in arithmetic ────

    public void testNullInArithmetic() {
        var ex = expectThrows(EvaluationException.class, () -> evaluate("let x : Double = null in x + 1"));
        assertThat(ex.getMessage(), containsString("null"));
    }

    // ──── Complex expressions ────

    public void testFactorialLike() {
        var result = evaluate("let f = fn n -> n * (n - 1) * (n - 2) in f 5");
        assertThat(result, is(new Value.DoubleVal(60.0)));
    }

    public void testNestedRecordProjection() {
        assertThat(evaluate("{ inner: { x: 42 } }.inner.x"), is(new Value.DoubleVal(42.0)));
    }

    // ──── Open-row tests (Phase 1d) ────

    public void testRowPolymorphicFunction() {
        var result = evaluate("let getX = fn r -> r.x in let a = getX { x: 10, y: 20 } in a + getX { x: 5 }");
        assertThat(result, is(new Value.DoubleVal(15.0)));
    }

    public void testUpdateSugarPreservesExtraFields() {
        var result = evaluate("let upd = { _ | x = 99 } in upd { x: 1, y: 2 }");
        assertThat(result, is(new Value.RecordVal(Map.of("x", new Value.DoubleVal(99.0), "y", new Value.DoubleVal(2.0)))));
    }

    // ──── Annotated polymorphic functions (Phase 1d) ────

    public void testAnnotatedIdentityEndToEnd() {
        assertThat(evaluate("let id : a -> a = fn x -> x in id 42"), is(new Value.DoubleVal(42.0)));
    }

    public void testAnnotatedIdentityPolymorphicUse() {
        assertThat(evaluate("let id : a -> a = fn x -> x in let a = id 1 in id true"), is(new Value.BooleanVal(true)));
    }

    public void testAnnotatedMultiParamFunction() {
        assertThat(evaluate("let const : a -> b -> a = fn x y -> x in const 42 true"), is(new Value.DoubleVal(42.0)));
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
        assertThat(evaluate("{ let x = 10; let y = 20; x + y }"), is(new Value.DoubleVal(30.0)));
    }

    // ──── Pattern Matching ────

    public void testMatchLiteral() {
        var result = evaluate("match 42 | 42 -> true | _ -> false");
        assertThat(result, instanceOf(Value.BooleanVal.class));
        assertTrue(((Value.BooleanVal) result).value());
    }

    public void testMatchVariable() {
        var result = evaluate("match 42 | x -> x");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(42.0, ((Value.DoubleVal) result).value(), 0.0);
    }

    public void testMatchWildcardFallback() {
        var result = evaluate("match 42 | 1 -> false | _ -> true");
        assertThat(result, instanceOf(Value.BooleanVal.class));
        assertTrue(((Value.BooleanVal) result).value());
    }

    public void testMatchRecordDestructuring() {
        var result = evaluate("match { a: 1, b: 2 } | { a: x } -> x");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(1.0, ((Value.DoubleVal) result).value(), 0.0);
    }

    public void testMatchRecordTail() {
        var result = evaluate("match { a: 1, b: 2 } | { a: x | rest } -> rest.b");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(2.0, ((Value.DoubleVal) result).value(), 0.0);
    }

    public void testMatchListDestructuring() {
        var result = evaluate("match [1, 2] | [x, y] -> y");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(2.0, ((Value.DoubleVal) result).value(), 0.0);
    }

    public void testMatchConsList() {
        var result = evaluate("match [1, 2] | [h | t] -> h");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(1.0, ((Value.DoubleVal) result).value(), 0.0);
    }

    public void testMatchConsListTail() {
        var result = evaluate("match [1, 2] | [h | t] -> List.head t");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(2.0, ((Value.DoubleVal) result).value(), 0.0);
    }

    public void testMatchNoMatch() {
        var e = expectThrows(EvaluationException.class, () -> evaluate("match 42 | 1 -> true"));
        assertThat(e.getMessage(), containsString("No match for value"));
    }

    public void testIfElseEvaluation() {
        var result = evaluate("if true then 1 else 2");
        assertThat(result, instanceOf(Value.DoubleVal.class));
        assertEquals(1.0, ((Value.DoubleVal) result).value(), 0.0);

        var result2 = evaluate("if false then 1 else 2");
        assertThat(result2, instanceOf(Value.DoubleVal.class));
        assertEquals(2.0, ((Value.DoubleVal) result2).value(), 0.0);
    }

    // ──── Stream built-ins (Phase 2.8) ────
    //
    // These tests construct Core IR directly and inject a ListVal into the
    // evaluator environment, bypassing parsing and elaboration. This validates
    // the built-in map/filter/reduce logic without requiring a real ES client.

    private static final Source SRC = Source.EMPTY;
    private static final MonoType DBL = new MonoType.TCon("Double");
    private static final MonoType BOOL = new MonoType.TCon("Boolean");

    private static Value.ListVal testStream() {
        var row1 = new Value.RecordVal(linkedMap("name", new Value.KeywordVal("alice"), "age", new Value.DoubleVal(30)));
        var row2 = new Value.RecordVal(linkedMap("name", new Value.KeywordVal("bob"), "age", new Value.DoubleVal(25)));
        var row3 = new Value.RecordVal(linkedMap("name", new Value.KeywordVal("carol"), "age", new Value.DoubleVal(35)));
        return new Value.ListVal(List.of(row1, row2, row3));
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
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(expr, env, future);
        return future.actionGet();
    }

    public void testMapProjectField() {
        // map (fn r -> r.age) stream — extracts .age from each record
        var stream = testStream();
        var body = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL);
        var lambda = new CoreLam(SRC, "r", DBL, body, DBL);
        var mapFree = new CoreFree(SRC, "List.map", DBL);
        var mapApplied = new CoreApp(SRC, mapFree, lambda, DBL);
        var fullExpr = new CoreApp(SRC, mapApplied, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.ListVal.class));
        var elements = ((Value.ListVal) result).elements();
        assertThat(elements.size(), is(3));
        assertThat(elements.get(0), is(new Value.DoubleVal(30)));
        assertThat(elements.get(1), is(new Value.DoubleVal(25)));
        assertThat(elements.get(2), is(new Value.DoubleVal(35)));
    }

    public void testMapTransformField() {
        var stream = testStream();
        var proj = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL);
        var lit1 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.DoubleLit(1),
            DBL
        );
        var add = new CorePrimOp(SRC, Op.ADD, List.of(proj, lit1), DBL);
        var lambda = new CoreLam(SRC, "r", DBL, add, DBL);
        var mapFree = new CoreFree(SRC, "List.map", DBL);
        var mapApplied = new CoreApp(SRC, mapFree, lambda, DBL);
        var fullExpr = new CoreApp(SRC, mapApplied, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.ListVal.class));
        var elements = ((Value.ListVal) result).elements();
        assertThat(elements.size(), is(3));
        assertThat(elements.get(0), is(new Value.DoubleVal(31)));
        assertThat(elements.get(1), is(new Value.DoubleVal(26)));
        assertThat(elements.get(2), is(new Value.DoubleVal(36)));
    }

    public void testFilterByPredicate() {
        // filter (fn r -> r.age > 28) stream — keeps alice(30) and carol(35)
        var stream = testStream();
        var proj = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL);
        var lit28 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.DoubleLit(28),
            DBL
        );
        var gt = new CorePrimOp(SRC, Op.GT, List.of(proj, lit28), BOOL);
        var lambda = new CoreLam(SRC, "r", DBL, gt, DBL);
        var filterFree = new CoreFree(SRC, "List.filter", DBL);
        var filterApplied = new CoreApp(SRC, filterFree, lambda, DBL);
        var fullExpr = new CoreApp(SRC, filterApplied, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.ListVal.class));
        var elements = ((Value.ListVal) result).elements();
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
        var lambda = new CoreLam(SRC, "r", DBL, trueBody, DBL);
        var filterFree = new CoreFree(SRC, "List.filter", DBL);
        var filterApplied = new CoreApp(SRC, filterFree, lambda, DBL);
        var fullExpr = new CoreApp(SRC, filterApplied, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.ListVal.class));
        assertThat(((Value.ListVal) result).elements().size(), is(3));
    }

    public void testFilterRemovesAll() {
        // filter (fn r -> false) stream — removes everything
        var stream = testStream();
        var falseBody = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.BooleanLit(false),
            BOOL
        );
        var lambda = new CoreLam(SRC, "r", DBL, falseBody, DBL);
        var filterFree = new CoreFree(SRC, "List.filter", DBL);
        var filterApplied = new CoreApp(SRC, filterFree, lambda, DBL);
        var fullExpr = new CoreApp(SRC, filterApplied, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.ListVal.class));
        assertThat(((Value.ListVal) result).elements().size(), is(0));
    }

    public void testReduceSumAges() {
        // reduce (fn acc elem -> acc + elem.age) 0 stream — sums ages: 30+25+35=90
        var stream = testStream();
        // fn acc elem -> acc + elem.age
        // de Bruijn: elem=0, acc=1
        var accVar = new CoreVar(SRC, 1, "acc", DBL);
        var elemProj = new CoreProject(SRC, new CoreVar(SRC, 0, "elem", DBL), "age", DBL);
        var addBody = new CorePrimOp(SRC, Op.ADD, List.of(accVar, elemProj), DBL);
        var innerLam = new CoreLam(SRC, "elem", DBL, addBody, DBL);
        var outerLam = new CoreLam(SRC, "acc", DBL, innerLam, DBL);

        var reduceFree = new CoreFree(SRC, "List.reduce", DBL);
        var lit0 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.DoubleLit(0),
            DBL
        );
        // reduce outerLam 0 stream
        var reduceF = new CoreApp(SRC, reduceFree, outerLam, DBL);
        var reduceInit = new CoreApp(SRC, reduceF, lit0, DBL);
        var fullExpr = new CoreApp(SRC, reduceInit, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, is(new Value.DoubleVal(90)));
    }

    public void testReduceEmptyStream() {
        // reduce (fn acc elem -> acc + 1) 0 emptyStream — returns initial value
        var emptyStream = new Value.ListVal(List.of());
        var accVar = new CoreVar(SRC, 1, "acc", DBL);
        var lit1 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.DoubleLit(1),
            DBL
        );
        var addBody = new CorePrimOp(SRC, Op.ADD, List.of(accVar, lit1), DBL);
        var innerLam = new CoreLam(SRC, "elem", DBL, addBody, DBL);
        var outerLam = new CoreLam(SRC, "acc", DBL, innerLam, DBL);

        var reduceFree = new CoreFree(SRC, "List.reduce", DBL);
        var lit0 = new org.elasticsearch.xpack.piescript.core.CoreLit(
            SRC,
            new org.elasticsearch.xpack.piescript.types.LitVal.DoubleLit(0),
            DBL
        );
        var reduceF = new CoreApp(SRC, reduceFree, outerLam, DBL);
        var reduceInit = new CoreApp(SRC, reduceF, lit0, DBL);
        var fullExpr = new CoreApp(SRC, reduceInit, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, emptyStream);
        assertThat(result, is(new Value.DoubleVal(0)));
    }

    public void testMapOnEmptyStream() {
        var emptyStream = new Value.ListVal(List.of());
        var body = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL);
        var lambda = new CoreLam(SRC, "r", DBL, body, DBL);
        var mapFree = new CoreFree(SRC, "List.map", DBL);
        var mapApplied = new CoreApp(SRC, mapFree, lambda, DBL);
        var fullExpr = new CoreApp(SRC, mapApplied, new CoreVar(SRC, 0, "stream", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, emptyStream);
        assertThat(result, instanceOf(Value.ListVal.class));
        assertThat(((Value.ListVal) result).elements().size(), is(0));
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
            new Evaluator(testDeps(pool)).evaluate(coreExpr, future);
            return future.actionGet(5, TimeUnit.SECONDS);
        } finally {
            pool.shutdown();
        }
    }

    public void testSpawnAndWhenPureValue() {
        assertThat(evaluate("let ch = spawn 42 in when (ch x) -> x"), is(new Value.DoubleVal(42)));
    }

    public void testSpawnAndWhenWithComputation() {
        assertThat(evaluate("let ch = spawn (1 + 2) in when (ch x) -> x + 10"), is(new Value.DoubleVal(13)));
    }

    public void testSpawnAndWhenBoolean() {
        assertThat(evaluate("let ch = spawn true in when (ch b) -> b"), is(new Value.BooleanVal(true)));
    }

    public void testSpawnAndWhenString() {
        assertThat(evaluate("let ch = spawn \"hello\" in when (ch s) -> s"), is(new Value.KeywordVal("hello")));
    }

    public void testSpawnAndWhenRecord() {
        var result = evaluate("let ch = spawn { x: 1, y: 2 } in when (ch r) -> r.x + r.y");
        assertThat(result, is(new Value.DoubleVal(3)));
    }

    public void testWhenMultipleBindings() {
        var result = evaluate("let a = spawn 10 in let b = spawn 20 in when (a x) & (b y) -> x + y");
        assertThat(result, is(new Value.DoubleVal(30)));
    }

    public void testWhenThreeBindings() {
        var result = evaluate("let a = spawn 1 in let b = spawn 2 in let c = spawn 3 in when (a x) & (b y) & (c z) -> x + y + z");
        assertThat(result, is(new Value.DoubleVal(6)));
    }

    public void testWhenMultipleBindingsProducesRecord() {
        var result = evaluate("let a = spawn 42 in let b = spawn \"hello\" in when (a num) & (b greeting) -> { n: num, g: greeting }");
        assertThat(result, instanceOf(Value.RecordVal.class));
        var fields = ((Value.RecordVal) result).fields();
        assertThat(fields.get("n"), is(new Value.DoubleVal(42)));
        assertThat(fields.get("g"), is(new Value.KeywordVal("hello")));
    }

    public void testSpawnNestedInWhenBody() {
        var result = evaluate("let ch1 = spawn 10 in when (ch1 x) -> let ch2 = spawn (x + 5) in when (ch2 y) -> y");
        assertThat(result, is(new Value.DoubleVal(15)));
    }

    public void testSpawnAndWhenAsyncWithThreadPool() throws Exception {
        assertThat(evaluateAsync("let ch = spawn 42 in when (ch x) -> x + 1"), is(new Value.DoubleVal(43)));
    }

    public void testMultiChannelWhenAsyncWithThreadPool() throws Exception {
        var result = evaluateAsync("let a = spawn 10 in let b = spawn 20 in when (a x) & (b y) -> x + y");
        assertThat(result, is(new Value.DoubleVal(30)));
    }

    public void testSpawnWithLambdaBody() {
        var result = evaluate("let ch = spawn (let f = fn x -> x * 2 in f 21) in when (ch x) -> x");
        assertThat(result, is(new Value.DoubleVal(42)));
    }

    // ──── spawn! / send (Block C) ────

    public void testSpawnBangAndSendInteger() {
        var result = evaluate("let ch = spawn! in let u = send ch 42 in when (ch x) -> x");
        assertThat(result, is(new Value.DoubleVal(42)));
    }

    public void testSpawnBangAndSendBoolean() {
        var result = evaluate("let ch = spawn! in let u = send ch true in when (ch x) -> x");
        assertThat(result, is(new Value.BooleanVal(true)));
    }

    public void testSpawnBangAndSendString() {
        var result = evaluate("let ch = spawn! in let u = send ch \"hello\" in when (ch x) -> x");
        assertThat(result, is(new Value.KeywordVal("hello")));
    }

    public void testSpawnBangAndSendRecord() {
        var result = evaluate("let ch = spawn! in let u = send ch { a: 1, b: 2 } in when (ch r) -> r.a + r.b");
        assertThat(result, is(new Value.DoubleVal(3)));
    }

    public void testSendReturnsNull() {
        var result = evaluate("let ch = spawn! in send ch 42");
        assertThat(result, is(new Value.NullVal()));
    }

    public void testSpawnBangAndSendWithComputation() {
        var result = evaluate("let ch = spawn! in let u = send ch (10 + 20) in when (ch x) -> x * 2");
        assertThat(result, is(new Value.DoubleVal(60)));
    }

    public void testSpawnBangMultipleChannelsSendAndWhen() {
        var result = evaluate("let a = spawn! in let b = spawn! in let u = send a 10 in let u = send b 20 in when (a x) & (b y) -> x + y");
        assertThat(result, is(new Value.DoubleVal(30)));
    }

    public void testSpawnBangAndSendAsyncWithThreadPool() throws Exception {
        var result = evaluateAsync("let ch = spawn! in let u = send ch 99 in when (ch x) -> x");
        assertThat(result, is(new Value.DoubleVal(99)));
    }

    public void testSendToSpawnedChannelWithBody() {
        var result = evaluate("let ch = spawn 42 in when (ch x) -> x");
        assertThat(result, is(new Value.DoubleVal(42)));
    }

    // ──── List utility builtins (Block B) ────

    public void testHeadReturnFirstElement() {
        var stream = testStream();
        var headFree = new CoreFree(SRC, "List.head", DBL);
        var fullExpr = new CoreApp(SRC, headFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.RecordVal.class));
        var fields = ((Value.RecordVal) result).fields();
        assertThat(fields.get("name"), is(new Value.KeywordVal("alice")));
        assertThat(fields.get("age"), is(new Value.DoubleVal(30)));
    }

    public void testHeadEmptyListThrows() {
        var emptyList = new Value.ListVal(List.of());
        var headFree = new CoreFree(SRC, "List.head", DBL);
        var fullExpr = new CoreApp(SRC, headFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(fullExpr, new Value[] { emptyList }, future);
        var ex = expectThrows(EvaluationException.class, future::actionGet);
        assertThat(ex.getMessage(), containsString("empty list"));
    }

    public void testTailReturnsRest() {
        var stream = testStream();
        var tailFree = new CoreFree(SRC, "List.tail", DBL);
        var fullExpr = new CoreApp(SRC, tailFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, instanceOf(Value.ListVal.class));
        var elements = ((Value.ListVal) result).elements();
        assertThat(elements.size(), is(2));
        assertThat(((Value.RecordVal) elements.get(0)).fields().get("name"), is(new Value.KeywordVal("bob")));
        assertThat(((Value.RecordVal) elements.get(1)).fields().get("name"), is(new Value.KeywordVal("carol")));
    }

    public void testTailEmptyListThrows() {
        var emptyList = new Value.ListVal(List.of());
        var tailFree = new CoreFree(SRC, "List.tail", DBL);
        var fullExpr = new CoreApp(SRC, tailFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(fullExpr, new Value[] { emptyList }, future);
        var ex = expectThrows(EvaluationException.class, future::actionGet);
        assertThat(ex.getMessage(), containsString("empty list"));
    }

    public void testLengthReturnsSize() {
        var stream = testStream();
        var lengthFree = new CoreFree(SRC, "List.length", DBL);
        var fullExpr = new CoreApp(SRC, lengthFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, is(new Value.DoubleVal(3)));
    }

    public void testIsEmptyFalseForNonEmpty() {
        var stream = testStream();
        var isEmptyFree = new CoreFree(SRC, "List.isEmpty", DBL);
        var fullExpr = new CoreApp(SRC, isEmptyFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, stream);
        assertThat(result, is(new Value.BooleanVal(false)));
    }

    public void testIsEmptyTrueForEmpty() {
        var emptyList = new Value.ListVal(List.of());
        var isEmptyFree = new CoreFree(SRC, "List.isEmpty", DBL);
        var fullExpr = new CoreApp(SRC, isEmptyFree, new CoreVar(SRC, 0, "list", DBL), DBL);

        var result = evaluateWithEnv(fullExpr, emptyList);
        assertThat(result, is(new Value.BooleanVal(true)));
    }

    public void testTopologyWithoutClusterServiceThrows() {
        var topologyFree = new CoreFree(SRC, "Cluster.topology", DBL);
        var fullExpr = new CoreApp(SRC, topologyFree, new CoreVar(SRC, 0, "arg", DBL), DBL);

        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(
            fullExpr,
            new Value[] { new Value.KeywordVal("cluster") },
            future
        );
        var ex = expectThrows(EvaluationException.class, future::actionGet);
        assertThat(ex.getMessage(), containsString("requires cluster service"));
    }

    public void testRoutingWithoutClusterServiceThrows() {
        var routingFree = new CoreFree(SRC, "Index.routing", DBL);
        var fullExpr = new CoreApp(SRC, routingFree, new CoreVar(SRC, 0, "index", DBL), DBL);

        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(
            fullExpr,
            new Value[] { new Value.KeywordVal("test") },
            future
        );
        var ex = expectThrows(EvaluationException.class, future::actionGet);
        assertThat(ex.getMessage(), containsString("requires cluster service"));
    }

    // ──── Math builtins ────

    public void testAbs() {
        assertThat(evaluate("Math.abs (-5)"), is(new Value.DoubleVal(5.0)));
        assertThat(evaluate("Math.abs 3"), is(new Value.DoubleVal(3.0)));
    }

    public void testFloor() {
        assertThat(evaluate("Math.floor 3.7"), is(new Value.DoubleVal(3.0)));
        assertThat(evaluate("Math.floor (-2.3)"), is(new Value.DoubleVal(-3.0)));
    }

    public void testCeil() {
        assertThat(evaluate("Math.ceil 3.2"), is(new Value.DoubleVal(4.0)));
        assertThat(evaluate("Math.ceil (-2.7)"), is(new Value.DoubleVal(-2.0)));
    }

    public void testRound() {
        assertThat(evaluate("Math.round 3.5"), is(new Value.DoubleVal(4.0)));
        assertThat(evaluate("Math.round 3.4"), is(new Value.DoubleVal(3.0)));
    }

    public void testSqrt() {
        assertThat(evaluate("Math.sqrt 9"), is(new Value.DoubleVal(3.0)));
        assertThat(evaluate("Math.sqrt 2"), is(new Value.DoubleVal(Math.sqrt(2))));
    }

    public void testLog() {
        var result = evaluate("Math.log 1");
        assertThat(result, is(new Value.DoubleVal(0.0)));
    }

    public void testMin() {
        assertThat(evaluate("Math.min 3 5"), is(new Value.DoubleVal(3.0)));
        assertThat(evaluate("Math.min 10 2"), is(new Value.DoubleVal(2.0)));
    }

    public void testMax() {
        assertThat(evaluate("Math.max 3 5"), is(new Value.DoubleVal(5.0)));
        assertThat(evaluate("Math.max 10 2"), is(new Value.DoubleVal(10.0)));
    }

    public void testPow() {
        assertThat(evaluate("Math.pow 2 3"), is(new Value.DoubleVal(8.0)));
        assertThat(evaluate("Math.pow 3 2"), is(new Value.DoubleVal(9.0)));
    }

    public void testToInt() {
        assertThat(evaluate("Math.toInt 3.7"), is(new Value.DoubleVal(3.0)));
        assertThat(evaluate("Math.toInt (-2.9)"), is(new Value.DoubleVal(-2.0)));
    }

    public void testMathComposition() {
        assertThat(evaluate("Math.abs (Math.floor (-3.7))"), is(new Value.DoubleVal(4.0)));
        assertThat(evaluate("Math.pow (Math.sqrt 9) 2"), is(new Value.DoubleVal(9.0)));
    }

    public void testMathWithArithmetic() {
        assertThat(evaluate("Math.abs (-5) + Math.max 3 7"), is(new Value.DoubleVal(12.0)));
    }

    public void testMathInLambda() {
        assertThat(evaluate("let double = fn x -> x * 2 in Math.abs (double (-3))"), is(new Value.DoubleVal(6.0)));
    }

    public void testMinPartialApplication() {
        assertThat(evaluate("let clamp = Math.min 100 in clamp 150"), is(new Value.DoubleVal(100.0)));
        assertThat(evaluate("let clamp = Math.min 100 in clamp 50"), is(new Value.DoubleVal(50.0)));
    }

    // ──── ESQL compilation via Symbol (Block F — D-052) ────

    public void testEsqlFromProducesSymbol() {
        var from = new CoreFree(SRC, "ESQL.from", DBL);
        var idx = new Value.IndexVal("logs-*", "uuid", Map.of("status", "keyword"));
        var expr = new CoreApp(SRC, from, new CoreVar(SRC, 0, "idx", DBL), DBL);
        var result = evaluateWithEnv(expr, idx);
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("FROM logs-*", ((Value.Symbol) result).esql());
    }

    public void testEsqlKeepAppendsToSymbol() {
        // Build: ESQL.keep (fn r -> { name: r.name, age: r.age }) (Symbol("FROM logs-*"))
        var from = new Value.Symbol("FROM logs-*");

        // Lambda body: { name: r.name, age: r.age } where r = CoreVar(0)
        var recordBody = new CoreRecord(
            SRC,
            List.of("name", "age"),
            List.of(
                new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "name", DBL),
                new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL)
            ),
            DBL
        );
        var keepLam = new CoreLam(SRC, "r", DBL, recordBody, DBL);

        // Evaluate the lambda to get a ClosureVal (no captures, empty env)
        var closureFuture = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(keepLam, new Value[] {}, closureFuture);
        var closure = closureFuture.actionGet();
        assertThat(closure, instanceOf(Value.ClosureVal.class));

        // Apply ESQL.keep: args = [closure, Symbol("FROM logs-*")]
        var keepFree = new CoreFree(SRC, "ESQL.keep", DBL);
        var keepApplied = new CoreApp(SRC, keepFree, new CoreVar(SRC, 0, "pred", DBL), DBL);
        var fullExpr = new CoreApp(SRC, keepApplied, new CoreVar(SRC, 1, "plan", DBL), DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(fullExpr, new Value[] { closure, from }, future);
        var result = future.actionGet();
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("FROM logs-* | KEEP name, age", ((Value.Symbol) result).esql());
    }

    public void testEsqlLimitAppendsToSymbol() {
        var from = new Value.Symbol("FROM logs-*");
        var limitFree = new CoreFree(SRC, "ESQL.limit", DBL);
        var limitApplied = new CoreApp(SRC, limitFree, new CoreVar(SRC, 0, "n", DBL), DBL);
        var fullExpr = new CoreApp(SRC, limitApplied, new CoreVar(SRC, 1, "plan", DBL), DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(
            fullExpr,
            new Value[] { new Value.DoubleVal(100), from },
            future
        );
        var result = future.actionGet();
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("FROM logs-* | LIMIT 100", ((Value.Symbol) result).esql());
    }

    public void testEsqlWhereCompilesPredicateViaSymbol() {
        // Build: let threshold = 18 in ESQL.where (fn r -> r.age > threshold) (Symbol("FROM logs-*"))
        // The lambda captures threshold from env. ESQL.where applies it with Symbol("") row.
        var from = new Value.Symbol("FROM logs-*");
        var threshold = new Value.DoubleVal(18);

        // Lambda body: r.age > threshold. CoreVar(0)=r (param), CoreVar(1)=threshold (captured)
        var predBody = new CorePrimOp(
            SRC,
            Op.GT,
            List.of(new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL), new CoreVar(SRC, 1, "threshold", DBL)),
            DBL
        );
        var predLam = new CoreLam(SRC, "r", DBL, predBody, DBL);

        // First evaluate the lambda to create a ClosureVal capturing [threshold] env
        var closureFuture = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(predLam, new Value[] { threshold }, closureFuture);
        var closure = closureFuture.actionGet();
        assertThat(closure, instanceOf(Value.ClosureVal.class));

        // Now apply ESQL.where: args = [closure, Symbol("FROM logs-*")]
        var whereFree = new CoreFree(SRC, "ESQL.where", DBL);
        var whereApplied = new CoreApp(SRC, whereFree, new CoreVar(SRC, 0, "pred", DBL), DBL);
        var fullExpr = new CoreApp(SRC, whereApplied, new CoreVar(SRC, 1, "plan", DBL), DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(fullExpr, new Value[] { closure, from }, future);
        var result = future.actionGet();
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("FROM logs-* | WHERE (age > 18)", ((Value.Symbol) result).esql());
    }

    public void testSymbolProjectionProducesFieldName() {
        var proj = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "status", DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(proj, new Value[] { new Value.Symbol("") }, future);
        var result = future.actionGet();
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("status", ((Value.Symbol) result).esql());
    }

    public void testSymbolPrimOpCompilesBothOperands() {
        var left = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "price", DBL);
        var right = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "qty", DBL);
        var mul = new CorePrimOp(SRC, Op.MUL, List.of(left, right), DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(mul, new Value[] { new Value.Symbol("") }, future);
        var result = future.actionGet();
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("(price * qty)", ((Value.Symbol) result).esql());
    }

    public void testSymbolBooleanOperators() {
        var left = new CorePrimOp(
            SRC,
            Op.EQ,
            List.of(new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "status", DBL), new CoreVar(SRC, 1, "val", DBL)),
            DBL
        );
        var right = new CorePrimOp(
            SRC,
            Op.GT,
            List.of(new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "age", DBL), new CoreVar(SRC, 2, "min", DBL)),
            DBL
        );
        var and = new CorePrimOp(SRC, Op.AND, List.of(left, right), DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(
            and,
            new Value[] { new Value.Symbol(""), new Value.KeywordVal("active"), new Value.DoubleVal(18) },
            future
        );
        var result = future.actionGet();
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("((status == \"active\") AND (age > 18))", ((Value.Symbol) result).esql());
    }

    public void testEsqlExplainReturnsString() {
        var from = new Value.Symbol("FROM logs-*");
        var explainFree = new CoreFree(SRC, "ESQL.explain", DBL);
        var fullExpr = new CoreApp(SRC, explainFree, new CoreVar(SRC, 0, "plan", DBL), DBL);
        var result = evaluateWithEnv(fullExpr, from);
        assertThat(result, instanceOf(Value.KeywordVal.class));
        assertEquals("FROM logs-*", ((Value.KeywordVal) result).value());
    }

    public void testCompileValueToEsqlLiterals() {
        assertEquals("42", EvalBuiltins.compileValueToEsql(new Value.DoubleVal(42)));
        assertEquals("3.14", EvalBuiltins.compileValueToEsql(new Value.DoubleVal(3.14)));
        assertEquals("\"hello\"", EvalBuiltins.compileValueToEsql(new Value.KeywordVal("hello")));
        assertEquals("true", EvalBuiltins.compileValueToEsql(new Value.BooleanVal(true)));
        assertEquals("false", EvalBuiltins.compileValueToEsql(new Value.BooleanVal(false)));
        assertEquals("null", EvalBuiltins.compileValueToEsql(new Value.NullVal()));
        assertEquals("field", EvalBuiltins.compileValueToEsql(new Value.Symbol("field")));
    }

    // ──── ESQL stats/aggregate NbE compilation tests ────

    public void testEsqlCountProducesSymbol() {
        // ESQL.count "*" → Symbol("COUNT(*)")
        var countFree = new CoreFree(SRC, "ESQL.count", DBL);
        var expr = new CoreApp(SRC, countFree, new CoreVar(SRC, 0, "field", DBL), DBL);
        var result = evaluateWithEnv(expr, new Value.KeywordVal("*"));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("COUNT(*)", ((Value.Symbol) result).esql());
    }

    public void testEsqlAvgProducesSymbol() {
        // ESQL.avg (\r -> r.salary) → Symbol("AVG(salary)")
        var body = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "salary", DBL);
        var lam = new CoreLam(SRC, "r", DBL, body, DBL);
        var closure = evaluateWithEnv(lam);

        var avgFree = new CoreFree(SRC, "ESQL.avg", DBL);
        var expr = new CoreApp(SRC, avgFree, new CoreVar(SRC, 0, "c", DBL), DBL);
        var result = evaluateWithEnv(expr, closure);
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("AVG(salary)", ((Value.Symbol) result).esql());
    }

    public void testEsqlStatsCompiles() {
        // ESQL.stats (\r -> { count: ESQL.count "*" }) (Symbol("FROM idx"))
        // → Symbol("FROM idx | STATS count = COUNT(*)")
        var countFree = new CoreFree(SRC, "ESQL.count", DBL);
        var countApp = new CoreApp(SRC, countFree, new CoreVar(SRC, 1, "star", DBL), DBL);
        var recordBody = new CoreRecord(SRC, List.of("count"), List.of(countApp), DBL);
        var lam = new CoreLam(SRC, "r", DBL, recordBody, DBL);

        // Evaluate lambda with ["*"] in env to capture the "*" argument
        var closure = evaluateWithEnv(lam, new Value.KeywordVal("*"));

        var statsFree = new CoreFree(SRC, "ESQL.stats", DBL);
        var statsApplied = new CoreApp(SRC, statsFree, new CoreVar(SRC, 0, "agg", DBL), DBL);
        var fullExpr = new CoreApp(SRC, statsApplied, new CoreVar(SRC, 1, "plan", DBL), DBL);
        var result = evaluateWithEnv(fullExpr, closure, new Value.Symbol("FROM idx"));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("FROM idx | STATS count = COUNT(*)", ((Value.Symbol) result).esql());
    }

    public void testEsqlStatsByCompiles() {
        // ESQL.statsBy (\r -> { count: ESQL.count "*" }) (\r -> { dept: r.department }) (Symbol("FROM emp"))
        // → Symbol("FROM emp | STATS count = COUNT(*) BY dept = department")

        // Agg closure: \r -> { count: ESQL.count "*" }
        var countFree = new CoreFree(SRC, "ESQL.count", DBL);
        var countApp = new CoreApp(SRC, countFree, new CoreVar(SRC, 1, "star", DBL), DBL);
        var aggRecord = new CoreRecord(SRC, List.of("count"), List.of(countApp), DBL);
        var aggLam = new CoreLam(SRC, "r", DBL, aggRecord, DBL);
        var aggClosure = evaluateWithEnv(aggLam, new Value.KeywordVal("*"));

        // Group closure: \r -> { dept: r.department }
        var deptProj = new CoreProject(SRC, new CoreVar(SRC, 0, "r", DBL), "department", DBL);
        var groupRecord = new CoreRecord(SRC, List.of("dept"), List.of(deptProj), DBL);
        var groupLam = new CoreLam(SRC, "r", DBL, groupRecord, DBL);
        var groupClosure = evaluateWithEnv(groupLam);

        var statsByFree = new CoreFree(SRC, "ESQL.statsBy", DBL);
        var app1 = new CoreApp(SRC, statsByFree, new CoreVar(SRC, 0, "agg", DBL), DBL);
        var app2 = new CoreApp(SRC, app1, new CoreVar(SRC, 1, "group", DBL), DBL);
        var fullExpr = new CoreApp(SRC, app2, new CoreVar(SRC, 2, "plan", DBL), DBL);
        var result = evaluateWithEnv(fullExpr, aggClosure, groupClosure, new Value.Symbol("FROM emp"));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("FROM emp | STATS count = COUNT(*) BY dept = department", ((Value.Symbol) result).esql());
    }

    public void testEsqlBucketProducesSymbol() {
        // ESQL.bucket (Symbol("timestamp")) 3600.0 → Symbol("BUCKET(timestamp, 3600)")
        var bucketFree = new CoreFree(SRC, "ESQL.bucket", DBL);
        var app1 = new CoreApp(SRC, bucketFree, new CoreVar(SRC, 0, "field", DBL), DBL);
        var expr = new CoreApp(SRC, app1, new CoreVar(SRC, 1, "span", DBL), DBL);
        var result = evaluateWithEnv(expr, new Value.Symbol("timestamp"), new Value.DoubleVal(3600));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("BUCKET(timestamp, 3600)", ((Value.Symbol) result).esql());
    }

    public void testCompileValueToEsqlEscapesStrings() {
        assertEquals("\"say \\\"hi\\\"\"", EvalBuiltins.compileValueToEsql(new Value.KeywordVal("say \"hi\"")));
    }

    // ──── ESQL.top / ESQL.values NbE compilation tests ────

    public void testEsqlTopProducesSymbol() {
        // ESQL.top (Symbol("age")) 3.0 "desc" → Symbol("TOP(age, 3, \"desc\")")
        var topFree = new CoreFree(SRC, "ESQL.top", DBL);
        var app1 = new CoreApp(SRC, topFree, new CoreVar(SRC, 0, "field", DBL), DBL);
        var app2 = new CoreApp(SRC, app1, new CoreVar(SRC, 1, "count", DBL), DBL);
        var expr = new CoreApp(SRC, app2, new CoreVar(SRC, 2, "order", DBL), DBL);
        var result = evaluateWithEnv(expr, new Value.Symbol("age"), new Value.DoubleVal(3), new Value.KeywordVal("desc"));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("TOP(age, 3, \"desc\")", ((Value.Symbol) result).esql());
    }

    public void testEsqlTopAscProducesSymbol() {
        var topFree = new CoreFree(SRC, "ESQL.top", DBL);
        var app1 = new CoreApp(SRC, topFree, new CoreVar(SRC, 0, "field", DBL), DBL);
        var app2 = new CoreApp(SRC, app1, new CoreVar(SRC, 1, "count", DBL), DBL);
        var expr = new CoreApp(SRC, app2, new CoreVar(SRC, 2, "order", DBL), DBL);
        var result = evaluateWithEnv(expr, new Value.Symbol("score"), new Value.DoubleVal(5), new Value.KeywordVal("asc"));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("TOP(score, 5, \"asc\")", ((Value.Symbol) result).esql());
    }

    public void testEsqlTopInvalidOrderFails() {
        var topFree = new CoreFree(SRC, "ESQL.top", DBL);
        var app1 = new CoreApp(SRC, topFree, new CoreVar(SRC, 0, "field", DBL), DBL);
        var app2 = new CoreApp(SRC, app1, new CoreVar(SRC, 1, "count", DBL), DBL);
        var expr = new CoreApp(SRC, app2, new CoreVar(SRC, 2, "order", DBL), DBL);
        var future = new PlainActionFuture<Value>();
        new Evaluator(testDeps(EsExecutors.DIRECT_EXECUTOR_SERVICE)).evaluate(
            expr,
            new Value[] { new Value.Symbol("age"), new Value.DoubleVal(3), new Value.KeywordVal("bad") },
            future
        );
        var ex = expectThrows(EvaluationException.class, future::actionGet);
        assertThat(ex.getMessage(), containsString("order must be \"asc\" or \"desc\""));
    }

    public void testEsqlValuesProducesSymbol() {
        // ESQL.values (Symbol("name")) → Symbol("VALUES(name)")
        var valuesFree = new CoreFree(SRC, "ESQL.values", DBL);
        var expr = new CoreApp(SRC, valuesFree, new CoreVar(SRC, 0, "field", DBL), DBL);
        var result = evaluateWithEnv(expr, new Value.Symbol("name"));
        assertThat(result, instanceOf(Value.Symbol.class));
        assertEquals("VALUES(name)", ((Value.Symbol) result).esql());
    }

    // ──── EsqlValueConverter type-driven materialization tests ────

    public void testConvertCellScalar() {
        assertEquals(new Value.DoubleVal(42), EsqlValueConverter.convertCell(42));
        assertEquals(new Value.DoubleVal(3.14), EsqlValueConverter.convertCell(3.14));
        assertEquals(new Value.KeywordVal("hello"), EsqlValueConverter.convertCell("hello"));
        assertEquals(new Value.BooleanVal(true), EsqlValueConverter.convertCell(true));
        assertTrue(EsqlValueConverter.convertCell(null) instanceof Value.NullVal);
    }

    public void testConvertCellMvTakesFirst() {
        // Without listColumns, MV fields take first element
        var result = EsqlValueConverter.convertCell(java.util.List.of(10, 20, 30));
        assertEquals(new Value.DoubleVal(10), result);
    }

    public void testConvertCellMvEmptyIsNull() {
        var result = EsqlValueConverter.convertCell(java.util.List.of());
        assertTrue(result instanceof Value.NullVal);
    }

    private static EvalDependencies testDeps(java.util.concurrent.Executor executor) {
        return new EvalDependencies(
            null,
            executor,
            null,
            transportService,
            new ChannelRegistry(),
            transportService.getLocalNode().getId(),
            null,
            null,
            null,
            null
        );
    }
}
