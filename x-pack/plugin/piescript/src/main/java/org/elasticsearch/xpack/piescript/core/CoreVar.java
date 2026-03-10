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
 * Variable reference using a de Bruijn index. Index 0 is the immediately
 * enclosing binder; each additional binder between the occurrence and
 * its definition increments the index by one.
 */
public final class CoreVar extends CoreExpr {

    private final int index;
    @Nullable
    private final String debugName;
    private final MonoType type;

    public CoreVar(Source source, int index, @Nullable String debugName, MonoType type) {
        super(source, List.of());
        this.index = index;
        this.debugName = debugName;
        this.type = type;
    }

    public int index() {
        return index;
    }

    @Nullable
    public String debugName() {
        return debugName;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreVar> info() {
        return NodeInfo.create(this, CoreVar::new, index, debugName, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        throw new UnsupportedOperationException("leaf node has no children to replace");
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, debugName, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreVar other = (CoreVar) obj;
        return index == other.index && Objects.equals(debugName, other.debugName) && Objects.equals(type, other.type);
    }
}
