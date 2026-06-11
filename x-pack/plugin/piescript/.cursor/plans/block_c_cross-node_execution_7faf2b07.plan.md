---
name: Block C Cross-Node Execution
overview: "COMPLETE. Cross-node code execution for piescript: `spawn!`, `send`, channel registry, Value/CoreExpr serialization, transport handler, inbox-based remote closure evaluation, builder DSL, multi-node integration tests, numeric unification (Double), and math builtins. Ref: c7b160cb-0062-4a7e-a930-c0ec2437d7ee"
todos:
  - id: d045
    content: Write D-045 decision record in decisions.md capturing all Block C design decisions (ChannelVal, registry, single transport handler, inbox argument, when locality)
    status: completed
  - id: c1-grammar
    content: "C.1: Grammar — add SpawnBangExpr and SendExpr productions, SEND keyword, regenerate ANTLR"
    status: completed
  - id: c1-core-ir
    content: "C.1: Core IR — nullable body on CoreSpawn, new CoreSend node, update CoreExpr permits clause"
    status: completed
  - id: c1-rename
    content: "C.1: Rename SpawnVal to ChannelVal(nodeId, channelId) across the codebase"
    status: completed
  - id: c1-registry
    content: "C.1: Implement ChannelRegistry, add to EvalDependencies with localNodeId"
    status: completed
  - id: c1-elaborator
    content: "C.1: Elaborator — handle SpawnBangExpr in Spawns.java, new Sends.java for SendExpr"
    status: completed
  - id: c1-evaluator
    content: "C.1: Evaluator — CoreSpawn null body, CoreSend local dispatch, update EvalCoordination for ChannelVal"
    status: completed
  - id: c1-tests
    content: "C.1: Tests — parser, elaborator, evaluator tests for spawn! and send (local only)"
    status: completed
  - id: c2-value-serial
    content: "C.2: Value serialization — writeTo/readFrom with type-tag dispatch for all 11 variants"
    status: completed
  - id: c2-corexpr-serial
    content: "C.2: CoreExpr serialization — writeTo/readFrom for all 16 variants plus MonoType, RowType, LitVal, Op"
    status: completed
  - id: c2-tests
    content: "C.2: Serialization round-trip tests for every Value and CoreExpr variant"
    status: completed
  - id: c3-transport
    content: "C.3: Transport handler — PiescriptSendRequest, PiescriptSendAction, TransportPiescriptSendAction"
    status: completed
  - id: c3-inbox
    content: "C.3: Inbox registration at plugin startup — persistent ActionListener that evaluates closures with node info"
    status: completed
  - id: c3-remote-send
    content: "C.3: Remote send routing in Evaluator — local vs remote dispatch based on nodeId"
    status: completed
  - id: c3-when-check
    content: "C.3: when locality check in EvalCoordination — reject remote channels"
    status: completed
  - id: c3-topology
    content: "C.3: Add inbox field to topology node records (EvalTopology + Prelude type scheme)"
    status: completed
  - id: c3-inbox-async
    content: "C.3: Fix inbox handler to be fire-and-forget — respond immediately, evaluate closure asynchronously (D-047)"
    status: completed
  - id: c3-local-inbox
    content: "C.3: Fix local inbox sends — always route inbox through transport, not ChannelRegistry"
    status: completed
  - id: c4-dsl
    content: "C.4: CoreExpr/Value/MonoType builder DSL — implemented as Exprs, Values, Types (not CoreDsl/ValueDsl/TypeDsl as planned)"
    status: completed
  - id: c5-integration
    content: "C.5: Multi-node integration tests — 10 tests in PiescriptMultiNodeIT on 3-node cluster (topology, inbox, fan-out, remote compute, round-trip, prove-remote, math-on-remote)"
    status: completed
  - id: numeric-unification
    content: "Bonus: Unified all numbers to Double (D-020 resolved) — integer literals elaborate to DoubleLit, ESQL numerics widened at boundary"
    status: completed
  - id: math-builtins
    content: "Bonus: 10 math builtins (abs, floor, ceil, round, sqrt, log, min, max, pow, toInt) in Prelude + EvalBuiltins"
    status: completed
  - id: docs
    content: Update current-state.md, roadmap.md — Block C fully complete, test counts, session refs, stale entries cleaned
    status: completed
