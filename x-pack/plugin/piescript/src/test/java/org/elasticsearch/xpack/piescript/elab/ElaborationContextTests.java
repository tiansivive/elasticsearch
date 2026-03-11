/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import static org.hamcrest.Matchers.is;

public class ElaborationContextTests extends ESTestCase {

    // ──── Empty context ────

    public void testEmptyContext() {
        var ctx = ElaborationContext.EMPTY;
        assertThat(ctx.depth(), is(0));
        assertThat(ctx.bindingLevel(), is(0));
    }

    public void testLookupInEmptyContext() {
        var ctx = ElaborationContext.EMPTY;
        assertTrue(ctx.lookup("x").isEmpty());
    }

    // ──── Binding and lookup ────

    public void testBindAndLookup() {
        var scheme = TypeScheme.mono(new MonoType.TCon("Integer"));
        var ctx = ElaborationContext.EMPTY.bind("x", scheme);

        var result = ctx.lookup("x");
        assertTrue(result.isPresent());
        assertThat(result.get().index(), is(0));
        assertThat(result.get().scheme(), is(scheme));
    }

    public void testDeBruijnIndexing() {
        var intScheme = TypeScheme.mono(new MonoType.TCon("Integer"));
        var boolScheme = TypeScheme.mono(new MonoType.TCon("Boolean"));

        var ctx = ElaborationContext.EMPTY.bind("x", intScheme).bind("y", boolScheme);

        var yResult = ctx.lookup("y");
        assertThat(yResult.get().index(), is(0));
        assertThat(yResult.get().scheme(), is(boolScheme));

        var xResult = ctx.lookup("x");
        assertThat(xResult.get().index(), is(1));
        assertThat(xResult.get().scheme(), is(intScheme));
    }

    public void testShadowing() {
        var outer = TypeScheme.mono(new MonoType.TCon("Integer"));
        var inner = TypeScheme.mono(new MonoType.TCon("Boolean"));

        var ctx = ElaborationContext.EMPTY.bind("x", outer).bind("x", inner);

        var result = ctx.lookup("x");
        assertThat(result.get().index(), is(0));
        assertThat(result.get().scheme(), is(inner));
    }

    // ──── Immutability ────

    public void testBindDoesNotMutateOriginal() {
        var ctx = ElaborationContext.EMPTY;
        var extended = ctx.bind("x", TypeScheme.mono(new MonoType.TCon("Integer")));

        assertThat(ctx.depth(), is(0));
        assertTrue(ctx.lookup("x").isEmpty());

        assertThat(extended.depth(), is(1));
        assertTrue(extended.lookup("x").isPresent());
    }

    public void testScopeUnwindingViaCallStack() {
        var intScheme = TypeScheme.mono(new MonoType.TCon("Integer"));
        var boolScheme = TypeScheme.mono(new MonoType.TCon("Boolean"));

        var outerCtx = ElaborationContext.EMPTY.bind("x", intScheme);

        var innerCtx = outerCtx.bind("y", boolScheme);
        assertThat(innerCtx.depth(), is(2));
        assertTrue(innerCtx.lookup("y").isPresent());

        assertThat(outerCtx.depth(), is(1));
        assertTrue(outerCtx.lookup("y").isEmpty());
    }

    // ──── Binding level ────

    public void testBindingLevel() {
        var ctx = ElaborationContext.EMPTY;
        assertThat(ctx.bindingLevel(), is(0));

        var inner = ctx.enterBindingLevel();
        assertThat(inner.bindingLevel(), is(1));

        var deeper = inner.enterBindingLevel();
        assertThat(deeper.bindingLevel(), is(2));

        var back = deeper.exitBindingLevel();
        assertThat(back.bindingLevel(), is(1));
    }

    public void testEnterBindingLevelDoesNotMutateOriginal() {
        var ctx = ElaborationContext.EMPTY;
        var inner = ctx.enterBindingLevel();

        assertThat(ctx.bindingLevel(), is(0));
        assertThat(inner.bindingLevel(), is(1));
    }

    public void testBindingLevelPreservedAcrossBinds() {
        var ctx = ElaborationContext.EMPTY.enterBindingLevel();
        var extended = ctx.bind("x", TypeScheme.mono(new MonoType.TCon("Integer")));

        assertThat(extended.bindingLevel(), is(1));
    }

    // ──── Depth ────

    public void testDepthGrowsWithBindings() {
        var ctx = ElaborationContext.EMPTY;
        assertThat(ctx.depth(), is(0));

        ctx = ctx.bind("a", TypeScheme.mono(new MonoType.TCon("Integer")));
        assertThat(ctx.depth(), is(1));

        ctx = ctx.bind("b", TypeScheme.mono(new MonoType.TCon("Boolean")));
        assertThat(ctx.depth(), is(2));
    }
}
