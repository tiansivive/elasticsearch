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
import static org.hamcrest.Matchers.instanceOf;

@ThreadLeakFilters(filters = TestClustersThreadFilter.class)
public class PiescriptIT extends ESRestTestCase {

    @ClassRule
    public static ElasticsearchCluster cluster = ElasticsearchCluster.local()
        .distribution(DistributionType.DEFAULT)
        .setting("xpack.security.enabled", "false")
        .setting("xpack.ml.enabled", "false")
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

        if (indexExists("piescript-typed") == false) {
            Request createTyped = new Request("PUT", "/piescript-typed");
            createTyped.setJsonEntity("""
                {
                  "settings": {"number_of_shards": 1, "number_of_replicas": 0},
                  "mappings": {
                    "properties": {
                      "name":   {"type": "keyword"},
                      "age":    {"type": "integer"},
                      "active": {"type": "boolean"}
                    }
                  }
                }
                """);
            assertOK(adminClient().performRequest(createTyped));

            Request bulkTyped = new Request("POST", "/piescript-typed/_bulk");
            bulkTyped.addParameter("refresh", "true");
            bulkTyped.setJsonEntity("""
                {"index":{}}
                {"name":"alice","age":30,"active":true}
                {"index":{}}
                {"name":"bob","age":25,"active":false}
                {"index":{}}
                {"name":"carol","age":35,"active":true}
                """);
            assertOK(adminClient().performRequest(bulkTyped));
        }
    }

    public void testQueryTypechecking() throws IOException {
        Request request = piescriptDevRequest("query `FROM piescript-test | SORT status ASC | LIMIT 10`");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.containsKey("type"), equalTo(true));
        String type = (String) responseMap.get("type");
        assertThat(type, containsString("Stream"));
        assertThat(type, containsString("message"));
        assertThat(type, containsString("status"));
        assertThat(responseMap.containsKey("eval"), equalTo(true));
    }

    public void testQueryTypecheckingWithFilter() throws IOException {
        Request request = piescriptDevRequest("query `FROM piescript-test | WHERE status >= 500`");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        String type = (String) responseMap.get("type");
        assertThat(type, containsString("Stream"));
        assertThat(responseMap.containsKey("eval"), equalTo(true));
    }

    // ──── Eager query evaluation (Phase 2.8) ────

    public void testQueryEvalReturnsStream() throws IOException {
        Request request = piescriptRequest("query `FROM piescript-typed | SORT name ASC | LIMIT 10`");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        String type = (String) responseMap.get("type");
        assertThat(type, containsString("Stream"));
        Object result = responseMap.get("result");
        assertThat(result, instanceOf(List.class));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result;
        assertThat(rows, hasSize(3));
        assertThat(rows.get(0).get("name"), equalTo("alice"));
        assertThat(rows.get(1).get("name"), equalTo("bob"));
        assertThat(rows.get(2).get("name"), equalTo("carol"));
    }

    public void testQueryEvalMapProjectField() throws IOException {
        Request request = piescriptRequest("query `FROM piescript-typed | SORT name ASC | LIMIT 10` |> map (fn r -> r.name)");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        Object result = responseMap.get("result");
        assertThat(result, instanceOf(List.class));
        @SuppressWarnings("unchecked")
        List<Object> elements = (List<Object>) result;
        assertThat(elements, hasSize(3));
        assertThat(elements.get(0), equalTo("alice"));
        assertThat(elements.get(1), equalTo("bob"));
        assertThat(elements.get(2), equalTo("carol"));
    }

    public void testQueryEvalFilterByPredicate() throws IOException {
        Request request = piescriptRequest("query `FROM piescript-typed | SORT name ASC | LIMIT 10` |> filter (fn r -> r.active)");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        Object result = responseMap.get("result");
        assertThat(result, instanceOf(List.class));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result;
        assertThat(rows, hasSize(2));
        assertThat(rows.get(0).get("name"), equalTo("alice"));
        assertThat(rows.get(1).get("name"), equalTo("carol"));
    }

    public void testQueryEvalReduceSumAges() throws IOException {
        Request request = piescriptRequest("query `FROM piescript-typed | SORT name ASC | LIMIT 10` |> reduce (fn acc r -> acc + r.age) 0");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.get("result"), equalTo(90));
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

    // ──── Expression evaluation (Phase 1c) ────

    public void testExpressionEval() throws IOException {
        Request request = piescriptRequest("let x = 1 + 2 in x");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.get("type"), equalTo("Integer"));
        assertThat(responseMap.get("result"), equalTo(3));
    }

    public void testRecordEval() throws IOException {
        Request request = piescriptRequest("{ x: 1, y: 2 }");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.get("type"), equalTo("{ x: Integer, y: Integer }"));
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) responseMap.get("result");
        assertThat(result.get("x"), equalTo(1));
        assertThat(result.get("y"), equalTo(2));
    }

    public void testLambdaEval() throws IOException {
        Request request = piescriptRequest("fn x -> x");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.get("result"), equalTo("<function>"));
    }

    public void testBooleanEval() throws IOException {
        Request request = piescriptRequest("true && false");
        Response response = client().performRequest(request);
        assertOK(response);

        Map<String, Object> responseMap = entityAsMap(response);
        assertThat(responseMap.get("type"), equalTo("Boolean"));
        assertThat(responseMap.get("result"), equalTo(false));
    }

    public void testTypeError() throws IOException {
        Request request = piescriptRequest("1 + true");
        ResponseException e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), greaterThanOrEqualTo(400));
    }

    public void testParseError() throws IOException {
        Request request = piescriptRequest("let = in");
        ResponseException e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), greaterThanOrEqualTo(400));
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

    private static Request piescriptDevRequest(String program) {
        Request request = new Request("POST", "/_piescript/dev");
        request.setJsonEntity("{\"program\":\"" + program + "\"}");
        request.addParameter("error_trace", "true");
        RequestOptions.Builder options = RequestOptions.DEFAULT.toBuilder();
        options.setWarningsHandler(warnings -> false);
        request.setOptions(options);
        return request;
    }
}
