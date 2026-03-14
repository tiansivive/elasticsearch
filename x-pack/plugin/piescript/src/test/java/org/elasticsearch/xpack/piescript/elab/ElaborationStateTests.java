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
import org.elasticsearch.xpack.piescript.types.TypeScheme;

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
        assertThat(m0.kind(), is(Kind.TYPE));
        assertThat(m1.kind(), is(Kind.TYPE));
    }

    public void testFreshRowAllocatesRowMeta() {
        var state = new ElaborationState();
        var m = state.freshRow(0);

        assertThat(m.id(), is(0));
        assertThat(m.kind(), is(Kind.ROW));
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

        var rigid = state.freshRigid(Kind.TYPE);
        state.solve(alpha.id(), rigid);
        var scheme = new TypeScheme(Map.of(rigid.id(), Kind.TYPE), idType);

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
