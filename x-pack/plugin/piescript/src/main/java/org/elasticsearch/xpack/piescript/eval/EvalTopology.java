/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.cluster.metadata.ProjectId;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.routing.IndexRoutingTable;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardRoutingState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Topology builtin evaluation: reads cluster state and converts index
 * routing information into typed piescript records. See D-044.
 *
 * <p>Returns a record with two views of the same topology data:
 * <ul>
 *   <li>{@code shards} — a list of shard records, each with a nested node record</li>
 *   <li>{@code nodes} — a list of node records, each with a nested list of shard records</li>
 * </ul>
 *
 * <p>Only {@link ShardRoutingState#STARTED} shards are included — unassigned
 * and initializing shards have no node and are not useful for the distributed
 * vertical slice (Block C: shipping code to data nodes).
 */
final class EvalTopology {

    private EvalTopology() {}

    static void resolveTopology(Evaluator eval, Value indexArg, ActionListener<Value> listener) {
        var clusterService = eval.deps.clusterService();
        if (clusterService == null) {
            listener.onFailure(new EvaluationException("topology evaluation requires cluster service"));
            return;
        }

        String indexName = switch (indexArg) {
            case Value.KeywordVal kw -> kw.value();
            default -> throw new AssertionError("type checker bug: expected Keyword, got " + indexArg);
        };

        var clusterState = clusterService.state();
        IndexRoutingTable indexRouting = clusterState.routingTable(ProjectId.DEFAULT).index(indexName);
        if (indexRouting == null) {
            listener.onFailure(new EvaluationException("index [" + indexName + "] not found in routing table"));
            return;
        }

        List<ShardRouting> startedShards = indexRouting.shardsWithState(ShardRoutingState.STARTED);
        var shardRecords = new ArrayList<Value>();
        var nodeShardMap = new LinkedHashMap<String, List<Value>>();
        var nodeInfoMap = new LinkedHashMap<String, Value.RecordVal>();

        for (ShardRouting shard : startedShards) {
            DiscoveryNode node = clusterState.nodes().get(shard.currentNodeId());
            if (node == null) {
                continue;
            }

            var nodeRecord = buildNodeRecord(node);
            var shardCoreFields = buildShardCoreFields(indexName, shard);

            var shardFields = new LinkedHashMap<>(shardCoreFields);
            shardFields.put("node", nodeRecord);
            shardRecords.add(new Value.RecordVal(shardFields));

            nodeInfoMap.putIfAbsent(node.getId(), nodeRecord);
            nodeShardMap.computeIfAbsent(node.getId(), k -> new ArrayList<>()).add(new Value.RecordVal(shardCoreFields));
        }

        var nodeRecords = new ArrayList<Value>();
        for (var entry : nodeInfoMap.entrySet()) {
            var baseFields = entry.getValue().fields();
            var nodeFields = new LinkedHashMap<>(baseFields);
            nodeFields.put("shards", new Value.ListVal(nodeShardMap.getOrDefault(entry.getKey(), List.of())));
            nodeRecords.add(new Value.RecordVal(nodeFields));
        }

        var result = new Value.RecordVal(Map.of("shards", new Value.ListVal(shardRecords), "nodes", new Value.ListVal(nodeRecords)));
        listener.onResponse(result);
    }

    private static Value.RecordVal buildNodeRecord(DiscoveryNode node) {
        return new Value.RecordVal(
            Map.of(
                "id",
                new Value.KeywordVal(node.getId()),
                "name",
                new Value.KeywordVal(node.getName()),
                "address",
                new Value.KeywordVal(node.getHostAddress())
            )
        );
    }

    private static Map<String, Value> buildShardCoreFields(String indexName, ShardRouting shard) {
        var fields = new LinkedHashMap<String, Value>();
        fields.put("index", new Value.KeywordVal(indexName));
        fields.put("shard_id", new Value.IntegerVal(shard.shardId().id()));
        fields.put("primary", new Value.BooleanVal(shard.primary()));
        fields.put("state", new Value.KeywordVal(shard.state().name()));
        return fields;
    }
}
