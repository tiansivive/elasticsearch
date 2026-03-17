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
 * Spawn primitive. Two forms (D-045):
 * <ul>
 *   <li>{@code spawn expr} — forks {@code expr} asynchronously, returns {@code Channel bodyType}.
 *       Body is non-null.</li>
 *   <li>{@code spawn!} — bare channel creation, returns {@code Channel alpha} (fresh meta).
 *       Body is null. The channel is completed later via explicit {@code send}.</li>
 * </ul>
 *
 * See D-040, D-041, D-042, D-045.
 */
public final class CoreSpawn extends CoreExpr {

    private final MonoType type;

    public CoreSpawn(Source source, @Nullable CoreExpr body, MonoType type) {
        super(source, body != null ? List.of(body) : List.of());
        this.type = type;
    }

    /**
     * The body expression to evaluate asynchronously, or {@code null} for bare
     * channel creation ({@code spawn!}).
     */
    @Nullable
    public CoreExpr body() {
        return children().isEmpty() ? null : children().get(0);
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
        if (newChildren.isEmpty()) {
            return new CoreSpawn(source(), null, type);
        }
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
