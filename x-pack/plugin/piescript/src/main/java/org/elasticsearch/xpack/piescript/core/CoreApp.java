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
 * Function application: {@code fn arg}. Left-associative; multi-argument
 * application is represented as nested {@code CoreApp} nodes.
 */
public final class CoreApp extends CoreExpr {

    private final MonoType type;

    public CoreApp(Source source, CoreExpr fn, CoreExpr arg, MonoType type) {
        super(source, List.of(fn, arg));
        this.type = type;
    }

    public CoreExpr fn() {
        return children().get(0);
    }

    public CoreExpr arg() {
        return children().get(1);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreApp> info() {
        return NodeInfo.create(this, CoreApp::new, fn(), arg(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreApp(source(), newChildren.get(0), newChildren.get(1), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreApp other = (CoreApp) obj;
        return Objects.equals(type, other.type);
    }
}
