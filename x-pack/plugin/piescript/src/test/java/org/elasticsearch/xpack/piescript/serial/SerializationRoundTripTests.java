/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.serial;

import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreExprSerialization;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreList;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreQueryExec;
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
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.TypeSerialization;
import org.elasticsearch.xpack.piescript.types.Types;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.xpack.piescript.core.Exprs.add;
import static org.elasticsearch.xpack.piescript.core.Exprs.app;
import static org.elasticsearch.xpack.piescript.core.Exprs.binding;
import static org.elasticsearch.xpack.piescript.core.Exprs.field;
import static org.elasticsearch.xpack.piescript.core.Exprs.free;
import static org.elasticsearch.xpack.piescript.core.Exprs.lam;
import static org.elasticsearch.xpack.piescript.core.Exprs.let;
import static org.elasticsearch.xpack.piescript.core.Exprs.lit;
import static org.elasticsearch.xpack.piescript.core.Exprs.proj;
import static org.elasticsearch.xpack.piescript.core.Exprs.rec;
import static org.elasticsearch.xpack.piescript.core.Exprs.send;
import static org.elasticsearch.xpack.piescript.core.Exprs.spawn;
import static org.elasticsearch.xpack.piescript.core.Exprs.spawnBang;
import static org.elasticsearch.xpack.piescript.core.Exprs.typeAbs;
import static org.elasticsearch.xpack.piescript.core.Exprs.typeApp;
import static org.elasticsearch.xpack.piescript.core.Exprs.update;
import static org.elasticsearch.xpack.piescript.core.Exprs.var;
import static org.elasticsearch.xpack.piescript.core.Exprs.when;
import static org.elasticsearch.xpack.piescript.eval.Values.bool;
import static org.elasticsearch.xpack.piescript.eval.Values.builtin;
import static org.elasticsearch.xpack.piescript.eval.Values.channelVal;
import static org.elasticsearch.xpack.piescript.eval.Values.closure;
import static org.elasticsearch.xpack.piescript.eval.Values.doubleVal;
import static org.elasticsearch.xpack.piescript.eval.Values.intVal;
import static org.elasticsearch.xpack.piescript.eval.Values.keyword;
import static org.elasticsearch.xpack.piescript.eval.Values.longVal;
import static org.elasticsearch.xpack.piescript.eval.Values.nullVal;
import static org.elasticsearch.xpack.piescript.types.Types.BOOLEAN;
import static org.elasticsearch.xpack.piescript.types.Types.INTEGER;
import static org.elasticsearch.xpack.piescript.types.Types.KEYWORD;
import static org.elasticsearch.xpack.piescript.types.Types.arrow;
import static org.elasticsearch.xpack.piescript.types.Types.channel;
import static org.elasticsearch.xpack.piescript.types.Types.list;
import static org.elasticsearch.xpack.piescript.types.Types.record;
import static org.elasticsearch.xpack.piescript.types.Types.rigid;
import static org.hamcrest.Matchers.containsString;

/**
 * Round-trip serialization tests for all piescript wire types:
 * {@link MonoType}, {@link org.elasticsearch.xpack.piescript.types.RowType}, {@link LitVal}, {@link Op},
 * {@link CoreExpr} (16 variants), and {@link Value} (11 variants).
 */
public class SerializationRoundTripTests extends ESTestCase {

    // ──── MonoType ────

    public void testMonoTypeTCon() throws IOException {
        assertMonoTypeRoundTrip(INTEGER);
    }

    public void testMonoTypeArrow() throws IOException {
        assertMonoTypeRoundTrip(arrow(INTEGER, BOOLEAN));
    }

    public void testMonoTypeRecordClosed() throws IOException {
        assertMonoTypeRoundTrip(record(Map.of("x", INTEGER, "y", BOOLEAN)));
    }

    public void testMonoTypeRecordOpen() throws IOException {
        var row = org.elasticsearch.xpack.piescript.types.RowType.open(Map.of("x", INTEGER), new MonoType.Meta(42, 1, Types.ROW));
        assertMonoTypeRoundTrip(new MonoType.RecordType(row));
    }

    public void testMonoTypeApp() throws IOException {
        assertMonoTypeRoundTrip(list(INTEGER));
    }

    public void testMonoTypeMeta() throws IOException {
        assertMonoTypeRoundTrip(new MonoType.Meta(7, 2, Types.TYPE));
    }

    public void testMonoTypeRigid() throws IOException {
        assertMonoTypeRoundTrip(rigid(3));
    }

    public void testMonoTypeNestedArrow() throws IOException {
        assertMonoTypeRoundTrip(arrow(arrow(INTEGER, BOOLEAN), arrow(KEYWORD, INTEGER)));
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
        assertLitValRoundTrip(new LitVal.KeywordLit(new org.apache.lucene.util.BytesRef("hello")));
    }

