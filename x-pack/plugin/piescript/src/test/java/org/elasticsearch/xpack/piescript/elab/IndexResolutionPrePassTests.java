/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.piescript.parser.PiescriptParser;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Tests for the pure (non-async) parts of {@link IndexResolutionPrePass}:
 * CST walking and query collection. The async resolution is tested via
 * integration tests in T2.10.
 */
public class IndexResolutionPrePassTests extends ESTestCase {

    private final PiescriptParser parser = new PiescriptParser();

    public void testNoQueriesInPureExpression() {
        var cst = parser.parse("1 + 2");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, is(empty()));
    }

    public void testSingleQueryAtTopLevel() {
        var cst = parser.parse("query `FROM logs-*`");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, hasSize(1));
        assertEquals("logs-*", queries.get(0).parsedBody().indexPattern());
    }

    public void testQueryInLetBinding() {
        var cst = parser.parse("let docs = query `FROM logs-*`; docs");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, hasSize(1));
        assertEquals("logs-*", queries.get(0).parsedBody().indexPattern());
    }

    public void testMultipleQueries() {
        var cst = parser.parse("let a = query `FROM logs-*`; let b = query `FROM metrics-*`; a");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, hasSize(2));
        assertEquals("logs-*", queries.get(0).parsedBody().indexPattern());
        assertEquals("metrics-*", queries.get(1).parsedBody().indexPattern());
    }

    public void testQueryWithPipes() {
        var cst = parser.parse("query `FROM logs-* | WHERE status >= 500 | LIMIT 10`");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, hasSize(1));
        assertEquals("logs-*", queries.get(0).parsedBody().indexPattern());
        assertEquals("FROM logs-* | WHERE status >= 500 | LIMIT 10", queries.get(0).parsedBody().fullEsqlQuery());
    }

    public void testQueryInBlock() {
        var cst = parser.parse("{ let docs = query `FROM logs-*`; docs }");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, hasSize(1));
        assertEquals("logs-*", queries.get(0).parsedBody().indexPattern());
    }

    public void testQueryWithMultipleIndexPatterns() {
        var cst = parser.parse("query `FROM logs-nginx, logs-apache`");
        var queries = IndexResolutionPrePass.collectQueries(cst);
        assertThat(queries, hasSize(1));
        assertEquals("logs-nginx, logs-apache", queries.get(0).parsedBody().indexPattern());
    }
}
