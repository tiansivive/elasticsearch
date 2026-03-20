#!/usr/bin/env bash
# Multi-node cross-node execution tests.
# Requires: 3-node cluster with security disabled, piescript-test index created.
#
# Run with:
#   ./gradlew run -Dtests.es.xpack.security.enabled=false -Drun.license_type=trial \
#     -I ../scripts/es-dev-config/multinode.gradle
# Then:
#   bash x-pack/plugin/piescript/debug/setup-test-index-multinode.sh
#   bash x-pack/plugin/piescript/debug/test-multinode.sh

BASE="localhost:9200"
EVAL="$BASE/_piescript/eval"
CT='Content-Type: application/json'

post() { curl -s -X POST "$EVAL" -H "$CT" -d "$1" | jq; }

echo "========================================"
echo "  Multi-Node Cross-Execution Tests"
echo "========================================"

# ── 1. Cluster topology — verify we see multiple nodes ──
echo ""
echo "=== 1. Cluster topology (expect 3 nodes) ==="
post '{"program": "let topo = topology \"cluster\" in { local_id: topo.local.id, local_name: topo.local.name, node_count: length topo.nodes }"}'

# ── 2. List all node names ──
echo ""
echo "=== 2. All node names ==="
post '{"program": "let topo = topology \"cluster\" in map (fn n -> n.name) topo.nodes"}'

# ── 3. Send closure to local inbox — basic smoke test ──
echo ""
echo "=== 3. Send to local inbox (echo back node id) ==="
post '{"program": "let topo = topology \"cluster\" in let ch = spawn! in let u = send topo.local.inbox (fn info -> send ch info.id) in when (ch result) -> result"}'

# ── 4. Send closure to a remote node's inbox ──
# Pick the second node in the list (likely different from local).
echo ""
echo "=== 4. Send to remote node inbox (echo back remote node id) ==="
post '{"program": "let topo = topology \"cluster\" in let remote = head (filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch info.id) in when (ch result) -> { local: topo.local.id, remote_ran_on: result }"}'

# ── 5. Send closure to ALL remote nodes (fan-out) ──
# Each remote node sends back its name; we collect results.
echo ""
echo "=== 5. Fan-out: send closure to each remote node ==="
.
# ── 6. Remote computation — send arithmetic to a remote node ──
echo ""
echo "=== 6. Remote computation (1 + 2 + 3 on remote node) ==="
post '{"program": "let topo = topology \"cluster\" in let remote = head (filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch (1 + 2 + 3)) in when (ch result) -> { computed_on: remote.name, result: result }"}'

# ── 7. Round-trip: send data to remote, transform it there, get it back ──
echo ""
echo "=== 7. Round-trip: send value, transform remotely, return ==="
post '{"program": "let topo = topology \"cluster\" in let remote = head (filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch { node: info.name, answer: 21 * 2 }) in when (ch result) -> result"}'

# ── 8. Verify local vs remote — prove code ran on different node ──
echo ""
echo "=== 8. Prove remote execution (local id != remote execution id) ==="
post '{"program": "let topo = topology \"cluster\" in let remote = head (filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch info.id) in when (ch remote_id) -> { local: topo.local.id, remote: remote_id, same_node: topo.local.id == remote_id }"}'

# ── 9. Triangle coordination: A orchestrates B↔C direct communication ──
# A sends identical closures (abstracted via let-binding) to B and C.
# Each creates local channels, sends refs back to A.
# A cross-forwards the refs so B and C can message each other directly.
# B and C ack back to A after receiving each other's message.
# Exercises: name-passing (channels-in-channels), closure capture, multi-phase
# coordination, direct B↔C communication, higher-order function abstraction.
echo ""
echo "=== 9. Triangle: A orchestrates B↔C direct communication ==="
PROG9='let topo = topology "cluster"
in let remotes = filter (fn n -> n.id != topo.local.id) topo.nodes
in let nodeB = head remotes
in let nodeC = head (tail remotes)

in let ackB = spawn!
in let ackC = spawn!
in let refsFromB = spawn!
in let refsFromC = spawn!

in let makeAgent = fn refsBack ack label -> fn info ->
  let myCh = spawn!
  in let peerCh = spawn!
  in let u = send refsBack { myCh: myCh, peerCh: peerCh }
  in when (peerCh peer) ->
    let u2 = send peer { from: info.name, role: label }
    in when (myCh msg) ->
      send ack { node: info.name, role: label,
                 peer_node: msg.from, peer_role: msg.role }

in let u1 = send nodeB.inbox (makeAgent refsFromB ackB "agent-B")
in let u3 = send nodeC.inbox (makeAgent refsFromC ackC "agent-C")

in when (refsFromB bRefs) & (refsFromC cRefs) ->
  let u5 = send bRefs.peerCh cRefs.myCh
  in let u6 = send cRefs.peerCh bRefs.myCh
  in when (ackB rb) & (ackC rc) ->
    { b_ran_on: rb.node, b_role: rb.role,
      b_heard_from: rb.peer_node, b_peer_role: rb.peer_role,
      c_ran_on: rc.node, c_role: rc.role,
      c_heard_from: rc.peer_node, c_peer_role: rc.peer_role }'
post "$(jq -n --arg p "$PROG9" '{"program": $p}')"

echo ""
echo "========================================"
echo "  Done"
echo "========================================"
