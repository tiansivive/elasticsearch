/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.Alternative;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLoop;
import org.elasticsearch.xpack.piescript.core.CoreRepeat;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;
import org.elasticsearch.xpack.piescript.types.Types;

import java.util.ArrayList;

/**
 * Elaborates loop/repeat expressions.
 */
final class Loops {

    private Loops() {}

    static CoreExpr loop(PiescriptAntlrParser.LoopExprContext ctx, ElaborationContext ectx, Elaborator elab) {
        var src = Elaborator.source(ctx);
        var stateType = elab.state.freshType(ectx.bindingLevel());
        var resultType = elab.state.freshType(ectx.bindingLevel());

        var init = elab.elaborate(ctx.expr(), ectx);
        elab.emitConstraint(init.type(), stateType, src);

        var loopCtx = ectx.enterLoop(stateType);
        var arms = new ArrayList<Alternative>(ctx.alternative().size());

        for (var armCtx : ctx.alternative()) {
            var armSrc = Elaborator.source(armCtx);
            var pat = Matches.inferPattern(armCtx.pattern(), ectx.bindingLevel(), elab.state, elab);
            elab.emitConstraint(stateType, pat.patType(), armSrc);

            var armCtxEnv = loopCtx;
            for (var binding : pat.bindings()) {
                armCtxEnv = armCtxEnv.bind(binding.name(), TypeScheme.mono(binding.type()));
            }

            var body = elab.elaborate(armCtx.expr(), armCtxEnv);
            if (body.type() instanceof MonoType.AppType app && Types.REPEAT.equals(app.constructor())) {
                elab.emitConstraint(app.argument(), stateType, armSrc);
            } else {
                elab.emitConstraint(body.type(), resultType, armSrc);
            }
            arms.add(new Alternative(pat.pat(), body));
        }

        return new CoreLoop(src.source(), init, arms, resultType);
    }

    static CoreExpr repeat(PiescriptAntlrParser.RepeatExprContext ctx, ElaborationContext ectx, Elaborator elab) {
        var src = Elaborator.source(ctx);
        var stateType = ectx.loopStateType().orElseThrow(() -> Elaborator.error(src, "repeat can only be used inside a loop"));
        var expr = elab.elaborate(ctx.expr(), ectx);
        elab.emitConstraint(expr.type(), stateType, src);
        return new CoreRepeat(src.source(), expr, Types.repeat(stateType));
    }
}