    public void testLitValBoolean() throws IOException {
        assertLitValRoundTrip(new LitVal.BooleanLit(true));
    }

    public void testLitValNull() throws IOException {
        assertLitValRoundTrip(new LitVal.NullLit());
    }

    public void testLitValIndex() throws IOException {
        assertLitValRoundTrip(new LitVal.IndexLit("logs-test", Map.of("status", "integer", "message", "keyword")));
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
        assertCoreExprRoundTrip(var(2, "x", INTEGER));
    }

    public void testCoreVarNullDebugName() throws IOException {
        assertCoreExprRoundTrip(var(0, INTEGER));
    }

    public void testCoreFree() throws IOException {
        assertCoreExprRoundTrip(free("List.map", arrow(INTEGER, INTEGER)));
    }

    public void testCoreLit() throws IOException {
        assertCoreExprRoundTrip(lit(42));
    }

    public void testCoreLam() throws IOException {
        assertCoreExprRoundTrip(lam("x", INTEGER, var(0, "x", INTEGER)));
    }

    public void testCoreApp() throws IOException {
        var fn = var(0, "f", arrow(INTEGER, BOOLEAN));
        assertCoreExprRoundTrip(app(fn, lit(42)));
    }

    public void testCoreLet() throws IOException {
        assertCoreExprRoundTrip(let("x", lit(42), var(0, "x", INTEGER)));
    }

    public void testCoreRecord() throws IOException {
        assertCoreExprRoundTrip(rec(field("a", lit(1)), field("b", lit(true))));
    }

    public void testCoreProject() throws IOException {
        var recExpr = var(0, "r", record(Map.of("x", INTEGER)));
        assertCoreExprRoundTrip(proj(recExpr, "x", INTEGER));
    }

    public void testCoreUpdate() throws IOException {
        var recExpr = var(0, "r", record(Map.of("x", INTEGER)));
        assertCoreExprRoundTrip(update(recExpr, field("x", lit(99))));
    }

    public void testCorePrimOp() throws IOException {
        assertCoreExprRoundTrip(add(lit(1), lit(2)));
    }

    public void testCoreTypeAbs() throws IOException {
        var body = var(0, "x", rigid(1));
        assertCoreExprRoundTrip(typeAbs(1, Types.TYPE, body));
    }

    public void testCoreTypeApp() throws IOException {
        var polyExpr = var(0, "id", arrow(rigid(1), rigid(1)));
        assertCoreExprRoundTrip(typeApp(polyExpr, INTEGER, arrow(INTEGER, INTEGER)));
    }

    public void testCoreSpawnWithBody() throws IOException {
        assertCoreExprRoundTrip(spawn(lit(42)));
    }

    public void testCoreSpawnBare() throws IOException {
        assertCoreExprRoundTrip(spawnBang(INTEGER));
    }

    public void testCoreWhen() throws IOException {
        var ch = var(0, "ch", channel(INTEGER));
        var body = var(1, "x", INTEGER);
        assertCoreExprRoundTrip(when(List.of(binding(ch, "x")), body));
    }

    public void testCoreWhenMultipleBindings() throws IOException {
        var ch1 = var(0, "a", channel(INTEGER));
        var ch2 = var(1, "b", channel(BOOLEAN));
        var bindings = List.of(binding(ch1, "x"), binding(ch2, "y"));
        assertCoreExprRoundTrip(when(bindings, lit(1)));
    }

    public void testCoreSend() throws IOException {
        var ch = var(0, "ch", channel(INTEGER));
        assertCoreExprRoundTrip(send(ch, lit(42)));
    }

    public void testCoreExprNestedLet() throws IOException {
        var inner = let("y", lit(1), var(0, "y", INTEGER));
        assertCoreExprRoundTrip(let("x", inner, var(0, "x", INTEGER)));
    }

    // ──── Value ────

    public void testValueInteger() throws IOException {
        assertValueRoundTrip(intVal(42));
    }

    public void testValueLong() throws IOException {
        assertValueRoundTrip(longVal(9999999999L));
    }

    public void testValueDouble() throws IOException {
        assertValueRoundTrip(doubleVal(3.14));
    }

    public void testValueKeyword() throws IOException {
        assertValueRoundTrip(keyword("hello"));
    }

    public void testValueBoolean() throws IOException {
        assertValueRoundTrip(bool(true));
    }

    public void testValueNull() throws IOException {
        assertValueRoundTrip(nullVal());
    }

    public void testValueRecord() throws IOException {
        assertValueRoundTrip(new Value.RecordVal(Map.of("x", intVal(1), "y", bool(true))));
    }

    public void testValueNestedRecord() throws IOException {
        var inner = new Value.RecordVal(Map.of("a", intVal(1)));
        assertValueRoundTrip(new Value.RecordVal(Map.of("nested", inner, "flat", keyword("hello"))));
    }

