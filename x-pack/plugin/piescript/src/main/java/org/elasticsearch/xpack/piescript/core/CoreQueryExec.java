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
 * Query execution boundary: evaluates the inner expression (an {@code ESQL r} plan),
 * compiles it to an ESQL string, and fires {@code EsqlQueryAction}. Produced by the
 * {@code query expr ;} syntax. The type is {@code List (Record r)} — the materialized
 * result rows. See D-052.
 */
public final class CoreQueryExec extends CoreExpr {

    private final MonoType type;

    public CoreQueryExec(Source source, CoreExpr plan, MonoType type) {
        super(source, List.of(plan));
        this.type = type;
    }

    public CoreExpr plan() {
        return children().get(0);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreQueryExec> info() {
        return NodeInfo.create(this, CoreQueryExec::new, children().get(0), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreQueryExec(source(), newChildren.get(0), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreQueryExec other = (CoreQueryExec) obj;
        return Objects.equals(type, other.type);
    }
}
