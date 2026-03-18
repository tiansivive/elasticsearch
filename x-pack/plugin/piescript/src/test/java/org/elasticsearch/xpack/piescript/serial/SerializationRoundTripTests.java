/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.serial;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.esql.core.tree.Location;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreExprSerialization;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreQuery;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreSend;
import org.elasticsearch.xpack.piescript.core.CoreSpawn;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreTypeApp;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.core.CoreWhen;
import org.elasticsearch.xpack.piescript.eval.Value;
import org.elasticsearch.xpack.piescript.eval.ValueSerialization;
import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeSerialization;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Round-trip serialization tests for all piescript wire types:
 * {@link MonoType}, {@link RowType}, {@link LitVal}, {@link Op},
 * {@link CoreExpr} (16 variants), and {@link Value} (11 variants).
 */
public class SerializationRoundTripTests extends ESTestCase {

    private static final Source SRC = new Source(new Location(1, 0), "test");
    private static final MonoType INTEGER = new MonoType.TCon("Integer");
    private static final MonoType BOOLEAN = new MonoType.TCon("Boolean");
    private static final MonoType KEYWORD = new MonoType.TCon("Keyword");

    // ──── MonoType ────

    public void testMonoTypeTCon() throws IOException {
        assertMonoTypeRoundTrip(INTEGER);
    }

    public void testMonoTypeArrow() throws IOException {
        assertMonoTypeRoundTrip(new MonoType.Arrow(INTEGER, BOOLEAN));
    }

    public void testMonoTypeRecordClosed() throws IOException {
        var row = RowType.closed(Map.of("x", INTEGER, "y", BOOLEAN));
        assertMonoTypeRoundTrip(new MonoType.RecordType(row));
    }

    public void testMonoTypeRecordOpen() throws IOException {
        var row = RowType.open(Map.of("x", INTEGER), new MonoType.Meta(42, 1, Kind.ROW));
        assertMonoTypeRoundTrip(new MonoType.RecordType(row));
    }

    public void testMonoTypeApp() throws IOException {
        assertMonoTypeRoundTrip(new MonoType.AppType(new MonoType.TCon("List"), INTEGER));
    }

    public void testMonoTypeMeta() throws IOException {
        assertMonoTypeRoundTrip(new MonoType.Meta(7, 2, Kind.TYPE));
    }

    public void testMonoTypeRigid() throws IOException {
        assertMonoTypeRoundTrip(new MonoType.Rigid(3, Kind.TYPE));
    }

    public void testMonoTypeNestedArrow() throws IOException {
        var type = new MonoType.Arrow(new MonoType.Arrow(INTEGER, BOOLEAN), new MonoType.Arrow(KEYWORD, INTEGER));
        assertMonoTypeRoundTrip(type);
    }

    // ──── LitVal ────

    public void testLitValInteger() throws IOException {
        assertLitValRoundTrip(new LitVal.IntegerLit(42));
    }

    public void testLitValLong() throws IOException {
        assertLitValRoundTrip(new LitVal.LongLit(9999999999L));
    }

    public void testLitValDouble() throws IOException {
        assertLitValRoundTrip(new LitVal.DoubleLit(3.14));
    }

    public void testLitValKeyword() throws IOException {
        assertLitValRoundTrip(new LitVal.KeywordLit(new BytesRef("hello")));
    }

    public void testLitValBoolean() throws IOException {
        assertLitValRoundTrip(new LitVal.BooleanLit(true));
    }

    public void testLitValNull() throws IOException {
        assertLitValRoundTrip(new LitVal.NullLit());
    }

    // ──── Op ────

    public void testOpRoundTrip() throws IOException {
        for (Op op : Op.values()) {
            var out = new BytesStreamOutput();
            TypeSerialization.writeOp(out, op);
            assertEquals(op, TypeSerialization.readOp(out.bytes().streamInput()));
        }
    }

    // ──── CoreExpr ────

    public void testCoreVar() throws IOException {
        assertCoreExprRoundTrip(new CoreVar(SRC, 2, "x", INTEGER));
    }

    public void testCoreVarNullDebugName() throws IOException {
        assertCoreExprRoundTrip(new CoreVar(SRC, 0, null, INTEGER));
    }

    public void testCoreFree() throws IOException {
        assertCoreExprRoundTrip(new CoreFree(SRC, "map", new MonoType.Arrow(INTEGER, INTEGER)));
    }

    public void testCoreLit() throws IOException {
        assertCoreExprRoundTrip(new CoreLit(SRC, new LitVal.IntegerLit(42), INTEGER));
    }

