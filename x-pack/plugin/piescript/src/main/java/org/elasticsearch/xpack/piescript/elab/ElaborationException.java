/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

/**
 * Thrown by the elaborator on the first type error (fail-fast, per D1.14).
 * Carries source location as line/column (avoiding runtime dependency on
 * {@code Source.source()} which transitively requires the compute module).
 */
public class ElaborationException extends RuntimeException {

    private final int line;
    private final int column;
    private final TypeError typeError;

    public ElaborationException(int line, int column, TypeError typeError, String message) {
        super(formatMessage(line, column, message));
        this.line = line;
        this.column = column;
        this.typeError = typeError;
    }

    public ElaborationException(int line, int column, String message) {
        super(formatMessage(line, column, message));
        this.line = line;
        this.column = column;
        this.typeError = null;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public TypeError typeError() {
        return typeError;
    }

    private static String formatMessage(int line, int column, String message) {
        if (line <= 0) {
            return message;
        }
        return "line " + line + ":" + column + " " + message;
    }
}
