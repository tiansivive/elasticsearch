/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.core.Nullable;

import java.util.concurrent.Executor;

/**
 * Bundles external dependencies needed by the evaluator and its handler classes
 * ({@link EvalBuiltins}, {@link EvalCoordination}, etc.). Avoids passing an
 * expanding list of individual parameters through the {@link Evaluator}
 * constructor as new capabilities are added (Block B: ClusterService,
 * Block C: TransportService + channel registry, etc.).
 *
 * @param client         the node client for ESQL query execution; null in unit tests
 * @param executor       thread pool executor for spawned computations
 * @param clusterService cluster state access for topology builtins; null in unit tests
 */
public record EvalDependencies(@Nullable Client client, Executor executor, @Nullable ClusterService clusterService) {}
