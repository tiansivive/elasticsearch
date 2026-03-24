/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.List;

/**
 * Block elaboration: sequences of let-statements and expressions terminated by a final expression.
 */
final class Blocks {

    private Blocks() {}

    static CoreExpr block(Elaborator elab, PiescriptAntlrParser.BlockContext block, ElaborationContext ctx) {
        return blockStmts(elab, block.blockStmt(), 0, block.expr(), ctx, null);
    }

    static CoreExpr checkBlock(Elaborator elab, PiescriptAntlrParser.BlockContext block, MonoType expected, ElaborationContext ctx) {
        return blockStmts(elab, block.blockStmt(), 0, block.expr(), ctx, expected);
    }

    private static CoreExpr blockStmts(
        Elaborator elab,
        List<PiescriptAntlrParser.BlockStmtContext> stmts,
        int index,
        PiescriptAntlrParser.ExprContext finalExpr,
        ElaborationContext ctx,
        MonoType expectedBody
    ) {
        if (index >= stmts.size()) {
            if (expectedBody != null) {
                return elab.check(finalExpr, TypeScheme.mono(expectedBody), ctx, Elaborator.source(finalExpr));
            }
            return elab.elaborate(finalExpr, ctx);
        }

        var stmt = stmts.get(index);
        return switch (stmt) {
            case PiescriptAntlrParser.BlockLetContext let -> {
                var s = Elaborator.source(let);
                var name = let.ident().getText();
                var letCtx = ctx.enterBindingLevel();

                TypeScheme expectedScheme = let.type() != null
                    ? TypeAnnotations.toTypeScheme(elab, ctx, let.type())
                    : TypeScheme.mono(elab.state.freshType(letCtx.bindingLevel()));

                CoreExpr rhs = elab.check(let.expr(), expectedScheme, letCtx, s);

                TypeScheme scheme;
                CoreExpr wrappedRhs;
                if (let.type() != null) {
                    scheme = expectedScheme;
                    wrappedRhs = rhs;
                } else {
                    scheme = elab.generalize(expectedScheme.body(), letCtx.bindingLevel());
                    wrappedRhs = Polymorphism.wrapTypeAbs(rhs, scheme, s.source());
                }
                var bodyCtx = ctx.bind(name, scheme);
                var body = blockStmts(elab, stmts, index + 1, finalExpr, bodyCtx, expectedBody);
                yield new CoreLet(s.source(), name, wrappedRhs.type(), wrappedRhs, body, body.type());
            }
            case PiescriptAntlrParser.BlockExprStmtContext exprStmt -> {
                elab.elaborate(exprStmt.expr(), ctx);
                yield blockStmts(elab, stmts, index + 1, finalExpr, ctx, expectedBody);
            }
            default -> throw Elaborator.error(Elaborator.source(stmt), "unexpected block statement");
        };
    }
}
