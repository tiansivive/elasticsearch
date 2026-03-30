/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.compute.operator.exchange.ExchangeService;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Bundles external dependencies needed by the evaluator and its handler classes
 * ({@link EvalBuiltins}, {@link EvalCoordination}, etc.). Avoids passing an
 * expanding list of individual parameters through the {@link Evaluator}
 * constructor as new capabilities are added.
 *
 * @param client           the node client for ESQL query execution
 * @param executor         thread pool executor for spawned computations
 * @param clusterService   cluster state access for topology builtins
 * @param transportService transport service for cross-node sends (D-045)
 * @param channelRegistry  per-node channel registry mapping channel IDs to listeners (D-045)
 * @param localNodeId      the discovery node ID of this node
 * @param indicesService   access to index shards for Shard.open (D-050)
 * @param exchangeService  compute engine exchange service for streaming (D-054)
 * @param task             the transport task for this request (for exchange child request tracking)
 * @param force            lazy type resolver — chases Meta chains to concrete types (from ElaborationState::force)
 */
public record EvalDependencies(
    @Nullable Client client,
    Executor executor,
    @Nullable ClusterService clusterService,
    @Nullable TransportService transportService,
    ChannelRegistry channelRegistry,
    String localNodeId,
    @Nullable IndicesService indicesService,
    @Nullable ExchangeService exchangeService,
    @Nullable Task task,
    @Nullable Function<MonoType, MonoType> force
) {}
