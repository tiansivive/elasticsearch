/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import java.util.Locale;

/**
 * Extracts index pattern information from the raw ESQL body text captured
 * by the lexer's {@code ESQL_MODE}.
 *
 * <p>Currently uses simple string parsing rather than the ANTLR grammar to
 * extract the {@code FROM} clause index patterns. This is a pragmatic v0
 * approach; a future improvement should parse the {@code FROM} clause
 * structurally in the ANTLR grammar to avoid fragile string manipulation.
 *
 * <p>The rest of the ESQL body (pipes, WHERE, etc.) is treated as opaque
 * text passed through to {@code EsqlQueryAction}.
 */
public final class EsqlBodyParser {

    private EsqlBodyParser() {}

    /**
     * Parsed information from an ESQL body.
     *
     * @param indexPattern the comma-separated index pattern(s) from the FROM clause
     *                     (e.g., {@code "logs-*"} or {@code "logs-nginx, logs-apache"})
     * @param fullEsqlQuery the complete ESQL query text, suitable for {@code EsqlQueryAction}
     */
    public record ParsedEsqlBody(String indexPattern, String fullEsqlQuery) {}

    /**
     * Parse the raw ESQL body text captured by the lexer.
     *
     * <p>The body arrives with enclosing backtick delimiters from the lexer's
     * {@code ESQL_MODE}; these are stripped before processing.
     *
     * <p>Expects the body to start with {@code FROM <index-pattern>}. The index
     * pattern portion ends at the first {@code |} (pipe), {@code METADATA}
     * keyword, or end of string.
     *
     * @param esqlBody the raw text from the {@code ESQL_BODY} token (backtick-delimited)
     * @return parsed body with extracted index pattern and full query
     * @throws IllegalArgumentException if the body doesn't start with FROM
     *         or contains no index patterns
     */
    public static ParsedEsqlBody parse(String esqlBody) {
        String stripped = esqlBody.strip();
        if (stripped.startsWith("`") && stripped.endsWith("`")) {
            stripped = stripped.substring(1, stripped.length() - 1);
        }
        String trimmed = stripped.strip();
        if (trimmed.toUpperCase(Locale.ROOT).startsWith("FROM ") == false) {
            throw new IllegalArgumentException("ESQL query body must start with FROM, got: " + truncate(trimmed));
        }

        String afterFrom = trimmed.substring(5);

        int endIdx = afterFrom.length();
        int pipeIdx = afterFrom.indexOf('|');
        if (pipeIdx >= 0) {
            endIdx = Math.min(endIdx, pipeIdx);
        }
        int metaIdx = afterFrom.toUpperCase(Locale.ROOT).indexOf("METADATA");
        if (metaIdx >= 0) {
            endIdx = Math.min(endIdx, metaIdx);
        }

        String patternsPart = afterFrom.substring(0, endIdx).strip();
        if (patternsPart.isEmpty()) {
            throw new IllegalArgumentException("no index patterns found in FROM clause");
        }

        return new ParsedEsqlBody(patternsPart, trimmed);
    }

    private static String truncate(String s) {
        return s.length() > 40 ? s.substring(0, 40) + "..." : s;
    }
}
