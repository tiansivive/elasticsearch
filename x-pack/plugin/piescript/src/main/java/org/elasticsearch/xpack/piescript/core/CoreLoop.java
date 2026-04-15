/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.xpack.esql.core.tree.NodeInfo;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Structured loop expression.
 *
 * <p>The first child is the initial state expression. Each subsequent child is
 * the corresponding arm body from {@code arms}, with patterns stored in
 * {@link Alternative}.
 */
public final class CoreLoop extends CoreExpr {

    private final List<Alternative> arms;
    private final MonoType type;

    public CoreLoop(Source source, CoreExpr init, List<Alternative> arms, MonoType type) {
        super(source, buildChildren(init, arms));
        this.arms = List.copyOf(arms);
        this.type = type;
    }

    private static List<CoreExpr> buildChildren(CoreExpr init, List<Alternative> arms) {
        var children = new ArrayList<CoreExpr>(arms.size() + 1);
        children.add(init);
        for (var arm : arms) {
            children.add(arm.body());
        }
        return children;
    }

    public CoreExpr init() {
        return children().get(0);
    }

    public List<Alternative> arms() {
        return arms;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreLoop> info() {
        return NodeInfo.create(this, CoreLoop::new, init(), arms, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        if (newChildren.size() != arms.size() + 1) {
            throw new IllegalArgumentException("expected " + (arms.size() + 1) + " children, got " + newChildren.size());
        }
        var newInit = newChildren.get(0);
        var newArms = new ArrayList<Alternative>(arms.size());
        for (int i = 0; i < arms.size(); i++) {
            newArms.add(new Alternative(arms.get(i).pattern(), newChildren.get(i + 1)));
        }
        return new CoreLoop(source(), newInit, newArms, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, arms, init());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        var other = (CoreLoop) obj;
        return Objects.equals(type, other.type) && Objects.equals(arms, other.arms) && Objects.equals(init(), other.init());
    }
}