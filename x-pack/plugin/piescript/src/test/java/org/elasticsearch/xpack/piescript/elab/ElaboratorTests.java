/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.esql.core.type.DataType;
import org.elasticsearch.xpack.esql.core.type.EsField;
import org.elasticsearch.xpack.esql.core.type.InvalidMappedField;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreQuery;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreTypeApp;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class ElaboratorTests extends ESTestCase {

    private static final MonoType INTEGER = new MonoType.TCon("Integer");
    private static final MonoType LONG = new MonoType.TCon("Long");
    private static final MonoType DOUBLE = new MonoType.TCon("Double");
    private static final MonoType KEYWORD = new MonoType.TCon("Keyword");
    private static final MonoType BOOLEAN = new MonoType.TCon("Boolean");
    private static final MonoType NULL_TYPE = new MonoType.TCon("Null");

    private ElaborationState state;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        state = new ElaborationState();
    }

    private CoreExpr elaborate(String source) {
        var parser = new PiescriptParser();
        var program = parser.parse(source);
        var elaborator = new Elaborator(state);
        return elaborator.elaborateProgram(program);
    }

    private MonoType resolveType(CoreExpr expr) {
        return TypeWalker.resolveDeep(expr.type(), state);
    }

    // ──── Literals ────

    public void testIntegerLiteral() {
        var result = elaborate("42");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.DoubleLit(42.0)));
        assertThat(result.type(), is(DOUBLE));
    }

    public void testNegativeIntegerLiteral() {
        var result = elaborate("-7");
        assertThat(result, instanceOf(CorePrimOp.class));
        var neg = (CorePrimOp) result;
        assertThat(neg.op(), is(Op.NEG));
        assertThat(resolveType(neg), is(DOUBLE));
    }

    public void testLargeIntegerBecomesDouble() {
        var result = elaborate("3000000000");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.DoubleLit(3_000_000_000.0)));
        assertThat(result.type(), is(DOUBLE));
    }

    public void testDecimalLiteral() {
        var result = elaborate("3.14");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.DoubleLit(3.14)));
        assertThat(result.type(), is(DOUBLE));
    }

    public void testStringLiteral() {
        var result = elaborate("\"hello\"");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.KeywordLit(new BytesRef("hello"))));
        assertThat(result.type(), is(KEYWORD));
    }

    public void testStringEscapeSequences() {
        var result = elaborate("\"line1\\nline2\"");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.KeywordLit(new BytesRef("line1\nline2"))));
    }

    public void testTrueLiteral() {
        var result = elaborate("true");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.BooleanLit(true)));
        assertThat(result.type(), is(BOOLEAN));
    }

    public void testFalseLiteral() {
        var result = elaborate("false");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.BooleanLit(false)));
        assertThat(result.type(), is(BOOLEAN));
    }

    public void testNullLiteral() {
        var result = elaborate("null");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), instanceOf(LitVal.NullLit.class));
        assertThat(result.type(), is(NULL_TYPE));
    }

    // ──── Let bindings ────

    public void testLetBinding() {
        var result = elaborate("let x = 1 in x");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.debugName(), is("x"));
        assertThat(let.rhs(), instanceOf(CoreLit.class));
        assertThat(let.body(), instanceOf(CoreVar.class));
        assertThat(resolveType(let), is(DOUBLE));
    }

    public void testLetWithTypeAnnotation() {
        var result = elaborate("let x : Double = 1 in x");
        assertThat(result, instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testNestedLet() {
        var result = elaborate("let x = 1 in let y = 2 in x");
        assertThat(result, instanceOf(CoreLet.class));
        var outer = (CoreLet) result;
        assertThat(outer.body(), instanceOf(CoreLet.class));
        var inner = (CoreLet) outer.body();
        assertThat(inner.body(), instanceOf(CoreVar.class));
        var ref = (CoreVar) inner.body();
        assertThat(ref.debugName(), is("x"));
        assertThat(ref.index(), is(1));
    }

    public void testLetShadowing() {
        var result = elaborate("let x = 1 in let x = true in x");
        assertThat(resolveType(result), is(BOOLEAN));
    }

    // ──── Top-level bindings ────

    public void testTopBinding() {
        var result = elaborate("let x = 42; x");
        assertThat(result, instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testMultipleTopBindings() {
        var result = elaborate("let x = 1; let y = 2; x");
        assertThat(result, instanceOf(CoreLet.class));
        var outer = (CoreLet) result;
        assertThat(outer.body(), instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Lambdas ────

    public void testIdentityLambda() {
        var result = elaborate("fn x -> x");
        assertThat(result, instanceOf(CoreLam.class));
        var lam = (CoreLam) result;
        assertThat(lam.debugName(), is("x"));
        assertThat(lam.body(), instanceOf(CoreVar.class));
        var resolved = resolveType(result);
        assertThat(resolved, instanceOf(MonoType.Arrow.class));
    }

    public void testTypedParamLambda() {
        var result = elaborate("fn (x : Double) -> x");
        assertThat(result, instanceOf(CoreLam.class));
        var lam = (CoreLam) result;
        assertThat(lam.paramType(), is(DOUBLE));
        assertThat(resolveType(result), is(new MonoType.Arrow(DOUBLE, DOUBLE)));
    }

    public void testMultiParamLambda() {
        var result = elaborate("fn x y -> x");
        assertThat(result, instanceOf(CoreLam.class));
        var outer = (CoreLam) result;
        assertThat(outer.body(), instanceOf(CoreLam.class));
    }

    // ──── Function application ────

    public void testApplication() {
        var result = elaborate("let f = fn (x : Double) -> x in f 42");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.body(), instanceOf(CoreApp.class));
        assertThat(resolveType(let), is(DOUBLE));
    }

    public void testApplicationInfersParamType() {
        var result = elaborate("let f = fn x -> x in f 42");
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Let-polymorphism ────

    public void testLetPolymorphism() {
        var result = elaborate("let id = fn x -> x in let a = id 1 in id true");
        assertThat(resolveType(result), is(BOOLEAN));
    }

    // ──── Arithmetic operators ────

    public void testAddition() {
        var result = elaborate("1 + 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        var op = (CorePrimOp) result;
        assertThat(op.op(), is(Op.ADD));
        assertThat(resolveType(op), is(DOUBLE));
    }

    public void testSubtraction() {
        var result = elaborate("3 - 1");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.SUB));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testMultiplication() {
        var result = elaborate("2 * 3");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.MUL));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testDivision() {
        var result = elaborate("10 / 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.DIV));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testModulo() {
        var result = elaborate("10 % 3");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.MOD));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testArithmeticChain() {
        var result = elaborate("1 + 2 * 3");
        assertThat(result, instanceOf(CorePrimOp.class));
        var add = (CorePrimOp) result;
        assertThat(add.op(), is(Op.ADD));
        assertThat(add.args().get(1), instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) add.args().get(1)).op(), is(Op.MUL));
    }

    // ──── Comparison operators ────

    public void testEquality() {
        var result = elaborate("1 == 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.EQ));
        assertThat(resolveType(result), is(BOOLEAN));
    }

    public void testNotEqual() {
        var result = elaborate("1 != 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.NEQ));
        assertThat(resolveType(result), is(BOOLEAN));
    }

    public void testKeywordEquality() {
        assertThat(resolveType(elaborate("\"a\" == \"b\"")), is(BOOLEAN));
        assertThat(resolveType(elaborate("\"a\" != \"b\"")), is(BOOLEAN));
    }

    public void testBooleanEquality() {
        assertThat(resolveType(elaborate("true == false")), is(BOOLEAN));
        assertThat(resolveType(elaborate("true != false")), is(BOOLEAN));
    }

    public void testEqualityTypeMismatchFails() {
        var e = expectThrows(ElaborationException.class, () -> elaborate("1 == \"hello\""));
        assertThat(e.getMessage(), containsString("type mismatch"));
    }

    public void testLessThan() {
        var result = elaborate("1 < 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.LT));
        assertThat(resolveType(result), is(BOOLEAN));
    }

    public void testGreaterThan() {
        var result = elaborate("1 > 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.GT));
    }

    public void testLessThanOrEqual() {
        var result = elaborate("1 <= 2");
        assertThat(((CorePrimOp) result).op(), is(Op.LTE));
    }

    public void testGreaterThanOrEqual() {
        var result = elaborate("1 >= 2");
        assertThat(((CorePrimOp) result).op(), is(Op.GTE));
    }

    // ──── Boolean operators ────

    public void testAnd() {
        var result = elaborate("true && false");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.AND));
        assertThat(resolveType(result), is(BOOLEAN));
    }

    public void testOr() {
        var result = elaborate("true || false");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.OR));
        assertThat(resolveType(result), is(BOOLEAN));
    }

    public void testNot() {
        var result = elaborate("!true");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.NOT));
        assertThat(resolveType(result), is(BOOLEAN));
    }

    // ──── Records ────

    public void testEmptyRecord() {
        var result = elaborate("{}");
        assertThat(result, instanceOf(CoreRecord.class));
        var rec = (CoreRecord) result;
        assertThat(rec.labels().size(), is(0));
        assertThat(resolveType(rec), is(new MonoType.RecordType(RowType.closed(Map.of()))));
    }

    public void testRecordLiteral() {
        var result = elaborate("{ name: \"alice\", age: 42 }");
        assertThat(result, instanceOf(CoreRecord.class));
        var rec = (CoreRecord) result;
        assertThat(rec.labels().size(), is(2));
        assertThat(rec.labels().get(0), is("name"));
        assertThat(rec.labels().get(1), is("age"));
        var recType = resolveType(rec);
        assertThat(recType, instanceOf(MonoType.RecordType.class));
    }

    public void testRecordProjection() {
        var result = elaborate("{ x: 1, y: 2 }.x");
        assertThat(result, instanceOf(CoreProject.class));
        var proj = (CoreProject) result;
        assertThat(proj.label(), is("x"));
        assertThat(resolveType(proj), is(DOUBLE));
    }

    public void testRecordUpdate() {
        var result = elaborate("let r = { x: 1, y: 2 } in { r | x = 3 }");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.body(), instanceOf(CoreUpdate.class));
        var update = (CoreUpdate) let.body();
        assertThat(update.labels().get(0), is("x"));
        assertThat(resolveType(update), instanceOf(MonoType.RecordType.class));
    }

    public void testRecordUpdateAddsField() {
        var result = elaborate("let r = { x: 1 } in { r | y = true }");
        var let = (CoreLet) result;
        var update = (CoreUpdate) let.body();
        var recType = (MonoType.RecordType) resolveType(update);
        assertThat(recType.row().fields().containsKey("x"), is(true));
        assertThat(recType.row().fields().containsKey("y"), is(true));
        assertThat(recType.row().fields().get("y"), is(BOOLEAN));
    }

    // ──── Pipe operator ────

    public void testPipeOperator() {
        var result = elaborate("let f = fn (x : Double) -> x in 42 |> f");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.body(), instanceOf(CoreApp.class));
        assertThat(resolveType(let), is(DOUBLE));
    }

    // ──── Accessor sugar ────

    public void testAccessor() {
        var result = elaborate(".name");
        assertThat(result, instanceOf(CoreLam.class));
        var lam = (CoreLam) result;
        assertThat(lam.debugName(), is("$acc"));
        assertThat(lam.body(), instanceOf(CoreProject.class));
        assertThat(lam.paramType(), instanceOf(MonoType.RecordType.class));
    }

    public void testAccessorPipedIntoRecord() {
        var result = elaborate("{ x: 1 } |> .x");
        assertThat(result, instanceOf(CoreApp.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Update sugar ────

    public void testUpdateSugar() {
        var result = elaborate("{ _ | x = 1 }");
        assertThat(result, instanceOf(CoreLam.class));
        var lam = (CoreLam) result;
        assertThat(lam.debugName(), is("$upd"));
        assertThat(lam.body(), instanceOf(CoreUpdate.class));
    }

    // ──── Block expressions ────

    public void testBlockWithLetStmt() {
        var result = elaborate("{ let x = 1; x }");
        assertThat(result, instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testBlockWithExprStmt() {
        var result = elaborate("{ 1; 2 }");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testBlockMultipleStmts() {
        var result = elaborate("{ let x = 1; let y = 2; x + y }");
        assertThat(result, instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Parentheses and ascription ────

    public void testParenExpr() {
        var result = elaborate("(42)");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testTypeAscription() {
        var result = elaborate("(42 : Double)");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── De Bruijn indices ────

    public void testDeBruijnIndex0ForImmediateBinder() {
        var result = elaborate("fn x -> x");
        var lam = (CoreLam) result;
        var body = (CoreVar) lam.body();
        assertThat(body.index(), is(0));
    }

    public void testDeBruijnIndexInNestedLambda() {
        var result = elaborate("fn x y -> x");
        var outer = (CoreLam) result;
        var inner = (CoreLam) outer.body();
        var ref = (CoreVar) inner.body();
        assertThat(ref.index(), is(1));
        assertThat(ref.debugName(), is("x"));
    }

    public void testDeBruijnIndexInLet() {
        var result = elaborate("let x = 1 in let y = 2 in x");
        var outer = (CoreLet) result;
        var inner = (CoreLet) outer.body();
        var ref = (CoreVar) inner.body();
        assertThat(ref.index(), is(1));
        assertThat(ref.debugName(), is("x"));
    }

    // ──── Type inference through application ────

    public void testLambdaParamTypeInferredFromUsage() {
        var result = elaborate("let f = fn x -> x + 1 in f 10");
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Error cases ────

    public void testUnboundVariable() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("x"));
        assertThat(ex.getMessage(), containsString("unbound variable"));
    }

    public void testTypeMismatchInArithmetic() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("true + 1"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testBooleanMismatchInLogic() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("1 && 2"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testNotOnNonBoolean() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("!42"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testNegationOnNonInteger() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("-true"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testApplicationOfNonFunction() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("let x = 1 in x 2"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testDuplicateRecordField() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("{ x: 1, x: 2 }"));
        assertThat(ex.getMessage(), containsString("duplicate field"));
    }

    public void testProjectionOnNonRecord() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("42.x"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testProjectionMissingField() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("{ x: 1 }.y"));
        assertThat(ex.getMessage(), containsString("missing fields"));
    }

    public void testWrongAnnotationType() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("(true : Double)"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testLetAnnotationMismatch() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("let x : Boolean = 1 in x"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testIfExprNotSupported() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("if true then 1 else 2"));
        assertThat(ex.getMessage(), containsString("not yet supported"));
    }

    public void testUpdateOnNonRecord() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("{ 42 | x = 1 }"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testUnknownTypeAnnotation() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("(42 : Foo)"));
        assertThat(ex.getMessage(), containsString("unknown type"));
    }

    // ──── Deferred tests (Phase 1c) ────

    public void testOccursCheck() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("fn x -> x x"));
        assertThat(ex.getMessage(), containsString("infinite type"));
    }

    public void testCrossTypeArithmeticStringPlusInt() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("\"hello\" + 1"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testCrossTypeArithmeticDoublePlusInt() {
        var result = elaborate("3.14 + 1");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testLambdaAppliedToWrongType() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("(fn (x : Double) -> x) \"hello\""));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    // ──── Open-row tests (Phase 1d) ────

    public void testRowPolymorphicLet() {
        var result = elaborate("let get = fn r -> r.x in get { x: 1, y: 2 }");
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testRowPolymorphicLetDifferentShapes() {
        var result = elaborate("let getX = fn r -> r.x in let a = getX { x: 1 } in getX { x: 2, y: true }");
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Type annotation with type variables (Phase 1d) ────

    public void testTypeAnnotationWithTypeVar() {
        var result = elaborate("let id : a -> a = fn x -> x in id 42");
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testTypeAnnotationMismatch() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("let f : a -> a = fn x -> 42 in f"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    public void testAnnotatedIdentityPolymorphic() {
        var result = elaborate("let id : a -> a = fn x -> x in let a = id 1 in id true");
        assertThat(resolveType(result), is(BOOLEAN));
    }

    // ──── Polytype ascription (D-036) ────

    @AwaitsFix(bugUrl = "D-038: MonoType needs Forall variant for polytype ascription")
    public void testPolytypeAscription() {
        var result = elaborate("let f = (fn x -> x : a -> a) in let a = f 1 in f true");
        assertThat(resolveType(result), is(BOOLEAN));
    }

    @AwaitsFix(bugUrl = "D-038: MonoType needs Forall variant for polytype ascription")
    public void testPolytypeAscriptionApplied() {
        var result = elaborate("(fn x -> x : a -> a) 42");
        assertThat(resolveType(result), is(DOUBLE));
    }

    @AwaitsFix(bugUrl = "D-038: MonoType needs Forall variant for polytype ascription")
    public void testAscriptionMismatchWithPolytype() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("(42 : a -> a)"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    // ──── Record check mode (D-036) ────

    public void testRecordCheckedAgainstAnnotation() {
        var result = elaborate("let r : { x: Double, y: Boolean } = { x: 1, y: true } in r.x");
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testRecordCheckFieldMismatch() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("let r : { x: Double } = { x: true } in r"));
        assertThat(ex.getMessage(), containsString("type mismatch"));
    }

    // ──── Multi-param lambda check (D-036) ────

    public void testMultiParamLambdaCheckedAgainstAnnotation() {
        var result = elaborate("let add : Double -> Double -> Double = fn x y -> x + y in add 1 2");
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testMultiParamLambdaPolymorphicAnnotation() {
        var result = elaborate("let const : a -> b -> a = fn x y -> x in let a = const 1 true in const true 42");
        assertThat(resolveType(result), is(BOOLEAN));
    }

    // ──── Let/block body propagation (D-036) ────

    public void testLetBodyCheckPropagation() {
        var result = elaborate("(let x = fn a -> a in x 42 : Double)");
        assertThat(resolveType(result), is(DOUBLE));
    }

    public void testBlockBodyCheckPropagation() {
        var result = elaborate("({ let x = 1; x } : Double)");
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── CoreTypeAbs / CoreTypeApp wrapping ────

    public void testPolymorphicLetProducesCoreTypeAbs() {
        var result = elaborate("let id = fn x -> x in id 42");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.rhs(), instanceOf(CoreTypeAbs.class));
    }

    public void testPolymorphicUseProducesCoreTypeApp() {
        var result = elaborate("let id = fn x -> x in id 42");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.body(), instanceOf(CoreApp.class));
        var app = (CoreApp) let.body();
        assertThat(app.fn(), instanceOf(CoreTypeApp.class));
    }

    // ──── Lambda param type variable ────

    public void testLambdaParamTypeVarIsMonomorphic() {
        var result = elaborate("let f = fn (x : Double) -> fn (y : Double) -> x + y in f 1 2");
        assertThat(resolveType(result), is(DOUBLE));
    }

    // ──── Spawn / When (Block A) ────

    public void testSpawnProducesChannelType() {
        var result = elaborate("spawn 42");
        assertThat(result, instanceOf(org.elasticsearch.xpack.piescript.core.CoreSpawn.class));
        var type = resolveType(result);
        assertThat(type, instanceOf(MonoType.AppType.class));
        var appType = (MonoType.AppType) type;
        assertEquals(new MonoType.TCon("Channel"), appType.constructor());
        assertEquals(DOUBLE, appType.argument());
    }

    public void testSpawnExpressionTypeInfersBody() {
        var result = elaborate("spawn (1 + 2)");
        var type = resolveType(result);
        assertThat(type, instanceOf(MonoType.AppType.class));
        var appType = (MonoType.AppType) type;
        assertEquals(new MonoType.TCon("Channel"), appType.constructor());
        assertEquals(DOUBLE, appType.argument());
    }

    public void testSpawnBooleanBody() {
        var result = elaborate("spawn true");
        var type = resolveType(result);
        var appType = (MonoType.AppType) type;
        assertEquals(new MonoType.TCon("Channel"), appType.constructor());
        assertEquals(BOOLEAN, appType.argument());
    }

    public void testWhenUnwrapsChannelType() {
        var result = elaborate("let ch = spawn 42 in when (ch x) -> x");
        var type = resolveType(result);
        assertEquals(DOUBLE, type);
    }

    public void testWhenBodyExpressionType() {
        var result = elaborate("let ch = spawn 42 in when (ch x) -> x + 1");
        var type = resolveType(result);
        assertEquals(DOUBLE, type);
    }

    public void testWhenMultipleBindingsType() {
        var result = elaborate("let a = spawn 1 in let b = spawn true in when (a x) & (b y) -> x");
        var type = resolveType(result);
        assertEquals(DOUBLE, type);
    }

    public void testWhenMultipleBindingsSecondType() {
        var result = elaborate("let a = spawn 1 in let b = spawn true in when (a x) & (b y) -> y");
        var type = resolveType(result);
        assertEquals(BOOLEAN, type);
    }

    public void testWhenProducesRecord() {
        var result = elaborate("let a = spawn 1 in let b = spawn true in when (a x) & (b y) -> { num: x, flag: y }");
        var type = resolveType(result);
        assertThat(type, instanceOf(MonoType.RecordType.class));
        var row = ((MonoType.RecordType) type).row();
        assertEquals(DOUBLE, row.fields().get("num"));
        assertEquals(BOOLEAN, row.fields().get("flag"));
    }

    public void testWhenChannelTypeMismatchFails() {
        var e = expectThrows(ElaborationException.class, () -> elaborate("when (42 x) -> x"));
        assertThat(e.getMessage(), containsString("Channel"));
    }

    // ──── spawn! / send (Block C) ────

    public void testSpawnBangProducesChannelType() {
        var result = elaborate("spawn!");
        assertThat(result, instanceOf(org.elasticsearch.xpack.piescript.core.CoreSpawn.class));
        var spawn = (org.elasticsearch.xpack.piescript.core.CoreSpawn) result;
        assertNull(spawn.body());
        var type = resolveType(result);
        assertThat(type, instanceOf(MonoType.AppType.class));
        var appType = (MonoType.AppType) type;
        assertEquals(new MonoType.TCon("Channel"), appType.constructor());
    }

    public void testSpawnBangTypeUnifiesWithSend() {
        var result = elaborate("let ch = spawn! in let u = send ch 42 in when (ch x) -> x");
        var type = resolveType(result);
        assertEquals(DOUBLE, type);
    }

    public void testSpawnBangTypeUnifiesWithSendBoolean() {
        var result = elaborate("let ch = spawn! in let u = send ch true in when (ch x) -> x");
        var type = resolveType(result);
        assertEquals(BOOLEAN, type);
    }

    public void testSendProducesNullType() {
        var result = elaborate("let ch = spawn! in send ch 42");
        var type = resolveType(result);
        assertEquals(new MonoType.TCon("Null"), type);
    }

    public void testSendRequiresChannelType() {
        var e = expectThrows(ElaborationException.class, () -> elaborate("send 42 99"));
        assertThat(e.getMessage(), containsString("Channel"));
    }

    public void testSendValueMustMatchChannelElementType() {
        var e = expectThrows(ElaborationException.class, () -> elaborate("let ch = spawn 42 in send ch true"));
        assertThat(e.getMessage(), containsString("mismatch"));
    }

    // ──── Value restriction (D-046) ────

    public void testValueRestrictionLambdaGeneralizes() {
        var result = elaborate("let id = fn x -> x in id 42");
        assertEquals(DOUBLE, resolveType(result));
    }

    public void testValueRestrictionLambdaUsedAtMultipleTypes() {
        var result = elaborate("let id = fn x -> x in let a = id 42 in id true");
        assertEquals(BOOLEAN, resolveType(result));
    }

    public void testValueRestrictionSpawnBangStaysMonomorphic() {
        var result = elaborate("let ch = spawn! in let u = send ch 42 in when (ch x) -> x");
        assertEquals(DOUBLE, resolveType(result));
    }

    public void testValueRestrictionApplicationNotGeneralized() {
        var result = elaborate("let x = (fn a -> a) 42 in x");
        assertEquals(DOUBLE, resolveType(result));
    }

    // ──── Query expression typing (Phase 2: T2.5/T2.6) ────

    private CoreExpr elaborateWithMappings(String source, Map<String, ResolvedMapping> mappings) {
        var parser = new PiescriptParser();
        var program = parser.parse(source);
        state.setResolvedMappings(mappings);
        var elaborator = new Elaborator(state);
        return elaborator.elaborateProgram(program);
    }

    private static EsField field(DataType type) {
        return new EsField("_", type, Map.of(), true, EsField.TimeSeriesFieldType.UNKNOWN);
    }

    public void testQueryExprProducesCoreQuery() {
        var mapping = new ResolvedMapping(
            "logs-*",
            Map.of("status", field(DataType.INTEGER), "message", field(DataType.KEYWORD)),
            Set.of()
        );
        var result = elaborateWithMappings("query `FROM logs-*`", Map.of("logs-*", mapping));
        assertThat(result, instanceOf(CoreQuery.class));
        var query = (CoreQuery) result;
        assertEquals("logs-*", query.indexPattern());
        assertEquals("FROM logs-*", query.esqlQuery());
    }

    public void testQueryExprTypeIsStreamRecord() {
        var mapping = new ResolvedMapping(
            "logs-*",
            Map.of("status", field(DataType.INTEGER), "message", field(DataType.KEYWORD)),
            Set.of()
        );
        var result = elaborateWithMappings("query `FROM logs-*`", Map.of("logs-*", mapping));
        var type = resolveType(result);
        assertThat(type, instanceOf(MonoType.AppType.class));
        var appType = (MonoType.AppType) type;
        assertEquals(new MonoType.TCon("List"), appType.constructor());
        assertThat(appType.argument(), instanceOf(MonoType.RecordType.class));
        var recordType = (MonoType.RecordType) appType.argument();
        assertEquals(DOUBLE, recordType.row().fields().get("status"));
        assertEquals(KEYWORD, recordType.row().fields().get("message"));
    }

    public void testQueryExprInLetBinding() {
        var mapping = new ResolvedMapping("logs-*", Map.of("status", field(DataType.LONG)), Set.of());
        var result = elaborateWithMappings("let docs = query `FROM logs-*`; docs", Map.of("logs-*", mapping));
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.rhs(), instanceOf(CoreQuery.class));
        var type = resolveType(let.body());
        assertThat(type, instanceOf(MonoType.AppType.class));
    }

    public void testQueryExprSkipsMetaFields() {
        var mapping = new ResolvedMapping(
            "logs-*",
            Map.of("status", field(DataType.INTEGER), "_id", field(DataType.KEYWORD), "_index", field(DataType.KEYWORD)),
            Set.of()
        );
        var result = elaborateWithMappings("query `FROM logs-*`", Map.of("logs-*", mapping));
        var appType = (MonoType.AppType) resolveType(result);
        var recordType = (MonoType.RecordType) appType.argument();
        assertEquals(1, recordType.row().fields().size());
        assertTrue(recordType.row().fields().containsKey("status"));
        assertFalse(recordType.row().fields().containsKey("_id"));
    }

    public void testQueryExprInvalidMappedFieldBecomesUnsupported() {
        var conflict = new InvalidMappedField("status", Map.of("integer", Set.of("index-1"), "keyword", Set.of("index-2")));
        var mapping = new ResolvedMapping("logs-*", Map.of("status", conflict, "message", field(DataType.KEYWORD)), Set.of());
        var result = elaborateWithMappings("query `FROM logs-*`", Map.of("logs-*", mapping));
        var appType = (MonoType.AppType) resolveType(result);
        var recordType = (MonoType.RecordType) appType.argument();
        assertEquals(new MonoType.TCon("Unsupported"), recordType.row().fields().get("status"));
        assertEquals(KEYWORD, recordType.row().fields().get("message"));
    }

    public void testQueryExprInvalidMappedFieldEmitsDiagnostic() {
        var conflict = new InvalidMappedField("status", Map.of("integer", Set.of("index-1"), "keyword", Set.of("index-2")));
        var mapping = new ResolvedMapping("logs-*", Map.of("status", conflict), Set.of());
        elaborateWithMappings("query `FROM logs-*`", Map.of("logs-*", mapping));
        assertThat(state.diagnostics().size(), is(1));
        assertThat(state.diagnostics().get(0), containsString("status"));
        assertThat(state.diagnostics().get(0), containsString("logs-*"));
    }

    public void testQueryExprWithNoMappingThrows() {
        var e = expectThrows(ElaborationException.class, () -> elaborateWithMappings("query `FROM nonexistent-*`", Map.of()));
        assertThat(e.getMessage(), containsString("no resolved mapping"));
        assertThat(e.getMessage(), containsString("nonexistent-*"));
    }

    public void testQueryExprWithEsqlPipes() {
        var mapping = new ResolvedMapping("logs-*", Map.of("status", field(DataType.INTEGER)), Set.of());
        var result = elaborateWithMappings("query `FROM logs-* | WHERE status >= 500 | LIMIT 10`", Map.of("logs-*", mapping));
        assertThat(result, instanceOf(CoreQuery.class));
        var query = (CoreQuery) result;
        assertEquals("FROM logs-* | WHERE status >= 500 | LIMIT 10", query.esqlQuery());
    }

    // ──── Block B builtins type inference ────

    public void testHeadTypeInference() {
        var result = elaborate("List.head");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }

    public void testLengthTypeInference() {
        var result = elaborate("List.length");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }

    public void testIsEmptyTypeInference() {
        var result = elaborate("List.isEmpty");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }

    public void testTopologyTypeIsArrow() {
        var result = elaborate("Cluster.topology");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }

    public void testRoutingTypeIsArrow() {
        var result = elaborate("Index.routing");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }

    public void testShardsTypeIsArrow() {
        var result = elaborate("Index.shards");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }

    public void testNodesTypeIsArrow() {
        var result = elaborate("Index.nodes");
        assertThat(resolveType(result), instanceOf(MonoType.Arrow.class));
    }
}
