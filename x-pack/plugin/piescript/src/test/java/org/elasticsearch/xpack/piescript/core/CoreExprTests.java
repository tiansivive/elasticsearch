/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public class CoreExprTests extends ESTestCase {

    private static final Source SRC = Source.EMPTY;
    private static final MonoType INT = new MonoType.TCon("Integer");
    private static final MonoType BOOL = new MonoType.TCon("Boolean");
    private static final MonoType KW = new MonoType.TCon("Keyword");

    // ──── CoreVar ────

    public void testVarConstruction() {
        var v = new CoreVar(SRC, 0, "x", INT);
        assertThat(v.index(), is(0));
        assertThat(v.debugName(), is("x"));
        assertThat(v.type(), is(INT));
        assertThat(v.children(), hasSize(0));
    }

    public void testVarNullDebugName() {
        var v = new CoreVar(SRC, 1, null, INT);
        assertNull(v.debugName());
    }

    public void testVarEquality() {
        var a = new CoreVar(SRC, 0, "x", INT);
        var b = new CoreVar(SRC, 0, "x", INT);
        var c = new CoreVar(SRC, 1, "y", INT);
        assertThat(a, is(b));
        assertNotEquals(a, c);
    }

    public void testVarReplaceChildrenThrows() {
        var v = new CoreVar(SRC, 0, "x", INT);
        expectThrows(UnsupportedOperationException.class, () -> v.replaceChildren(List.of()));
    }

    // ──── CoreLit ────

    public void testLitConstruction() {
        var lit = new CoreLit(SRC, new LitVal.IntegerLit(42), INT);
        assertThat(((LitVal.IntegerLit) lit.value()).value(), is(42));
        assertThat(lit.type(), is(INT));
        assertThat(lit.children(), hasSize(0));
    }

    public void testLitEquality() {
        var a = new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef("hi")), KW);
        var b = new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef("hi")), KW);
        var c = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        assertThat(a, is(b));
        assertNotEquals(a, c);
    }

    // ──── CoreLam ────

    public void testLamConstruction() {
        var body = new CoreVar(SRC, 0, "x", INT);
        var arrow = new MonoType.Arrow(INT, INT);
        var lam = new CoreLam(SRC, "x", INT, body, arrow);
        assertThat(lam.debugName(), is("x"));
        assertThat(lam.paramType(), is(INT));
        assertThat(lam.body(), sameInstance(body));
        assertThat(lam.type(), is(arrow));
        assertThat(lam.children(), hasSize(1));
    }

    public void testLamReplaceChildren() {
        var body = new CoreVar(SRC, 0, "x", INT);
        var arrow = new MonoType.Arrow(INT, INT);
        var lam = new CoreLam(SRC, "x", INT, body, arrow);
        var newBody = new CoreLit(SRC, new LitVal.IntegerLit(99), INT);
        var replaced = (CoreLam) lam.replaceChildren(List.of(newBody));
        assertThat(replaced.body(), sameInstance(newBody));
        assertThat(replaced.debugName(), is("x"));
        assertThat(replaced.paramType(), is(INT));
    }

    // ──── CoreApp ────

    public void testAppConstruction() {
        var fn = new CoreVar(SRC, 0, "f", new MonoType.Arrow(INT, BOOL));
        var arg = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        var app = new CoreApp(SRC, fn, arg, BOOL);
        assertThat(app.fn(), sameInstance(fn));
        assertThat(app.arg(), sameInstance(arg));
        assertThat(app.type(), is(BOOL));
        assertThat(app.children(), hasSize(2));
    }

    public void testAppReplaceChildren() {
        var fn = new CoreVar(SRC, 0, "f", new MonoType.Arrow(INT, BOOL));
        var arg = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        var app = new CoreApp(SRC, fn, arg, BOOL);
        var newArg = new CoreLit(SRC, new LitVal.IntegerLit(2), INT);
        var replaced = (CoreApp) app.replaceChildren(List.of(fn, newArg));
        assertThat(replaced.arg(), sameInstance(newArg));
        assertThat(replaced.fn(), sameInstance(fn));
    }

    // ──── CoreLet ────

    public void testLetConstruction() {
        var rhs = new CoreLit(SRC, new LitVal.IntegerLit(42), INT);
        var body = new CoreVar(SRC, 0, "x", INT);
        var let = new CoreLet(SRC, "x", INT, rhs, body, INT);
        assertThat(let.debugName(), is("x"));
        assertThat(let.bindType(), is(INT));
        assertThat(let.rhs(), sameInstance(rhs));
        assertThat(let.body(), sameInstance(body));
        assertThat(let.type(), is(INT));
        assertThat(let.children(), hasSize(2));
    }

    public void testLetReplaceChildren() {
        var rhs = new CoreLit(SRC, new LitVal.IntegerLit(42), INT);
        var body = new CoreVar(SRC, 0, "x", INT);
        var let = new CoreLet(SRC, "x", INT, rhs, body, INT);
        var newRhs = new CoreLit(SRC, new LitVal.IntegerLit(99), INT);
        var replaced = (CoreLet) let.replaceChildren(List.of(newRhs, body));
        assertThat(replaced.rhs(), sameInstance(newRhs));
        assertThat(replaced.body(), sameInstance(body));
    }

    // ──── CoreRecord ────

    public void testRecordConstruction() {
        var nameVal = new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef("alice")), KW);
        var ageVal = new CoreLit(SRC, new LitVal.IntegerLit(30), INT);
        var recType = new MonoType.RecordType(RowType.closed(Map.of("name", KW, "age", INT)));
        var rec = new CoreRecord(SRC, List.of("name", "age"), List.of(nameVal, ageVal), recType);
        assertThat(rec.labels(), is(List.of("name", "age")));
        assertThat(rec.children(), hasSize(2));
        assertThat(rec.type(), is(recType));
    }

    public void testRecordFields() {
        var nameVal = new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef("alice")), KW);
        var ageVal = new CoreLit(SRC, new LitVal.IntegerLit(30), INT);
        var recType = new MonoType.RecordType(RowType.closed(Map.of("name", KW, "age", INT)));
        var rec = new CoreRecord(SRC, List.of("name", "age"), List.of(nameVal, ageVal), recType);
        var fields = rec.fields();
        assertThat(fields, hasSize(2));
        assertThat(fields.get(0).label(), is("name"));
        assertThat(fields.get(0).value(), sameInstance(nameVal));
        assertThat(fields.get(1).label(), is("age"));
        assertThat(fields.get(1).value(), sameInstance(ageVal));
    }

    public void testRecordCreateFactory() {
        var nameVal = new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef("bob")), KW);
        var recType = new MonoType.RecordType(RowType.closed(Map.of("name", KW)));
        var rec = CoreRecord.create(SRC, List.of(new CoreField("name", nameVal)), recType);
        assertThat(rec.labels(), is(List.of("name")));
        assertThat(rec.children().get(0), sameInstance(nameVal));
    }

    public void testRecordReplaceChildren() {
        var v1 = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        var v2 = new CoreLit(SRC, new LitVal.IntegerLit(2), INT);
        var recType = new MonoType.RecordType(RowType.closed(Map.of("a", INT)));
        var rec = new CoreRecord(SRC, List.of("a"), List.of(v1), recType);
        var replaced = (CoreRecord) rec.replaceChildren(List.of(v2));
        assertThat(replaced.children().get(0), sameInstance(v2));
        assertThat(replaced.labels(), is(List.of("a")));
    }

    // ──── CoreProject ────

    public void testProjectConstruction() {
        var recType = new MonoType.RecordType(RowType.closed(Map.of("x", INT)));
        var rec = new CoreRecord(SRC, List.of("x"), List.of(new CoreLit(SRC, new LitVal.IntegerLit(1), INT)), recType);
        var proj = new CoreProject(SRC, rec, "x", INT);
        assertThat(proj.expr(), sameInstance(rec));
        assertThat(proj.label(), is("x"));
        assertThat(proj.type(), is(INT));
        assertThat(proj.children(), hasSize(1));
    }

    // ──── CoreUpdate ────

    public void testUpdateConstruction() {
        var base = new CoreVar(SRC, 0, "r", new MonoType.RecordType(RowType.closed(Map.of("x", INT))));
        var newVal = new CoreLit(SRC, new LitVal.IntegerLit(99), INT);
        var update = CoreUpdate.create(SRC, base, List.of(new CoreField("x", newVal)), INT);
        assertThat(update.expr(), sameInstance(base));
        assertThat(update.labels(), is(List.of("x")));
        assertThat(update.children(), hasSize(2));
        var fields = update.updates();
        assertThat(fields, hasSize(1));
        assertThat(fields.get(0).label(), is("x"));
        assertThat(fields.get(0).value(), sameInstance(newVal));
    }

    // ──── CorePrimOp ────

    public void testBinaryPrimOp() {
        var left = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        var right = new CoreLit(SRC, new LitVal.IntegerLit(2), INT);
        var add = new CorePrimOp(SRC, Op.ADD, List.of(left, right), INT);
        assertThat(add.op(), is(Op.ADD));
        assertThat(add.args(), hasSize(2));
        assertThat(add.type(), is(INT));
    }

    public void testUnaryPrimOp() {
        var operand = new CoreLit(SRC, new LitVal.BooleanLit(true), BOOL);
        var not = new CorePrimOp(SRC, Op.NOT, List.of(operand), BOOL);
        assertThat(not.op(), is(Op.NOT));
        assertThat(not.args(), hasSize(1));
    }

    public void testPrimOpReplaceChildren() {
        var a = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        var b = new CoreLit(SRC, new LitVal.IntegerLit(2), INT);
        var add = new CorePrimOp(SRC, Op.ADD, List.of(a, b), INT);
        var c = new CoreLit(SRC, new LitVal.IntegerLit(3), INT);
        var replaced = (CorePrimOp) add.replaceChildren(List.of(a, c));
        assertThat(replaced.args().get(1), sameInstance(c));
        assertThat(replaced.op(), is(Op.ADD));
    }

    // ──── NamedWriteable (not supported) ────

    public void testWriteableThrows() {
        var v = new CoreVar(SRC, 0, "x", INT);
        expectThrows(UnsupportedOperationException.class, v::getWriteableName);
        expectThrows(UnsupportedOperationException.class, () -> v.writeTo(null));
    }

    // ──── Tree traversal ────

    public void testTransformDown() {
        var body = new CoreLit(SRC, new LitVal.IntegerLit(42), INT);
        var arrow = new MonoType.Arrow(INT, INT);
        var lam = new CoreLam(SRC, "x", INT, body, arrow);
        var app = new CoreApp(SRC, lam, new CoreLit(SRC, new LitVal.IntegerLit(1), INT), INT);

        var literals = app.collect(CoreLit.class);
        assertThat(literals, hasSize(2));
    }

    // ──── CoreField ────

    public void testCoreField() {
        var val = new CoreLit(SRC, new LitVal.IntegerLit(1), INT);
        var field = new CoreField("x", val);
        assertThat(field.label(), is("x"));
        assertThat(field.value(), sameInstance(val));
    }

    // ──── CoreLoop / CoreRepeat ────

    public void testLoopConstruction() {
        var init = new CoreLit(SRC, new LitVal.IntegerLit(0), INT);
        var body1 = new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef("done")), KW);
        var repeatType = new MonoType.AppType(new MonoType.TCon("Repeat"), INT);
        var body2 = new CoreRepeat(SRC, new CoreLit(SRC, new LitVal.IntegerLit(1), INT), repeatType);
        var arm1 = new Alternative(new Pattern.LitPat(new LitVal.IntegerLit(10)), body1);
        var arm2 = new Alternative(new Pattern.WildcardPat(), body2);

        var loop = new CoreLoop(SRC, init, List.of(arm1, arm2), KW);
        assertThat(loop.init(), sameInstance(init));
        assertThat(loop.arms(), hasSize(2));
        assertThat(loop.type(), is(KW));
        assertThat(loop.children(), hasSize(3));
    }

    public void testRepeatConstruction() {
        var expr = new CoreVar(SRC, 0, "n", INT);
        var repeatType = new MonoType.AppType(new MonoType.TCon("Repeat"), INT);
        var repeat = new CoreRepeat(SRC, expr, repeatType);
        assertThat(repeat.expr(), sameInstance(expr));
        assertThat(repeat.type(), is(repeatType));
        assertThat(repeat.children(), hasSize(1));
    }
}
