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

import java.util.List;
import java.util.Objects;

/**
 * Field projection: {@code expr.label}. Extracts a single field from a
 * record-typed expression.
 */
public final class CoreProject extends CoreExpr {

    private final String label;
    private final MonoType type;

    public CoreProject(Source source, CoreExpr expr, String label, MonoType type) {
        super(source, List.of(expr));
        this.label = label;
        this.type = type;
    }

    public CoreExpr expr() {
        return children().get(0);
    }

    public String label() {
        return label;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreProject> info() {
        return NodeInfo.create(this, CoreProject::new, expr(), label, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreProject(source(), newChildren.get(0), label, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreProject other = (CoreProject) obj;
        return Objects.equals(label, other.label) && Objects.equals(type, other.type);
    }
}
