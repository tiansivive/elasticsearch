/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.xpack.piescript.eval.Value;
import org.elasticsearch.xpack.piescript.eval.ValueSerialization;

import java.io.IOException;

/**
 * Transport request for cross-node channel sends. Carries a target channel ID
 * and the value payload. The channel ID may be a regular channel (completed
 * in the target node's {@code ChannelRegistry}) or the well-known {@code "inbox"}
 * channel (evaluated as a closure on the target node).
 *
 * <p>Uses {@link ValueSerialization} for full-fidelity payload serialization,
 * including closures with captured environments.
 */
public class PiescriptSendRequest extends ActionRequest {

    public static final String INBOX_CHANNEL_ID = "inbox";

    private final String channelId;
    private final Value payload;

    public PiescriptSendRequest(String channelId, Value payload) {
        this.channelId = channelId;
        this.payload = payload;
    }

    public PiescriptSendRequest(StreamInput in) throws IOException {
        super(in);
        this.channelId = in.readString();
        this.payload = ValueSerialization.readValue(in);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(channelId);
        ValueSerialization.writeValue(out, payload);
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    public String channelId() {
        return channelId;
    }

    public Value payload() {
        return payload;
    }
}
