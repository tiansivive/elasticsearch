/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.xpack.esql.core.tree.NodeInfo;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;
import java.util.Objects;

/**
 * Literal value. The {@link LitVal} variants are aligned with ES DataType
 * (see Phase 1 plan D1.3).
 */
public final class CoreLit extends CoreExpr {

    private final LitVal value;
    private final MonoType type;

    public CoreLit(Source source, LitVal value, MonoType type) {
        super(source, List.of());
        this.value = value;
        this.type = type;
    }

    public LitVal value() {
        return value;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreLit> info() {
        return NodeInfo.create(this, CoreLit::new, value, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        throw new UnsupportedOperationException("leaf node has no children to replace");
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreLit other = (CoreLit) obj;
        return Objects.equals(value, other.value) && Objects.equals(type, other.type);
    }
}
