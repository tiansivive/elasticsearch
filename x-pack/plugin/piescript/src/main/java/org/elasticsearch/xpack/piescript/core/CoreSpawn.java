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
 * Spawn primitive: launches an expression asynchronously and returns a channel
 * ({@code Channel bodyType}) that will carry the result when the computation
 * completes. See D-040, D-041.
 */
public final class CoreSpawn extends CoreExpr {

    private final MonoType type;

    public CoreSpawn(Source source, CoreExpr body, MonoType type) {
        super(source, List.of(body));
        this.type = type;
    }

    public CoreExpr body() {
        return children().get(0);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreSpawn> info() {
        return NodeInfo.create(this, CoreSpawn::new, body(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreSpawn(source(), newChildren.get(0), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreSpawn other = (CoreSpawn) obj;
        return Objects.equals(type, other.type);
    }
}
