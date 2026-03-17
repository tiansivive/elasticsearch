/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.List;

/**
 * Let-binding elaboration: top-level bindings and {@code let ... in ...} expressions.
 *
 * <p>Applies the <b>value restriction</b> (D-046): only syntactic values (lambdas,
 * literals, variables, records of values) are generalized. Side-effecting expressions
 * like {@code spawn}, {@code spawn!}, {@code send}, function application, and queries
 * keep their monomorphic type, preventing unsound polymorphism over mutable channels.
 */
final class Let {

    private Let() {}

    static CoreExpr topBindings(
        Elaborator elab,
        List<PiescriptAntlrParser.TopBindingContext> bindings,
        int index,
        PiescriptAntlrParser.ExprContext finalExpr,
        ElaborationContext ctx
    ) {
        if (index >= bindings.size()) {
            return elab.elaborate(finalExpr, ctx);
        }

        var binding = bindings.get(index);
        var src = Elaborator.source(binding);
        var name = binding.ident().getText();
        var letCtx = ctx.enterBindingLevel();

        TypeScheme expectedScheme = binding.type() != null
            ? TypeAnnotations.toTypeScheme(elab, binding.type())
            : TypeScheme.mono(elab.state.freshType(letCtx.bindingLevel()));

        CoreExpr rhs = elab.check(binding.expr(), expectedScheme, letCtx, src);

        TypeScheme scheme;
        CoreExpr wrappedRhs;
        if (binding.type() != null) {
            scheme = expectedScheme;
            wrappedRhs = rhs;
        } else if (isSyntacticValue(rhs)) {
            scheme = elab.generalize(expectedScheme.body(), letCtx.bindingLevel());
            wrappedRhs = Polymorphism.wrapTypeAbs(rhs, scheme, src.source());
        } else {
            elab.solveConstraints();
            scheme = TypeScheme.mono(expectedScheme.body());
            wrappedRhs = rhs;
        }
        var bodyCtx = ctx.bind(name, scheme);
        var body = topBindings(elab, bindings, index + 1, finalExpr, bodyCtx);

        return new CoreLet(src.source(), name, wrappedRhs.type(), wrappedRhs, body, body.type());
    }

    static CoreExpr let(Elaborator elab, PiescriptAntlrParser.LetExprContext let, ElaborationContext ctx) {
        return letImpl(elab, let, ctx, null);
    }

    static CoreExpr checkLet(Elaborator elab, PiescriptAntlrParser.LetExprContext let, MonoType expected, ElaborationContext ctx) {
        return letImpl(elab, let, ctx, expected);
    }

    private static CoreExpr letImpl(
        Elaborator elab,
        PiescriptAntlrParser.LetExprContext let,
        ElaborationContext ctx,
        MonoType expectedBody
    ) {
        var src = Elaborator.source(let);
        var name = let.ident().getText();
        var letCtx = ctx.enterBindingLevel();

        TypeScheme expectedScheme = let.type() != null
            ? TypeAnnotations.toTypeScheme(elab, let.type())
            : TypeScheme.mono(elab.state.freshType(letCtx.bindingLevel()));

        CoreExpr rhs = elab.check(let.expr(0), expectedScheme, letCtx, src);

        TypeScheme scheme;
        CoreExpr wrappedRhs;
        if (let.type() != null) {
            scheme = expectedScheme;
            wrappedRhs = rhs;
        } else if (isSyntacticValue(rhs)) {
            scheme = elab.generalize(expectedScheme.body(), letCtx.bindingLevel());
            wrappedRhs = Polymorphism.wrapTypeAbs(rhs, scheme, src.source());
        } else {
            elab.solveConstraints();
            scheme = TypeScheme.mono(expectedScheme.body());
            wrappedRhs = rhs;
        }
        var bodyCtx = ctx.bind(name, scheme);
        CoreExpr body;
        if (expectedBody != null) {
            body = elab.check(let.expr(1), TypeScheme.mono(expectedBody), bodyCtx, src);
        } else {
            body = elab.elaborate(let.expr(1), bodyCtx);
        }

        return new CoreLet(src.source(), name, wrappedRhs.type(), wrappedRhs, body, body.type());
    }

    /**
     * Value restriction (D-046): only syntactic values are safe to generalize.
     * Non-values (applications, side-effecting primitives) keep monomorphic types.
     */
    private static boolean isSyntacticValue(CoreExpr expr) {
        return switch (expr) {
            case CoreLit ignored -> true;
            case CoreLam ignored -> true;
            case CoreVar ignored -> true;
            case CoreFree ignored -> true;
            case CoreRecord rec -> rec.children().stream().allMatch(Let::isSyntacticValue);
            case CoreTypeAbs ta -> isSyntacticValue(ta.body());
            default -> false;
        };
    }
}
