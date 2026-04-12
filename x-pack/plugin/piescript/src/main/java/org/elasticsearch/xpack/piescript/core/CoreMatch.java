/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.ArrayList;
import java.util.List;

/**
 * A pattern matching expression.
 * Evaluates the scrutinee, then tries each arm top-to-bottom.
 * If an arm matches, its pattern variables are bound in the environment
 * and the corresponding body is evaluated.
 *
 * <p>The scrutinee is the first child (index 0). The arm bodies
 * are the remaining children (indices 1..n).
 */
public final class CoreMatch extends CoreExpr {
    private final List<Alternative> arms;
    private final MonoType type;

    public CoreMatch(Source source, CoreExpr scrutinee, List<Alternative> arms, MonoType type) {
        super(source, buildChildren(scrutinee, arms));
        this.arms = List.copyOf(arms);
        this.type = type;
    }

    private static List<CoreExpr> buildChildren(CoreExpr scrutinee, List<Alternative> arms) {
        List<CoreExpr> children = new ArrayList<>(arms.size() + 1);
        children.add(scrutinee);
        for (Alternative arm : arms) {
            children.add(arm.body());
        }
        return children;
    }

    public CoreExpr scrutinee() {
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
    protected org.elasticsearch.xpack.esql.core.tree.NodeInfo<CoreMatch> info() {
        return org.elasticsearch.xpack.esql.core.tree.NodeInfo.create(this, CoreMatch::new, scrutinee(), arms, type);
    }

    @Override
    public CoreMatch replaceChildren(List<CoreExpr> newChildren) {
        if (newChildren.size() != arms.size() + 1) {
            throw new IllegalArgumentException("expected " + (arms.size() + 1) + " children, got " + newChildren.size());
        }
        CoreExpr newScrutinee = newChildren.get(0);
        List<Alternative> newArms = new ArrayList<>(arms.size());
        for (int i = 0; i < arms.size(); i++) {
            newArms.add(new Alternative(arms.get(i).pattern(), newChildren.get(i + 1)));
        }
        return new CoreMatch(source(), newScrutinee, newArms, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreMatch coreMatch = (CoreMatch) o;
        return type.equals(coreMatch.type) && arms.equals(coreMatch.arms) && scrutinee().equals(coreMatch.scrutinee());
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + arms.hashCode();
        result = 31 * result + scrutinee().hashCode();
        return result;
    }
}
