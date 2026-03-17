/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreSend;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;

/**
 * Send expression elaboration: {@code send channel value}. The channel
 * expression must have type {@code Channel a}, and the value must have type
 * {@code a}. The result type is {@code Null} (send is a side effect).
 * See D-045.
 */
final class Sends {

    private Sends() {}

    static CoreExpr send(Elaborator elab, PiescriptAntlrParser.SendExprContext s, ElaborationContext ctx) {
        var src = Elaborator.source(s);
        var channel = elab.elaborate(s.primary(), ctx);
        var value = elab.elaborate(s.expr(), ctx);

        var elementMeta = elab.state.freshType(ctx.bindingLevel());
        elab.emitConstraint(channel.type(), new MonoType.AppType(Elaborator.CHANNEL, elementMeta), src);
        elab.emitConstraint(value.type(), elementMeta, src);

        return new CoreSend(src.source(), channel, value, Elaborator.NULL_TYPE);
    }
}
