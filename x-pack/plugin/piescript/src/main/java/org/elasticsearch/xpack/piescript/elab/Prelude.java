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
 * <p>Signatures:
 * <pre>
 *   map      : ∀a b. (a → b) → List a → List b
 *   filter   : ∀a.   (a → Boolean) → List a → List a
 *   reduce   : ∀a b. (b → a → b) → b → List a → b
 *   head     : ∀a. List a → a
 *   tail     : ∀a. List a → List a
 *   length   : ∀a. List a → Integer
 *   isEmpty  : ∀a. List a → Boolean
 *   topology : Keyword → { shards: List ShardRecord, nodes: List NodeRecord }
 * </pre>
 */
public final class Prelude {

    private Prelude() {}

    private static final MonoType.Rigid A0 = new MonoType.Rigid(-1, Kind.TYPE);
    private static final MonoType.Rigid B0 = new MonoType.Rigid(-2, Kind.TYPE);
    private static final MonoType KW = Elaborator.KEYWORD;
    private static final MonoType INT = Elaborator.INTEGER;
    private static final MonoType BOOL = Elaborator.BOOLEAN;

    /**
     * The module map containing all built-in function type schemes.
     * Pass this to {@link ElaborationContext#withModule} for the standard
     * Piescript elaboration environment.
     */
    public static final Map<String, TypeScheme> MODULE = buildModule();

    /** Arity (number of term-level arguments) for each built-in function. */
    public static final Map<String, Integer> ARITY = Map.of(
        "map",
        2,
        "filter",
        2,
        "reduce",
        3,
        "head",
        1,
        "tail",
        1,
        "length",
        1,
        "isEmpty",
        1,
        "topology",
        1
    );

    private static Map<String, TypeScheme> buildModule() {
        var module = new LinkedHashMap<String, TypeScheme>();
        module.put("map", mapScheme());
        module.put("filter", filterScheme());
        module.put("reduce", reduceScheme());
        module.put("head", listToA());          // ∀a. List a → a
        module.put("tail", listToList());        // ∀a. List a → List a
        module.put("length", listToInt());       // ∀a. List a → Integer
        module.put("isEmpty", listToBool());     // ∀a. List a → Boolean
        module.put("topology", topologyScheme());
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

    // length : ∀(a:TYPE). List a → Integer
    private static TypeScheme listToInt() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(list(A0), INT));
    }

    // isEmpty : ∀(a:TYPE). List a → Boolean
    private static TypeScheme listToBool() {
        var quantified = new LinkedHashMap<Integer, Kind>();
        quantified.put(A0.id(), Kind.TYPE);
        return new TypeScheme(quantified, new MonoType.Arrow(list(A0), BOOL));
    }

    // topology : Keyword → { shards: List ShardRecord, nodes: List NodeRecord }
    // where ShardRecord = { index: Keyword, shard_id: Integer, primary: Boolean, state: Keyword,
    // node: { id: Keyword, name: Keyword, address: Keyword } }
    // NodeRecord = { id: Keyword, name: Keyword, address: Keyword,
    // shards: List { index: Keyword, shard_id: Integer, primary: Boolean, state: Keyword } }
    private static TypeScheme topologyScheme() {
        var nodeRecord = record(Map.of("id", KW, "name", KW, "address", KW));
        var shardCore = Map.of("index", KW, "shard_id", INT, "primary", BOOL, "state", KW);

        var shardRecordFields = new LinkedHashMap<String, MonoType>(shardCore);
        shardRecordFields.put("node", nodeRecord);
        var shardRecord = record(shardRecordFields);

        var nodeRecordFields = new LinkedHashMap<String, MonoType>();
        nodeRecordFields.put("id", KW);
        nodeRecordFields.put("name", KW);
        nodeRecordFields.put("address", KW);
        nodeRecordFields.put("shards", list(record(shardCore)));
        var nodeRecordFull = record(nodeRecordFields);

        var resultType = record(Map.of("shards", list(shardRecord), "nodes", list(nodeRecordFull)));
        var body = new MonoType.Arrow(KW, resultType);
        return TypeScheme.mono(body);
    }

    static MonoType.RecordType record(Map<String, MonoType> fields) {
        return new MonoType.RecordType(RowType.closed(fields));
    }

    static MonoType.AppType list(MonoType element) {
        return new MonoType.AppType(Elaborator.LIST, element);
    }
}
