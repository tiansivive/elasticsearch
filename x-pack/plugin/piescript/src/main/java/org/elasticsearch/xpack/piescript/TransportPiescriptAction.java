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
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.esql.action.EsqlQueryAction;
import org.elasticsearch.xpack.esql.action.EsqlQueryRequest;
import org.elasticsearch.xpack.piescript.core.CorePrinter;
import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.Elaborator;
import org.elasticsearch.xpack.piescript.eval.Evaluator;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;

public class TransportPiescriptAction extends HandledTransportAction<PiescriptRequest, PiescriptResponse> {

    private final Client client;
    private final PiescriptParser parser = new PiescriptParser();

    @Inject
    public TransportPiescriptAction(TransportService transportService, ActionFilters actionFilters, Client client) {
        super(PiescriptAction.NAME, transportService, actionFilters, PiescriptRequest::new, EsExecutors.DIRECT_EXECUTOR_SERVICE);
        this.client = client;
    }

    @Override
    protected void doExecute(Task task, PiescriptRequest request, ActionListener<PiescriptResponse> listener) {
        String program = request.program().strip();
        if (program.startsWith("query") && program.endsWith(";")) {
            executeQueryPassthrough(program, listener);
        } else {
            executeExpression(program, listener);
        }
    }

    private void executeQueryPassthrough(String program, ActionListener<PiescriptResponse> listener) {
        String esql;
        try {
            esql = extractEsqlQuery(program);
        } catch (IllegalArgumentException e) {
            listener.onFailure(e);
            return;
        }
        EsqlQueryRequest esqlRequest = EsqlQueryRequest.syncEsqlQueryRequest(esql);
        client.execute(EsqlQueryAction.INSTANCE, esqlRequest, listener.map(PiescriptResponse::fromEsqlResponse));
    }

    private void executeExpression(String program, ActionListener<PiescriptResponse> listener) {
        try {
            var cst = parser.parse(program);
            var state = new ElaborationState();
            var elaborator = new Elaborator(state);
            var coreExpr = elaborator.elaborateProgram(cst);
            var evaluator = new Evaluator();
            var value = evaluator.evaluate(coreExpr);
            var type = CorePrinter.printType(coreExpr.type(), state);
            listener.onResponse(PiescriptResponse.fromValue(value, type));
        } catch (Exception e) {
            listener.onFailure(e);
        }
    }

    static String extractEsqlQuery(String program) {
        String trimmed = program.strip();
        if (trimmed.startsWith("query") == false) {
            throw new IllegalArgumentException("program must start with 'query'");
        }
        if (trimmed.endsWith(";") == false) {
            throw new IllegalArgumentException("program must end with ';'");
        }
        return trimmed.substring("query".length(), trimmed.length() - 1).strip();
    }
}
