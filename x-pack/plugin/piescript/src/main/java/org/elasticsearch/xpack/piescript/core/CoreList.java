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
 * A list literal in Core IR. Children are the element expressions, all
 * sharing the same element type {@code a}. The node type is {@code List a}.
 */
public final class CoreList extends CoreExpr {

    private final MonoType type;

    public CoreList(Source source, List<CoreExpr> elements, MonoType type) {
        super(source, elements);
        this.type = type;
    }

    public List<CoreExpr> elements() {
        return children();
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreList> info() {
        return NodeInfo.create(this, CoreList::new, children(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreList(source(), newChildren, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreList other = (CoreList) obj;
        return Objects.equals(type, other.type);
    }
}
