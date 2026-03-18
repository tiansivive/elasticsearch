/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.logging.LogManager;
import org.elasticsearch.logging.Logger;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.piescript.eval.ChannelRegistry;
import org.elasticsearch.xpack.piescript.eval.EvalDependencies;
import org.elasticsearch.xpack.piescript.eval.EvaluationException;
import org.elasticsearch.xpack.piescript.eval.Evaluator;
import org.elasticsearch.xpack.piescript.eval.Value;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Transport handler for cross-node channel sends (D-045, D-047). Handles two cases:
 *
 * <ol>
 *   <li><b>Regular channel</b> — looks up the channel ID in the node's {@link ChannelRegistry}
 *       and completes it with the payload value.</li>
 *   <li><b>Inbox</b> — the well-known {@code "inbox"} channel. Validates the payload is a
 *       {@link Value.ClosureVal}, responds immediately (fire-and-forget), then evaluates the
 *       closure asynchronously on the executor with local node info as the argument.</li>
 * </ol>
 *
 * <p>Both paths respond to the transport layer immediately. The initiator's {@code send}
 * returns {@code Null} as soon as the message is delivered, regardless of what the recipient
 * does with it. Closure evaluation errors on the target node are logged locally; they are
 * never propagated back to the sender (D-047: remote closure errors are the target node's
 * responsibility, not the initiator's).
 */
public class TransportPiescriptSendAction extends HandledTransportAction<PiescriptSendRequest, ActionResponse.Empty> {

    private static final Logger logger = LogManager.getLogger(TransportPiescriptSendAction.class);

    private final ChannelRegistry channelRegistry;
    private final Client client;
    private final Executor executor;
    private final ClusterService clusterService;
    private final TransportService transportService;

    @Inject
    public TransportPiescriptSendAction(
        TransportService transportService,
        ActionFilters actionFilters,
        ThreadPool threadPool,
        Client client,
        ClusterService clusterService,
        ChannelRegistry channelRegistry
    ) {
        super(
            PiescriptSendAction.NAME,
            transportService,
            actionFilters,
            PiescriptSendRequest::new,
            threadPool.executor(ThreadPool.Names.GENERIC)
        );
        this.channelRegistry = channelRegistry;
        this.client = client;
        this.executor = threadPool.executor(ThreadPool.Names.GENERIC);
        this.clusterService = clusterService;
        this.transportService = transportService;
    }

    private EvalDependencies buildEvalDeps() {
        return new EvalDependencies(client, executor, clusterService, transportService, channelRegistry, transportService.getLocalNode().getId());
    }

    @Override
    protected void doExecute(Task task, PiescriptSendRequest request, ActionListener<ActionResponse.Empty> listener) {
        if (PiescriptSendRequest.INBOX_CHANNEL_ID.equals(request.channelId())) {
            handleInbox(request.payload(), listener);
        } else {
            channelRegistry.complete(request.channelId(), request.payload());
            listener.onResponse(ActionResponse.Empty.INSTANCE);
        }
    }

    private void handleInbox(Value payload, ActionListener<ActionResponse.Empty> listener) {
        if (payload instanceof Value.ClosureVal == false) {
            listener.onFailure(new EvaluationException("inbox expected ClosureVal, got " + payload.getClass().getSimpleName()));
            return;
        }
        listener.onResponse(ActionResponse.Empty.INSTANCE);
        var evalDeps = buildEvalDeps();
        var nodeInfo = buildLocalNodeInfo();
        var evaluator = new Evaluator(evalDeps);
        executor.execute(() -> evaluator.applyFunction(payload, nodeInfo, ActionListener.wrap(ignored -> {}, e -> {
            logger.warn("inbox closure evaluation failed on node [{}]", evalDeps.localNodeId(), e);
        })));
    }

    private Value.RecordVal buildLocalNodeInfo() {
        var localNode = transportService.getLocalNode();
        return new Value.RecordVal(
            Map.of(
                "id",
                new Value.KeywordVal(localNode.getId()),
                "name",
                new Value.KeywordVal(localNode.getName()),
                "address",
                new Value.KeywordVal(localNode.getHostAddress())
            )
        );
    }
}
