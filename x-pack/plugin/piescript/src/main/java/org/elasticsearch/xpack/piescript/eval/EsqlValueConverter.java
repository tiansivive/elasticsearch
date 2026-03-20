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
 * <p>Multi-value fields ({@code List<?>}) use a v0 simplification: only the
 * first element is kept.
 */
public final class EsqlValueConverter {

    private EsqlValueConverter() {}

    /**
     * Convert an entire ESQL query response into a materialized list.
     * Column names become record field keys; cell values become field values.
     * Column order is preserved via {@link LinkedHashMap}.
     */
    public static Value.ListVal convertResponse(EsqlQueryResponse response) {
        List<ColumnInfoImpl> columns = response.columns();
        var elements = new ArrayList<Value>();
        for (Iterable<Object> row : response.rows()) {
            elements.add(convertRow(columns, row));
        }
        return new Value.ListVal(elements);
    }

    /**
     * Convert a single ESQL row into a {@link Value.RecordVal} by zipping
     * column metadata with cell values.
     */
    static Value.RecordVal convertRow(List<ColumnInfoImpl> columns, Iterable<Object> row) {
        var fields = new LinkedHashMap<String, Value>();
        int i = 0;
        for (Object cell : row) {
            fields.put(columns.get(i).name(), convertCell(cell));
            i++;
        }
        return new Value.RecordVal(fields);
    }

    /**
     * Convert a single ESQL cell value to a piescript {@link Value}.
     * Dispatches on Java runtime type since ESQL's {@code ResponseValueUtils}
     * has already performed the block-to-object conversion.
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
}