    public void testCoreLam() throws IOException {
        var body = new CoreVar(SRC, 0, "x", INTEGER);
        assertCoreExprRoundTrip(new CoreLam(SRC, "x", INTEGER, body, new MonoType.Arrow(INTEGER, INTEGER)));
    }

    public void testCoreApp() throws IOException {
        var fn = new CoreVar(SRC, 0, "f", new MonoType.Arrow(INTEGER, BOOLEAN));
        var arg = new CoreLit(SRC, new LitVal.IntegerLit(42), INTEGER);
        assertCoreExprRoundTrip(new CoreApp(SRC, fn, arg, BOOLEAN));
    }

    public void testCoreLet() throws IOException {
        var rhs = new CoreLit(SRC, new LitVal.IntegerLit(42), INTEGER);
        var body = new CoreVar(SRC, 0, "x", INTEGER);
        assertCoreExprRoundTrip(new CoreLet(SRC, "x", INTEGER, rhs, body, INTEGER));
    }

    public void testCoreRecord() throws IOException {
        var v1 = new CoreLit(SRC, new LitVal.IntegerLit(1), INTEGER);
        var v2 = new CoreLit(SRC, new LitVal.BooleanLit(true), BOOLEAN);
        assertCoreExprRoundTrip(
            new CoreRecord(
                SRC,
                List.of("a", "b"),
                List.of(v1, v2),
                new MonoType.RecordType(RowType.closed(Map.of("a", INTEGER, "b", BOOLEAN)))
            )
        );
    }

