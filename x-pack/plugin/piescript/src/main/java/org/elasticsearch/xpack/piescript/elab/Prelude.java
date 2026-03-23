/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.entry;

/**
 * The Piescript prelude: built-in functions available as module-level free
 * variables. These are wired into the {@link ElaborationContext} module map
 * so they can be referenced without explicit import.
 *
 * <p>Type schemes use pre-allocated Rigid IDs that are disjoint from the
 * IDs produced by {@link ElaborationState#freshRigid}. Since instantiation
 * substitutes all quantified Rigids with fresh metas, the IDs only need
 * to be internally consistent within each scheme.
 *
 * <p>All builtins use qualified names ({@code Namespace.name}). See D-050.
 *
 * <p>Signatures:
 * <pre>
 *   List.map      : ∀a b. (a → b) → List a → List b
 *   List.filter   : ∀a.   (a → Boolean) → List a → List a
 *   List.reduce   : ∀a b. (b → a → b) → b → List a → b
 *   List.head     : ∀a. List a → a
 *   List.tail     : ∀a. List a → List a
 *   List.length   : ∀a. List a → Double
 *   List.isEmpty  : ∀a. List a → Boolean
 *   List.at       : ∀a. Double → List a → a
 *   Math.abs      : Double → Double
 *   Math.floor    : Double → Double
 *   Math.ceil     : Double → Double
 *   Math.round    : Double → Double
 *   Math.sqrt     : Double → Double
 *   Math.log      : Double → Double
 *   Math.min      : Double → Double → Double
 *   Math.max      : Double → Double → Double
 *   Math.pow      : Double → Double → Double
 *   Math.toInt    : Double → Double
 *   Cluster.topology : Keyword → { local: NodeBase, nodes: List NodeBase }
 *   Index.routing    : ∀r. Index r → { shards: List ShardRecord, nodes: List NodeRecord }
 *   Index.shards     : ∀r. Index r → List ShardRecord
 *   Index.nodes      : ∀r. Index r → List NodeRecord
 *   Shard.open       : ∀r. Index r → ShardRecord → { match_all: Boolean } → Channel (Searcher r)
 *   Shard.consume    : ∀r. Double → Searcher r → List (DocRef r)
 *   Shard.read       : ∀r. DocRef r → r
 *   Shard.writer     : ∀r. Index r → ShardRecord → Channel (Writer r)
 *   Shard.write      : ∀r. Writer r → Keyword → r → { seq_no: Double, version: Double, result: Keyword }
 *   Shard.refresh    : ∀r. Writer r → Channel { refreshed: Boolean }
 *   Shard.globalCheckpoint : ∀r. Index r → ShardRecord → Double
 *   Index.bulk       : ∀r. Keyword → List r → Channel { total: Double, written: Double, failed: Double }
 * </pre>
 *
 * <p>{@code Cluster.topology "cluster"} returns cluster-level info: the local (coordinator)
 * node and all nodes with their inboxes. {@code Index.routing "index"} returns index-level
 * shard placement. {@code Index.shards} and {@code Index.nodes} are conveniences over
 * {@code Index.routing}. See D-048, D-050.
 */
public final class Prelude {

    private Prelude() {}

    private static final MonoType.Rigid A0 = new MonoType.Rigid(-1, Kind.TYPE);
    private static final MonoType.Rigid B0 = new MonoType.Rigid(-2, Kind.TYPE);
    private static final MonoType.Rigid R0 = new MonoType.Rigid(-3, Kind.ROW);

    private static final MonoType KW = Elaborator.KEYWORD;
    private static final MonoType DBL = Elaborator.DOUBLE;
    private static final MonoType BOOL = Elaborator.BOOLEAN;
    private static final MonoType NULL = Elaborator.NULL_TYPE;

    /**
     * The module map containing all built-in function type schemes.
     * Pass this to {@link ElaborationContext#withModule} for the standard
     * Piescript elaboration environment.
     */
    public static final Map<String, TypeScheme> MODULE = buildModule();

