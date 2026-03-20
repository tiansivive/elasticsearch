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
import org.elasticsearch.test.TestClustersThreadFilter;
import org.elasticsearch.test.cluster.ElasticsearchCluster;
import org.elasticsearch.test.cluster.local.distribution.DistributionType;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.ClassRule;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Multi-node integration tests for piescript cross-node code execution (Block C.5).
 *
 * <p>Exercises {@code topology}, {@code send}, {@code spawn!}, {@code when}, and inbox-based
 * closure shipping across a 3-node cluster. Each test sends a piescript program via REST,
 * which the coordinator node elaborates, evaluates, and (for cross-node tests) ships closures
 * to remote nodes for execution.
 */
@ThreadLeakFilters(filters = TestClustersThreadFilter.class)
public class PiescriptMultiNodeIT extends ESRestTestCase {

    @ClassRule
    public static ElasticsearchCluster cluster = ElasticsearchCluster.local()
        .nodes(3)
        .distribution(DistributionType.DEFAULT)
        .setting("xpack.security.enabled", "false")
        .setting("xpack.ml.enabled", "false")
        .setting("xpack.license.self_generated.type", "trial")
        .build();

    @Override
    protected String getTestRestCluster() {
        return cluster.getHttpAddresses();
    }

    // ──── Topology ────

    public void testTopologyShowsThreeNodes() throws IOException {
        var result = evalRecord("Cluster.topology \"cluster\"");
        @SuppressWarnings("unchecked")
        var nodes = (List<Map<String, Object>>) result.get("nodes");
        assertThat(nodes.size(), is(3));
    }

    public void testTopologyListsAllNodeNames() throws IOException {
        var result = evalList("let topo = Cluster.topology \"cluster\" in List.map (fn n -> n.name) topo.nodes");
        assertThat(result.size(), is(3));
        for (var name : result) {
            assertThat(name, instanceOf(String.class));
        }
    }

    public void testTopologyLocalNodeHasInbox() throws IOException {
        var result = evalRecord("let topo = Cluster.topology \"cluster\" in topo.local");
        assertThat(result.get("id"), notNullValue());
        assertThat(result.get("name"), notNullValue());
        assertThat(result.get("address"), notNullValue());
    }

    // ──── Local inbox ────

    public void testSendToLocalInbox() throws IOException {
        var result = eval(
            "let topo = Cluster.topology \"cluster\" "
                + "in let ch = spawn! "
                + "in let u = send topo.local.inbox (fn info -> send ch info.id) "
                + "in when (ch result) -> result"
        );
        assertThat(result, instanceOf(String.class));
        assertThat(((String) result).isEmpty(), is(false));
    }

    // ──── Remote inbox ────

    public void testSendToRemoteInbox() throws IOException {
        var result = evalRecord(
            "let topo = Cluster.topology \"cluster\" "
                + "in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) "
                + "in let ch = spawn! "
                + "in let u = send remote.inbox (fn info -> send ch info.id) "
                + "in when (ch result) -> { local: topo.local.id, remote_ran_on: result }"
        );
        assertThat(result.get("local"), notNullValue());
        assertThat(result.get("remote_ran_on"), notNullValue());
        assertThat(result.get("local"), not(equalTo(result.get("remote_ran_on"))));
    }

    public void testRemoteComputation() throws IOException {
        var result = evalRecord(
            "let topo = Cluster.topology \"cluster\" "
                + "in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) "
                + "in let ch = spawn! "
                + "in let u = send remote.inbox (fn info -> send ch (1 + 2 + 3)) "
                + "in when (ch result) -> { computed_on: remote.name, result: result }"
        );
        assertThat(result.get("result"), equalTo(6));
    }

    public void testRemoteRoundTripTransform() throws IOException {
        var result = evalRecord(
            "let topo = Cluster.topology \"cluster\" "
                + "in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) "
                + "in let ch = spawn! "
                + "in let u = send remote.inbox (fn info -> send ch { node: info.name, answer: 21 * 2 }) "
                + "in when (ch result) -> result"
        );
        assertThat(result.get("node"), instanceOf(String.class));
        assertThat(result.get("answer"), equalTo(42));
    }

    // ──── Prove remote execution ────

    public void testProveCodeRanOnDifferentNode() throws IOException {
        var result = evalRecord(
            "let topo = Cluster.topology \"cluster\" "
                + "in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) "
                + "in let ch = spawn! "
                + "in let u = send remote.inbox (fn info -> send ch info.id) "
                + "in when (ch remote_id) -> "
                + "  { local: topo.local.id, remote: remote_id, same_node: topo.local.id == remote_id }"
        );
        assertThat(result.get("same_node"), equalTo(false));
        assertThat(result.get("local"), not(equalTo(result.get("remote"))));
    }

    // ──── Fan-out ────

    public void testFanOutToAllRemoteNodes() throws IOException {
        var result = evalRecord(
            "let topo = Cluster.topology \"cluster\" "
                + "in let remotes = List.filter (fn n -> n.id != topo.local.id) topo.nodes "
                + "in let ch1 = spawn! "
                + "in let ch2 = spawn! "
                + "in let u1 = send (List.head remotes).inbox (fn info -> send ch1 info.name) "
                + "in let rest = List.tail remotes "
                + "in let u2 = send (List.head rest).inbox (fn info -> send ch2 info.name) "
                + "in when (ch1 name1) & (ch2 name2) -> { ran_on_1: name1, ran_on_2: name2 }"
        );
        assertThat(result.get("ran_on_1"), instanceOf(String.class));
        assertThat(result.get("ran_on_2"), instanceOf(String.class));
        assertThat(result.get("ran_on_1"), not(equalTo(result.get("ran_on_2"))));
    }

    // ──── Math builtins on remote node ────

    public void testMathOnRemoteNode() throws IOException {
        var result = evalRecord(
            "let topo = Cluster.topology \"cluster\" "
                + "in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) "
                + "in let ch = spawn! "
                + "in let u = send remote.inbox (fn info -> send ch { s: Math.sqrt 16, a: Math.abs (-42), p: Math.pow 2 10 }) "
                + "in when (ch r) -> r"
        );
        assertThat(result.get("s"), equalTo(4));
        assertThat(result.get("a"), equalTo(42));
        assertThat(result.get("p"), equalTo(1024));
    }

    // ──── Helpers ────

    private Object eval(String program) throws IOException {
        Request request = piescriptRequest(program);
        Response response = client().performRequest(request);
        assertOK(response);
        Map<String, Object> responseMap = entityAsMap(response);
        return responseMap.get("result");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> evalRecord(String program) throws IOException {
        Object result = eval(program);
        assertThat(result, instanceOf(Map.class));
        return (Map<String, Object>) result;
    }

    @SuppressWarnings("unchecked")
    private List<Object> evalList(String program) throws IOException {
        Object result = eval(program);
        assertThat(result, instanceOf(List.class));
        return (List<Object>) result;
    }

    private static Request piescriptRequest(String program) {
        Request request = new Request("POST", "/_piescript/eval");
        String escaped = program.replace("\\", "\\\\").replace("\"", "\\\"");
        request.setJsonEntity("{\"program\":\"" + escaped + "\"}");
        request.addParameter("error_trace", "true");
        RequestOptions.Builder options = RequestOptions.DEFAULT.toBuilder();
        options.setWarningsHandler(warnings -> false);
        request.setOptions(options);
        return request;
    }
}
