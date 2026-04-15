/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.List;

/**
 * Lambda elaboration in both modes: {@link #infer} (synthesis) and
 * {@link #check} (checking against a known {@link MonoType.Arrow}).
 *
 * <p>The ∀-CHECK rule (peeling quantifiers, inserting {@code CoreTypeAbs}) is
 * handled by the {@link Elaborator#check} dispatcher before reaching this class,
 * so the expected type here is always a concrete {@link MonoType.Arrow}.
 */
final class Lambda {

    private Lambda() {}

    // ──── Infer (synthesis) mode ────

    static CoreExpr infer(Elaborator elab, PiescriptAntlrParser.LambdaExprContext lam, ElaborationContext ctx) {
        return inferParams(elab, lam.param(), 0, lam.expr(), ctx, Elaborator.source(lam));
    }

    private static CoreExpr inferParams(
        Elaborator elab,
        List<PiescriptAntlrParser.ParamContext> params,
        int index,
        PiescriptAntlrParser.ExprContext bodyExpr,
        ElaborationContext ctx,
        Elaborator.Src lamSource
    ) {
        if (index >= params.size()) {
            return elab.elaborate(bodyExpr, ctx);
        }

        var param = params.get(index);
        String name;
        MonoType paramType;

        switch (param) {
            case PiescriptAntlrParser.UntypedParamContext u -> {
                name = u.ident().getText();
                paramType = elab.state.freshType(ctx.bindingLevel());
            }
            case PiescriptAntlrParser.TypedParamContext t -> {
                name = t.ident().getText();
                paramType = TypeAnnotations.toMonoType(elab, ctx, t.type());
            }
            default -> throw Elaborator.error(Elaborator.source(param), "unexpected parameter form");
        }

        var innerCtx = ctx.enterLambda().bind(name, TypeScheme.mono(paramType));
        var body = inferParams(elab, params, index + 1, bodyExpr, innerCtx, lamSource);
        var arrowType = new MonoType.Arrow(paramType, body.type());
        var nodeSrc = index == 0 ? lamSource : Elaborator.source(param);

        return new CoreLam(nodeSrc.source(), name, paramType, body, arrowType);
    }

    // ──── Check mode ────

    /**
     * Check a lambda against a known {@link MonoType.Arrow}. The arrow's domain
     * provides the parameter type (for untyped params) or a constraint target
     * (for typed params). The body is checked against the codomain.
     */
    static CoreExpr check(Elaborator elab, PiescriptAntlrParser.LambdaExprContext lam, MonoType expected, ElaborationContext ctx) {
        var src = Elaborator.source(lam);
        return checkParams(elab, lam.param(), 0, lam.expr(), asArrow(elab, expected, ctx, src), ctx, src);
    }

    private static CoreExpr checkParams(
        Elaborator elab,
        List<PiescriptAntlrParser.ParamContext> params,
        int index,
        PiescriptAntlrParser.ExprContext bodyExpr,
        MonoType.Arrow expectedArrow,
        ElaborationContext ctx,
        Elaborator.Src lamSource
    ) {
        var param = params.get(index);
        String name;
        MonoType paramType;

        switch (param) {
            case PiescriptAntlrParser.UntypedParamContext u -> {
                name = u.ident().getText();
                paramType = expectedArrow.param();
            }
            case PiescriptAntlrParser.TypedParamContext t -> {
                name = t.ident().getText();
                paramType = TypeAnnotations.toMonoType(elab, ctx, t.type());
                elab.emitConstraint(paramType, expectedArrow.param(), Elaborator.source(t));
            }
            default -> throw Elaborator.error(Elaborator.source(param), "unexpected parameter form");
        }

        var innerCtx = ctx.enterLambda().bind(name, TypeScheme.mono(paramType));
        var codomain = expectedArrow.result();

        CoreExpr body;
        if (index + 1 >= params.size()) {
            body = elab.check(bodyExpr, TypeScheme.mono(codomain), innerCtx, lamSource);
        } else {
            body = checkParams(elab, params, index + 1, bodyExpr, asArrow(elab, codomain, ctx, lamSource), innerCtx, lamSource);
        }

        var arrowType = new MonoType.Arrow(paramType, body.type());
        var nodeSrc = index == 0 ? lamSource : Elaborator.source(param);

        return new CoreLam(nodeSrc.source(), name, paramType, body, arrowType);
    }

    /**
     * Ensure a type is an {@link MonoType.Arrow}, decomposing a meta into a
     * fresh arrow shape if needed. Used for multi-parameter lambda checking
     * where the codomain must be an arrow for the next parameter.
     */
    private static MonoType.Arrow asArrow(Elaborator elab, MonoType type, ElaborationContext ctx, Elaborator.Src src) {
        if (type instanceof MonoType.Arrow a) {
            return a;
        }
        var paramType = elab.state.freshType(ctx.bindingLevel());
        var returnType = elab.state.freshType(ctx.bindingLevel());
        var arrow = new MonoType.Arrow(paramType, returnType);
        elab.emitConstraint(type, arrow, src);
        return arrow;
    }
}