isProject: false
---

# Block C — Cross-Node Code Execution

## Decision Record: D-045

Before implementation, write D-045 in [decisions.md](x-pack/plugin/piescript/docs/decisions.md) capturing the design decisions from this discussion:

- `**SpawnVal` renamed to `ChannelVal**`: reflects that channels are the concept, spawn is just one way to create them.
- `**ChannelVal(nodeId, channelId)**`: metadata-only representation. The `SubscribableListener` moves to a per-node `ChannelRegistry`. Trivially serializable (two strings).
- `**ChannelRegistry**`: `ConcurrentHashMap<String, ActionListener<Value>>` per node. Regular channels auto-remove on completion. Inbox is a persistent reusable entry.
- **One transport handler (`piescript/send`)**: receives `(channelId, value)`, calls `registry.get(channelId).onResponse(value)`. Uniform, value-agnostic, channel-agnostic. No branching on channel ID or value type.
- **Inbox argument = local node info**: the inbox closure receives the local node's info record as its lambda argument. Dependency injection via lambda abstraction; eliminates the need for a `local_node` primitive. Inbox type: `Channel (NodeInfo -> Null)`.
- `**when` only works on local channels: natural detection — program only sees remote inboxes (via topology), and `SubscribableListener` lookup only finds locally-spawned channels.
- **Channels carrying channels**: `Channel (Channel a)` is a natural pattern (pi-calculus name passing) that falls out of HM inference.

---

## C.1 — `spawn!` + `send` + ChannelRegistry (local only)

All unit-testable with `DIRECT_EXECUTOR_SERVICE`. No transport, no serialization.

### Grammar

In [PiescriptAntlrParser.g4](x-pack/plugin/piescript/src/main/antlr/PiescriptAntlrParser.g4), the `BANG` token already exists in the lexer. Two changes:

- `**spawn!`: extend the `SpawnExpr` production to optionally accept `BANG` instead of `expr`:

```
  | SPAWN BANG                                         # SpawnBangExpr
  | SPAWN expr                                         # SpawnExpr


```

- `**send**`: add a new `SEND` keyword to [PiescriptLexer.g4](x-pack/plugin/piescript/src/main/antlr/PiescriptLexer.g4) and a new production:

```
  | SEND expr expr                                     # SendExpr


```

Regenerate the ANTLR parser after grammar changes.

### Core IR

- `**CoreSpawn**`: make body nullable in [CoreSpawn.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/core/CoreSpawn.java). When body is null, `children()` is empty. Update `info()`, `replaceChildren()`, `equals()`, `hashCode()`.
- `**CoreSend**`: new Core IR node in `piescript.core` — `CoreSend(Source, CoreExpr channel, CoreExpr value, MonoType type)`. Two children (channel, value). Add to the `permits` clause on [CoreExpr.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/core/CoreExpr.java).

### Value rename: `SpawnVal` -> `ChannelVal`

In [Value.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/Value.java):

- Rename `SpawnVal(SubscribableListener<Value> channel)` to `ChannelVal(String nodeId, String channelId)`.
- Update all references across the codebase (Evaluator, EvalCoordination, EvalTopology, PiescriptResponse, tests).

### ChannelRegistry

New class [ChannelRegistry.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/ChannelRegistry.java) in `piescript.eval`:

- Backed by `ConcurrentHashMap<String, ActionListener<Value>>`
- `registerChannel(String channelId, SubscribableListener<Value> listener)` — wraps listener to auto-remove on completion
- `lookup(String channelId)` — returns the `SubscribableListener` for `when` subscription (separate from the ActionListener used by `send`)
- `send(String channelId, Value value)` — calls `registry.get(channelId).onResponse(value)`
- For C.1, all channels are local. Remote routing added in C.3.

