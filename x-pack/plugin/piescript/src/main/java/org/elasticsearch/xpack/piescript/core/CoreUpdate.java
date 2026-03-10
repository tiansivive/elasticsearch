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
 * Record update: {@code { expr | name = "bob", age = 31 }}. The base expression
 * is children[0]; the update values are children[1..n], parallel to
 * {@code labels}.
 */
public final class CoreUpdate extends CoreExpr {

    private final List<String> labels;
    private final MonoType type;

    /**
     * @param labels field names being updated, parallel to children[1..n]
     * @param children [expr, updateVal1, updateVal2, ...] — base expression followed by update values
     */
    public CoreUpdate(Source source, List<String> labels, List<CoreExpr> children, MonoType type) {
        super(source, children);
        this.labels = List.copyOf(labels);
        this.type = type;
    }

    /** Convenience factory from a base expression and field list. */
    public static CoreUpdate create(Source source, CoreExpr expr, List<CoreField> updates, MonoType type) {
        List<String> labels = new ArrayList<>(updates.size());
        List<CoreExpr> children = new ArrayList<>(1 + updates.size());
        children.add(expr);
        for (CoreField f : updates) {
            labels.add(f.label());
            children.add(f.value());
        }
        return new CoreUpdate(source, labels, children, type);
    }

    public CoreExpr expr() {
        return children().get(0);
    }

    public List<String> labels() {
        return labels;
    }

    /** Reconstruct the update field list by zipping labels with children[1..n]. */
    public List<CoreField> updates() {
        List<CoreExpr> values = children();
        List<CoreField> result = new ArrayList<>(labels.size());
        for (int i = 0; i < labels.size(); i++) {
            result.add(new CoreField(labels.get(i), values.get(i + 1)));
        }
        return result;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreUpdate> info() {
        return NodeInfo.create(this, CoreUpdate::new, labels, children(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreUpdate(source(), labels, newChildren, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labels, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreUpdate other = (CoreUpdate) obj;
        return Objects.equals(labels, other.labels) && Objects.equals(type, other.type);
    }
}