    /** Arity (number of term-level arguments) for each built-in function. */
    public static final Map<String, Integer> ARITY = Map.ofEntries(
        entry("List.map", 2),
        entry("List.filter", 2),
        entry("List.reduce", 3),
        entry("List.head", 1),
        entry("List.tail", 1),
        entry("List.length", 1),
        entry("List.isEmpty", 1),
        entry("List.at", 2),
        entry("Math.abs", 1),
        entry("Math.floor", 1),
        entry("Math.ceil", 1),
        entry("Math.round", 1),
        entry("Math.sqrt", 1),
        entry("Math.log", 1),
        entry("Math.min", 2),
        entry("Math.max", 2),
        entry("Math.pow", 2),
        entry("Math.toInt", 1),
        entry("Cluster.topology", 1),
        entry("Index.routing", 1),
        entry("Index.shards", 1),
        entry("Index.nodes", 1),
        entry("Shard.open", 3),
        entry("Shard.consume", 2),
        entry("Shard.read", 1),
        entry("Shard.writer", 2),
        entry("Shard.write", 3),
        entry("Shard.refresh", 1),
        entry("Shard.globalCheckpoint", 2),
        entry("Index.bulk", 2)
    );

    private static Map<String, TypeScheme> buildModule() {
        var module = new LinkedHashMap<String, TypeScheme>();
        module.put("List.map", mapScheme());
        module.put("List.filter", filterScheme());
        module.put("List.reduce", reduceScheme());
        module.put("List.head", listToA());          // ∀a. List a → a
        module.put("List.tail", listToList());        // ∀a. List a → List a
        module.put("List.length", listToDouble());     // ∀a. List a → Double
        module.put("List.isEmpty", listToBool());     // ∀a. List a → Boolean
        module.put("List.at", listAtScheme());        // ∀a. Double → List a → a
        module.put("Math.abs", dblToDbl());
        module.put("Math.floor", dblToDbl());
        module.put("Math.ceil", dblToDbl());
        module.put("Math.round", dblToDbl());
        module.put("Math.sqrt", dblToDbl());
        module.put("Math.log", dblToDbl());
        module.put("Math.min", dblDblToDbl());
        module.put("Math.max", dblDblToDbl());
        module.put("Math.pow", dblDblToDbl());
        module.put("Math.toInt", dblToDbl());
        module.put("Cluster.topology", clusterTopologyScheme());
        module.put("Index.routing", routingScheme());
        module.put("Index.shards", shardsScheme());
        module.put("Index.nodes", nodesScheme());
        module.put("Shard.open", shardOpenScheme());
        module.put("Shard.consume", shardConsumeScheme());
        module.put("Shard.read", shardReadScheme());
        module.put("Shard.writer", shardWriterScheme());
        module.put("Shard.write", shardWriteScheme());
        module.put("Shard.refresh", shardRefreshScheme());
        module.put("Shard.globalCheckpoint", shardGlobalCheckpointScheme());
        module.put("Index.bulk", indexBulkScheme());
        return Map.copyOf(module);
    }

    // map : ∀(a:TYPE, b:TYPE). (a → b) → List a → List b
    private static TypeScheme mapScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        quantified.put(B0.id(), Kind.TYPE);

