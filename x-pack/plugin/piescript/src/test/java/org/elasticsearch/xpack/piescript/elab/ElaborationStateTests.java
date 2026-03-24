/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;
import org.elasticsearch.xpack.piescript.types.Types;

import java.util.Map;

import static org.hamcrest.Matchers.is;

public class ElaborationStateTests extends ESTestCase {

    // ──── Fresh state ────

    public void testFreshStateIsEmpty() {
        var state = new ElaborationState();
        assertThat(state.metaCount(), is(0));
    }

    // ──── Metavar supply ────

    public void testFreshTypeAllocatesUniqueMetas() {
        var state = new ElaborationState();
        var m0 = state.freshType(0);
        var m1 = state.freshType(0);

        assertThat(m0.id(), is(0));
        assertThat(m1.id(), is(1));
        assertThat(m0.kind(), is(Types.TYPE));
        assertThat(m1.kind(), is(Types.TYPE));
    }

    public void testFreshRowAllocatesRowMeta() {
        var state = new ElaborationState();
        var m = state.freshRow(0);

        assertThat(m.id(), is(0));
        assertThat(m.kind(), is(Types.ROW));
    }

    public void testTypeAndRowShareSupply() {
        var state = new ElaborationState();
        var t0 = state.freshType(0);
        var r1 = state.freshRow(0);
        var t2 = state.freshType(0);

        assertThat(t0.id(), is(0));
        assertThat(r1.id(), is(1));
        assertThat(t2.id(), is(2));
        assertThat(state.metaCount(), is(3));
    }

    public void testFreshMetaRecordsBindingLevel() {
        var state = new ElaborationState();
        var m0 = state.freshType(0);
        assertThat(m0.bindingLevel(), is(0));

        var m1 = state.freshType(1);
        assertThat(m1.bindingLevel(), is(1));

        var m2 = state.freshRow(2);
        assertThat(m2.bindingLevel(), is(2));
    }

    // ──── Zonker ────

    public void testSolveAndResolve() {
        var state = new ElaborationState();
        var meta = state.freshType(0);
        var solution = new MonoType.TCon("Integer");

        state.solve(meta.id(), solution);

        assertThat(state.isSolved(meta.id()), is(true));
        assertTrue(state.resolve(meta.id()).isPresent());
        assertThat(state.resolve(meta.id()).get(), is(solution));
    }

    public void testUnsolvedMeta() {
        var state = new ElaborationState();
        var meta = state.freshType(0);

        assertThat(state.isSolved(meta.id()), is(false));
        assertTrue(state.resolve(meta.id()).isEmpty());
    }

    public void testChainResolution() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var beta = state.freshType(0);
        var integer = new MonoType.TCon("Integer");

        state.solve(alpha.id(), beta);
        state.solve(beta.id(), integer);

