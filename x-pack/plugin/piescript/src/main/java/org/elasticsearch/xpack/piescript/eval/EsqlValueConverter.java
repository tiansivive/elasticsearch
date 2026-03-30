/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.xpack.esql.action.ColumnInfoImpl;
import org.elasticsearch.xpack.esql.action.EsqlQueryResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Converts an {@link EsqlQueryResponse} into a piescript {@link Value.ListVal}.
 *
 * <p>Each ESQL row becomes a {@link Value.RecordVal} whose field names are the
 * column names from the response metadata, and whose field values are produced
 * by {@link #convertCell(Object)} using Java {@code instanceof} dispatch.
 *
 * <p>ESQL's {@code ResponseValueUtils} already converts {@code Page}/{@code Block}
 * data into standard Java types ({@code Integer}, {@code Long}, {@code Double},
 * {@code String}, {@code Boolean}, {@code null}) before exposing them through
 * {@link EsqlQueryResponse#rows()}. Datetime, IP, version, and geo types all
 * arrive as {@code String}. This converter maps them to {@link Value.KeywordVal}.
 *
 * <p>Multi-value fields ({@code List<?>}) are materialized as {@code ListVal} when
 * the elaborated type says the field is {@code List a} (e.g., from {@code ESQL.top}
 * or {@code ESQL.values}). Otherwise, the v0 simplification applies: only the
 * first element is kept.
 */
public final class EsqlValueConverter {

    private EsqlValueConverter() {}

    /**
     * Convert an entire ESQL query response into a materialized list.
     * Columns in {@code listColumns} are materialized as {@code ListVal};
     * all others use scalar conversion (first element for MV).
     */
    public static Value.ListVal convertResponse(EsqlQueryResponse response, Set<String> listColumns) {
        List<ColumnInfoImpl> columns = response.columns();
        var elements = new ArrayList<Value>();
        for (Iterable<Object> row : response.rows()) {
            elements.add(convertRow(columns, row, listColumns));
        }
        return new Value.ListVal(elements);
    }

    /**
     * Backward-compatible overload — all columns use scalar conversion.
     */
    public static Value.ListVal convertResponse(EsqlQueryResponse response) {
        return convertResponse(response, Set.of());
    }

    static Value.RecordVal convertRow(List<ColumnInfoImpl> columns, Iterable<Object> row, Set<String> listColumns) {
        var fields = new LinkedHashMap<String, Value>();
        int i = 0;
        for (Object cell : row) {
            String colName = columns.get(i).name();
            fields.put(colName, listColumns.contains(colName) ? convertCellAsList(cell) : convertCell(cell));
            i++;
        }
        return new Value.RecordVal(fields);
    }

    /**
     * Convert a single ESQL cell value to a piescript {@link Value} (scalar).
     * For MV fields, takes only the first element (v0 simplification).
     */
    static Value convertCell(Object cell) {
        if (cell == null) return new Value.NullVal();
        if (cell instanceof Integer v) return new Value.DoubleVal(v);
        if (cell instanceof Long v) return new Value.DoubleVal(v);
        if (cell instanceof Double v) return new Value.DoubleVal(v);
        if (cell instanceof Boolean v) return new Value.BooleanVal(v);
        if (cell instanceof String v) return new Value.KeywordVal(v);
        if (cell instanceof List<?> list) {
            return list.isEmpty() ? new Value.NullVal() : convertCell(list.getFirst());
        }
        return new Value.KeywordVal(cell.toString());
    }

    /**
     * Convert a cell to a {@code ListVal} — all elements preserved.
     * Used for columns whose elaborated type is {@code List a}.
     */
    private static Value convertCellAsList(Object cell) {
        if (cell == null) return new Value.ListVal(List.of());
        if (cell instanceof List<?> list) {
            var elements = new ArrayList<Value>(list.size());
            for (Object elem : list) {
                elements.add(convertCell(elem));
            }
            return new Value.ListVal(elements);
        }
        // Scalar value for a List-typed column — wrap in singleton list
        return new Value.ListVal(List.of(convertCell(cell)));
    }
}