        var body = new MonoType.Arrow(new MonoType.Arrow(A0, B0), new MonoType.Arrow(list(A0), list(B0)));
        return new TypeScheme(quantified, body);
    }

    // filter : ∀(a:TYPE). (a → Boolean) → List a → List a
    private static TypeScheme filterScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);

        var body = new MonoType.Arrow(new MonoType.Arrow(A0, Elaborator.BOOLEAN), new MonoType.Arrow(list(A0), list(A0)));
        return new TypeScheme(quantified, body);
    }

    // reduce : ∀(a:TYPE, b:TYPE). (b → a → b) → b → List a → b
    private static TypeScheme reduceScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        quantified.put(B0.id(), Kind.TYPE);

        var body = new MonoType.Arrow(
            new MonoType.Arrow(B0, new MonoType.Arrow(A0, B0)),
            new MonoType.Arrow(B0, new MonoType.Arrow(list(A0), B0))
        );
        return new TypeScheme(quantified, body);
    }

    // head : ∀(a:TYPE). List a → a
    private static TypeScheme listToA() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(list(A0), A0));
    }

    // tail : ∀(a:TYPE). List a → List a
    private static TypeScheme listToList() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(list(A0), list(A0)));
    }

    // length : ∀(a:TYPE). List a → Double
    private static TypeScheme listToDouble() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(list(A0), DBL));
    }

    // isEmpty : ∀(a:TYPE). List a → Boolean
    private static TypeScheme listToBool() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(list(A0), BOOL));
    }

    // Double → Double (monomorphic, no quantified vars)
    private static TypeScheme dblToDbl() {
        return TypeScheme.mono(new MonoType.Arrow(DBL, DBL));
    }

    // Double → Double → Double (monomorphic, curried)
    private static TypeScheme dblDblToDbl() {
        return TypeScheme.mono(new MonoType.Arrow(DBL, new MonoType.Arrow(DBL, DBL)));
    }

    // Shared type building blocks for topology/routing:
    // NodeInfoArg = { id: Keyword, name: Keyword, address: Keyword } — what inbox closures receive
    // NodeBase = NodeInfoArg & { inbox: Channel (NodeInfoArg → Null) }
    // ShardCore = { index: Keyword, shard_id: Integer, primary: Boolean, state: Keyword }
    // ShardRecord = ShardCore & { node: NodeBase }
    // NodeRecord = NodeBase & { shards: List ShardCore }

    private static MonoType.RecordType nodeBase() {
        var nodeInfoArg = record(Map.of("id", KW, "name", KW, "address", KW));
        var inboxType = channel(new MonoType.Arrow(nodeInfoArg, NULL));
        var fields = new LinkedHashMap<String, MonoType>();
        fields.put("id", KW);
        fields.put("name", KW);
        fields.put("address", KW);
        fields.put("inbox", inboxType);
        return record(fields);
    }

    private static Map<String, MonoType> shardCoreFields() {
        return Map.of("index", KW, "uuid", KW, "shard_id", DBL, "primary", BOOL, "state", KW);
    }

    // topology : Keyword → { local: NodeBase, nodes: List NodeBase } (D-048)
    private static TypeScheme clusterTopologyScheme() {
        var nb = nodeBase();
        var resultType = record(Map.of("local", nb, "nodes", list(nb)));
        return TypeScheme.mono(new MonoType.Arrow(KW, resultType));
    }

    // routing : ∀(r:Row). Index r → { shards: List ShardRecord, nodes: List NodeRecord } (D-048, D-050)
    private static TypeScheme routingScheme() {
        var nb = nodeBase();
        var shardCore = shardCoreFields();

        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCore);
        shardRecordFields.put("node", nb);
        var shardRecord = record(shardRecordFields);

        var nodeRecordFields = new LinkedHashMap<>(((RowType) nb.row()).fields());
        nodeRecordFields.put("shards", list(record(shardCore)));
        var nodeRecordFull = record(nodeRecordFields);

        var resultType = record(Map.of("shards", list(shardRecord), "nodes", list(nodeRecordFull)));
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(index(R0), resultType));
    }

    // shards : ∀(r:Row). Index r → List ShardRecord (convenience over routing)
    private static TypeScheme shardsScheme() {
        var nb = nodeBase();
        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCoreFields());
        shardRecordFields.put("node", nb);
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(index(R0), list(record(shardRecordFields))));
    }

    // nodes : ∀(r:Row). Index r → List NodeRecord (convenience over routing)
    private static TypeScheme nodesScheme() {
        var nb = nodeBase();
        var nodeRecordFields = new LinkedHashMap<>(((RowType) nb.row()).fields());
        nodeRecordFields.put("shards", list(record(shardCoreFields())));
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(index(R0), list(record(nodeRecordFields))));
    }

    // at : ∀a. Double → List a → a (0-based index access)
    private static TypeScheme listAtScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(DBL, new MonoType.Arrow(list(A0), A0)));
    }

    // Shard.open : ∀(r:Row). Index r → ShardRecord → { match_all: Boolean } → Channel (Searcher r)
    private static TypeScheme shardOpenScheme() {
        var nb = nodeBase();
        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCoreFields());
        shardRecordFields.put("node", nb);
        var shardRecordType = record(shardRecordFields);

        var queryType = record(Map.of("match_all", BOOL));

        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(
            quantified,
            new MonoType.Arrow(index(R0), new MonoType.Arrow(shardRecordType, new MonoType.Arrow(queryType, channel(searcher(R0)))))
        );
    }

    // Shard.consume : ∀(r:Row). Double → Searcher r → List (DocRef r)
    private static TypeScheme shardConsumeScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(DBL, new MonoType.Arrow(searcher(R0), list(docref(R0)))));
    }

    // Shard.read : ∀(r:Row). DocRef r → Record r
    private static TypeScheme shardReadScheme() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(docref(R0), new MonoType.RecordType(R0)));
    }

    static MonoType.RecordType record(Map<String, MonoType> fields) {
        return new MonoType.RecordType(RowType.closed(fields));
    }

    static MonoType.AppType list(MonoType element) {
        return new MonoType.AppType(Elaborator.LIST, element);
    }

    static MonoType.AppType channel(MonoType element) {
        return new MonoType.AppType(Elaborator.CHANNEL, element);
    }

    static MonoType.AppType index(MonoType schema) {
        return new MonoType.AppType(Elaborator.INDEX, schema);
    }

    static MonoType.AppType searcher(MonoType schema) {
        return new MonoType.AppType(Elaborator.SEARCHER, schema);
    }

    static MonoType.AppType docref(MonoType schema) {
        return new MonoType.AppType(Elaborator.DOCREF, schema);
    }

    static MonoType.AppType writer(MonoType schema) {
        return new MonoType.AppType(Elaborator.WRITER, schema);
    }

    // Shard.writer : ∀(r:Row). Index r → ShardRecord → Channel (Writer r)
    private static TypeScheme shardWriterScheme() {
        var nb = nodeBase();
        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCoreFields());
        shardRecordFields.put("node", nb);
        var shardRecordType = record(shardRecordFields);

        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(index(R0), new MonoType.Arrow(shardRecordType, channel(writer(R0)))));
    }

    // Shard.write : ∀(r:Row). Writer r → Keyword → Record r → WriteResult
    // The Keyword argument is the document _id (separate from the record body).
    // Future: Shard.write : ∀(r : Row). Writer r → { _id: Keyword | r } → WriteResult
    // WriteResult = { seq_no: Double, version: Double, result: Keyword }
    private static TypeScheme shardWriteScheme() {
        var writeResult = record(Map.of("seq_no", DBL, "version", DBL, "result", KW));
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(
            quantified,
            new MonoType.Arrow(writer(R0), new MonoType.Arrow(KW, new MonoType.Arrow(new MonoType.RecordType(R0), writeResult)))
        );
    }

    // Shard.refresh : ∀(r:Row). Writer r → Channel { refreshed: Boolean }
    private static TypeScheme shardRefreshScheme() {
        var refreshResult = record(Map.of("refreshed", BOOL));
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(writer(R0), channel(refreshResult)));
    }

    // Shard.globalCheckpoint : ∀(r:Row). Index r → ShardRecord → Double
    private static TypeScheme shardGlobalCheckpointScheme() {
        var nb = nodeBase();
        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCoreFields());
        shardRecordFields.put("node", nb);
        var shardRecordType = record(shardRecordFields);

        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(quantified, new MonoType.Arrow(index(R0), new MonoType.Arrow(shardRecordType, DBL)));
    }

    // Index.bulk : ∀(r:Row). Keyword → List (Record r) → Channel { total: Double, written: Double, failed: Double }
    private static TypeScheme indexBulkScheme() {
        var bulkResult = record(Map.of("total", DBL, "written", DBL, "failed", DBL));
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(R0.id(), Kind.ROW);
        return new TypeScheme(
            quantified,
            new MonoType.Arrow(KW, new MonoType.Arrow(list(new MonoType.RecordType(R0)), channel(bulkResult)))
        );
    }
}
