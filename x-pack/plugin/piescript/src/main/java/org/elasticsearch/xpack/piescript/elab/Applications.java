/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;

/**
 * Function application elaboration: normal application ({@code f x}) and
 * pipe application ({@code x |> f}).
 */
final class Applications {

    private Applications() {}

    /**
     * Normal application: {@code f x}. Constrains {@code f} to have type
     * {@code paramType -> resultType} and {@code x} to have type {@code paramType}.
     */
    static CoreExpr application(Elaborator elab, PiescriptAntlrParser.ApplicationContext app, ElaborationContext ctx) {
        var fn = elab.elaborate(app.appExpr(), ctx);
        var arg = elab.elaborate(app.primary(), ctx);
        var src = Elaborator.source(app);
        var paramType = elab.state.freshType(ctx.bindingLevel());
        var resultType = elab.state.freshType(ctx.bindingLevel());
        elab.emitConstraint(fn.type(), new MonoType.Arrow(paramType, resultType), src);
        elab.emitConstraint(arg.type(), paramType, src);
        return new CoreApp(src.source(), fn, arg, resultType);
    }

    /**
     * Pipe application: {@code x |> f}. Desugars to {@code f x}, constraining
     * {@code f} to have type {@code argType -> resultType}.
     */
    static CoreExpr pipe(Elaborator elab, PiescriptAntlrParser.PipeOpContext pipe, ElaborationContext ctx) {
        var arg = elab.elaborate(pipe.pipeExpr(), ctx);
        var fn = elab.elaborate(pipe.orExpr(), ctx);
        var src = Elaborator.source(pipe);
        var resultType = elab.state.freshType(ctx.bindingLevel());
        elab.emitConstraint(fn.type(), new MonoType.Arrow(arg.type(), resultType), src);
        return new CoreApp(src.source(), fn, arg, resultType);
    }
}
