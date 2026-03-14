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
import org.elasticsearch.xpack.piescript.core.CorePrinter;
import org.elasticsearch.xpack.piescript.elab.ElaborationException;
import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.Elaborator;
import org.elasticsearch.xpack.piescript.eval.EvaluationException;
import org.elasticsearch.xpack.piescript.eval.Evaluator;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;
import org.elasticsearch.xpack.piescript.parser.PiescriptParsingException;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/**
 * Development endpoint for inspecting each stage of the Piescript pipeline.
 * Returns the CST (parse tree), the elaborated Core IR, and the resolved type.
 *
 * <p>Stages are independent: a parse error prevents elaboration but still
 * returns the error; an elaboration (type) error still returns the CST.
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
                    String treeString = piescriptParser.parseToTreeString(program);
                    builder.field("tree", treeString);

                    var cst = piescriptParser.parse(program);
                    var state = new ElaborationState();
                    var elaborator = new Elaborator(state);
                    var coreExpr = elaborator.elaborateProgram(cst);

                    builder.field("core", CorePrinter.printExpr(coreExpr, state));
                    builder.field("core_raw", CorePrinter.printExprRaw(coreExpr));
                    builder.field("type", CorePrinter.printType(coreExpr.type(), state));
                    builder.field("constraints", CorePrinter.printConstraints(state));
                    builder.field("zonker", CorePrinter.printZonker(state));

                    try {
                        var evaluator = new Evaluator();
                        var value = evaluator.evaluate(coreExpr);
                        builder.field("eval", value.toString());
                    } catch (EvaluationException e) {
                        builder.field("eval_error", e.getMessage());
                    }
                } catch (PiescriptParsingException e) {
                    builder.field("parse_error", e.getMessage());
                } catch (ElaborationException e) {
                    builder.field("type_error", e.getMessage());
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
