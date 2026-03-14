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
 * Type application ({@code @}-node). Applies a single type argument to a
 * polymorphic expression. Multiple type arguments are represented as nested
 * {@code CoreTypeApp} nodes, mirroring how {@code CoreApp} nests for
 * multi-argument function application.
 *
 * <p>Emitted by the elaborator at instantiation sites (use sites of
 * polymorphic bindings).
 */
public final class CoreTypeApp extends CoreExpr {

    private final MonoType typeArg;
    @Nullable
    private final MonoType type;

    public CoreTypeApp(Source source, CoreExpr polyExpr, MonoType typeArg, @Nullable MonoType type) {
        super(source, List.of(polyExpr));
        this.typeArg = typeArg;
        this.type = type;
    }

    public CoreExpr polyExpr() {
        return children().get(0);
    }

    public MonoType typeArg() {
        return typeArg;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreTypeApp> info() {
        return NodeInfo.create(this, CoreTypeApp::new, polyExpr(), typeArg, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreTypeApp(source(), newChildren.get(0), typeArg, type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeArg, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreTypeApp other = (CoreTypeApp) obj;
        return Objects.equals(typeArg, other.typeArg) && Objects.equals(type, other.type);
    }
}
