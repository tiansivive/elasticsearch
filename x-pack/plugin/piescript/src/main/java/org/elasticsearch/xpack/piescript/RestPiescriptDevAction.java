/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestChunkedToXContentListener;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/**
 * Development endpoint that runs the same pipeline as {@code /_piescript/eval}
 * but returns the full debug output (CST tree, Core IR, type, constraints,
 * zonker, diagnostics) with graceful per-stage error reporting.
 *
 * <p>Dispatches to the same {@link TransportPiescriptAction} with
 * {@code dev=true}; the transport action collects intermediate results and
 * packages them into the response.
 *
 * <p>TODO: merge this endpoint into {@code /_piescript/eval} with a
 * {@code ?dev} query parameter, eliminating the need for a separate handler.
 */
public class RestPiescriptDevAction extends BaseRestHandler {

    @Override
    public String getName() {
        return "piescript_dev";
    }

    @Override
    public List<Route> routes() {
        return List.of(new Route(POST, "/_piescript/dev"));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        String program;
        try (XContentParser parser = request.contentOrSourceParamParser()) {
            program = parseProgram(parser);
        }
        PiescriptRequest piescriptRequest = new PiescriptRequest(program, true);
        return channel -> client.execute(PiescriptAction.INSTANCE, piescriptRequest, new RestChunkedToXContentListener<>(channel));
    }

    private static String parseProgram(XContentParser parser) throws IOException {
        String program = null;
        String currentFieldName = null;
        XContentParser.Token token;
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                if ("program".equals(currentFieldName)) {
                    program = parser.text();
                }
            }
        }
        if (program == null) {
            throw new IllegalArgumentException("request body must contain a [program] field");
        }
        return program;
    }
}
