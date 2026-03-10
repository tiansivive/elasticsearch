/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.parser;

/**
 * Thrown when a Piescript program cannot be parsed.
 */
public class PiescriptParsingException extends RuntimeException {

    public PiescriptParsingException(String message) {
        super(message);
    }

    public PiescriptParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