    public void testValueList() throws IOException {
        assertValueRoundTrip(new Value.ListVal(List.of(intVal(1), intVal(2), intVal(3))));
    }

    public void testValueEmptyList() throws IOException {
        assertValueRoundTrip(new Value.ListVal(List.of()));
    }

    public void testValueListOfRecords() throws IOException {
        var r1 = new Value.RecordVal(Map.of("x", intVal(1)));
        var r2 = new Value.RecordVal(Map.of("x", intVal(2)));
        assertValueRoundTrip(new Value.ListVal(List.of(r1, r2)));
    }

    public void testValueChannel() throws IOException {
        assertValueRoundTrip(channelVal("node-1", "ch-42"));
    }

    public void testValueBuiltin() throws IOException {
        assertValueRoundTrip(builtin("List.map", 2));
    }

    public void testValueBuiltinPartiallyApplied() throws IOException {
        assertValueRoundTrip(builtin("List.map", 2, List.of(intVal(1))));
    }

    public void testValueClosure() throws IOException {
        assertValueRoundTrip(closure(var(0, "x", INTEGER), intVal(42), bool(true)));
    }

    public void testValueClosureEmptyEnv() throws IOException {
        assertValueRoundTrip(closure(lit(1)));
    }

    public void testValueClosureNestedInRecord() throws IOException {
        var cl = closure(var(0, "x", INTEGER), intVal(10));
        assertValueRoundTrip(new Value.RecordVal(Map.of("fn", cl, "name", keyword("test"))));
    }

    public void testValueClosureCapturingClosure() throws IOException {
        var innerClosure = closure(var(0, "y", INTEGER), intVal(1));
        var outerBody = app(var(0, "f", arrow(INTEGER, INTEGER)), var(1, "x", INTEGER));
        assertValueRoundTrip(closure(outerBody, innerClosure, intVal(2)));
    }

    public void testValueChannelInClosureEnv() throws IOException {
        assertValueRoundTrip(closure(var(0, "ch", channel(INTEGER)), channelVal("node-2", "ch-99")));
    }

    public void testValueIndex() throws IOException {
        assertValueRoundTrip(new Value.IndexVal("logs-test", "abc-123-uuid", Map.of("status", "integer", "message", "keyword")));
    }

    public void testValueSearcherNotSerializable() {
        var ex = expectThrows(IOException.class, () -> {
            var out = new BytesStreamOutput();
            ValueSerialization.writeValue(out, new Value.SearcherVal(null));
        });
        assertThat(ex.getMessage(), containsString("not serializable"));
    }

    public void testValueDocRefNotSerializable() {
        var ex = expectThrows(IOException.class, () -> {
            var out = new BytesStreamOutput();
            ValueSerialization.writeValue(out, new Value.DocRefVal(null, 0, null));
        });
        assertThat(ex.getMessage(), containsString("not serializable"));
    }

    public void testValueWriterNotSerializable() {
        var ex = expectThrows(IOException.class, () -> {
            var out = new BytesStreamOutput();
            ValueSerialization.writeValue(out, new Value.WriterVal(null));
        });
        assertThat(ex.getMessage(), containsString("not serializable"));
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
            case CoreList e -> {
                var a = (CoreList) actual;
                assertEquals(e.elements().size(), a.elements().size());
                for (int i = 0; i < e.elements().size(); i++) {
                    assertCoreExprEquals(e.elements().get(i), a.elements().get(i));
                }
            }
            case CoreQueryExec e -> {
                var a = (CoreQueryExec) actual;
                assertCoreExprEquals(e.plan(), a.plan());
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
            case Value.IndexVal e -> {
                var a = (Value.IndexVal) actual;
                assertEquals(e.name(), a.name());
                assertEquals(e.uuid(), a.uuid());
                assertEquals(e.fieldTypes(), a.fieldTypes());
            }
            case Value.ExchangeVal e -> {
                var a = (Value.ExchangeVal) actual;
                assertEquals(e.exchangeId(), a.exchangeId());
                assertEquals(e.columnNames(), a.columnNames());
                assertEquals(e.bufferSize(), a.bufferSize());
            }
            case Value.SearcherVal ignored -> fail("SearcherVal should not be serialized");
            case Value.DocRefVal ignored -> fail("DocRefVal should not be serialized");
            case Value.WriterVal ignored -> fail("WriterVal should not be serialized");
            case Value.Symbol ignored -> fail("Symbol should not be serialized");
            case Value.PageVal ignored -> fail("PageVal should not be serialized");
            case Value.ExchangeSinkVal ignored -> fail("ExchangeSinkVal should not be serialized");
            case Value.ExchangeSourceVal ignored -> fail("ExchangeSourceVal should not be serialized");
        }
    }
}
