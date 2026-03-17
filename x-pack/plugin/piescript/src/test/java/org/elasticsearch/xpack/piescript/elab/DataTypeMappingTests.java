/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.esql.core.type.DataType;
import org.elasticsearch.xpack.piescript.types.MonoType;

public class DataTypeMappingTests extends ESTestCase {

    public void testCoreNumericTypes() {
        assertEquals(Elaborator.INTEGER, DataTypeMapping.toPiescriptType(DataType.INTEGER));
        assertEquals(Elaborator.LONG, DataTypeMapping.toPiescriptType(DataType.LONG));
        assertEquals(Elaborator.DOUBLE, DataTypeMapping.toPiescriptType(DataType.DOUBLE));
        assertEquals(Elaborator.UNSIGNED_LONG, DataTypeMapping.toPiescriptType(DataType.UNSIGNED_LONG));
    }

    public void testWidenedNumerics() {
        assertEquals(Elaborator.INTEGER, DataTypeMapping.toPiescriptType(DataType.SHORT));
        assertEquals(Elaborator.INTEGER, DataTypeMapping.toPiescriptType(DataType.BYTE));
        assertEquals(Elaborator.DOUBLE, DataTypeMapping.toPiescriptType(DataType.FLOAT));
        assertEquals(Elaborator.DOUBLE, DataTypeMapping.toPiescriptType(DataType.HALF_FLOAT));
        assertEquals(Elaborator.DOUBLE, DataTypeMapping.toPiescriptType(DataType.SCALED_FLOAT));
    }

    public void testStringTypes() {
        assertEquals(Elaborator.KEYWORD, DataTypeMapping.toPiescriptType(DataType.KEYWORD));
        assertEquals(Elaborator.KEYWORD, DataTypeMapping.toPiescriptType(DataType.TEXT));
    }

    public void testDateTypes() {
        assertEquals(Elaborator.DATETIME, DataTypeMapping.toPiescriptType(DataType.DATETIME));
        assertEquals(Elaborator.DATETIME, DataTypeMapping.toPiescriptType(DataType.DATE_NANOS));
    }

    public void testMiscTypes() {
        assertEquals(Elaborator.BOOLEAN, DataTypeMapping.toPiescriptType(DataType.BOOLEAN));
        assertEquals(Elaborator.NULL_TYPE, DataTypeMapping.toPiescriptType(DataType.NULL));
        assertEquals(Elaborator.IP, DataTypeMapping.toPiescriptType(DataType.IP));
        assertEquals(Elaborator.VERSION, DataTypeMapping.toPiescriptType(DataType.VERSION));
    }

    public void testGeoTypes() {
        assertEquals(Elaborator.GEO_POINT, DataTypeMapping.toPiescriptType(DataType.GEO_POINT));
        assertEquals(Elaborator.CARTESIAN_POINT, DataTypeMapping.toPiescriptType(DataType.CARTESIAN_POINT));
        assertEquals(Elaborator.GEO_SHAPE, DataTypeMapping.toPiescriptType(DataType.GEO_SHAPE));
        assertEquals(Elaborator.CARTESIAN_SHAPE, DataTypeMapping.toPiescriptType(DataType.CARTESIAN_SHAPE));
    }

    public void testUnsupportedFallback() {
        assertEquals(Elaborator.UNSUPPORTED, DataTypeMapping.toPiescriptType(DataType.UNSUPPORTED));
        assertEquals(Elaborator.UNSUPPORTED, DataTypeMapping.toPiescriptType(DataType.COUNTER_LONG));
        assertEquals(Elaborator.UNSUPPORTED, DataTypeMapping.toPiescriptType(DataType.OBJECT));
    }

    public void testIsSupportedTrue() {
        assertTrue(DataTypeMapping.isSupported(DataType.INTEGER));
        assertTrue(DataTypeMapping.isSupported(DataType.KEYWORD));
        assertTrue(DataTypeMapping.isSupported(DataType.BOOLEAN));
    }

    public void testIsSupportedFalse() {
        assertFalse(DataTypeMapping.isSupported(DataType.UNSUPPORTED));
        assertFalse(DataTypeMapping.isSupported(DataType.COUNTER_LONG));
        assertFalse(DataTypeMapping.isSupported(DataType.OBJECT));
    }

    public void testListTypeConstructorExists() {
        assertEquals("List", ((MonoType.TCon) Elaborator.LIST).name());
    }
}
