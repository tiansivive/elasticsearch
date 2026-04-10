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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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

    @Before
    public void setupWriteIndex() throws IOException {
        if (indexExists("piescript-mn-write") == false) {
            Request createIndex = new Request("PUT", "/piescript-mn-write");
            createIndex.setJsonEntity("""
                {
                  "settings": {"number_of_shards": 3, "number_of_replicas": 0},
                  "mappings": {
                    "properties": {
                      "name":  {"type": "keyword"},
                      "score": {"type": "double"}
                    }
                  }
                }
                """);
            assertOK(adminClient().performRequest(createIndex));
        }

        if (indexExists("piescript-mn-read") == false) {
            Request createRead = new Request("PUT", "/piescript-mn-read");
            createRead.setJsonEntity("""
                {
                  "settings": {"number_of_shards": 1, "number_of_replicas": 0},
                  "mappings": {
                    "properties": {
                      "name":  {"type": "keyword"},
                      "score": {"type": "double"}
                    }
                  }
                }
                """);
            assertOK(adminClient().performRequest(createRead));

            Request bulk = new Request("POST", "/piescript-mn-read/_bulk");
            bulk.addParameter("refresh", "true");
            bulk.setJsonEntity("""
                {"index":{}}
                {"name":"alice","score":100.0}
                {"index":{}}
                {"name":"bob","score":95.5}
                {"index":{}}
                {"name":"carol","score":80.0}
                """);
            assertOK(adminClient().performRequest(bulk));
        }
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

    // ──── Block E: write primitives (D-051) ────

    public void testRemoteShardWrite() throws IOException {
        var result = evalRecord(
            "use \"piescript-mn-write\" as dest; "
                + "let shards = Index.shards dest "
                + "in let primary = List.head (List.filter (fn s -> s.primary) shards) "
                + "in let ch = spawn! "
                + "in let u = send primary.node.inbox (fn info -> "
                + "  let wch = Shard.writer dest primary "
                + "  in when (wch writer) -> "
                + "    let r = Shard.write writer \"remote-write-1\" { name: \"remote-write\", score: 99 } "
                + "    in send ch { node: info.name, seq_no: r.seq_no }"
                + ") "
                + "in when (ch result) -> result"
        );
        assertThat(result.get("node"), instanceOf(String.class));
        assertThat(((Number) result.get("seq_no")).doubleValue(), greaterThanOrEqualTo(0.0));
    }

    public void testWriterValNotSerializableOverWire() throws IOException {
        Request request = piescriptRequest(
            "use \"piescript-mn-write\" as dest; "
                + "let shards = Index.shards dest "
                + "in let primary = List.head (List.filter (fn s -> s.primary) shards) "
                + "in let ch = spawn! "
                + "in let u = send primary.node.inbox (fn info -> "
                + "  let wch = Shard.writer dest primary "
                + "  in when (wch writer) -> send ch writer"
                + ") "
                + "in when (ch w) -> w"
        );
        var e = expectThrows(ResponseException.class, () -> client().performRequest(request));
        assertThat(e.getResponse().getStatusLine().getStatusCode(), greaterThanOrEqualTo(400));
    }

    // ──── Block G: Exchange streaming (D-054) ────

    public void testRemoteExchangeStreaming() throws IOException {
        String program = """
            use "piescript-mn-read" as idx;
            let shards = Index.shards idx;
            let shard = List.head shards;
            let topo = Cluster.topology "cluster";
            
            let ch = spawn!;
            let u = send shard.node.inbox (fn info ->
              let exch = Exchange.open ["name", "score"] 1024.0 in
              let sink = Exchange.sink exch in
              let sch = Shard.open idx shard { match_all: true } in
              let u3 = when (sch searcher) ->
                let docs = Shard.consume 100.0 searcher in
                let page = Shard.stream searcher docs in
                let u1 = Exchange.addPage sink page in
                let u2 = Exchange.finish sink in
                send ch { node: info.name, exch: exch }
              in true
            );
            
            when (ch producerResult) ->
              let source = Exchange.connect producerResult.exch in
              let countCh = spawn! in
              let p = Exchange.poll source (fn page ->
                send countCh (Page.count page)
              ) in
              when (p done) & (countCh count) ->
                { producer: producerResult.node, count: count }
            """;
        var result = evalRecord(program);
        assertThat(result.get("producer"), notNullValue());
        assertThat(((Number) result.get("count")).doubleValue(), equalTo(3.0));
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
        String escaped = program.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        request.setJsonEntity("{\"program\":\"" + escaped + "\"}");
        request.addParameter("error_trace", "true");
        RequestOptions.Builder options = RequestOptions.DEFAULT.toBuilder();
        options.setWarningsHandler(warnings -> false);
        request.setOptions(options);
        return request;
    }
}
