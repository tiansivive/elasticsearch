/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class UnifierTests extends ESTestCase {

    private static final MonoType INTEGER = new MonoType.TCon("Integer");
    private static final MonoType BOOLEAN = new MonoType.TCon("Boolean");
    private static final MonoType KEYWORD = new MonoType.TCon("Keyword");
    private static final MonoType NULL = new MonoType.TCon("Null");

    // ──── Identical types ────

    public void testIdenticalTCon() {
        var state = new ElaborationState();
        assertTrue(Unifier.unify(INTEGER, INTEGER, state).isEmpty());
    }

    public void testIdenticalArrow() {
        var state = new ElaborationState();
        var arrow = new MonoType.Arrow(INTEGER, BOOLEAN);
        assertTrue(Unifier.unify(arrow, arrow, state).isEmpty());
    }

    // ──── Meta solving ────

    public void testMetaLeft() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);

        assertTrue(Unifier.unify(alpha, INTEGER, state).isEmpty());
        assertThat(state.zonkOrKeep(alpha), is(INTEGER));
    }

    public void testMetaRight() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);

        assertTrue(Unifier.unify(INTEGER, alpha, state).isEmpty());
        assertThat(state.zonkOrKeep(alpha), is(INTEGER));
    }

    public void testMetaMeta() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var beta = state.freshType(0);

        assertTrue(Unifier.unify(alpha, beta, state).isEmpty());
        // One should point to the other
        assertTrue(state.isSolved(alpha.id()) || state.isSolved(beta.id()));
    }

    public void testTransitiveChain() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var beta = state.freshType(0);

        assertTrue(Unifier.unify(alpha, beta, state).isEmpty());
        assertTrue(Unifier.unify(beta, INTEGER, state).isEmpty());

        assertThat(state.zonkOrKeep(alpha), is(INTEGER));
        assertThat(state.zonkOrKeep(beta), is(INTEGER));
    }

    public void testMetaAlreadySolved() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);

        assertTrue(Unifier.unify(alpha, INTEGER, state).isEmpty());
        assertTrue(Unifier.unify(alpha, INTEGER, state).isEmpty());
    }

    public void testMetaSolvedConflict() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);

        assertTrue(Unifier.unify(alpha, INTEGER, state).isEmpty());
        var error = Unifier.unify(alpha, BOOLEAN, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    // ──── Occurs check ────

    public void testOccursCheck() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var infinite = new MonoType.Arrow(alpha, INTEGER);

        var error = Unifier.unify(alpha, infinite, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.InfiniteType.class));
    }

    public void testOccursCheckNested() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var nested = new MonoType.Arrow(INTEGER, new MonoType.Arrow(alpha, BOOLEAN));

        var error = Unifier.unify(alpha, nested, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.InfiniteType.class));
    }

    // ──── TCon ────

    public void testTConMismatch() {
        var state = new ElaborationState();
        var error = Unifier.unify(INTEGER, BOOLEAN, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    // ──── Null-as-bottom ────

    public void testNullUnifiesWithAnything() {
        var state = new ElaborationState();
        assertTrue(Unifier.unify(NULL, INTEGER, state).isEmpty());
        assertTrue(Unifier.unify(BOOLEAN, NULL, state).isEmpty());
        assertTrue(Unifier.unify(NULL, NULL, state).isEmpty());
    }

    public void testNullUnifiesWithArrow() {
        var state = new ElaborationState();
        var arrow = new MonoType.Arrow(INTEGER, BOOLEAN);
        assertTrue(Unifier.unify(NULL, arrow, state).isEmpty());
    }

    public void testNullUnifiesWithMeta() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        assertTrue(Unifier.unify(NULL, alpha, state).isEmpty());
    }

    public void testNullInArrowParam() {
        var state = new ElaborationState();
        var a = new MonoType.Arrow(NULL, INTEGER);
        var b = new MonoType.Arrow(BOOLEAN, INTEGER);
        assertTrue(Unifier.unify(a, b, state).isEmpty());
    }

    // ──── Arrow ────

    public void testArrowSuccess() {
        var state = new ElaborationState();
        var a = new MonoType.Arrow(INTEGER, BOOLEAN);
        var b = new MonoType.Arrow(INTEGER, BOOLEAN);
        assertTrue(Unifier.unify(a, b, state).isEmpty());
    }

    public void testArrowParamMismatch() {
        var state = new ElaborationState();
        var a = new MonoType.Arrow(INTEGER, BOOLEAN);
        var b = new MonoType.Arrow(KEYWORD, BOOLEAN);

        var error = Unifier.unify(a, b, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    public void testArrowResultMismatch() {
        var state = new ElaborationState();
        var a = new MonoType.Arrow(INTEGER, BOOLEAN);
        var b = new MonoType.Arrow(INTEGER, KEYWORD);

        var error = Unifier.unify(a, b, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    public void testArrowWithMetas() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var beta = state.freshType(0);
        var a = new MonoType.Arrow(alpha, BOOLEAN);
        var b = new MonoType.Arrow(INTEGER, beta);

        assertTrue(Unifier.unify(a, b, state).isEmpty());
        assertThat(state.zonkOrKeep(alpha), is(INTEGER));
        assertThat(state.zonkOrKeep(beta), is(BOOLEAN));
    }

    // ──── Record (closed rows) ────

    public void testRecordSuccess() {
        var state = new ElaborationState();
        var a = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER, "y", BOOLEAN)));
        var b = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER, "y", BOOLEAN)));
        assertTrue(Unifier.unify(a, b, state).isEmpty());
    }

    public void testRecordMissingField() {
        var state = new ElaborationState();
        var a = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER, "y", BOOLEAN)));
        var b = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));

        var error = Unifier.unify(a, b, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.MissingFields.class));
        var missing = (TypeError.MissingFields) error.get();
        assertTrue(missing.missing().contains("y"));
    }

    public void testRecordExtraField() {
        var state = new ElaborationState();
        var a = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));
        var b = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER, "z", KEYWORD)));

        var error = Unifier.unify(a, b, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.MissingFields.class));
    }

    public void testRecordFieldTypeMismatch() {
        var state = new ElaborationState();
        var a = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));
        var b = new MonoType.RecordType(RowType.closed(Map.of("x", BOOLEAN)));

        var error = Unifier.unify(a, b, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.FieldMismatch.class));
        var fieldErr = (TypeError.FieldMismatch) error.get();
        assertThat(fieldErr.label(), is("x"));
    }

    public void testRecordWithMetas() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var a = new MonoType.RecordType(RowType.closed(Map.of("x", alpha)));
        var b = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));

        assertTrue(Unifier.unify(a, b, state).isEmpty());
        assertThat(state.zonkOrKeep(alpha), is(INTEGER));
    }

    public void testEmptyRecords() {
        var state = new ElaborationState();
        var a = new MonoType.RecordType(RowType.closed(Map.of()));
        var b = new MonoType.RecordType(RowType.closed(Map.of()));
        assertTrue(Unifier.unify(a, b, state).isEmpty());
    }

    // ──── AppType ────

    public void testAppTypeSuccess() {
        var state = new ElaborationState();
        var stream = new MonoType.TCon("Stream");
        var a = new MonoType.AppType(stream, INTEGER);
        var b = new MonoType.AppType(stream, INTEGER);
        assertTrue(Unifier.unify(a, b, state).isEmpty());
    }

    public void testAppTypeMismatch() {
        var state = new ElaborationState();
        var a = new MonoType.AppType(new MonoType.TCon("Stream"), INTEGER);
        var b = new MonoType.AppType(new MonoType.TCon("List"), INTEGER);

        var error = Unifier.unify(a, b, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    // ──── Cross-form mismatch ────

    public void testArrowVsTCon() {
        var state = new ElaborationState();
        var error = Unifier.unify(new MonoType.Arrow(INTEGER, BOOLEAN), INTEGER, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    public void testRecordVsTCon() {
        var state = new ElaborationState();
        var record = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));
        var error = Unifier.unify(record, INTEGER, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    public void testArrowVsRecord() {
        var state = new ElaborationState();
        var arrow = new MonoType.Arrow(INTEGER, BOOLEAN);
        var record = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));
        var error = Unifier.unify(arrow, record, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    // ──── Open-row unification (D-030) ────

    public void testOpenRowAbsorbsExcess() {
        var state = new ElaborationState();
        var tail = state.freshRow(0);
        var open = new MonoType.RecordType(RowType.open(Map.of("x", INTEGER), tail));
        var closed = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER, "y", BOOLEAN)));

        assertTrue(Unifier.unify(open, closed, state).isEmpty());
        var resolved = state.resolveRow(RowType.open(Map.of("x", INTEGER), tail));
        assertTrue(resolved.fields().containsKey("y"));
        assertThat(resolved.fields().get("y"), is(BOOLEAN));
    }

    public void testClosedRowExcessVsOpenTail() {
        var state = new ElaborationState();
        var tail = state.freshRow(0);
        var open = new MonoType.RecordType(RowType.open(Map.of("x", INTEGER), tail));
        var closed = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));

        assertTrue(Unifier.unify(open, closed, state).isEmpty());
    }

    public void testOpenRowExcessVsClosed() {
        var state = new ElaborationState();
        var closed = new MonoType.RecordType(RowType.closed(Map.of("x", INTEGER)));
        var open = new MonoType.RecordType(RowType.open(Map.of("x", INTEGER, "y", BOOLEAN), state.freshRow(0)));

        var error = Unifier.unify(closed, open, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.MissingFields.class));
    }

    public void testTwoOpenRowsUnify() {
        var state = new ElaborationState();
        var tail1 = state.freshRow(0);
        var tail2 = state.freshRow(0);
        var open1 = new MonoType.RecordType(RowType.open(Map.of("x", INTEGER), tail1));
        var open2 = new MonoType.RecordType(RowType.open(Map.of("y", BOOLEAN), tail2));

        assertTrue(Unifier.unify(open1, open2, state).isEmpty());
        var resolved1 = state.resolveRow(RowType.open(Map.of("x", INTEGER), tail1));
        assertTrue(resolved1.fields().containsKey("y"));
        var resolved2 = state.resolveRow(RowType.open(Map.of("y", BOOLEAN), tail2));
        assertTrue(resolved2.fields().containsKey("x"));
    }

    // ──── Rigid type variables (D-031) ────

    public void testRigidSameId() {
        var state = new ElaborationState();
        var r = new MonoType.Rigid(0, Kind.TYPE);
        assertTrue(Unifier.unify(r, r, state).isEmpty());
    }

    public void testRigidDifferentId() {
        var state = new ElaborationState();
        var r1 = new MonoType.Rigid(0, Kind.TYPE);
        var r2 = new MonoType.Rigid(1, Kind.TYPE);
        var error = Unifier.unify(r1, r2, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    public void testRigidVsTCon() {
        var state = new ElaborationState();
        var r = new MonoType.Rigid(0, Kind.TYPE);
        var error = Unifier.unify(r, INTEGER, state);
        assertTrue(error.isPresent());
        assertThat(error.get(), instanceOf(TypeError.Mismatch.class));
    }

    public void testMetaSolvesToRigid() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var r = new MonoType.Rigid(99, Kind.TYPE);
        assertTrue(Unifier.unify(alpha, r, state).isEmpty());
        assertThat(state.zonkOrKeep(alpha), is(r));
    }
}
