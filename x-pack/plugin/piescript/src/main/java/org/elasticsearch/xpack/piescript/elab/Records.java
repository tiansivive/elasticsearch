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
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Record elaboration: literals, projection, accessor sugar, update, and update sugar.
 */
final class Records {

    private Records() {}

    /**
     * Check a record literal against an expected type. Decomposes the expected
     * type into a {@link MonoType.RecordType} so that known field types flow
     * inward to each field expression via {@code check}.
     */
    static CoreExpr check(Elaborator elab, PiescriptAntlrParser.RecordLiteralContext r, MonoType expected, ElaborationContext ctx) {
        var s = Elaborator.source(r);
        var resolved = elab.state.zonkOrKeep(expected);

        Map<String, MonoType> expectedFields;
        if (resolved instanceof MonoType.RecordType(var row) && row instanceof RowType rowType) {
            expectedFields = elab.state.resolveRow(rowType).fields();
        } else if (resolved instanceof MonoType.Meta) {
            expectedFields = Map.of();
        } else {
            return record(elab, r, ctx);
        }

        var labels = new ArrayList<String>();
        var values = new ArrayList<CoreExpr>();
        var fieldTypes = new LinkedHashMap<String, MonoType>();

        for (var field : r.recordField()) {
            var label = field.ident().getText();
            if (fieldTypes.containsKey(label)) {
                throw Elaborator.error(Elaborator.source(field), "duplicate field: " + label);
            }
            var knownFieldType = expectedFields.get(label);
            CoreExpr value;
            if (knownFieldType != null) {
                value = elab.check(field.expr(), TypeScheme.mono(knownFieldType), ctx, Elaborator.source(field));
            } else {
                value = elab.elaborate(field.expr(), ctx);
            }
            labels.add(label);
            values.add(value);
            fieldTypes.put(label, value.type());
        }

        var row = RowType.closed(fieldTypes);
        var recordType = new MonoType.RecordType(row);
        elab.emitConstraint(recordType, expected, s);
        return new CoreRecord(s.source(), labels, values, recordType);
    }

    static CoreExpr record(Elaborator elab, PiescriptAntlrParser.RecordLiteralContext r, ElaborationContext ctx) {
        var s = Elaborator.source(r);
        var labels = new ArrayList<String>();
        var values = new ArrayList<CoreExpr>();
        var fieldTypes = new LinkedHashMap<String, MonoType>();

        for (var field : r.recordField()) {
            var label = field.ident().getText();
            if (fieldTypes.containsKey(label)) {
                throw Elaborator.error(Elaborator.source(field), "duplicate field: " + label);
            }
            var value = elab.elaborate(field.expr(), ctx);
            labels.add(label);
            values.add(value);
            fieldTypes.put(label, value.type());
        }

        var row = RowType.closed(fieldTypes);
        return new CoreRecord(s.source(), labels, values, new MonoType.RecordType(row));
    }

    static CoreExpr projection(Elaborator elab, PiescriptAntlrParser.ProjectionContext p, ElaborationContext ctx) {
        var s = Elaborator.source(p);
        var label = p.ident().getText();

        // Qualified builtin name: Namespace.name (D-050)
        if (p.primary() instanceof PiescriptAntlrParser.VariableContext varCtx && varCtx.ident().UPPER_IDENT() != null) {
            var namespaceName = varCtx.ident().getText();
            if (ctx.lookup(namespaceName).isEmpty()) {
                var qualifiedName = namespaceName + "." + label;
                var moduleLookup = ctx.lookupModule(qualifiedName);
                if (moduleLookup.isPresent()) {
                    var scheme = moduleLookup.get();
                    if (scheme.quantified().isEmpty()) {
                        return new CoreFree(s.source(), qualifiedName, scheme.body());
                    }
                    return Polymorphism.instantiateAndWrap(
                        elab,
                        type -> new CoreFree(s.source(), qualifiedName, type),
                        scheme,
                        ctx,
                        s.source()
                    );
                }
            }
        }

        var expr = elab.elaborate(p.primary(), ctx);
        var fieldType = elab.state.freshType(ctx.bindingLevel());
        var rowTail = elab.state.freshRow(ctx.bindingLevel());
        var expected = new MonoType.RecordType(RowType.open(Map.of(label, fieldType), rowTail));
        elab.emitConstraint(expr.type(), expected, s);
        return new CoreProject(s.source(), expr, label, fieldType);
    }

