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
import org.elasticsearch.xpack.esql.action.EsqlQueryResponse;

public class TransportPiescriptAction extends HandledTransportAction<PiescriptRequest, EsqlQueryResponse> {

    private final Client client;

    @Inject
    public TransportPiescriptAction(TransportService transportService, ActionFilters actionFilters, Client client) {
        super(PiescriptAction.NAME, transportService, actionFilters, PiescriptRequest::new, EsExecutors.DIRECT_EXECUTOR_SERVICE);
        this.client = client;
    }

    @Override
    protected void doExecute(Task task, PiescriptRequest request, ActionListener<EsqlQueryResponse> listener) {
        String esqlQuery;
        try {
            esqlQuery = extractEsqlQuery(request.program());
        } catch (IllegalArgumentException e) {
            listener.onFailure(e);
            return;
        }
        EsqlQueryRequest esqlRequest = EsqlQueryRequest.syncEsqlQueryRequest(esqlQuery);
        client.execute(EsqlQueryAction.INSTANCE, esqlRequest, listener);
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
