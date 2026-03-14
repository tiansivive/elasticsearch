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
import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;
import java.util.Objects;

/**
 * Type abstraction (big-lambda / {@code Λ}). Introduces a single type variable
 * into scope. Multiple quantifiers are represented as nested {@code CoreTypeAbs}
 * nodes, mirroring how {@code CoreLam} nests for multi-parameter functions.
 *
 * <p>Emitted by the elaborator at generalization sites (let-bindings with
 * polymorphic types). The body contains {@link MonoType.Rigid} references
 * that refer to {@code rigidId}.
 */
public final class CoreTypeAbs extends CoreExpr {

    private final int rigidId;
    private final Kind kind;
    @Nullable
    private final MonoType type;

    public CoreTypeAbs(Source source, int rigidId, Kind kind, CoreExpr body, @Nullable MonoType type) {
        super(source, List.of(body));
        this.rigidId = rigidId;
        this.kind = kind;
        this.type = type;
    }

    public int rigidId() {
        return rigidId;
    }

    public Kind kind() {
        return kind;
    }

    public CoreExpr body() {
        return children().get(0);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreTypeAbs> info() {
        return NodeInfo.create(this, CoreTypeAbs::new, rigidId, kind, body(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreTypeAbs(source(), rigidId, kind, newChildren.get(0), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rigidId, kind, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreTypeAbs other = (CoreTypeAbs) obj;
        return rigidId == other.rigidId && kind == other.kind && Objects.equals(type, other.type);
    }
}
