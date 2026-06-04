/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.core.Alternative;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreMatch;
import org.elasticsearch.xpack.piescript.core.Pattern;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.elasticsearch.xpack.piescript.elab.Elaborator.BOOLEAN;
import static org.elasticsearch.xpack.piescript.elab.Elaborator.LIST;

/**
 * Elaborates {@code match} expressions and desugars {@code if/then/else} into matches.
 */
final class Matches {

    private Matches() {}

    /**
     * Elaborates an {@code if/then/else} expression by desugaring it to a boolean match.
     * {@code if c then a else b} -> {@code match c | true -> a | false -> b}
     */
    static CoreExpr desugarIf(PiescriptAntlrParser.IfExprContext ctx, ElaborationContext ectx, Elaborator elab) {
        var src = Elaborator.source(ctx);
        var cond = elab.elaborate(ctx.expr(0), ectx);
        elab.emitConstraint(cond.type(), BOOLEAN, src);

        var thenBody = elab.elaborate(ctx.expr(1), ectx);
        var elseBody = elab.elaborate(ctx.expr(2), ectx);

        var resultType = elab.state.freshType(ectx.bindingLevel());
        elab.emitConstraint(resultType, thenBody.type(), src);
        elab.emitConstraint(resultType, elseBody.type(), src);

        var trueArm = new Alternative(new Pattern.LitPat(new LitVal.BooleanLit(true)), thenBody);
        var falseArm = new Alternative(new Pattern.LitPat(new LitVal.BooleanLit(false)), elseBody);

        return new CoreMatch(src.source(), cond, List.of(trueArm, falseArm), resultType);
    }

    /**
     * Elaborates a {@code match} expression in synthesis mode.
     */
    static CoreExpr match(PiescriptAntlrParser.MatchExprContext ctx, ElaborationContext ectx, Elaborator elab) {
        var src = Elaborator.source(ctx);
        var scrutinee = elab.elaborate(ctx.expr(), ectx);
        var resultType = elab.state.freshType(ectx.bindingLevel());

        var arms = elaborateArms(ctx.alternative(), scrutinee.type(), resultType, ectx, elab);
        return new CoreMatch(src.source(), scrutinee, arms, resultType);
    }

    /**
     * Elaborates a {@code match} expression in checking mode.
     */
    static CoreExpr checkMatch(PiescriptAntlrParser.MatchExprContext ctx, MonoType expectedType, ElaborationContext ectx, Elaborator elab) {
        var src = Elaborator.source(ctx);
        var scrutinee = elab.elaborate(ctx.expr(), ectx);

        var arms = elaborateArms(ctx.alternative(), scrutinee.type(), expectedType, ectx, elab);
        return new CoreMatch(src.source(), scrutinee, arms, expectedType);
    }

    private static List<Alternative> elaborateArms(
        List<PiescriptAntlrParser.AlternativeContext> armCtxs,
        MonoType scrutineeType,
        MonoType expectedResultType,
        ElaborationContext ectx,
        Elaborator elab
    ) {
        var arms = new ArrayList<Alternative>(armCtxs.size());
        for (var armCtx : armCtxs) {
            var src = Elaborator.source(armCtx);
            var patResult = inferPattern(armCtx.pattern(), ectx.bindingLevel(), elab.state, elab);

            // The pattern type must unify with the scrutinee type
            elab.emitConstraint(scrutineeType, patResult.patType, src);

            // Extend the environment with pattern variables
            var armEnv = ectx;
            for (var binding : patResult.bindings) {
                armEnv = armEnv.bind(binding.name, new org.elasticsearch.xpack.piescript.types.TypeScheme(Map.of(), binding.type));
            }

            // Check the body against the expected result type
            var expectedScheme = new TypeScheme(Map.of(), expectedResultType);
            var body = elab.check(armCtx.expr(), expectedScheme, armEnv, src);
            arms.add(new Alternative(patResult.pat, body));
        }
        return arms;
    }

    record PatternResult(Pattern pat, MonoType patType, List<Binding> bindings) {}

    record Binding(String name, MonoType type) {}

    static PatternResult inferPattern(PiescriptAntlrParser.PatternContext ctx, int bindingLevel, ElaborationState state, Elaborator elab) {
        var bindings = new ArrayList<Binding>();
        var result = inferPatternRecursive(ctx, bindingLevel, state, elab, bindings);
        return new PatternResult(result.pat, result.type, bindings);
    }

    private record PatAndType(Pattern pat, MonoType type) {}

