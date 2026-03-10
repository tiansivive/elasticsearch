/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakFilters;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.test.TestClustersThreadFilter;
import org.elasticsearch.test.cluster.ElasticsearchCluster;
import org.elasticsearch.test.cluster.local.distribution.DistributionType;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.Before;
import org.junit.ClassRule;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

@ThreadLeakFilters(filters = TestClustersThreadFilter.class)
public class PiescriptIT extends ESRestTestCase {

    @ClassRule
    public static ElasticsearchCluster cluster = ElasticsearchCluster.local()
        .distribution(DistributionType.DEFAULT)
        .setting("xpack.security.enabled", "false")
        .setting("xpack.license.self_generated.type", "trial")
        .build();

    @Override
    protected String getTestRestCluster() {
        return cluster.getHttpAddresses();
    }

    @Before
    public void setupIndex() throws IOException {
        if (indexExists("piescript-test")) {
            return;
        }
        Request createIndex = new Request("PUT", "/piescript-test");
        createIndex.setJsonEntity("""
            {"settings":{"number_of_shards":1,"number_of_replicas":0}}
            """);
        assertOK(adminClient().performRequest(createIndex));

        Request bulk = new Request("POST", "/piescript-test/_bulk");
        bulk.addParameter("refresh", "true");
        bulk.setJsonEntity("""
            {"index":{}}
            {"message":"hello","status":200}
            {"index":{}}
            {"message":"world","status":500}
            {"index":{}}
            {"message":"error","status":503}
            """);
        assertOK(adminClient().performRequest(bulk));
    }

    public void testBasicQueryPassthrough() throws IOException {
        Request request = piescriptRequest("query FROM piescript-test | SORT status ASC | LIMIT 10;");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.containsKey("columns"), equalTo(true));
        assertThat(responseMap.containsKey("values"), equalTo(true));

        @SuppressWarnings("unchecked")
        List<List<Object>> values = (List<List<Object>>) responseMap.get("values");
        assertThat(values, hasSize(3));
    }

    public void testQueryWithFilter() throws IOException {
        Request request = piescriptRequest("query FROM piescript-test | WHERE status >= 500;");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        @SuppressWarnings("unchecked")
        List<List<Object>> values = (List<List<Object>>) responseMap.get("values");
        assertThat(values, hasSize(2));
    }

    public void testInvalidEsqlQuery() throws IOException {
        Request request = piescriptRequest("query INVALID SYNTAX HERE;");
        ResponseException e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), greaterThanOrEqualTo(400));
    }

    public void testEmptyProgram() throws IOException {
        Request request = new Request("POST", "/_piescript/eval");
        request.setJsonEntity("""
            {"program":""}
            """);
        ResponseException e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), equalTo(400));
        assertThat(e.getMessage(), containsString("[program] is required"));
    }

    public void testMalformedProgramNoQueryPrefix() throws IOException {
        Request request = piescriptRequest("FROM piescript-test;");
        ResponseException e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), greaterThanOrEqualTo(400));
        assertThat(e.getMessage(), containsString("must start with 'query'"));
    }

    public void testMissingProgramField() throws IOException {
        Request request = new Request("POST", "/_piescript/eval");
        request.setJsonEntity("""
            {"something":"else"}
            """);
        ResponseException e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), greaterThanOrEqualTo(400));
        assertThat(e.getMessage(), containsString("[program]"));
    }

    private static Request piescriptRequest(String program) {
        Request request = new Request("POST", "/_piescript/eval");
        request.setJsonEntity("{\"program\":\"" + program + "\"}");
        request.addParameter("error_trace", "true");
        RequestOptions.Builder options = RequestOptions.DEFAULT.toBuilder();
        options.setWarningsHandler(warnings -> false);
        request.setOptions(options);
        return request;
    }
}
