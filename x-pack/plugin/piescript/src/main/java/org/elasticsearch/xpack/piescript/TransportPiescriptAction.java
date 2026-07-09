/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.compute.operator.exchange.ExchangeService;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.piescript.core.CorePrinter;
import org.elasticsearch.xpack.piescript.elab.ElaborationException;
import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.Elaborator;
import org.elasticsearch.xpack.piescript.elab.IndexResolutionPrePass;
import org.elasticsearch.xpack.piescript.elab.ResolvedMapping;
import org.elasticsearch.xpack.piescript.eval.ChannelRegistry;
import org.elasticsearch.xpack.piescript.eval.EvalDependencies;
import org.elasticsearch.xpack.piescript.eval.Evaluator;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.parser.PiescriptParsingException;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class TransportPiescriptAction extends HandledTransportAction<PiescriptRequest, PiescriptResponse> {

    private final PiescriptParser parser = new PiescriptParser();
    private final IndexResolutionPrePass indexResolutionPrePass;
    private final Client client;
    private final Executor executor;
    private final ClusterService clusterService;
    private final TransportService transportService;
    private final ChannelRegistry channelRegistry;
    private final IndicesService indicesService;
    private final ExchangeService exchangeService;

    @Inject
    public TransportPiescriptAction(
        TransportService transportService,
        ActionFilters actionFilters,
        Client client,
        ThreadPool threadPool,
        ClusterService clusterService,
        ChannelRegistry channelRegistry,
        IndicesService indicesService,
        ExchangeService exchangeService
    ) {
        super(PiescriptAction.NAME, transportService, actionFilters, PiescriptRequest::new, threadPool.executor(ThreadPool.Names.GENERIC));
        this.indexResolutionPrePass = IndexResolutionPrePass.create(client, transportService, clusterService);
        this.client = client;
        this.executor = threadPool.executor(ThreadPool.Names.GENERIC);
        this.clusterService = clusterService;
        this.transportService = transportService;
        this.channelRegistry = channelRegistry;
        this.indicesService = indicesService;
        this.exchangeService = exchangeService;
    }

    private EvalDependencies buildEvalDeps(Task task, java.util.function.Function<MonoType, MonoType> force) {
        return new EvalDependencies(
            client,
            executor,
            clusterService,
            transportService,
            channelRegistry,
            transportService.getLocalNode().getId(),
            indicesService,
            exchangeService,
            task,
            force
        );
    }

    @Override
    protected void doExecute(Task task, PiescriptRequest request, ActionListener<PiescriptResponse> listener) {
        String program = request.program().strip();
        if (request.dev()) {
            executeDev(task, program, listener);
        } else {
            executeEval(task, program, listener);
        }
    }

    // ── Normal eval pipeline ──

    private void executeEval(Task task, String program, ActionListener<PiescriptResponse> listener) {
        try {
            var cst = parser.parse(program);
            var useIndexNames = IndexResolutionPrePass.collectUseDeclarations(cst);

            if (useIndexNames.isEmpty()) {
                elaborateAndEvaluate(task, cst, null, listener);
            } else {
                indexResolutionPrePass.resolve(useIndexNames, listener.delegateFailureAndWrap((l, resolvedMappings) -> {
                    executor.execute(() -> elaborateAndEvaluate(task, cst, resolvedMappings, l));
                }));
            }
        } catch (Exception e) {
            listener.onFailure(e);
        }
    }

    private void elaborateAndEvaluate(
        Task task,
        PiescriptAntlrParser.ProgramContext cst,
        Map<String, ResolvedMapping> resolvedMappings,
        ActionListener<PiescriptResponse> listener
    ) {
        try {
            var state = new ElaborationState();
            if (resolvedMappings != null) {
                state.setResolvedMappings(resolvedMappings);
            }
            var elaborator = new Elaborator(state);
            var coreExpr = elaborator.elaborateProgram(cst);
            var type = CorePrinter.printType(coreExpr.type(), state);
            var evaluator = new Evaluator(buildEvalDeps(task, state::force));
            evaluator.evaluate(
                coreExpr,
                listener.delegateFailureAndWrap((l, value) -> l.onResponse(PiescriptResponse.fromValue(value, type)))
            );
        } catch (Exception e) {
            listener.onFailure(e);
        }
    }

    // ── Dev pipeline: same stages, never fails — errors are reported in the response ──

    private void executeDev(Task task, String program, ActionListener<PiescriptResponse> listener) {
        String treeString;
        PiescriptAntlrParser.ProgramContext cst;
        try {
            treeString = parser.parseToTreeString(program);
            cst = parser.parse(program);
        } catch (PiescriptParsingException e) {
            listener.onResponse(devParseError(e.getMessage()));
            return;
        }

        try {
            var useIndexNames = IndexResolutionPrePass.collectUseDeclarations(cst);
            if (useIndexNames.isEmpty()) {
                elaborateAndEvaluateDev(task, cst, treeString, null, listener);
            } else {
                indexResolutionPrePass.resolve(useIndexNames, ActionListener.wrap(resolvedMappings -> {
                    executor.execute(() -> elaborateAndEvaluateDev(task, cst, treeString, resolvedMappings, listener));
                }, e -> { listener.onResponse(devTypeError(treeString, "index resolution failed: " + e.getMessage())); }));
            }
        } catch (Exception e) {
            listener.onResponse(devTypeError(treeString, e.getMessage()));
        }
    }

    private void elaborateAndEvaluateDev(
        Task task,
        PiescriptAntlrParser.ProgramContext cst,
        String treeString,
        Map<String, ResolvedMapping> resolvedMappings,
        ActionListener<PiescriptResponse> listener
    ) {
        try {
            var state = new ElaborationState();
            if (resolvedMappings != null) {
                state.setResolvedMappings(resolvedMappings);
            }
            var elaborator = new Elaborator(state);
            var coreExpr = elaborator.elaborateProgram(cst);

            String core = CorePrinter.printExpr(coreExpr, state);
            String coreRaw = CorePrinter.printExprRaw(coreExpr);
            String type = CorePrinter.printType(coreExpr.type(), state);
            String constraints = CorePrinter.printConstraints(state);
            String zonker = CorePrinter.printZonker(state);
            List<String> diagnostics = state.diagnostics();

            var evaluator = new Evaluator(buildEvalDeps(task, state::force));
            evaluator.evaluate(
                coreExpr,
                ActionListener.wrap(
                    value -> listener.onResponse(
                        PiescriptResponse.fromDev(
                            new PiescriptResponse.DevInfo(
                                treeString,
                                core,
                                coreRaw,
                                type,
                                constraints,
                                zonker,
                                diagnostics,
                                value.toString(),
                                null,
                                null,
                                null
                            )
                        )
                    ),
                    e -> listener.onResponse(
                        PiescriptResponse.fromDev(
                            new PiescriptResponse.DevInfo(
                                treeString,
                                core,
                                coreRaw,
                                type,
                                constraints,
                                zonker,
                                diagnostics,
                                null,
                                e.getMessage(),
                                null,
                                null
                            )
                        )
                    )
                )
            );
        } catch (ElaborationException e) {
            listener.onResponse(devTypeError(treeString, e.getMessage()));
        } catch (Exception e) {
            listener.onResponse(devTypeError(treeString, e.getMessage()));
        }
    }

    private static PiescriptResponse devParseError(String message) {
        return PiescriptResponse.fromDev(
            new PiescriptResponse.DevInfo(null, null, null, null, null, null, List.of(), null, null, message, null)
        );
    }

    private static PiescriptResponse devTypeError(String tree, String message) {
        return PiescriptResponse.fromDev(
            new PiescriptResponse.DevInfo(tree, null, null, null, null, null, List.of(), null, null, null, message)
        );
    }
}
