/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.core.Nullable;
import org.elasticsearch.xpack.esql.core.tree.NodeInfo;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;
import java.util.Objects;

/**
 * Let-binding: {@code let x = rhs in body}. The bound variable uses de Bruijn
 * index 0 inside the body. {@code bindType} is the elaborated type of the RHS.
 * At the type level, the binding may be generalized to a polymorphic
 * {@link org.elasticsearch.xpack.piescript.types.TypeScheme} by the elaborator;
 * {@code bindType} here is the monomorphic instance before generalization.
 */
public final class CoreLet extends CoreExpr {

    @Nullable
    private final String debugName;
    private final MonoType bindType;
    private final MonoType type;

    public CoreLet(Source source, @Nullable String debugName, MonoType bindType, CoreExpr rhs, CoreExpr body, MonoType type) {
        super(source, List.of(rhs, body));
        this.debugName = debugName;
        this.bindType = bindType;
        this.type = type;
    }

    @Nullable
    public String debugName() {
        return debugName;
    }

    public MonoType bindType() {
        return bindType;
    }

    public CoreExpr rhs() {
        return children().get(0);
    }

    public CoreExpr body() {
        return children().get(1);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreLet> info() {
        return NodeInfo.create(this, CoreLet::new, debugName, bindType, rhs(), body(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreLet(source(), debugName, bindType, newChildren.get(0), newChildren.get(1), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debugName, bindType, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreLet other = (CoreLet) obj;
        return Objects.equals(debugName, other.debugName) && Objects.equals(bindType, other.bindType) && Objects.equals(type, other.type);
    }
}
