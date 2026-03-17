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
 * Send primitive: {@code send channel value}. Completes the channel with the
 * given value. The channel expression must evaluate to a {@code ChannelVal};
 * routing (local vs. remote) is determined at evaluation time based on the
 * channel's owner node. See D-045.
 *
 * <p>Type: {@code Channel a -> a -> Null}. The result type is always
 * {@code Null} (send is a side effect).
 */
public final class CoreSend extends CoreExpr {

    private final MonoType type;

    public CoreSend(Source source, CoreExpr channel, CoreExpr value, MonoType type) {
        super(source, List.of(channel, value));
        this.type = type;
    }

    public CoreExpr channel() {
        return children().get(0);
    }

    public CoreExpr value() {
        return children().get(1);
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreSend> info() {
        return NodeInfo.create(this, CoreSend::new, channel(), value(), type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        return new CoreSend(source(), newChildren.get(0), newChildren.get(1), type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, children());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreSend other = (CoreSend) obj;
        return Objects.equals(type, other.type);
    }
}