Add `ChannelRegistry` and `String localNodeId` to [EvalDependencies.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/EvalDependencies.java).

### Elaborator

- `**Spawns.java**`: handle `SpawnBangExpr` — infer type as `Channel alpha` (fresh meta). No body to elaborate. Emit `CoreSpawn(source, null, chanType)`.
- **New `Sends.java`**: handle `SendExpr` — elaborate channel expression, constrain to `Channel alpha` (fresh meta), elaborate value expression, constrain to `alpha`. Result type is `Null`. Emit `CoreSend(source, channelExpr, valueExpr, NULL_TYPE)`.
- Wire both into the `elaborate()` switch in [Elaborator.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/elab/Elaborator.java).

### Evaluator

In [Evaluator.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/Evaluator.java):

- `**CoreSpawn` (null body): generate UUID, create `SubscribableListener`, register in `ChannelRegistry`, return `ChannelVal(localNodeId, channelId)`.
- `**CoreSpawn` (non-null body): same as above, plus fork body to executor; body result completes channel via registry.
- `**CoreSend`: evaluate channel expr to `ChannelVal`, evaluate value, call `registry.send(channelId, value)`. Return `NullVal`. (C.1: local only. C.3: adds remote routing.)

In [EvalCoordination.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/EvalCoordination.java):

- `resolveChannel`: cast to `ChannelVal`, look up `SubscribableListener` in `ChannelRegistry.lookup(channelId)`, call `addListener`. (Currently casts to `SpawnVal` and accesses `.channel()` directly — line 48.)

### Tests

In [EvaluatorTests.java](x-pack/plugin/piescript/src/test/java/org/elasticsearch/xpack/piescript/eval/EvaluatorTests.java):

- `spawn!` creates a channel, `send` completes it, `when` receives the value
- `spawn! + send` equivalence with `spawn expr`
- Multi-channel: `spawn!` two channels, `send` to both, `when` joins
- `send` return value is `NullVal`

In [ElaboratorTests.java](x-pack/plugin/piescript/src/test/java/org/elasticsearch/xpack/piescript/elab/ElaboratorTests.java):

- `spawn!` infers `Channel alpha`
- `send ch 42` where `ch : Channel Integer` type-checks
- `send ch "hello"` where `ch : Channel Integer` is a type error
- `send` result type is `Null`

In [PiescriptParserTests.java](x-pack/plugin/piescript/src/test/java/org/elasticsearch/xpack/piescript/parser/PiescriptParserTests.java):

- Parse `spawn!`, parse `send x y`

---

## C.2 — Serialization (Value + CoreExpr + types)

Can run in parallel with C.1. Purely mechanical, no runtime changes.

### Value serialization

Add `writeTo(StreamOutput)` and `readFrom(StreamInput)` static methods to [Value.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/Value.java). Type-tag byte dispatch:

- `IntegerVal` (0), `LongVal` (1), `DoubleVal` (2), `KeywordVal` (3), `BooleanVal` (4), `NullVal` (5) — trivial
- `RecordVal` (6) — write field count + key/value pairs (recursive)
- `ListVal` (7) — write element count + elements (recursive)
- `ClosureVal` (8) — write `CoreExpr` body + `Value[]` env (recursive, depends on CoreExpr serialization)
- `BuiltinVal` (9) — write name + arity + partial args
- `ChannelVal` (10) — write `nodeId` + `channelId` (two strings)

### CoreExpr serialization

Replace the `UnsupportedOperationException` stubs in [CoreExpr.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/core/CoreExpr.java) (lines 52-59). Implement `writeTo` on each of the 16 variants (15 existing + `CoreSend`). Add a static `readFrom(StreamInput)` factory with type-tag dispatch.

