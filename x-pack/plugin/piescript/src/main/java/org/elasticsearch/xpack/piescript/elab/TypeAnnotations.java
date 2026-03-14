/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Translates surface type syntax (CST nodes) into internal type representations
 * ({@link TypeScheme} and {@link MonoType}). Lowercase identifiers become
 * {@link MonoType.Rigid} variables (D-031, D-033); uppercase identifiers are
 * looked up in the known-types table.
 */
final class TypeAnnotations {

    private TypeAnnotations() {}

    /**
     * Translate a surface type annotation into a {@link TypeScheme}. Lowercase
     * identifiers in type position become quantified {@link MonoType.Rigid}
     * variables; uppercase identifiers are looked up in the known-types table.
     */
    static TypeScheme toTypeScheme(Elaborator elab, PiescriptAntlrParser.TypeContext typeCtx) {
        var rigidScope = new LinkedHashMap<String, MonoType.Rigid>();
        var body = translateType(elab, typeCtx, rigidScope);
        if (rigidScope.isEmpty()) {
            return TypeScheme.mono(body);
        }
        var quantified = new LinkedHashMap<Integer, Kind>();
        for (var rigid : rigidScope.values()) {
            quantified.put(rigid.id(), rigid.kind());
        }
        return new TypeScheme(quantified, body);
    }

    /**
     * Translate a surface type into a {@link MonoType}, discarding any implicit
     * quantification. Appropriate for positions where a monotype is expected
     * (e.g., lambda parameter annotations).
     */
    static MonoType toMonoType(Elaborator elab, PiescriptAntlrParser.TypeContext typeCtx) {
        return toTypeScheme(elab, typeCtx).body();
    }

    static MonoType translateType(Elaborator elab, PiescriptAntlrParser.TypeContext typeCtx, Map<String, MonoType.Rigid> rigidScope) {
        return switch (typeCtx) {
            case PiescriptAntlrParser.FunctionTypeContext fn -> new MonoType.Arrow(
                translatePrimary(elab, fn.typePrimary(), rigidScope),
                translateType(elab, fn.type(), rigidScope)
            );
            case PiescriptAntlrParser.TypeAtomContext atom -> translatePrimary(elab, atom.typePrimary(), rigidScope);
            default -> throw Elaborator.error(Elaborator.source(typeCtx), "unexpected type syntax");
        };
    }

    private static MonoType translatePrimary(
        Elaborator elab,
        PiescriptAntlrParser.TypePrimaryContext primary,
        Map<String, MonoType.Rigid> rigidScope
    ) {
        return switch (primary) {
            case PiescriptAntlrParser.TypeConContext tc -> {
                var name = tc.UPPER_IDENT().getText();
                var type = Elaborator.KNOWN_TYPES.get(name);
                if (type == null) {
                    throw Elaborator.error(Elaborator.source(tc), "unknown type: " + name);
                }
                yield type;
            }
            case PiescriptAntlrParser.TypeVarContext tv -> {
                var name = tv.LOWER_IDENT().getText();
                yield rigidScope.computeIfAbsent(name, k -> elab.state.freshRigid(Kind.TYPE));
            }
            case PiescriptAntlrParser.RecordTypeContext rt -> {
                var fields = new LinkedHashMap<String, MonoType>();
                for (var field : rt.rowType().rowField()) {
                    fields.put(field.ident().getText(), translateType(elab, field.type(), rigidScope));
                }
                var rowTailNode = rt.rowType().LOWER_IDENT();
                if (rowTailNode != null) {
                    var rowVarName = rowTailNode.getText();
                    var rowRigid = rigidScope.computeIfAbsent(rowVarName, k -> elab.state.freshRigid(Kind.ROW));
                    yield new MonoType.RecordType(new RowType(fields, Optional.of(new MonoType.Meta(rowRigid.id(), 0, Kind.ROW))));
                }
                yield new MonoType.RecordType(RowType.closed(fields));
            }
            case PiescriptAntlrParser.ParenTypeContext pt -> translateType(elab, pt.type(), rigidScope);
            default -> throw Elaborator.error(Elaborator.source(primary), "unexpected type syntax");
        };
    }
}
