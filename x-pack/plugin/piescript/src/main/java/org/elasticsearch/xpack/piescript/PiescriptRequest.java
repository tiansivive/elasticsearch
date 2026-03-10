/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.CompositeIndicesRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

import static org.elasticsearch.action.ValidateActions.addValidationError;

public class PiescriptRequest extends ActionRequest implements CompositeIndicesRequest {

    private final String program;

    public PiescriptRequest(String program) {
        this.program = program;
    }

    public PiescriptRequest(StreamInput in) throws IOException {
        super(in);
        this.program = in.readString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(program);
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
}