Each variant writes: tag byte + MonoType + variant-specific fields + children (recursive).

### Supporting type serialization

- `MonoType` — tag + variant fields for `TCon`, `Arrow`, `RecordType`, `AppType`, `Meta`, `Rigid`
- `RowType` — tag + variant fields for `EmptyRow`, `RowExtend`
- `LitVal` — tag + variant fields
- `Op` — enum ordinal
- `Kind` — enum ordinal

### Tests

New `ValueSerializationTests.java` and `CoreExprSerializationTests.java`: round-trip every variant through `StreamOutput` / `StreamInput` and assert equality.

---

## C.3 — Transport handler + inbox + remote `send`

Depends on C.1 + C.2.

### Transport action: `piescript/send`

New classes in `piescript.eval` (or a new `piescript.transport` package):

- `**PiescriptSendRequest**` — `TransportRequest` containing `channelId: String` and `serialized Value` (using C.2 serialization).
- `**PiescriptSendAction**` — `ActionType<TransportResponse.Empty>` with name `"internal:data/write/piescript/send"`.
- `**TransportPiescriptSendAction**` — handler: deserialize value, call `channelRegistry.send(channelId, value)`. Registered via `TransportService.registerRequestHandler`.

Register the handler in [PiescriptPlugin.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptPlugin.java) — the plugin will need to implement a lifecycle interface (e.g., `ClusterPlugin` or use `createComponents`) to access `TransportService` at startup.

### Inbox registration

At plugin startup on every node:

- Build local node info `RecordVal` from `ClusterService.localNode()` (same structure as topology node records, plus `inbox` field).
- Register inbox in `ChannelRegistry` as a persistent `ActionListener<Value>` that creates a new `Evaluator(localDeps)` and applies the received closure with the node info as argument.

### Remote `send` routing in Evaluator

Update `CoreSend` evaluation in [Evaluator.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/Evaluator.java):

- Evaluate channel expr to `ChannelVal(nodeId, channelId)`
- If `nodeId == deps.localNodeId()` -> local: `registry.send(channelId, value)`
- If remote -> serialize value, send `PiescriptSendRequest(channelId, value)` to `nodeId` via `TransportService`

### `when` locality check

Update [EvalCoordination.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/EvalCoordination.java) `resolveChannel`:

- After evaluating to `ChannelVal(nodeId, channelId)`, check `nodeId == localNodeId`. If remote, fail with "cannot wait on remote channel".

### EvalDependencies expansion

Add `TransportService` to [EvalDependencies.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/EvalDependencies.java) (nullable for unit tests).

### Topology update: inbox field

In [EvalTopology.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/EvalTopology.java), add `inbox` field to node records: `ChannelVal(node.getId(), "INBOX")`.

In [Prelude.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/elab/Prelude.java), update the `topologyScheme()` node record type to include `inbox: Channel (NodeInfo -> Null)`.

---

## C.4 — CoreExpr / Value / MonoType Builder DSL :white_check_mark:

> **Implementation note**: The DSL was implemented with different class names than planned.
> Instead of `CoreDsl`/`ValueDsl`/`TypeDsl`, the actual classes are:
> - `Exprs` (in `piescript.core`) — CoreExpr factory methods
> - `Values` (in `piescript.core`) — Value factory methods
> - `Types` (in `piescript.core`) — MonoType factory methods and constants
>
> The `Values.record()` API uses `@SafeVarargs` with `Map.Entry<String, Value>...` and a
> companion `field(String, Value)` helper, instead of the planned variadic `(String, Value, ...)`
> pattern. This gives better type safety and extensibility.
>
> Java reserved keywords (`int`, `long`, `double`, `null`) forced a `Val` suffix on primitive
> factories: `intVal()`, `doubleVal()`, `nullVal()`, etc. `bool()` and `keyword()` don't need it.
>
> **Ref**: [Block C.4 implementation](c7b160cb-0062-4a7e-a930-c0ec2437d7ee)

