/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.test.ESTestCase;

import java.util.Map;

import static org.hamcrest.Matchers.is;

public class TypeDataStructureTests extends ESTestCase {

    // ──── Kind constants ────

    public void testKindConstants() {
        assertThat(Types.TYPE, is(new MonoType.TCon("Type")));
        assertThat(Types.ROW, is(new MonoType.TCon("Row")));
        assertNotEquals(Types.TYPE, Types.ROW);
    }

    // ──── MonoType ────

    public void testTCon() {
        var integer = new MonoType.TCon("Integer");
        assertThat(integer.name(), is("Integer"));
    }

    public void testTConEquality() {
        assertThat(new MonoType.TCon("Integer"), is(new MonoType.TCon("Integer")));
        assertNotEquals(new MonoType.TCon("Integer"), new MonoType.TCon("Long"));
    }

    public void testArrow() {
        var param = new MonoType.TCon("Integer");
        var result = new MonoType.TCon("Boolean");
        var arrow = new MonoType.Arrow(param, result);
        assertThat(arrow.param(), is(param));
        assertThat(arrow.result(), is(result));
    }

    public void testNestedArrow() {
        var a = new MonoType.TCon("Integer");
        var b = new MonoType.TCon("Boolean");
        var c = new MonoType.TCon("Keyword");
        var inner = new MonoType.Arrow(b, c);
        var outer = new MonoType.Arrow(a, inner);
        assertThat(outer.param(), is(a));
        assertThat(outer.result(), is(inner));
        assertThat(((MonoType.Arrow) outer.result()).param(), is(b));
    }

    public void testRecordType() {
        var row = RowType.closed(Map.of("name", new MonoType.TCon("Keyword"), "age", new MonoType.TCon("Integer")));
        var record = new MonoType.RecordType(row);
        assertThat(record.row(), is(row));
        assertThat(((RowType) record.row()).fields().size(), is(2));
    }

    public void testAppType() {
        var list = new MonoType.TCon("List");
        var record = new MonoType.TCon("Record");
        var app = new MonoType.AppType(list, record);
        assertThat(app.constructor(), is(list));
        assertThat(app.argument(), is(record));
    }

    public void testMeta() {
        var meta = new MonoType.Meta(0, 1, Types.TYPE);
        assertThat(meta.id(), is(0));
        assertThat(meta.bindingLevel(), is(1));
        assertThat(meta.kind(), is(Types.TYPE));
    }

    public void testRowMeta() {
        var meta = new MonoType.Meta(5, 2, Types.ROW);
        assertThat(meta.kind(), is(Types.ROW));
    }

    public void testMetaEquality() {
        var a = new MonoType.Meta(0, 1, Types.TYPE);
        var b = new MonoType.Meta(0, 1, Types.TYPE);
        var c = new MonoType.Meta(1, 1, Types.TYPE);
        assertThat(a, is(b));
        assertNotEquals(a, c);
    }

    // ──── RowType ────

    public void testClosedRow() {
        var row = RowType.closed(Map.of("x", new MonoType.TCon("Integer")));
        assertThat(row.fields().size(), is(1));
        assertThat(row.fields().get("x"), is(new MonoType.TCon("Integer")));
        assertTrue(row.tail().isEmpty());
    }

    public void testOpenRow() {
        var rowVar = new MonoType.Meta(0, 0, Types.ROW);
        var row = RowType.open(Map.of("x", new MonoType.TCon("Integer")), rowVar);
        assertThat(row.fields().size(), is(1));
        assertTrue(row.tail().isPresent());
        assertThat(row.tail().get(), is(rowVar));
    }

    public void testEmptyClosedRow() {
        var row = RowType.closed(Map.of());
        assertTrue(row.fields().isEmpty());
        assertTrue(row.tail().isEmpty());
    }

    // ──── TypeScheme ────

    public void testMonoScheme() {
        var type = new MonoType.TCon("Integer");
        var scheme = TypeScheme.mono(type);
        assertTrue(scheme.quantified().isEmpty());
        assertThat(scheme.body(), is(type));
    }

