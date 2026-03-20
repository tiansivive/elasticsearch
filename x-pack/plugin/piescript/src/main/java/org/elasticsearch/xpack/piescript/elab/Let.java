/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.esql.core.type.EsField;
import org.elasticsearch.xpack.esql.core.type.InvalidMappedField;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.LinkedHashMap;
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
        return switch (binding) {
            case PiescriptAntlrParser.TopLetContext topLet -> topLet(elab, topLet, bindings, index, finalExpr, ctx);
            case PiescriptAntlrParser.TopUseContext topUse -> topUse(elab, topUse, bindings, index, finalExpr, ctx);
            default -> {
                var src = Elaborator.source(binding);
                throw new ElaborationException(src.line(), src.column(), "unexpected top-level binding form");
            }
        };
    }

    private static CoreExpr topLet(
        Elaborator elab,
        PiescriptAntlrParser.TopLetContext binding,
        List<PiescriptAntlrParser.TopBindingContext> bindings,
        int index,
        PiescriptAntlrParser.ExprContext finalExpr,
        ElaborationContext ctx
    ) {
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

    /**
     * Elaborate {@code use "index-name" as idx} into a {@link CoreLet} binding an
     * {@link LitVal.IndexLit} with type {@code Index r} where {@code r} is the
     * concrete row type from the resolved field caps.
     *
     * @throws ElaborationException if no resolved mapping was found (pre-pass not run
     *                              or index does not exist)
     */
    private static CoreExpr topUse(
        Elaborator elab,
        PiescriptAntlrParser.TopUseContext binding,
        List<PiescriptAntlrParser.TopBindingContext> bindings,
        int index,
        PiescriptAntlrParser.ExprContext finalExpr,
        ElaborationContext ctx
    ) {
        var src = Elaborator.source(binding);
        var name = binding.LOWER_IDENT().getText();
        var rawIndexName = Elaborator.unquote(binding.QUOTED_STRING().getText());

        var mapping = elab.state.resolvedMapping(rawIndexName);
        if (mapping == null) {
            throw Elaborator.error(src, "no resolved mapping for index [" + rawIndexName + "]");
        }

        var rowFields = new LinkedHashMap<String, MonoType>();
        var fieldTypes = new LinkedHashMap<String, String>();
        for (var entry : mapping.fieldMap().entrySet()) {
            String fieldName = entry.getKey();
            EsField esField = entry.getValue();
            if (fieldName.startsWith("_")) {
                continue;
            }
            if (esField instanceof InvalidMappedField conflict) {
                rowFields.put(fieldName, Elaborator.UNSUPPORTED);
                elab.state.addDiagnostic("field [" + fieldName + "] in [" + rawIndexName + "]: " + conflict.errorMessage());
                continue;
            }
            MonoType piescriptType = DataTypeMapping.toPiescriptType(esField.getDataType());
            rowFields.put(fieldName, piescriptType);
            fieldTypes.put(fieldName, esField.getDataType().name().toLowerCase());
        }

        var rowType = RowType.closed(rowFields);
        var indexType = new MonoType.AppType(Elaborator.INDEX, new MonoType.RecordType(rowType));

        var litVal = new LitVal.IndexLit(rawIndexName, fieldTypes);
        var rhs = new CoreLit(src.source(), litVal, indexType);

        var scheme = TypeScheme.mono(indexType);
        var bodyCtx = ctx.bind(name, scheme);
        var body = topBindings(elab, bindings, index + 1, finalExpr, bodyCtx);

        return new CoreLet(src.source(), name, indexType, rhs, body, body.type());
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