Reduce construction verbosity for tests and runtime code. Three static-import-friendly classes
with short factory methods.

### Actual API (as implemented)

```java
import static ...piescript.core.Exprs.*;
import static ...piescript.core.Values.*;
import static ...piescript.core.Types.*;

// Exprs
var expr = app(lam("x", DOUBLE, body), lit(42));
var sendExpr = send(var(0, chanType), lit("hello"));

// Values
var rec = record(field("id", keyword("abc")), field("name", keyword("node-1")));
var list = list(doubleVal(1), doubleVal(2), doubleVal(3));
var ch = channel("nodeId", "channelId");

// Types
var fnType = arrow(KW, DBL);
var recType = rec("id", KW, "name", KW);
var chanType = channel(arrow(nodeInfoType, NULL));
```

---

## C.5 — Integration tests :white_check_mark:

> **Implementation note**: Used `ESRestTestCase` with `ElasticsearchCluster.local().nodes(3)` in a
> new `PiescriptMultiNodeIT` class (not `ESIntegTestCase` as planned). REST-level tests are more
> representative of real usage and consistent with the existing `PiescriptIT`. The test scenarios
> evolved from the plan — we focused on end-to-end cross-node execution proof rather than error
> cases. Error tests (when-on-remote, error propagation) were deferred.
>
> Also fixed a latent JSON escaping bug in both `PiescriptIT` and `PiescriptMultiNodeIT`:
> programs containing double quotes (e.g., `topology "cluster"`) were not properly escaped in the
> JSON request body.
>
> **Ref**: [Block C.5 implementation](c7b160cb-0062-4a7e-a930-c0ec2437d7ee)

### Test infrastructure (as implemented)

New class `PiescriptMultiNodeIT` extending `ESRestTestCase` with a 3-node cluster:

```java
@ClassRule
public static ElasticsearchCluster cluster = ElasticsearchCluster.local()
    .nodes(3)
    .distribution(DistributionType.DEFAULT)
    .setting("xpack.security.enabled", "false")
    .setting("xpack.ml.enabled", "false")
    .setting("xpack.license.self_generated.type", "trial")
    .build();
```

### Actual test cases (10 tests)

1. **testTopologyShowsThreeNodes** — `topology "cluster"` returns exactly 3 nodes
2. **testTopologyListsAllNodeNames** — `map` over `topo.nodes` extracts all 3 names
3. **testTopologyLocalNodeHasInbox** — local node record has `id`, `name`, `address`
4. **testSendToLocalInbox** — send closure to local inbox, receive result via `spawn!/when`
5. **testSendToRemoteInbox** — send to remote node's inbox, verify result from different node
6. **testRemoteComputation** — `1 + 2 + 3` evaluated on remote node, result sent back
7. **testRemoteRoundTripTransform** — ship data to remote, transform (`21 * 2`), get record back
8. **testProveCodeRanOnDifferentNode** — `local.id != remote_id` and `same_node == false`
9. **testFanOutToAllRemoteNodes** — fan-out to both remote nodes, collect via `when ... & ...`
10. **testMathOnRemoteNode** — `sqrt`, `abs`, `pow` computed on remote node

### Manual testing

Manual multi-node testing scripts in `debug/test-multinode.sh` (9 scenarios including the
triangle coordination test).

---

## Documentation updates :white_check_mark:

All docs updated across multiple sessions:

- [current-state.md](x-pack/plugin/piescript/docs/current-state.md) — Block C fully complete, stale entries cleaned, test counts corrected, session refs added
- [roadmap.md](x-pack/plugin/piescript/docs/roadmap.md) — Block C `:white_check_mark:`, MVP table updated, revision date, session ref
- [decisions.md](x-pack/plugin/piescript/docs/decisions.md) — D-045, D-046, D-047 recorded

**Ref**: [Block C docs cleanup](c7b160cb-0062-4a7e-a930-c0ec2437d7ee)
