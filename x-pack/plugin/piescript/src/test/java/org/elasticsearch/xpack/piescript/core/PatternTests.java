/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.Types;

import java.util.List;
import java.util.Map;

public class PatternTests extends ESTestCase {

    public void testLitPat() {
        var lit = new LitVal.DoubleLit(42.0);
        var pat = new Pattern.LitPat(lit);
        assertEquals(lit, pat.value());
        assertEquals(new Pattern.LitPat(new LitVal.DoubleLit(42.0)), pat);
        assertNotEquals(new Pattern.LitPat(new LitVal.DoubleLit(43.0)), pat);
    }

    public void testVarPat() {
        var pat = new Pattern.VarPat("x", Types.DOUBLE);
        assertEquals("x", pat.debugName());
        assertEquals(Types.DOUBLE, pat.type());
        assertEquals(new Pattern.VarPat("x", Types.DOUBLE), pat);
        assertNotEquals(new Pattern.VarPat("y", Types.DOUBLE), pat);
    }

    public void testWildcardPat() {
        var pat = new Pattern.WildcardPat();
        assertEquals(new Pattern.WildcardPat(), pat);
    }

    public void testRecordPat() {
        var pat = new Pattern.RecordPat(Map.of("a", new Pattern.VarPat("a", Types.DOUBLE)), true, "rest");
        assertTrue(pat.hasTail());
        assertEquals("rest", pat.tailName());
        assertEquals(1, pat.fields().size());
        assertEquals(new Pattern.RecordPat(Map.of("a", new Pattern.VarPat("a", Types.DOUBLE)), true, "rest"), pat);
        assertNotEquals(new Pattern.RecordPat(Map.of("a", new Pattern.VarPat("a", Types.DOUBLE)), false, null), pat);
    }

    public void testListPat() {
        var pat = new Pattern.ListPat(List.of(new Pattern.VarPat("x", Types.DOUBLE)));
        assertEquals(1, pat.elements().size());
        assertEquals(new Pattern.ListPat(List.of(new Pattern.VarPat("x", Types.DOUBLE))), pat);
        assertNotEquals(new Pattern.ListPat(List.of()), pat);
    }

    public void testConsListPat() {
        var head = new Pattern.VarPat("h", Types.DOUBLE);
        var tail = new Pattern.VarPat("t", Types.list(Types.DOUBLE));
        var pat = new Pattern.ConsListPat(head, tail);
        assertEquals(head, pat.head());
        assertEquals(tail, pat.tail());
        assertEquals(new Pattern.ConsListPat(head, tail), pat);
        assertNotEquals(new Pattern.ConsListPat(tail, head), pat);
    }
}
