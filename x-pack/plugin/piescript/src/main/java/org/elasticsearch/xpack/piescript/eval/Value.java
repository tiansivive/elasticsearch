/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.xpack.piescript.core.CoreExpr;

import java.util.List;
import java.util.Map;

/**
 * Runtime values produced by the tree-walking evaluator. Each variant
 * corresponds to a type in the piescript type system.
 *
 * <p>String values use {@code String}, not Lucene's {@code BytesRef}.
 * The conversion boundary from the Core IR's {@code LitVal.KeywordLit(BytesRef)}
 * lives in the evaluator's {@code CoreLit} handler (see D-026).
 */
public sealed interface Value {

    record IntegerVal(int value) implements Value {}

    record LongVal(long value) implements Value {}

    record DoubleVal(double value) implements Value {}

    record KeywordVal(String value) implements Value {}

    record BooleanVal(boolean value) implements Value {}

    record NullVal() implements Value {}

    record RecordVal(Map<String, Value> fields) implements Value {}

    /**
     * A closure: a lambda body paired with the captured environment at the
     * point of lambda creation. The environment is indexed by de Bruijn index;
     * when applied, the argument is prepended to produce the body's environment.
     */
    record ClosureVal(CoreExpr body, Value[] env) implements Value {}

    /**
     * A materialized list of values. Produced by evaluating a {@code CoreQuery}
     * (via {@code EsqlQueryAction}), where each element is a {@code RecordVal}
     * corresponding to a row. After transforms ({@code map}, {@code filter}),
     * elements may be any {@code Value} type.
     *
     * <p>This is an eager, fully-materialized representation. The name "List"
     * accurately reflects that these are finite, in-memory collections. "Stream"
     * is reserved for future lazy/Exchange-backed streaming (D-043).
     */
    record ListVal(List<Value> elements) implements Value {}

    /**
     * A built-in function, possibly partially applied. Each application via
     * {@code CoreApp} adds an argument; once the arity is reached the built-in
     * executes. The {@code name} identifies which built-in to dispatch to.
     *
     * <p>Future work: unify built-in and closure application under a single
     * callable protocol so that {@code CoreApp} evaluation does not need to
     * branch on value variant. This would allow built-ins to be represented
     * as regular closures over synthetic Core IR bodies, eliminating the
     * special case in the evaluator.
     */
    record BuiltinVal(String name, int arity, List<Value> partialArgs) implements Value {}

    /**
     * A channel reference — pure metadata identifying a channel on a specific node.
     * The actual {@code SubscribableListener} lives in the per-node
     * {@link ChannelRegistry}, not in the value itself. This allows channels to
     * be serialized and sent across the wire. See D-040, D-041, D-045.
     *
     * @param nodeId    the node that owns this channel (matches a discovery node ID)
     * @param channelId unique channel identifier within that node
     */
    record ChannelVal(String nodeId, String channelId) implements Value {}
}