        assertThat(state.resolve(alpha.id()).get(), is(integer));
    }

    public void testResolveTypeMeta() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var integer = new MonoType.TCon("Integer");
        state.solve(alpha.id(), integer);

        assertThat(state.zonkOrKeep(alpha), is(integer));
    }

    public void testResolveTypeChain() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        var beta = state.freshType(0);
        var integer = new MonoType.TCon("Integer");

        state.solve(alpha.id(), beta);
        state.solve(beta.id(), integer);

        assertThat(state.zonkOrKeep(alpha), is(integer));
    }

    public void testResolveTypeNonMeta() {
        var state = new ElaborationState();
        var tcon = new MonoType.TCon("Integer");

        assertThat(state.zonkOrKeep(tcon), is(tcon));
    }

    public void testResolveTypeUnsolvedMeta() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);

        assertThat(state.zonkOrKeep(alpha), is(alpha));
    }

    public void testSolveRowMeta() {
        var state = new ElaborationState();
        var rowMeta = state.freshRow(0);
        var rowSolution = RowType.closed(Map.of("x", new MonoType.TCon("Integer")));

        state.solve(rowMeta.id(), rowSolution);

        assertThat(state.isSolved(rowMeta.id()), is(true));
        assertThat(state.resolve(rowMeta.id()).get(), is(rowSolution));
    }

    public void testZonkerView() {
        var state = new ElaborationState();
        var alpha = state.freshType(0);
        state.solve(alpha.id(), new MonoType.TCon("Integer"));

        assertThat(state.zonker().size(), is(1));
        assertTrue(state.zonker().containsKey(alpha.id()));
    }

    // ──── force: row merge (&) ────

    public void testMergeConcreteRows() {
        var state = new ElaborationState();
        var left = RowType.closed(Map.of("a", new MonoType.TCon("Integer")));
        var right = RowType.closed(Map.of("b", new MonoType.TCon("Keyword")));
        var mergeType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("&"), left), right);

        var result = state.force(mergeType);
        assertTrue(result instanceof RowType);
        var row = (RowType) result;
        assertThat(row.fields().size(), is(2));
        assertTrue(row.fields().containsKey("a"));
        assertTrue(row.fields().containsKey("b"));
    }

    public void testMergeRightBiasedOverlap() {
        var state = new ElaborationState();
        var left = RowType.closed(Map.of("x", new MonoType.TCon("Integer")));
        var right = RowType.closed(Map.of("x", new MonoType.TCon("Keyword")));
        var mergeType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("&"), left), right);

        var result = state.force(mergeType);
        assertTrue(result instanceof RowType);
        var row = (RowType) result;
        assertThat(row.fields().size(), is(1));
        assertThat(row.fields().get("x"), is(new MonoType.TCon("Keyword")));
    }

    public void testMergeStuckOnMeta() {
        var state = new ElaborationState();
        var meta = state.freshRow(0);
        var right = RowType.closed(Map.of("a", new MonoType.TCon("Integer")));
        var mergeType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("&"), meta), right);

        var result = state.force(mergeType);
        assertTrue(result instanceof MonoType.AppType);
    }

    public void testNestedMerge() {
        var state = new ElaborationState();
        var a = RowType.closed(Map.of("x", new MonoType.TCon("Integer")));
        var b = RowType.closed(Map.of("y", new MonoType.TCon("Keyword")));
        var c = RowType.closed(Map.of("z", new MonoType.TCon("Boolean")));
        // (a & b) & c
        var ab = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("&"), a), b);
        var abc = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("&"), ab), c);

        var result = state.force(abc);
        assertTrue(result instanceof RowType);
        var row = (RowType) result;
        assertThat(row.fields().size(), is(3));
        assertTrue(row.fields().containsKey("x"));
        assertTrue(row.fields().containsKey("y"));
        assertTrue(row.fields().containsKey("z"));
    }

    public void testForceResolvesThroughMeta() {
        var state = new ElaborationState();
        var meta = state.freshRow(0);
        var concreteRow = RowType.closed(Map.of("a", new MonoType.TCon("Integer")));
        state.solve(meta.id(), concreteRow);

        var result = state.force(meta);
        assertTrue(result instanceof RowType);
        assertThat(((RowType) result).fields().size(), is(1));
    }

    // ──── force: Pick and Omit ────

    public void testPickConcreteRows() {
        var state = new ElaborationState();
        var left = RowType.closed(
            Map.of("a", new MonoType.TCon("Integer"), "b", new MonoType.TCon("Keyword"), "c", new MonoType.TCon("Boolean"))
        );
        var right = RowType.closed(Map.of("a", new MonoType.TCon("Integer"), "c", new MonoType.TCon("Boolean")));
        var pickType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("Pick"), left), right);

        var result = state.force(pickType);
        assertTrue(result instanceof RowType);
        var row = (RowType) result;
        assertThat(row.fields().size(), is(2));
        assertTrue(row.fields().containsKey("a"));
        assertTrue(row.fields().containsKey("c"));
        assertFalse(row.fields().containsKey("b"));
    }

    public void testOmitConcreteRows() {
        var state = new ElaborationState();
        var left = RowType.closed(
            Map.of("a", new MonoType.TCon("Integer"), "b", new MonoType.TCon("Keyword"), "c", new MonoType.TCon("Boolean"))
        );
        var right = RowType.closed(Map.of("b", new MonoType.TCon("Keyword")));
        var omitType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("Omit"), left), right);

        var result = state.force(omitType);
        assertTrue(result instanceof RowType);
        var row = (RowType) result;
        assertThat(row.fields().size(), is(2));
        assertTrue(row.fields().containsKey("a"));
        assertTrue(row.fields().containsKey("c"));
        assertFalse(row.fields().containsKey("b"));
    }

    public void testPickStuckOnMeta() {
        var state = new ElaborationState();
        var meta = state.freshRow(0);
        var right = RowType.closed(Map.of("a", new MonoType.TCon("Integer")));
        var pickType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("Pick"), meta), right);

        var result = state.force(pickType);
        assertTrue(result instanceof MonoType.AppType);
    }

    public void testOmitStuckOnMeta() {
        var state = new ElaborationState();
        var left = RowType.closed(Map.of("a", new MonoType.TCon("Integer")));
        var meta = state.freshRow(0);
        var omitType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("Omit"), left), meta);

        var result = state.force(omitType);
        assertTrue(result instanceof MonoType.AppType);
    }

    public void testPickEmptySelector() {
        var state = new ElaborationState();
        var left = RowType.closed(Map.of("a", new MonoType.TCon("Integer"), "b", new MonoType.TCon("Keyword")));
        var right = RowType.closed(Map.of());
        var pickType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("Pick"), left), right);

        var result = state.force(pickType);
        assertTrue(result instanceof RowType);
        assertThat(((RowType) result).fields().size(), is(0));
    }

    public void testOmitEmptySelector() {
        var state = new ElaborationState();
        var left = RowType.closed(Map.of("a", new MonoType.TCon("Integer"), "b", new MonoType.TCon("Keyword")));
        var right = RowType.closed(Map.of());
        var omitType = new MonoType.AppType(new MonoType.AppType(new MonoType.TCon("Omit"), left), right);

        var result = state.force(omitType);
        assertTrue(result instanceof RowType);
        assertThat(((RowType) result).fields().size(), is(2));
    }

    // ──── Integrated: context + state working together ────

    /**
     * Simulates the elaboration of:
     * <pre>{@code let id = fn x -> x in id 42}</pre>
     *
     * Exercises the immutable context and mutable state together, following
     * the same flow as the real elaborator: fresh meta for the param, then
     * generalize solves the meta to a Rigid and builds a TypeScheme keyed
     * by Rigid IDs.
     */
    public void testLetPolymorphismWorkflow() {
        var state = new ElaborationState();
        var ctx = ElaborationContext.EMPTY;

        var letCtx = ctx.enterBindingLevel();
        var alpha = state.freshType(letCtx.bindingLevel());
        assertThat(alpha.bindingLevel(), is(1));

        var lamCtx = letCtx.bind("x", TypeScheme.mono(alpha));
        var xResult = lamCtx.lookup("x");
        assertTrue(xResult.isPresent());
        assertThat(xResult.get().index(), is(0));
        assertThat(xResult.get().scheme().body(), is(alpha));

        var idType = new MonoType.Arrow(alpha, alpha);

        var rigid = state.freshRigid(Types.TYPE);
        state.solve(alpha.id(), rigid);
        var scheme = new TypeScheme(Map.of(rigid.id(), Types.TYPE), idType);

        var bodyCtx = ctx.bind("id", scheme);
        var idResult = bodyCtx.lookup("id");
        assertTrue(idResult.isPresent());
        assertThat(idResult.get().index(), is(0));
        assertThat(idResult.get().scheme().quantified().size(), is(1));
        assertTrue(idResult.get().scheme().quantified().containsKey(rigid.id()));

        var beta = state.freshType(bodyCtx.bindingLevel());
        assertThat(beta.bindingLevel(), is(0));
        state.solve(beta.id(), new MonoType.TCon("Integer"));
        assertThat(state.zonkOrKeep(beta), is(new MonoType.TCon("Integer")));

        assertThat(ctx.depth(), is(0));
    }
}
