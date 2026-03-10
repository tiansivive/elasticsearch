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
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.parser.PiescriptParsingException;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/**
 * Development endpoint for inspecting the parse tree of a Piescript program.
 * Returns the LISP-style CST representation produced by ANTLR.
 */
public class RestPiescriptDevAction extends BaseRestHandler {

    private final PiescriptParser piescriptParser = new PiescriptParser();

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
        return channel -> {
            try (XContentBuilder builder = channel.newBuilder()) {
                builder.startObject();
                try {
                    String tree = piescriptParser.parseToTreeString(program);
                    builder.field("tree", tree);
                } catch (PiescriptParsingException e) {
                    builder.field("error", e.getMessage());
                }
                builder.endObject();
                channel.sendResponse(new RestResponse(RestStatus.OK, builder));
            }
        };
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
