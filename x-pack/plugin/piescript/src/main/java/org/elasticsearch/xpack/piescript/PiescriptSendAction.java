/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionType;

/**
 * Internal transport action for cross-node channel sends. This is a node-to-node
 * action, not exposed via REST. When a piescript program sends a value to a channel
 * on a remote node, the evaluator dispatches through this action.
 *
 * <p>The response is {@link ActionResponse.Empty} — send is fire-and-forget.
 * Results flow back through channels, not through the transport response.
 */
public class PiescriptSendAction extends ActionType<ActionResponse.Empty> {

    public static final PiescriptSendAction INSTANCE = new PiescriptSendAction();
    public static final String NAME = "internal:compute/piescript/send";

    private PiescriptSendAction() {
        super(NAME);
    }
}
