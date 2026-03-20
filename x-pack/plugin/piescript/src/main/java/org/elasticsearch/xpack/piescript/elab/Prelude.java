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
 *   Index.routing    : Keyword → { shards: List ShardRecord, nodes: List NodeRecord }
 *   Index.shards     : Keyword → List ShardRecord
 *   Index.nodes      : Keyword → List NodeRecord
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
        entry("Index.nodes", 1)
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
        return Map.of("index", KW, "shard_id", DBL, "primary", BOOL, "state", KW);
    }

    // topology : Keyword → { local: NodeBase, nodes: List NodeBase } (D-048)
    private static TypeScheme clusterTopologyScheme() {
        var nb = nodeBase();
        var resultType = record(Map.of("local", nb, "nodes", list(nb)));
        return TypeScheme.mono(new MonoType.Arrow(KW, resultType));
    }

    // routing : Keyword → { shards: List ShardRecord, nodes: List NodeRecord } (D-048)
    private static TypeScheme routingScheme() {
        var nb = nodeBase();
        var shardCore = shardCoreFields();

        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCore);
        shardRecordFields.put("node", nb);
        var shardRecord = record(shardRecordFields);

        var nodeRecordFields = new LinkedHashMap<>(nb.row().fields());
        nodeRecordFields.put("shards", list(record(shardCore)));
        var nodeRecordFull = record(nodeRecordFields);

        var resultType = record(Map.of("shards", list(shardRecord), "nodes", list(nodeRecordFull)));
        return TypeScheme.mono(new MonoType.Arrow(KW, resultType));
    }

    // shards : Keyword → List ShardRecord (convenience over routing)
    private static TypeScheme shardsScheme() {
        var nb = nodeBase();
        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCoreFields());
        shardRecordFields.put("node", nb);
        return TypeScheme.mono(new MonoType.Arrow(KW, list(record(shardRecordFields))));
    }

    // nodes : Keyword → List NodeRecord (convenience over routing)
    private static TypeScheme nodesScheme() {
        var nb = nodeBase();
        var nodeRecordFields = new LinkedHashMap<>(nb.row().fields());
        nodeRecordFields.put("shards", list(record(shardCoreFields())));
        return TypeScheme.mono(new MonoType.Arrow(KW, list(record(nodeRecordFields))));
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
}
