/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.Map;

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
        assertThat(((CoreLit) result).value(), is(new LitVal.IntegerLit(42)));
        assertThat(result.type(), is(INTEGER));
    }

    public void testNegativeIntegerLiteral() {
        var result = elaborate("-7");
        assertThat(result, instanceOf(CorePrimOp.class));
        var neg = (CorePrimOp) result;
        assertThat(neg.op(), is(Op.NEG));
        assertThat(resolveType(neg), is(INTEGER));
    }

    public void testLargeIntegerBecomesLong() {
        var result = elaborate("3000000000");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(((CoreLit) result).value(), is(new LitVal.LongLit(3_000_000_000L)));
        assertThat(result.type(), is(LONG));
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
        assertThat(resolveType(let), is(INTEGER));
    }

    public void testLetWithTypeAnnotation() {
        var result = elaborate("let x : Integer = 1 in x");
        assertThat(result, instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(INTEGER));
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
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testMultipleTopBindings() {
        var result = elaborate("let x = 1; let y = 2; x");
        assertThat(result, instanceOf(CoreLet.class));
        var outer = (CoreLet) result;
        assertThat(outer.body(), instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(INTEGER));
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
        var result = elaborate("fn (x : Integer) -> x");
        assertThat(result, instanceOf(CoreLam.class));
        var lam = (CoreLam) result;
        assertThat(lam.paramType(), is(INTEGER));
        assertThat(resolveType(result), is(new MonoType.Arrow(INTEGER, INTEGER)));
    }

    public void testMultiParamLambda() {
        var result = elaborate("fn x y -> x");
        assertThat(result, instanceOf(CoreLam.class));
        var outer = (CoreLam) result;
        assertThat(outer.body(), instanceOf(CoreLam.class));
    }

    // ──── Function application ────

    public void testApplication() {
        var result = elaborate("let f = fn (x : Integer) -> x in f 42");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.body(), instanceOf(CoreApp.class));
        assertThat(resolveType(let), is(INTEGER));
    }

    public void testApplicationInfersParamType() {
        var result = elaborate("let f = fn x -> x in f 42");
        assertThat(resolveType(result), is(INTEGER));
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
        assertThat(resolveType(op), is(INTEGER));
    }

    public void testSubtraction() {
        var result = elaborate("3 - 1");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.SUB));
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testMultiplication() {
        var result = elaborate("2 * 3");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.MUL));
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testDivision() {
        var result = elaborate("10 / 2");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.DIV));
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testModulo() {
        var result = elaborate("10 % 3");
        assertThat(result, instanceOf(CorePrimOp.class));
        assertThat(((CorePrimOp) result).op(), is(Op.MOD));
        assertThat(resolveType(result), is(INTEGER));
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
        assertThat(resolveType(proj), is(INTEGER));
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
        var result = elaborate("let f = fn (x : Integer) -> x in 42 |> f");
        assertThat(result, instanceOf(CoreLet.class));
        var let = (CoreLet) result;
        assertThat(let.body(), instanceOf(CoreApp.class));
        assertThat(resolveType(let), is(INTEGER));
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
        assertThat(resolveType(result), is(INTEGER));
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
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testBlockWithExprStmt() {
        var result = elaborate("{ 1; 2 }");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testBlockMultipleStmts() {
        var result = elaborate("{ let x = 1; let y = 2; x + y }");
        assertThat(result, instanceOf(CoreLet.class));
        assertThat(resolveType(result), is(INTEGER));
    }

    // ──── Parentheses and ascription ────

    public void testParenExpr() {
        var result = elaborate("(42)");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(resolveType(result), is(INTEGER));
    }

    public void testTypeAscription() {
        var result = elaborate("(42 : Integer)");
        assertThat(result, instanceOf(CoreLit.class));
        assertThat(resolveType(result), is(INTEGER));
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
        assertThat(resolveType(result), is(INTEGER));
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
        assertThat(ex.getMessage(), containsString("not a function"));
    }

    public void testDuplicateRecordField() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("{ x: 1, x: 2 }"));
        assertThat(ex.getMessage(), containsString("duplicate field"));
    }

    public void testProjectionOnNonRecord() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("42.x"));
        assertThat(ex.getMessage(), containsString("projection requires a record type"));
    }

    public void testProjectionMissingField() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("{ x: 1 }.y"));
        assertThat(ex.getMessage(), containsString("no field"));
    }

    public void testWrongAnnotationType() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("(true : Integer)"));
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
        assertThat(ex.getMessage(), containsString("record update requires a record type"));
    }

    public void testUnknownTypeAnnotation() {
        var ex = expectThrows(ElaborationException.class, () -> elaborate("(42 : Foo)"));
        assertThat(ex.getMessage(), containsString("unknown type"));
    }
}
