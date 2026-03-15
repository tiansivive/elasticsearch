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
import org.elasticsearch.xpack.piescript.core.CoreQuery;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Query expression elaboration: converts a {@code QueryExpr} CST node into a
 * typed {@link CoreQuery} using resolved index mappings.
 *
 * <p>The index pattern is extracted from the ESQL body text via
 * {@link EsqlBodyParser}. This duplicates the parse done in the pre-pass
 * ({@link IndexResolutionPrePass#collectQueries}), which is a known trade-off
 * of the v0 "opaque {@code ESQL_BODY} token" approach. When the ANTLR grammar
 * is improved to structurally capture the {@code FROM} clause, the elaborator
 * will read the index pattern directly from the CST and this double-parse goes
 * away.
 *
 * <p>Field mapping conversion handles three cases:
 * <ol>
 *   <li><b>Normal fields</b> — mapped via {@link DataTypeMapping#toPiescriptType}</li>
 *   <li><b>{@link InvalidMappedField}</b> — field has conflicting types across
 *       indices matching the pattern. Mapped to {@code Unsupported} and a
 *       diagnostic is recorded on {@link ElaborationState} (T2.5).</li>
 *   <li><b>Meta fields</b> ({@code _id}, {@code _source}, etc.) — skipped;
 *       accessible via ESQL's {@code METADATA} clause if needed.</li>
 * </ol>
 */
final class Queries {

    private Queries() {}

    /**
     * Elaborate a {@code QueryExpr} into a typed {@link CoreQuery}.
     *
     * @return a {@code CoreQuery} with type {@code Stream { field1: T1, field2: T2, ... }}
     * @throws ElaborationException if no resolved mapping is found for the index pattern
     */
    static CoreExpr query(Elaborator elab, PiescriptAntlrParser.QueryExprContext q, ElaborationContext ctx) {
        var src = Elaborator.source(q);
        String esqlBody = q.ESQL_BODY().getText();
        var parsed = EsqlBodyParser.parse(esqlBody);

        var mapping = elab.state.resolvedMapping(parsed.indexPattern());
        if (mapping == null) {
            throw Elaborator.error(src, "no resolved mapping for index pattern [" + parsed.indexPattern() + "]");
        }

        var rowFields = buildRowFields(mapping.fieldMap(), parsed.indexPattern(), elab, src);
        var rowType = RowType.closed(rowFields);
        var streamType = new MonoType.AppType(Elaborator.STREAM, new MonoType.RecordType(rowType));

        return new CoreQuery(src.source(), parsed.fullEsqlQuery(), parsed.indexPattern(), streamType);
    }

    /**
     * Convert an ESQL field map to a piescript row field map.
     * Handles {@link InvalidMappedField} conflicts (T2.5) by mapping them to
     * {@code Unsupported} and recording diagnostics.
     */
    private static Map<String, MonoType> buildRowFields(
        Map<String, EsField> fieldMap,
        String indexPattern,
        Elaborator elab,
        Elaborator.Src src
    ) {
        var fields = new LinkedHashMap<String, MonoType>();
        for (var entry : fieldMap.entrySet()) {
            String fieldName = entry.getKey();
            EsField esField = entry.getValue();

            if (fieldName.startsWith("_")) {
                continue;
            }

            if (esField instanceof InvalidMappedField conflict) {
                fields.put(fieldName, Elaborator.UNSUPPORTED);
                elab.state.addDiagnostic("field [" + fieldName + "] in [" + indexPattern + "]: " + conflict.errorMessage());
                continue;
            }

            MonoType fieldType = DataTypeMapping.toPiescriptType(esField.getDataType());
            fields.put(fieldName, fieldType);
        }
        return fields;
    }
}
