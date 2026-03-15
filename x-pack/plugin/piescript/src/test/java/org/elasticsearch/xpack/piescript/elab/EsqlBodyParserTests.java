/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.test.ESTestCase;

public class EsqlBodyParserTests extends ESTestCase {

    public void testSimpleFromClause() {
        var result = EsqlBodyParser.parse("FROM logs-*");
        assertEquals("logs-*", result.indexPattern());
        assertEquals("FROM logs-*", result.fullEsqlQuery());
    }

    public void testFromClauseWithPipe() {
        var result = EsqlBodyParser.parse("FROM logs-* | WHERE status >= 500");
        assertEquals("logs-*", result.indexPattern());
        assertEquals("FROM logs-* | WHERE status >= 500", result.fullEsqlQuery());
    }

    public void testMultipleIndexPatterns() {
        var result = EsqlBodyParser.parse("FROM logs-nginx, logs-apache | LIMIT 10");
        assertEquals("logs-nginx, logs-apache", result.indexPattern());
    }

    public void testFromClauseWithMetadata() {
        var result = EsqlBodyParser.parse("FROM logs-* METADATA _id | WHERE status > 200");
        assertEquals("logs-*", result.indexPattern());
        assertEquals("FROM logs-* METADATA _id | WHERE status > 200", result.fullEsqlQuery());
    }

    public void testFromClauseWithMetadataOnly() {
        var result = EsqlBodyParser.parse("FROM logs-* METADATA _id, _index");
        assertEquals("logs-*", result.indexPattern());
    }

    public void testLeadingWhitespace() {
        var result = EsqlBodyParser.parse("  FROM  logs-*  | LIMIT 5");
        assertEquals("logs-*", result.indexPattern());
        assertEquals("FROM  logs-*  | LIMIT 5", result.fullEsqlQuery());
    }

    public void testCaseInsensitiveFrom() {
        var result = EsqlBodyParser.parse("from logs-*");
        assertEquals("logs-*", result.indexPattern());
    }

    public void testWildcardPattern() {
        var result = EsqlBodyParser.parse("FROM *");
        assertEquals("*", result.indexPattern());
    }

    public void testMultiplePipes() {
        var result = EsqlBodyParser.parse("FROM metrics-* | WHERE cpu > 0.9 | SORT @timestamp DESC | LIMIT 100");
        assertEquals("metrics-*", result.indexPattern());
    }

    public void testMissingFromClause() {
        var e = expectThrows(IllegalArgumentException.class, () -> EsqlBodyParser.parse("SELECT * FROM logs"));
        assertThat(e.getMessage(), org.hamcrest.Matchers.containsString("must start with FROM"));
    }

    public void testEmptyPatterns() {
        var e = expectThrows(IllegalArgumentException.class, () -> EsqlBodyParser.parse("FROM | WHERE x > 1"));
        assertThat(e.getMessage(), org.hamcrest.Matchers.containsString("no index patterns"));
    }

    public void testEmptyBody() {
        var e = expectThrows(IllegalArgumentException.class, () -> EsqlBodyParser.parse(""));
        assertThat(e.getMessage(), org.hamcrest.Matchers.containsString("must start with FROM"));
    }
}
