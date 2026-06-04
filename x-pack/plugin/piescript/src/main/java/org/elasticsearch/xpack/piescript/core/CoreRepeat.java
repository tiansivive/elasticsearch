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
 * Loop step marker expression: {@code repeat expr}.
 */
public final class CoreRepeat extends CoreExpr {

    private final MonoType type;

    public CoreRepeat(Source source, CoreExpr expr, MonoType type) {
        super(source, List.of(expr));
        this.type = type;
    }

    public CoreExpr expr() {
        return children().get(0);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreRepeat> info() {
        return NodeInfo.create(this, CoreRepeat::new, expr(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreRepeat(source(), newChildren.get(0), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, expr());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        var other = (CoreRepeat) obj;
        return Objects.equals(type, other.type) && Objects.equals(expr(), other.expr());
    }
}
