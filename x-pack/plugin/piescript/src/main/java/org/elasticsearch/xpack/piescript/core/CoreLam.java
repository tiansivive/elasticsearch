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
 * Lambda abstraction. The parameter is not named in the Core IR (de Bruijn indices
 * make names unnecessary); {@code debugName} is retained for error messages and
 * pretty-printing. {@code paramType} is the elaborated type of the parameter.
 */
public final class CoreLam extends CoreExpr {

    @Nullable
    private final String debugName;
    private final MonoType paramType;
    private final MonoType type;

    public CoreLam(Source source, @Nullable String debugName, MonoType paramType, CoreExpr body, MonoType type) {
        super(source, List.of(body));
        this.debugName = debugName;
        this.paramType = paramType;
        this.type = type;
    }

    @Nullable
    public String debugName() {
        return debugName;
    }

    public MonoType paramType() {
        return paramType;
    }

    public CoreExpr body() {
        return children().get(0);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreLam> info() {
        return NodeInfo.create(this, CoreLam::new, debugName, paramType, body(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreLam(source(), debugName, paramType, newChildren.get(0), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debugName, paramType, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreLam other = (CoreLam) obj;
        return Objects.equals(debugName, other.debugName) && Objects.equals(paramType, other.paramType) && Objects.equals(type, other.type);
    }
}
