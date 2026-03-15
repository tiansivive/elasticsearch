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
 * A free variable reference: a name resolved from the module-level environment
 * rather than the de Bruijn indexed local scope. Free variables carry their
 * name (not an index) because they live outside the local binding structure.
 *
 * <p>Currently, every {@code CoreFree} refers to a built-in function
 * ({@code map}, {@code filter}, {@code reduce}). The module mechanism is
 * designed to be extended into a proper module/import system in future phases.
 */
public final class CoreFree extends CoreExpr {

    private final String name;
    private final MonoType type;

    public CoreFree(Source source, String name, MonoType type) {
        super(source, List.of());
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreFree> info() {
        return NodeInfo.create(this, CoreFree::new, name, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        throw new UnsupportedOperationException("leaf node has no children to replace");
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreFree other = (CoreFree) obj;
        return Objects.equals(name, other.name) && Objects.equals(type, other.type);
    }
}