    public void testCoreProject() throws IOException {
        var rec = new CoreVar(SRC, 0, "r", new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER))));
        assertCoreExprRoundTrip(new CoreProject(SRC, rec, "x", INTEGER));
    }

    public void testCoreUpdate() throws IOException {
        var rec = new CoreVar(SRC, 0, "r", new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER))));
        var newVal = new CoreLit(SRC, new LitVal.IntegerLit(99), INTEGER);
        assertCoreExprRoundTrip(new CoreUpdate(SRC, List.of("x"), List.of(rec, newVal), rec.type()));
    }

    public void testCorePrimOp() throws IOException {
        var a = new CoreLit(SRC, new LitVal.IntegerLit(1), INTEGER);
        var b = new CoreLit(SRC, new LitVal.IntegerLit(2), INTEGER);
        assertCoreExprRoundTrip(new CorePrimOp(SRC, Op.ADD, List.of(a, b), INTEGER));
    }

    public void testCoreTypeAbs() throws IOException {
        var body = new CoreVar(SRC, 0, "x", new MonoType.Rigid(1, Kind.TYPE));
        assertCoreExprRoundTrip(new CoreTypeAbs(SRC, 1, Kind.TYPE, body, body.type()));
    }

    public void testCoreTypeApp() throws IOException {
        var polyExpr = new CoreVar(SRC, 0, "id", new MonoType.Arrow(new MonoType.Rigid(1, Kind.TYPE), new MonoType.Rigid(1, Kind.TYPE)));
        assertCoreExprRoundTrip(new CoreTypeApp(SRC, polyExpr, INTEGER, new MonoType.Arrow(INTEGER, INTEGER)));
    }

    public void testCoreQuery() throws IOException {
        var type = new MonoType.AppType(new MonoType.TCon("List"), new MonoType.RecordType(RowType.closed(Map.of("status", INTEGER))));
        assertCoreExprRoundTrip(new CoreQuery(SRC, "FROM logs-*", "logs-*", type));
    }

    public void testCoreSpawnWithBody() throws IOException {
        var body = new CoreLit(SRC, new LitVal.IntegerLit(42), INTEGER);
        assertCoreExprRoundTrip(new CoreSpawn(SRC, body, new MonoType.AppType(new MonoType.TCon("Channel"), INTEGER)));
    }

    public void testCoreSpawnBare() throws IOException {
        assertCoreExprRoundTrip(new CoreSpawn(SRC, null, new MonoType.AppType(new MonoType.TCon("Channel"), INTEGER)));
    }

    public void testCoreWhen() throws IOException {
        var ch = new CoreVar(SRC, 0, "ch", new MonoType.AppType(new MonoType.TCon("Channel"), INTEGER));
        var body = new CoreVar(SRC, 1, "x", INTEGER);
        var bindings = List.of(new CoreWhen.WhenBinding(ch, "x"));
        assertCoreExprRoundTrip(new CoreWhen(SRC, bindings, body, INTEGER));
    }

    public void testCoreWhenMultipleBindings() throws IOException {
        var ch1 = new CoreVar(SRC, 0, "a", new MonoType.AppType(new MonoType.TCon("Channel"), INTEGER));
        var ch2 = new CoreVar(SRC, 1, "b", new MonoType.AppType(new MonoType.TCon("Channel"), BOOLEAN));
        var body = new CoreLit(SRC, new LitVal.IntegerLit(1), INTEGER);
        var bindings = List.of(new CoreWhen.WhenBinding(ch1, "x"), new CoreWhen.WhenBinding(ch2, "y"));
        assertCoreExprRoundTrip(new CoreWhen(SRC, bindings, body, INTEGER));
    }

    public void testCoreSend() throws IOException {
        var ch = new CoreVar(SRC, 0, "ch", new MonoType.AppType(new MonoType.TCon("Channel"), INTEGER));
        var val = new CoreLit(SRC, new LitVal.IntegerLit(42), INTEGER);
        assertCoreExprRoundTrip(new CoreSend(SRC, ch, val, new MonoType.TCon("Null")));
    }

    public void testCoreExprNestedLet() throws IOException {
        var lit = new CoreLit(SRC, new LitVal.IntegerLit(1), INTEGER);
        var inner = new CoreLet(SRC, "y", INTEGER, lit, new CoreVar(SRC, 0, "y", INTEGER), INTEGER);
        var outer = new CoreLet(SRC, "x", INTEGER, inner, new CoreVar(SRC, 0, "x", INTEGER), INTEGER);
        assertCoreExprRoundTrip(outer);
    }

    // ──── Value ────

    public void testValueInteger() throws IOException {
        assertValueRoundTrip(new Value.IntegerVal(42));
    }

    public void testValueLong() throws IOException {
        assertValueRoundTrip(new Value.LongVal(9999999999L));
    }

    public void testValueDouble() throws IOException {
        assertValueRoundTrip(new Value.DoubleVal(3.14));
    }

    public void testValueKeyword() throws IOException {
        assertValueRoundTrip(new Value.KeywordVal("hello"));
    }

    public void testValueBoolean() throws IOException {
        assertValueRoundTrip(new Value.BooleanVal(true));
    }

    public void testValueNull() throws IOException {
        assertValueRoundTrip(new Value.NullVal());
    }

    public void testValueRecord() throws IOException {
        assertValueRoundTrip(new Value.RecordVal(Map.of("x", new Value.IntegerVal(1), "y", new Value.BooleanVal(true))));
    }

    public void testValueNestedRecord() throws IOException {
        var inner = new Value.RecordVal(Map.of("a", new Value.IntegerVal(1)));
        assertValueRoundTrip(new Value.RecordVal(Map.of("nested", inner, "flat", new Value.KeywordVal("hello"))));
    }

    public void testValueList() throws IOException {
        assertValueRoundTrip(new Value.ListVal(List.of(new Value.IntegerVal(1), new Value.IntegerVal(2), new Value.IntegerVal(3))));
    }

    public void testValueEmptyList() throws IOException {
        assertValueRoundTrip(new Value.ListVal(List.of()));
    }

    public void testValueListOfRecords() throws IOException {
        var r1 = new Value.RecordVal(Map.of("x", new Value.IntegerVal(1)));
        var r2 = new Value.RecordVal(Map.of("x", new Value.IntegerVal(2)));
        assertValueRoundTrip(new Value.ListVal(List.of(r1, r2)));
    }

    public void testValueChannel() throws IOException {
        assertValueRoundTrip(new Value.ChannelVal("node-1", "ch-42"));
    }

    public void testValueBuiltin() throws IOException {
        assertValueRoundTrip(new Value.BuiltinVal("map", 2, List.of()));
    }

    public void testValueBuiltinPartiallyApplied() throws IOException {
        assertValueRoundTrip(new Value.BuiltinVal("map", 2, List.of(new Value.IntegerVal(1))));
    }

    public void testValueClosure() throws IOException {
        var body = new CoreVar(SRC, 0, "x", INTEGER);
        var env = new Value[] { new Value.IntegerVal(42), new Value.BooleanVal(true) };
        assertValueRoundTrip(new Value.ClosureVal(body, env));
    }

    public void testValueClosureEmptyEnv() throws IOException {
        var body = new CoreLit(SRC, new LitVal.IntegerLit(1), INTEGER);
        assertValueRoundTrip(new Value.ClosureVal(body, new Value[0]));
    }

    public void testValueClosureNestedInRecord() throws IOException {
        var body = new CoreVar(SRC, 0, "x", INTEGER);
        var closure = new Value.ClosureVal(body, new Value[] { new Value.IntegerVal(10) });
        assertValueRoundTrip(new Value.RecordVal(Map.of("fn", closure, "name", new Value.KeywordVal("test"))));
    }

    public void testValueClosureCapturingClosure() throws IOException {
        var innerBody = new CoreVar(SRC, 0, "y", INTEGER);
        var innerClosure = new Value.ClosureVal(innerBody, new Value[] { new Value.IntegerVal(1) });
        var outerBody = new CoreApp(
            SRC,
            new CoreVar(SRC, 0, "f", new MonoType.Arrow(INTEGER, INTEGER)),
            new CoreVar(SRC, 1, "x", INTEGER),
            INTEGER
        );
        assertValueRoundTrip(new Value.ClosureVal(outerBody, new Value[] { innerClosure, new Value.IntegerVal(2) }));
    }

    public void testValueChannelInClosureEnv() throws IOException {
        var body = new CoreVar(SRC, 0, "ch", new MonoType.AppType(new MonoType.TCon("Channel"), INTEGER));
        var env = new Value[] { new Value.ChannelVal("node-2", "ch-99") };
        assertValueRoundTrip(new Value.ClosureVal(body, env));
    }

    // ──── Helpers ────

    private void assertMonoTypeRoundTrip(MonoType type) throws IOException {
        var out = new BytesStreamOutput();
        TypeSerialization.writeMonoType(out, type);
        StreamInput in = out.bytes().streamInput();
        assertEquals(type, TypeSerialization.readMonoType(in));
    }

    private void assertLitValRoundTrip(LitVal lit) throws IOException {
        var out = new BytesStreamOutput();
        TypeSerialization.writeLitVal(out, lit);
        StreamInput in = out.bytes().streamInput();
        assertEquals(lit, TypeSerialization.readLitVal(in));
    }

    private void assertCoreExprRoundTrip(CoreExpr expr) throws IOException {
        var out = new BytesStreamOutput();
        CoreExprSerialization.writeCoreExpr(out, expr);
        StreamInput in = out.bytes().streamInput();
        CoreExpr result = CoreExprSerialization.readCoreExpr(in);
        assertCoreExprEquals(expr, result);
    }

    private void assertValueRoundTrip(Value value) throws IOException {
        var out = new BytesStreamOutput();
        ValueSerialization.writeValue(out, value);
        StreamInput in = out.bytes().streamInput();
        Value result = ValueSerialization.readValue(in);
        assertValueEquals(value, result);
    }

    /**
     * Deep structural comparison of CoreExpr trees, ignoring Source (which is
     * not serialized — deserialized nodes get a synthetic source).
     */
    private void assertCoreExprEquals(CoreExpr expected, CoreExpr actual) {
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.type(), actual.type());

        switch (expected) {
            case CoreVar e -> {
                var a = (CoreVar) actual;
                assertEquals(e.index(), a.index());
                assertEquals(e.debugName(), a.debugName());
            }
            case CoreFree e -> assertEquals(e.name(), ((CoreFree) actual).name());
            case CoreLit e -> assertEquals(e.value(), ((CoreLit) actual).value());
            case CoreLam e -> {
                var a = (CoreLam) actual;
                assertEquals(e.debugName(), a.debugName());
                assertEquals(e.paramType(), a.paramType());
                assertCoreExprEquals(e.body(), a.body());
            }
            case CoreApp e -> {
                var a = (CoreApp) actual;
                assertCoreExprEquals(e.fn(), a.fn());
                assertCoreExprEquals(e.arg(), a.arg());
            }
            case CoreLet e -> {
                var a = (CoreLet) actual;
                assertEquals(e.debugName(), a.debugName());
                assertEquals(e.bindType(), a.bindType());
                assertCoreExprEquals(e.rhs(), a.rhs());
                assertCoreExprEquals(e.body(), a.body());
            }
            case CoreRecord e -> {
                var a = (CoreRecord) actual;
                assertEquals(e.labels(), a.labels());
                assertEquals(e.children().size(), a.children().size());
                for (int i = 0; i < e.children().size(); i++) {
                    assertCoreExprEquals(e.children().get(i), a.children().get(i));
                }
            }
            case CoreProject e -> {
                var a = (CoreProject) actual;
                assertEquals(e.label(), a.label());
                assertCoreExprEquals(e.expr(), a.expr());
            }
            case CoreUpdate e -> {
                var a = (CoreUpdate) actual;
                assertEquals(e.labels(), a.labels());
                assertEquals(e.children().size(), a.children().size());
                for (int i = 0; i < e.children().size(); i++) {
                    assertCoreExprEquals(e.children().get(i), a.children().get(i));
                }
            }
            case CorePrimOp e -> {
                var a = (CorePrimOp) actual;
                assertEquals(e.op(), a.op());
                assertEquals(e.args().size(), a.args().size());
                for (int i = 0; i < e.args().size(); i++) {
                    assertCoreExprEquals(e.args().get(i), a.args().get(i));
                }
            }
            case CoreTypeAbs e -> {
                var a = (CoreTypeAbs) actual;
                assertEquals(e.rigidId(), a.rigidId());
                assertEquals(e.kind(), a.kind());
                assertCoreExprEquals(e.body(), a.body());
            }
            case CoreTypeApp e -> {
                var a = (CoreTypeApp) actual;
                assertEquals(e.typeArg(), a.typeArg());
                assertCoreExprEquals(e.polyExpr(), a.polyExpr());
            }
            case CoreQuery e -> {
                var a = (CoreQuery) actual;
                assertEquals(e.esqlQuery(), a.esqlQuery());
                assertEquals(e.indexPattern(), a.indexPattern());
            }
            case CoreSpawn e -> {
                var a = (CoreSpawn) actual;
                if (e.body() == null) {
                    assertNull(a.body());
                } else {
                    assertNotNull(a.body());
                    assertCoreExprEquals(e.body(), a.body());
                }
            }
            case CoreWhen e -> {
                var a = (CoreWhen) actual;
                var eb = e.bindings();
                var ab = a.bindings();
                assertEquals(eb.size(), ab.size());
                for (int i = 0; i < eb.size(); i++) {
                    assertCoreExprEquals(eb.get(i).channel(), ab.get(i).channel());
                    assertEquals(eb.get(i).debugName(), ab.get(i).debugName());
                }
                assertCoreExprEquals(e.body(), a.body());
            }
            case CoreSend e -> {
                var a = (CoreSend) actual;
                assertCoreExprEquals(e.channel(), a.channel());
                assertCoreExprEquals(e.value(), a.value());
            }
        }
    }

    /**
     * Deep structural comparison of Values. For ClosureVal, compares body
     * structurally and env element-by-element.
     */
    private void assertValueEquals(Value expected, Value actual) {
        assertEquals(expected.getClass(), actual.getClass());
        switch (expected) {
            case Value.IntegerVal e -> assertEquals(e.value(), ((Value.IntegerVal) actual).value());
            case Value.LongVal e -> assertEquals(e.value(), ((Value.LongVal) actual).value());
            case Value.DoubleVal e -> assertEquals(e.value(), ((Value.DoubleVal) actual).value(), 0.0);
            case Value.KeywordVal e -> assertEquals(e.value(), ((Value.KeywordVal) actual).value());
            case Value.BooleanVal e -> assertEquals(e.value(), ((Value.BooleanVal) actual).value());
            case Value.NullVal ignored -> {
            }
            case Value.RecordVal e -> {
                var a = (Value.RecordVal) actual;
                assertEquals(e.fields().keySet(), a.fields().keySet());
                for (var key : e.fields().keySet()) {
                    assertValueEquals(e.fields().get(key), a.fields().get(key));
                }
            }
            case Value.ListVal e -> {
                var a = (Value.ListVal) actual;
                assertEquals(e.elements().size(), a.elements().size());
                for (int i = 0; i < e.elements().size(); i++) {
                    assertValueEquals(e.elements().get(i), a.elements().get(i));
                }
            }
            case Value.ClosureVal e -> {
                var a = (Value.ClosureVal) actual;
                assertCoreExprEquals(e.body(), a.body());
                assertEquals(e.env().length, a.env().length);
                for (int i = 0; i < e.env().length; i++) {
                    assertValueEquals(e.env()[i], a.env()[i]);
                }
            }
            case Value.BuiltinVal e -> {
                var a = (Value.BuiltinVal) actual;
                assertEquals(e.name(), a.name());
                assertEquals(e.arity(), a.arity());
                assertEquals(e.partialArgs().size(), a.partialArgs().size());
                for (int i = 0; i < e.partialArgs().size(); i++) {
                    assertValueEquals(e.partialArgs().get(i), a.partialArgs().get(i));
                }
            }
            case Value.ChannelVal e -> {
                var a = (Value.ChannelVal) actual;
                assertEquals(e.nodeId(), a.nodeId());
                assertEquals(e.channelId(), a.channelId());
            }
        }
    }
}
