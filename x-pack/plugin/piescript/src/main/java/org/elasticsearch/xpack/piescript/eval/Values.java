/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.xpack.piescript.core.CoreExpr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Static factory methods for concise {@link Value} construction. Intended
 * for {@code import static} use in tests and production code.
 *
 * <p>Example usage:
 * <pre>{@code
 * import static org.elasticsearch.xpack.piescript.eval.Values.*;
 *
 * var v = record(field("name", keyword("alice")), field("age", intVal(30)));
 * var xs = list(intVal(1), intVal(2), intVal(3));
 * }</pre>
 */
public final class Values {

    private Values() {}

    // ──── Primitive value factories ────

    public static Value.IntegerVal intVal(int n) {
        return new Value.IntegerVal(n);
    }

    public static Value.LongVal longVal(long n) {
        return new Value.LongVal(n);
    }

    public static Value.DoubleVal doubleVal(double d) {
        return new Value.DoubleVal(d);
    }

    public static Value.KeywordVal keyword(String s) {
        return new Value.KeywordVal(s);
    }

    public static Value.BooleanVal bool(boolean b) {
        return new Value.BooleanVal(b);
    }

    public static Value.NullVal nullVal() {
        return new Value.NullVal();
    }

    // ──── Record factories ────

    public static Value.RecordVal record(Map<String, Value> fields) {
        return new Value.RecordVal(fields);
    }

    /** Record with deterministic field ordering via {@link Map#entry} varargs. */
    @SafeVarargs
    public static Value.RecordVal record(Map.Entry<String, Value>... entries) {
        var map = new LinkedHashMap<String, Value>();
        for (var e : entries) {
            map.put(e.getKey(), e.getValue());
        }
        return new Value.RecordVal(map);
    }

    /** Record field entry for use with {@link #record(Map.Entry[])}. */
    public static Map.Entry<String, Value> field(String name, Value value) {
        return Map.entry(name, value);
    }

    // ──── List factories ────

    public static Value.ListVal list(Value... elements) {
        return new Value.ListVal(List.of(elements));
    }

    public static Value.ListVal list(List<Value> elements) {
        return new Value.ListVal(elements);
    }

    // ──── Composite value factories ────

    public static Value.ClosureVal closure(CoreExpr body, Value... env) {
        return new Value.ClosureVal(body, env);
    }

    public static Value.BuiltinVal builtin(String name, int arity) {
        return new Value.BuiltinVal(name, arity, List.of());
    }

    public static Value.BuiltinVal builtin(String name, int arity, List<Value> partialArgs) {
        return new Value.BuiltinVal(name, arity, partialArgs);
    }

    public static Value.ChannelVal channelVal(String nodeId, String channelId) {
        return new Value.ChannelVal(nodeId, channelId);
    }
}