    private static PatAndType inferPatternRecursive(
        PiescriptAntlrParser.PatternContext ctx,
        int bindingLevel,
        ElaborationState state,
        Elaborator elab,
        List<Binding> bindings
    ) {
        var src = Elaborator.source(ctx);
        return switch (ctx) {
            case PiescriptAntlrParser.WildcardPatternContext w -> {
                var meta = state.freshType(bindingLevel);
                yield new PatAndType(new Pattern.WildcardPat(), meta);
            }
            case PiescriptAntlrParser.LitPatternContext l -> {
                var litExpr = elab.elaborateLiteral(l.literal());
                var litVal = ((org.elasticsearch.xpack.piescript.core.CoreLit) litExpr).value();
                yield new PatAndType(new Pattern.LitPat(litVal), litExpr.type());
            }
            case PiescriptAntlrParser.VarPatternContext v -> {
                var name = v.LOWER_IDENT().getText();
                var meta = state.freshType(bindingLevel);
                bindings.add(new Binding(name, meta));
                yield new PatAndType(new Pattern.VarPat(name, meta), meta);
            }
            case PiescriptAntlrParser.EmptyRecordPatternContext e -> {
                var row = RowType.closed(Map.of());
                yield new PatAndType(new Pattern.RecordPat(Map.of(), false, null), new MonoType.RecordType(row));
            }
            case PiescriptAntlrParser.RecordPatternContext r -> {
                // Record patterns bind fields in alphabetical order of field names
                // We use a TreeMap to sort the fields, then process them to collect bindings
                var sortedFields = new TreeMap<String, PiescriptAntlrParser.RecordPatFieldContext>();
                for (var fieldCtx : r.recordPatField()) {
                    String name = fieldCtx.LOWER_IDENT() != null ? fieldCtx.LOWER_IDENT().getText() : fieldCtx.ident().getText();
                    if (sortedFields.put(name, fieldCtx) != null) {
                        throw Elaborator.error(src, "duplicate field in record pattern: " + name);
                    }
                }

                var patFields = new TreeMap<String, Pattern>();
                var typeFields = new TreeMap<String, MonoType>();

                for (var entry : sortedFields.entrySet()) {
                    String name = entry.getKey();
                    var fieldCtx = entry.getValue();

                    if (fieldCtx.LOWER_IDENT() != null) {
                        // Shorthand: `{ name }`
                        var meta = state.freshType(bindingLevel);
                        bindings.add(new Binding(name, meta));
                        patFields.put(name, new Pattern.VarPat(name, meta));
                        typeFields.put(name, meta);
                    } else {
                        // Explicit: `{ name: pat }`
                        var fieldResult = inferPatternRecursive(fieldCtx.pattern(), bindingLevel, state, elab, bindings);
                        patFields.put(name, fieldResult.pat);
                        typeFields.put(name, fieldResult.type);
                    }
                }

                boolean hasTail = r.LOWER_IDENT() != null;
                String tailName = hasTail ? r.LOWER_IDENT().getText() : null;
                MonoType rowTail;

                if (hasTail) {
                    var tailRowMeta = state.freshRow(bindingLevel);
                    rowTail = tailRowMeta;
                    bindings.add(new Binding(tailName, new MonoType.RecordType(tailRowMeta)));
                } else {
                    rowTail = state.freshRow(bindingLevel); // Open row by default
                }

                var rowType = new RowType(typeFields, java.util.Optional.of(rowTail));
                yield new PatAndType(new Pattern.RecordPat(patFields, hasTail, tailName), new MonoType.RecordType(rowType));
            }
            case PiescriptAntlrParser.EmptyListPatternContext e -> {
                var elemMeta = state.freshType(bindingLevel);
                yield new PatAndType(new Pattern.ListPat(List.of()), new MonoType.AppType(LIST, elemMeta));
            }
            case PiescriptAntlrParser.ExactListPatternContext l -> {
                var elemMeta = state.freshType(bindingLevel);
                var elements = new ArrayList<Pattern>();
                for (var elemCtx : l.pattern()) {
                    var elemResult = inferPatternRecursive(elemCtx, bindingLevel, state, elab, bindings);
                    elab.emitConstraint(elemMeta, elemResult.type, src);
                    elements.add(elemResult.pat);
                }
                yield new PatAndType(new Pattern.ListPat(elements), new MonoType.AppType(LIST, elemMeta));
            }
            case PiescriptAntlrParser.ConsListPatternContext c -> {
                var elemMeta = state.freshType(bindingLevel);
                var headResult = inferPatternRecursive(c.pattern(0), bindingLevel, state, elab, bindings);
                elab.emitConstraint(elemMeta, headResult.type, src);

                var tailResult = inferPatternRecursive(c.pattern(1), bindingLevel, state, elab, bindings);
                elab.emitConstraint(new MonoType.AppType(LIST, elemMeta), tailResult.type, src);

                yield new PatAndType(new Pattern.ConsListPat(headResult.pat, tailResult.pat), new MonoType.AppType(LIST, elemMeta));
            }
            default -> throw new IllegalArgumentException("Unknown pattern context: " + ctx.getClass());
        };
    }
}
