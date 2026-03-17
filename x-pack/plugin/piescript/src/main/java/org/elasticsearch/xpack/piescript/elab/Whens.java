/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreWhen;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.List;

/**
 * When expression elaboration: converts a {@code WhenExpr} CST node into a
 * typed {@link CoreWhen}. Each channel binding is elaborated in the outer
 * scope, constrained to {@code Channel tau}, and the bound variable is extended
 * into the context for the body.
 */
final class Whens {

    private Whens() {}

    private record ElaboratedBinding(CoreExpr channel, String name, MonoType elementType) {}

    static CoreExpr when_(Elaborator elab, PiescriptAntlrParser.WhenExprContext w, ElaborationContext ctx) {
        var src = Elaborator.source(w);

        var elaborated = w.whenBinding().stream().map(b -> elaborateBinding(elab, b, ctx)).toList();

        var bodyCtx = foldContext(ctx, elaborated);
        var body = elab.elaborate(w.expr(), bodyCtx);

        var bindings = elaborated.stream().map(b -> new CoreWhen.WhenBinding(b.channel(), b.name())).toList();

        return new CoreWhen(src.source(), bindings, body, body.type());
    }

    private static ElaboratedBinding elaborateBinding(
        Elaborator elab,
        PiescriptAntlrParser.WhenBindingContext binding,
        ElaborationContext ctx
    ) {
        var channelExpr = elab.elaborate(binding.expr(), ctx);
        var name = binding.ident().getText();
        var elementMeta = elab.state.freshType(ctx.bindingLevel());
        elab.emitConstraint(channelExpr.type(), new MonoType.AppType(Elaborator.CHANNEL, elementMeta), Elaborator.source(binding));
        return new ElaboratedBinding(channelExpr, name, elementMeta);
    }

    private static ElaborationContext foldContext(ElaborationContext ctx, List<ElaboratedBinding> bindings) {
        var result = ctx;
        for (var b : bindings) {
            result = result.bind(b.name(), TypeScheme.mono(b.elementType()));
        }
        return result;
    }
}
