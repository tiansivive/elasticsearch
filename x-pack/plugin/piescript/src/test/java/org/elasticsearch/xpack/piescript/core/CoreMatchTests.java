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

import static org.elasticsearch.xpack.piescript.core.Exprs.arm;
import static org.elasticsearch.xpack.piescript.core.Exprs.lit;
import static org.elasticsearch.xpack.piescript.core.Exprs.match;
import static org.elasticsearch.xpack.piescript.core.Exprs.var;

public class CoreMatchTests extends ESTestCase {

    public void testConstruction() {
        var scrutinee = lit(42.0);
        var body1 = lit(1.0);
        var body2 = lit(2.0);
        
        var arm1 = arm(new Pattern.LitPat(new LitVal.DoubleLit(42.0)), body1);
        var arm2 = arm(new Pattern.WildcardPat(), body2);
        
        var m = match(scrutinee, arm1, arm2);
        
        assertEquals(Types.DOUBLE, m.type());
        assertEquals(scrutinee, m.scrutinee());
        assertEquals(2, m.arms().size());
        assertEquals(arm1, m.arms().get(0));
        assertEquals(arm2, m.arms().get(1));
        
        // Children should be scrutinee + bodies
        assertEquals(3, m.children().size());
        assertEquals(scrutinee, m.children().get(0));
        assertEquals(body1, m.children().get(1));
        assertEquals(body2, m.children().get(2));
    }

    public void testReplaceChildren() {
        var scrutinee = lit(42.0);
        var body1 = lit(1.0);
        var body2 = lit(2.0);
        
        var arm1 = arm(new Pattern.LitPat(new LitVal.DoubleLit(42.0)), body1);
        var arm2 = arm(new Pattern.WildcardPat(), body2);
        
        var m = match(scrutinee, arm1, arm2);
        
        var newScrutinee = lit(43.0);
        var newBody1 = lit(3.0);
        var newBody2 = lit(4.0);
        
        var replaced = m.replaceChildren(List.of(newScrutinee, newBody1, newBody2));
        
        assertEquals(newScrutinee, replaced.scrutinee());
        assertEquals(2, replaced.arms().size());
        assertEquals(newBody1, replaced.arms().get(0).body());
        assertEquals(newBody2, replaced.arms().get(1).body());
        
        // Pattern should be preserved
        assertEquals(arm1.pattern(), replaced.arms().get(0).pattern());
        assertEquals(arm2.pattern(), replaced.arms().get(1).pattern());
    }

    public void testReplaceChildrenWrongSize() {
        var m = match(lit(42.0), arm(new Pattern.WildcardPat(), lit(1.0)));
        expectThrows(IllegalArgumentException.class, () -> m.replaceChildren(List.of(lit(1.0))));
    }
}
