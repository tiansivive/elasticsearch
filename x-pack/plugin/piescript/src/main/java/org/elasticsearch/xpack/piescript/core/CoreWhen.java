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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * When primitive: synchronizes on one or more channels (join pattern from the
 * Join Calculus). When all channels have delivered values, bind each value to
 * its variable and evaluate the body. See D-040, D-041.
 */
public final class CoreWhen extends CoreExpr {

    private final List<String> debugNames;
    private final MonoType type;

    public record WhenBinding(CoreExpr channel, @Nullable String debugName) {}

    public CoreWhen(Source source, List<WhenBinding> bindings, CoreExpr body, MonoType type) {
        super(source, buildChildren(bindings, body));
        this.debugNames = bindings.stream().map(WhenBinding::debugName).toList();
        this.type = type;
    }

    private static List<CoreExpr> buildChildren(List<WhenBinding> bindings, CoreExpr body) {
        var children = new ArrayList<CoreExpr>(bindings.size() + 1);
        for (var b : bindings) {
            children.add(b.channel());
        }
        children.add(body);
        return children;
    }

    public List<WhenBinding> bindings() {
        var ch = children();
        int n = ch.size() - 1;
        return IntStream.range(0, n).mapToObj(i -> new WhenBinding(ch.get(i), debugNames.get(i))).toList();
    }

    public CoreExpr body() {
        return children().get(children().size() - 1);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreWhen> info() {
        return NodeInfo.create(this, CoreWhen::new, bindings(), body(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        int n = debugNames.size();
        var newBindings = IntStream.range(0, n).mapToObj(i -> new WhenBinding(newChildren.get(i), debugNames.get(i))).toList();
        return new CoreWhen(source(), newBindings, newChildren.get(n), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debugNames, type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreWhen other = (CoreWhen) obj;
        return Objects.equals(debugNames, other.debugNames) && Objects.equals(type, other.type);
    }
}