    /**
     * {@code .field} desugars to {@code fn $acc -> $acc.field}.
     */
    static CoreExpr accessor(Elaborator elab, PiescriptAntlrParser.AccessorContext a, ElaborationContext ctx) {
        var s = Elaborator.source(a);
        var label = a.ident().getText();
        var resultType = elab.state.freshType(ctx.bindingLevel());
        var rowTail = elab.state.freshRow(ctx.bindingLevel());
        var paramType = new MonoType.RecordType(RowType.open(Map.of(label, resultType), rowTail));

        var varExpr = new CoreVar(s.source(), 0, "$acc", paramType);
        var project = new CoreProject(s.source(), varExpr, label, resultType);
        return new CoreLam(s.source(), "$acc", paramType, project, new MonoType.Arrow(paramType, resultType));
    }

    static CoreExpr update(Elaborator elab, PiescriptAntlrParser.RecordUpdateExprContext u, ElaborationContext ctx) {
        var s = Elaborator.source(u);
        var baseExpr = elab.elaborate(u.expr(), ctx);

        var rowTail = elab.state.freshRow(ctx.bindingLevel());
        var expectedBase = new MonoType.RecordType(RowType.open(Map.of(), rowTail));
        elab.emitConstraint(baseExpr.type(), expectedBase, s);

        LinkedHashMap<String, MonoType> resultFields;
        Optional<MonoType> resultRowVar;
        var baseType = elab.state.zonkOrKeep(baseExpr.type());
        if (baseType instanceof MonoType.RecordType(var row) && row instanceof RowType rowType) {
            var resolved = elab.state.resolveRow(rowType);
            resultFields = new LinkedHashMap<>(resolved.fields());
            resultRowVar = resolved.tail();
        } else {
            resultFields = new LinkedHashMap<>();
            resultRowVar = Optional.of(rowTail);
        }

        var labels = new ArrayList<String>();
        var updateValues = new ArrayList<CoreExpr>();

        for (var upd : u.recordUpdate()) {
            var label = upd.ident().getText();
            var value = elab.elaborate(upd.expr(), ctx);
            if (resultFields.containsKey(label)) {
                elab.emitConstraint(value.type(), resultFields.get(label), Elaborator.source(upd));
            }
            labels.add(label);
            updateValues.add(value);
            resultFields.put(label, value.type());
        }

        var resultType = new MonoType.RecordType(new RowType(resultFields, resultRowVar));
        var children = new ArrayList<CoreExpr>();
        children.add(baseExpr);
        children.addAll(updateValues);
        return new CoreUpdate(s.source(), labels, children, resultType);
    }

    /**
     * {@code { _ | field = expr }} desugars to {@code fn $upd -> { $upd | field = expr }}.
     */
    static CoreExpr updateSugar(Elaborator elab, PiescriptAntlrParser.UpdateSugarContext u, ElaborationContext ctx) {
        var s = Elaborator.source(u);
        var rowTail = elab.state.freshRow(ctx.bindingLevel());
        var paramFields = new LinkedHashMap<String, MonoType>();
        for (var upd : u.recordUpdate()) {
            paramFields.put(upd.ident().getText(), elab.state.freshType(ctx.bindingLevel()));
        }
        var paramType = new MonoType.RecordType(RowType.open(paramFields, rowTail));
        var innerCtx = ctx.bind("$upd", TypeScheme.mono(paramType));

        var baseVar = new CoreVar(s.source(), 0, "$upd", paramType);
        var labels = new ArrayList<String>();
        var updateValues = new ArrayList<CoreExpr>();
        var resultFields = new LinkedHashMap<>(paramFields);

        for (var upd : u.recordUpdate()) {
            var label = upd.ident().getText();
            var value = elab.elaborate(upd.expr(), innerCtx);
            labels.add(label);
            updateValues.add(value);
            resultFields.put(label, value.type());
        }

        var resultType = new MonoType.RecordType(new RowType(resultFields, Optional.of(rowTail)));
        var children = new ArrayList<CoreExpr>();
        children.add(baseVar);
        children.addAll(updateValues);
        var updateExpr = new CoreUpdate(s.source(), labels, children, resultType);
        return new CoreLam(s.source(), "$upd", paramType, updateExpr, new MonoType.Arrow(paramType, resultType));
    }
}
