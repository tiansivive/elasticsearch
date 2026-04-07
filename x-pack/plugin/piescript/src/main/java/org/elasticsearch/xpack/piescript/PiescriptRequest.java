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

import java.io.IOException;

import static org.elasticsearch.action.ValidateActions.addValidationError;

public class PiescriptRequest extends ActionRequest {

    private final String program;
    private final boolean dev;

    public PiescriptRequest(String program) {
        this(program, false);
    }

    public PiescriptRequest(String program, boolean dev) {
        this.program = program;
        this.dev = dev;
    }

    public PiescriptRequest(StreamInput in) throws IOException {
        super(in);
        this.program = in.readString();
        this.dev = in.readBoolean();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(program);
        out.writeBoolean(dev);
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = null;
        if (program == null || program.isBlank()) {
            validationException = addValidationError("[program] is required", validationException);
        }
        return validationException;
    }

    public String program() {
        return program;
    }

    public boolean dev() {
        return dev;
    }
}
