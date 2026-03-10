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
import org.elasticsearch.xpack.piescript.types.Op;

import java.util.List;
import java.util.Objects;

/**
 * Primitive operation: {@code e1 + e2}, {@code !e}, {@code -e}, etc. The
 * {@link Op} enum identifies the operation; operands are the Node children.
 * Binary ops have 2 children; unary ops ({@link Op#NOT}, {@link Op#NEG})
 * have 1.
 */
public final class CorePrimOp extends CoreExpr {

    private final Op op;
    private final MonoType type;

    public CorePrimOp(Source source, Op op, List<CoreExpr> args, MonoType type) {
        super(source, args);
        this.op = op;
        this.type = type;
    }

    public Op op() {
        return op;
    }

    public List<CoreExpr> args() {
        return children();
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CorePrimOp> info() {
        return NodeInfo.create(this, CorePrimOp::new, op, children(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CorePrimOp(source(), op, newChildren, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CorePrimOp other = (CorePrimOp) obj;
        return op == other.op && Objects.equals(type, other.type);
    }
}
