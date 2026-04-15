/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.operator.exchange.ExchangeSink;
import org.elasticsearch.compute.operator.exchange.ExchangeSource;
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

    /**
     * Loop step marker produced by evaluating {@code repeat expr}.
     * Only loop evaluation should consume this value; reaching any other
     * consumer indicates a type-checking bug.
     */
    record RepeatVal(Value newState) implements Value {}

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

    /**
     * An index reference carrying name, UUID, and field type metadata.
     * Serializable — travels in closures sent to data nodes.
     * Created by {@code use} declarations after index resolution pre-pass.
     *
     * @param fieldTypes maps field names to ES type descriptors (e.g., "keyword", "long")
     */
    record IndexVal(String name, String uuid, Map<String, String> fieldTypes) implements Value {}

    /**
     * Opaque Lucene searcher state. Non-serializable, node-local.
     * Created by {@code Shard.open}, consumed by {@code Shard.consume}.
     * The {@link SearcherState} holds the engine searcher, compiled weight,
     * per-segment scorers, and mutable consumption cursor.
     */
    record SearcherVal(SearcherState state) implements Value {}

    /**
     * Opaque Lucene document reference. Non-serializable, tied to its Searcher.
     * Created by {@code Shard.consume} for each matching document.
     *
     * @param leafContext the segment containing this document
     * @param docId the segment-local document ID
     * @param searcherState back-reference to the owning searcher (for field reading)
     */
    record DocRefVal(org.apache.lucene.index.LeafReaderContext leafContext, int docId, SearcherState searcherState) implements Value {}

    /**
     * Opaque shard write context. Non-serializable, node-local.
     * Created by {@code Shard.writer} on a primary shard, consumed by
     * {@code Shard.write} and {@code Shard.refresh}. The {@link WriterState}
     * holds the {@code IndexShard} and {@code IndexService} references.
     * See D-051.
     */
    record WriterVal(WriterState state) implements Value {}

    /**
     * A symbolic ESQL expression fragment, built incrementally during evaluation
     * via NbE-style partial evaluation (D-052). When the evaluator encounters
     * operations on symbolic values (projections on the symbolic row, primops
     * with symbolic operands), it compiles the result to an ESQL string and
     * wraps it in a new {@code Symbol}. Non-serializable, ephemeral.
     *
     * <p>At the {@code query ... ;} boundary ({@code CoreQueryExec}), the
     * final {@code Symbol} carries the complete ESQL pipeline string.
     */
    record Symbol(String esql) implements Value {}

    /**
     * A columnar page from the compute engine. Non-serializable, node-local.
     * Created by {@code Shard.stream}, consumed by {@code Page.toList},
     * {@code Page.count}, and {@code Exchange.addPage}.
     *
     * @param page the compute engine Page (columnar blocks)
     * @param columnNames ordered field names corresponding to each block in the page
     */
    record PageVal(Page page, List<String> columnNames) implements Value {}

    /**
     * A serializable exchange descriptor. Carries the exchange ID, column names,
     * and buffer size — pure data that can travel in closures across nodes.
     * The actual sink/source infrastructure is instantiated locally by
     * {@code Exchange.sink} and {@code Exchange.connect}. See D-054.
     *
     * @param exchangeId unique identifier for the exchange
     * @param columnNames ordered field names mapping block indices to record fields
     * @param bufferSize maximum number of pages buffered in the exchange
     */
    record ExchangeVal(String nodeId, String exchangeId, List<String> columnNames, int bufferSize) implements Value {}

    /**
     * An exchange sink handle. Non-serializable, node-local.
     * Created by {@code Exchange.sink}, consumed by {@code Exchange.addPage}
     * and {@code Exchange.finish}.
     *
     * @param sink the compute engine exchange sink
     * @param columnNames ordered field names (from the parent Exchange descriptor)
     */
    record ExchangeSinkVal(ExchangeSink sink, List<String> columnNames) implements Value {}

    /**
     * An exchange source handle. Non-serializable, node-local.
     * Created by {@code Exchange.connect}, consumed by {@code Exchange.poll}.
     *
     * @param source the compute engine exchange source
     * @param columnNames ordered field names (from the parent Exchange descriptor)
     */
    record ExchangeSourceVal(ExchangeSource source, List<String> columnNames) implements Value {}
}