    public void testPolyScheme() {
        var meta = new MonoType.Meta(0, 0, Types.TYPE);
        var arrow = new MonoType.Arrow(meta, meta);
        var scheme = new TypeScheme(Map.of(0, Types.TYPE), arrow);
        assertThat(scheme.quantified().size(), is(1));
        assertTrue(scheme.quantified().containsKey(0));
        assertThat(scheme.body(), is(arrow));
    }

    // ──── LitVal ────

    public void testIntegerLit() {
        var lit = new LitVal.IntegerLit(42);
        assertThat(lit.value(), is(42));
    }

    public void testLongLit() {
        var lit = new LitVal.LongLit(9_999_999_999L);
        assertThat(lit.value(), is(9_999_999_999L));
    }

    public void testDoubleLit() {
        var lit = new LitVal.DoubleLit(3.14);
        assertThat(lit.value(), is(3.14));
    }

    public void testKeywordLit() {
        var lit = new LitVal.KeywordLit(new BytesRef("hello"));
        assertThat(lit.value(), is(new BytesRef("hello")));
    }

    public void testBooleanLit() {
        var t = new LitVal.BooleanLit(true);
        var f = new LitVal.BooleanLit(false);
        assertTrue(t.value());
        assertFalse(f.value());
    }

    public void testNullLit() {
        var lit = new LitVal.NullLit();
        assertThat(lit, is(new LitVal.NullLit()));
    }

    public void testLitValSealedHierarchy() {
        LitVal[] lits = {
            new LitVal.IntegerLit(1),
            new LitVal.LongLit(1L),
            new LitVal.DoubleLit(1.0),
            new LitVal.KeywordLit(new BytesRef("a")),
            new LitVal.BooleanLit(true),
            new LitVal.NullLit() };
        for (LitVal lit : lits) {
            assertNotNull(lit);
        }
    }

    // ──── Op ────

    public void testOpValues() {
        assertThat(Op.values().length, is(15));
    }

    public void testArithmeticOps() {
        assertThat(Op.valueOf("ADD"), is(Op.ADD));
        assertThat(Op.valueOf("SUB"), is(Op.SUB));
        assertThat(Op.valueOf("MUL"), is(Op.MUL));
        assertThat(Op.valueOf("DIV"), is(Op.DIV));
        assertThat(Op.valueOf("MOD"), is(Op.MOD));
    }

    public void testComparisonOps() {
        assertThat(Op.valueOf("EQ"), is(Op.EQ));
        assertThat(Op.valueOf("NEQ"), is(Op.NEQ));
        assertThat(Op.valueOf("LT"), is(Op.LT));
        assertThat(Op.valueOf("GT"), is(Op.GT));
        assertThat(Op.valueOf("LTE"), is(Op.LTE));
        assertThat(Op.valueOf("GTE"), is(Op.GTE));
    }

    public void testBooleanOps() {
        assertThat(Op.valueOf("AND"), is(Op.AND));
        assertThat(Op.valueOf("OR"), is(Op.OR));
        assertThat(Op.valueOf("NOT"), is(Op.NOT));
    }

    public void testUnaryOps() {
        assertThat(Op.valueOf("NEG"), is(Op.NEG));
        assertThat(Op.valueOf("NOT"), is(Op.NOT));
    }

    // ──── MonoType sealed interface exhaustiveness ────

    public void testMonoTypeSealedHierarchy() {
        MonoType[] types = {
            new MonoType.TCon("Integer"),
            new MonoType.Arrow(new MonoType.TCon("Integer"), new MonoType.TCon("Boolean")),
            new MonoType.RecordType(RowType.closed(Map.of())),
            new MonoType.AppType(new MonoType.TCon("List"), new MonoType.TCon("Record")),
            new MonoType.Meta(0, 0, Types.TYPE) };
        for (MonoType type : types) {
            assertNotNull(type);
        }
    }
}
