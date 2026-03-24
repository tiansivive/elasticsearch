/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import java.util.Map;

/**
 * Static factory methods and constants for concise {@link MonoType} and
 * {@link RowType} construction. Intended for {@code import static} use.
 *
 * <p>Example usage:
 * <pre>{@code
 * import static org.elasticsearch.xpack.piescript.types.Types.*;
 *
 * var fnType = arrow(INTEGER, BOOLEAN);
 * var recType = record(Map.of("x", INTEGER, "y", BOOLEAN));
 * var listOfInt = list(INTEGER);
 * }</pre>
 */
public final class Types {

    private Types() {}

    // ──── Kind constants (F-omega-lite, D-05X) ────

    /** The kind of value types: {@code Integer}, {@code Boolean}, {@code List a}, etc. */
    public static final MonoType TYPE = new MonoType.TCon("Type");

    /** The kind of row types used inside {@code Record} and as row variables. */
    public static final MonoType ROW = new MonoType.TCon("Row");

    // ──── Common type constants ────

    public static final MonoType INTEGER = new MonoType.TCon("Integer");
    public static final MonoType LONG = new MonoType.TCon("Long");
    public static final MonoType DOUBLE = new MonoType.TCon("Double");
    public static final MonoType KEYWORD = new MonoType.TCon("Keyword");
    public static final MonoType BOOLEAN = new MonoType.TCon("Boolean");
    public static final MonoType NULL = new MonoType.TCon("Null");
    public static final MonoType LIST = new MonoType.TCon("List");
    public static final MonoType CHANNEL = new MonoType.TCon("Channel");
    public static final MonoType DATETIME = new MonoType.TCon("DateTime");
    public static final MonoType UNSIGNED_LONG = new MonoType.TCon("UnsignedLong");
    public static final MonoType IP = new MonoType.TCon("Ip");
    public static final MonoType VERSION = new MonoType.TCon("Version");
    public static final MonoType GEO_POINT = new MonoType.TCon("GeoPoint");
    public static final MonoType CARTESIAN_POINT = new MonoType.TCon("CartesianPoint");
    public static final MonoType GEO_SHAPE = new MonoType.TCon("GeoShape");
    public static final MonoType CARTESIAN_SHAPE = new MonoType.TCon("CartesianShape");
    public static final MonoType UNSUPPORTED = new MonoType.TCon("Unsupported");

    // ──── Factory methods ────

    /** Function type: {@code param → result}. */
    public static MonoType.Arrow arrow(MonoType param, MonoType result) {
        return new MonoType.Arrow(param, result);
    }

    /** Record type with closed row (all fields known). */
    public static MonoType.RecordType record(Map<String, MonoType> fields) {
        return new MonoType.RecordType(RowType.closed(fields));
    }

    /** Type application: {@code List τ}. */
    public static MonoType.AppType list(MonoType element) {
        return new MonoType.AppType(LIST, element);
    }

    /** Type application: {@code Channel τ}. */
    public static MonoType.AppType channel(MonoType element) {
        return new MonoType.AppType(CHANNEL, element);
    }

    /** Unsolved type metavariable with kind {@code Type}. */
    public static MonoType.Meta meta(int id, int bindingLevel) {
        return new MonoType.Meta(id, bindingLevel, TYPE);
    }

    /** Unsolved metavariable with explicit kind (a {@link MonoType}). */
    public static MonoType.Meta meta(int id, int bindingLevel, MonoType kind) {
        return new MonoType.Meta(id, bindingLevel, kind);
    }

    /** Rigid (skolemized) type variable with kind {@code Type}. */
    public static MonoType.Rigid rigid(int id) {
        return new MonoType.Rigid(id, TYPE);
    }

    /** Rigid (skolemized) type variable with explicit kind (a {@link MonoType}). */
    public static MonoType.Rigid rigid(int id, MonoType kind) {
        return new MonoType.Rigid(id, kind);
    }
}
