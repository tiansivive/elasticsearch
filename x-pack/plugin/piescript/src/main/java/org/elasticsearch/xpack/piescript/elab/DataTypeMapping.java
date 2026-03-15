/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.esql.core.type.DataType;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.Map;

/**
 * Maps ESQL {@link DataType} values to piescript {@link MonoType} type constructors.
 *
 * <p>The mapping follows ESQL's widening rules: small numerics ({@code SHORT},
 * {@code BYTE}, {@code FLOAT}, {@code HALF_FLOAT}, {@code SCALED_FLOAT}) are
 * mapped to their widened piescript counterparts. Counter types and exotic types
 * (histograms, dense vectors, etc.) map to {@code Unsupported} for v0.
 *
 * <p>{@code TEXT} maps to {@code Keyword} because ESQL loads text fields
 * without analysis and piescript treats them identically to keywords.
 *
 * <p>{@code OBJECT} is handled separately during index resolution by recursing
 * into {@code EsField.properties} to build a nested record type.
 */
public final class DataTypeMapping {

    private DataTypeMapping() {}

    private static final Map<DataType, MonoType> MAPPING = Map.ofEntries(
        Map.entry(DataType.INTEGER, Elaborator.INTEGER),
        Map.entry(DataType.LONG, Elaborator.LONG),
        Map.entry(DataType.DOUBLE, Elaborator.DOUBLE),
        Map.entry(DataType.KEYWORD, Elaborator.KEYWORD),
        Map.entry(DataType.TEXT, Elaborator.KEYWORD),
        Map.entry(DataType.BOOLEAN, Elaborator.BOOLEAN),
        Map.entry(DataType.NULL, Elaborator.NULL_TYPE),
        Map.entry(DataType.DATETIME, Elaborator.DATETIME),
        Map.entry(DataType.DATE_NANOS, Elaborator.DATETIME),
        Map.entry(DataType.UNSIGNED_LONG, Elaborator.UNSIGNED_LONG),
        Map.entry(DataType.IP, Elaborator.IP),
        Map.entry(DataType.VERSION, Elaborator.VERSION),
        Map.entry(DataType.GEO_POINT, Elaborator.GEO_POINT),
        Map.entry(DataType.CARTESIAN_POINT, Elaborator.CARTESIAN_POINT),
        Map.entry(DataType.GEO_SHAPE, Elaborator.GEO_SHAPE),
        Map.entry(DataType.CARTESIAN_SHAPE, Elaborator.CARTESIAN_SHAPE),

        Map.entry(DataType.SHORT, Elaborator.INTEGER),
        Map.entry(DataType.BYTE, Elaborator.INTEGER),
        Map.entry(DataType.FLOAT, Elaborator.DOUBLE),
        Map.entry(DataType.HALF_FLOAT, Elaborator.DOUBLE),
        Map.entry(DataType.SCALED_FLOAT, Elaborator.DOUBLE)
    );

    /**
     * Map an ESQL {@link DataType} to a piescript {@link MonoType}.
     * Returns {@link Elaborator#UNSUPPORTED} for unmapped or exotic types.
     */
    public static MonoType toPiescriptType(DataType dataType) {
        return MAPPING.getOrDefault(dataType, Elaborator.UNSUPPORTED);
    }

    /**
     * Whether the given ESQL data type has a meaningful piescript mapping
     * (i.e. does not map to {@code Unsupported}).
     */
    public static boolean isSupported(DataType dataType) {
        return MAPPING.containsKey(dataType);
    }
}
