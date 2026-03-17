# Architecture

> **Living doc** — update when adding components, changing data flow, or making structural decisions.
>
> **Revised**: 2026-03-17. Distributed execution strategy refined (D-042): `spawn` is sugar over
> channel creation + send; piescript gives explicit control over distributed computing; the compute
> engine (Page/Block/Exchange) is infrastructure piescript orchestrates, not infrastructure piescript
> is built on. Channel registry and transport actions for cross-node execution are documented.
>
> Previous revision (2026-03-16): execution model redesigned around Join Calculus (D-040).
>
> **Ref**: [Distributed execution discussion](14bf4826-a39e-4012-ab4c-d73ad902a95f),
> [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef)

## System Overview

Piescript is an Elasticsearch x-pack plugin that adds a typed functional scripting language on top of
ESQL. Programs are received via REST, parsed, type-checked against real index mappings, and evaluated
by a uniformly asynchronous tree-walking interpreter. The evaluator uses continuation-passing style
(CPS) via `ActionListener` — pure expressions complete synchronously inline, while coordination
primitives (`spawn`, `when`) and ESQL queries execute asynchronously.

`spawn` forks computation to the GENERIC thread pool and returns a channel
(`SubscribableListener<Value>`). `when` synchronizes on one or more channels using a positional
collector (`AtomicArray` + `CountDown`), enabling concurrent multi-index query execution. The surface
keyword is `when` (not `join`) to avoid collision with SQL/ESQL JOIN terminology — see D-041.

```
Client
  │
  ▼
RestPiescriptAction          ← REST layer: parses JSON body, extracts "program" field
  │
  ▼
TransportPiescriptAction     ← Transport layer: orchestrates pipeline on GENERIC thread pool
  │
  ├─► Parser (ANTLR)         ← Phase 1a: source text → CST
  │     │
  │     ▼
  │   Index Resolution        ← Phase 2: resolve index patterns via Field Capabilities
  │     │
  │     ▼
  │   Elaborator              ← Phase 1b+: bidirectional HM inference → Core IR
  │     │
  │     ▼
  │   Evaluator               ← Phase 1c+: uniformly async tree-walking interpreter
  │     │
  │     ├─► Pure result       ← No spawn/when: callbacks fire synchronously, return value directly
  │     │
  │     └─► Async result      ← Has spawn/when: coordinate via channels, wire to ActionListener
  │           │
  │           ├─► ESQL Engine ← CoreQuery fires EsqlQueryAction (async via ActionListener)
  │           │
  │           └─► Channel ops ← spawn → fork to GENERIC, when → positional collector callbacks
  │
  ▼
Client (JSON response)
```

## Components

### REST Layer — `RestPiescriptAction`

- Registers `POST /_piescript/eval`.
- Parses the request body to extract the `program` string.
- Creates a `PiescriptRequest` and dispatches to the transport layer.
- Uses `RestRefCountedChunkedToXContentListener` for streaming response handling (same pattern as
  ESQL).

### Action Definition — `PiescriptAction`

- `ActionType<PiescriptResponse>` registered under `indices:data/read/piescript`.
- The `indices:data/read/` prefix integrates with ES security privilege resolution: users with
  index-level read access can run piescript programs that touch those indices.

### Request — `PiescriptRequest`

- Extends `ActionRequest`, implements `CompositeIndicesRequest`.
- `CompositeIndicesRequest` tells the security subsystem that the action touches indices determined
  at runtime (like ESQL), so authorization delegates to the underlying ESQL execution.
- Carries a single `program` string. Validates that it is non-blank.
- Serializable over transport via `StreamInput`/`StreamOutput`.

### Transport Layer — `TransportPiescriptAction`

- `HandledTransportAction` running on `threadPool.executor(ThreadPool.Names.GENERIC)` (D-004
  revision). Resolve callbacks also fork to GENERIC to avoid blocking coordination threads.
- Orchestrates the full pipeline: parse → index resolution → elaborate → evaluate.
- Wires the final evaluation result to the transport `ActionListener`. In Block A, the evaluator
  itself becomes async, so the transport action registers an `ActionListener<Value>` that the
  evaluator completes when done (including after async `when` resolution).

### Plugin Registration — `PiescriptPlugin`

- `ActionPlugin` that registers both the action handler and the REST handler.
- Declares `extendedPlugins = ['x-pack-esql']` in the build to access ESQL classes at compile time
  and ensure ESQL is loaded first at runtime.

## Security Model

Piescript reuses ESQL's authorization model:

1. The action name `indices:data/read/piescript` is registered in `RBACEngine
   .shouldAuthorizeIndexActionNameOnly()`, meaning the security engine checks index-level
   permissions but does not require a specific piescript privilege.
2. The action is added to the operator privileges allowlist in
   `Constants.java` so operator users can execute it.
3. At runtime, the `CompositeIndicesRequest` marker means index resolution is deferred to the ESQL
   engine, which performs its own authorization checks on the resolved indices.

## Core IR

The Core IR is a single `CoreExpr` sealed hierarchy — there is no separate `CoreProcess` layer.
Coordination primitives (`CoreSpawn`, `CoreWhen`) are `CoreExpr` variants alongside functional
nodes. This simplifies the IR and the evaluator: all nodes are evaluated by the same uniformly
async tree-walking interpreter (D-041).

### Functional Nodes (existing)

- `CoreVar`, `CoreFree`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`,
  `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp`, `CoreQuery`
- Callbacks fire synchronously (inline) for pure expressions.
- `CoreQuery` fires an ESQL query asynchronously and returns a `ListVal`.

### Coordination Nodes (Block A + Block C)

- `CoreSpawn(@Nullable CoreExpr body)` — launch `body` asynchronously, return a channel (D-042,
  D-045). When `body` is non-null: fork body evaluation, complete channel on result. When `body`
  is null: bare channel creation (`spawn!` syntax) — user completes it via explicit `send`.
  - Type: `Channel τ` (where `τ` is the body's type, or an unsolved meta for `spawn!`).
  - Evaluation: generate `channelId`, register a `SubscribableListener<Value>` in the
    `ChannelRegistry`, optionally fork `body`, return `ChannelVal(localNodeId, channelId)`.
  - `spawn!` bindings are subject to the **value restriction** (D-046): `let ch = spawn!` stays
    monomorphic (`Channel ?a`), preventing unsound polymorphism. Downstream `send`/`when` usage
    unifies `?a` to a concrete type.

- `CoreWhen(List<WhenBinding> channels, CoreExpr body)` — synchronize on channels, then evaluate
  `body` with bound values. (Surface keyword is `when` — see D-041.)
  - Each `WhenBinding` specifies a channel expression and a variable binding.
  - Type: the body's type, with channel value types bound to the variables.
  - Evaluation: look up each channel's `SubscribableListener` in the `ChannelRegistry` via
    `channelId`, register callbacks. Use a positional collector (`AtomicArray<Value>` +
    `CountDown`) to preserve binding order for de Bruijn indexing. When all channels complete,
    bind the received values and evaluate `body`.

- `CoreSend(CoreExpr channel, CoreExpr value)` — send a value on a channel (Block C, D-045).
  - Type: `Null` (fire-and-forget).
  - Locally: `channelRegistry.complete(channelId, value)`.
  - Cross-node: (C.3) serialize value, send transport message to the channel's owner node.
  - The channel reference is `ChannelVal(nodeId, channelId)` — the runtime routes accordingly.

### Prelude Built-ins

`map`, `filter`, `reduce` are prelude built-in functions (D-016), not Core IR nodes. They are
typed as normal polymorphic functions and operate over materialized `ListVal(List<Value>)` via
`applyFunction` callbacks. This keeps the IR uniform and enables a clean path to typeclasses.

Adding new combinators (`take`, `zip`, `groupBy`, `partition`, etc.) means adding prelude
functions, not extending the Core IR grammar.

## The Evaluator

The evaluator is a uniformly async tree-walking de Bruijn environment machine (D-041), split
across five classes for maintainability:

- `Evaluator` — core dispatch (`evaluate`, `applyFunction`) and all `CoreExpr` cases
- `EvalPrimOps` — arithmetic, comparison, and boolean operations
- `EvalBuiltins` — list processing (`map`, `filter`, `reduce`, `head`, `tail`, `length`,
  `isEmpty`) via `SubscribableListener` chaining for stack-safe sequential iteration
- `EvalCoordination` — `when` evaluation via `PositionalCollector` (encapsulates `AtomicArray` +
  `CountDown` + failure propagation)
- `EvalTopology` — `topology` builtin implementation (reads `ClusterState` → `RoutingTable` →
  `ShardRouting` → `DiscoveryNode`, converts to typed `RecordVal`/`ListVal`)

The evaluator takes an `EvalDependencies` record bundling `Client`, `Executor`, and
`ClusterService` (D-044). This replaces the growing constructor parameter list and scales to
Block C (which will add `TransportService` and a channel registry).

Every `evaluate` call takes an `ActionListener<Value>`. It handles all `CoreExpr` variants:

- **Functional nodes**: callbacks fire synchronously (inline). `CoreVar` looks up the de Bruijn
  environment. `CoreApp` applies a closure. `CoreLet` extends the environment. `CoreQuery` fires
  an ESQL query asynchronously via `ActionListener` and returns a `ListVal`.

- **Coordination nodes (Block A)**: evaluated asynchronously via `ActionListener` callbacks.
  `CoreSpawn` forks computation and returns a `SpawnVal` (wrapping a `SubscribableListener`).
  `CoreWhen` registers callbacks on channels via a positional collector and continues evaluation
  in the callback when all channels complete.

### Uniformly Async Evaluation Model (D-041)

The evaluator is uniformly async: every `evaluate` call takes an `ActionListener<Value>`. There
are no separate sync/async code paths. For pure expressions (no `spawn`/`when`), callbacks fire
synchronously on the calling thread — the async API has zero overhead. When coordination
primitives are encountered, the evaluator suspends the current computation and resumes it in a
callback when channels deliver their values.

This is a continuation-passing style (CPS) transformation of the evaluator, where
`SubscribableListener` callbacks serve as continuations. The ES infrastructure provides the
scheduling: `SubscribableListener.addListener` subscribes to channel results (firing immediately
if already complete), and `threadPool.executor(GENERIC)` provides safe threads for spawned
computations. Built-in functions (`map`, `filter`, `reduce`) use `SubscribableListener` chaining
(`newForked` → `andThen` → `addListener`) for stack-safe sequential stream processing.

## Runtime Values

`Value` is a sealed interface with variants:

| Variant | Description |
|---------|-------------|
| `IntegerVal` | Integer value |
| `LongVal` | Long value |
| `DoubleVal` | Double value |
| `KeywordVal(String)` | String/keyword value |
| `BooleanVal` | Boolean value |
| `NullVal` | Null value |
| `RecordVal` | Record with named fields |
| `ListVal(List<Value>)` | Eagerly materialized list of values (renamed from `StreamVal` in Block B — D-043) |
| `ClosureVal` | Lambda closure (code + captured environment) |
| `BuiltinVal` | Curried built-in function (name, arity, partial args) |
| `SpawnVal(SubscribableListener<Value>)` | Channel carrying an async result (Block A) |

## ES Infrastructure Mapping

The Join Calculus model maps directly to Elasticsearch's existing async infrastructure:

| Piescript Concept | ES Infrastructure | Notes |
|------------------|-------------------|-------|
| Single-value channel (`Channel τ`) | `SubscribableListener<Value>` | Single-completion future; other listeners subscribe for the result |
| `spawn` (fork computation) | `threadPool.executor(GENERIC).execute(...)` | Sugar for channel + fork + auto-send |
| `spawn!` (bare channel) | `new SubscribableListener<>()` | User completes via explicit `send` |
| `send` (local) | `listener.onResponse(value)` | Completes a channel locally |
| `send` (cross-node) | Transport message to channel owner | See channel registry below |
| `when` (channel synchronization) | Positional collector (`AtomicArray` + `CountDown`) | Preserves binding order for de Bruijn indexing |
| Error propagation | `ActionListener.onFailure(Exception)` | `SubscribableListener` propagates failures to all subscribers |
| Query execution | `client.execute(EsqlQueryAction, request, listener)` | Async via ActionListener |
| Topology discovery | `ClusterService.state()` → `RoutingTable` → `DiscoveryNode` | Reads existing cluster state |

### Cross-Node Infrastructure (Block C)

**Channel registry**: each node maintains a `ConcurrentHashMap<String, SubscribableListener<Value>>`
mapping channel IDs to local listeners. When `spawn!` creates a channel, it generates a UUID and
registers the listener. Channel references serialize as `ChannelRef(ownerNodeId, channelId)` —
two strings, trivially serializable inside closures.

**Transport actions**:
- `piescript/execute_closure` — coordinator → data node. Payload: serialized closure
  `(CoreExpr, Value[])` + result channel reference. Data node deserializes, evaluates, sends
  result on the specified channel.
- `piescript/channel_message` — any node → channel owner. Payload: `(channelId, serialized Value)`.
  Owner looks up the `SubscribableListener` in the channel registry and completes it.

This implements the Join Calculus locality property: messages travel to their channel's definition
site. A `send ch value` on a remote node routes the value to the node where `ch` was created.

**Value serialization**: all `Value` variants need `Writeable` implementations for transport.
Primitives (`IntegerVal`, `LongVal`, etc.) are trivial. `RecordVal` and `ListVal` are recursive.
`ClosureVal` requires Core IR serialization (`CoreExpr` tree + captured `Value[]` environment).
`SpawnVal` serializes as `ChannelRef(ownerNodeId, channelId)` — the deserializing side creates a
remote proxy that sends transport messages when `send` is called on it.

### The Type Stack: RawData / Page / Value

Three representations of data at different abstraction levels:

| Level | Type | What it is | When it's used |
|-------|------|-----------|----------------|
| Description | `RawData` (future) | Shard-local data reference + filters. No I/O. | Typeclass push-down: `filter pred rawdata` → Lucene query |
| Columnar | `Page`/`Block` | Batched, ref-counted, `Writeable`. What Lucene produces. | Scale: Exchange streaming. Not a piescript concern by default. |
| Values | `List` of `Value` | What piescript code operates on. `ListVal(List<Value>)` today. | All piescript computation. Backed by Page iterators at scale. |

Conversion between levels: `EsqlValueConverter` already converts `Page` rows → `RecordVal`. The
reverse (Value → Block) is straightforward given type information. The `RawData` → `Page`
transition is materialization (Lucene reads). Piescript doesn't abstract over scale decisions —
the user (or a library) chooses when to use simple `Value` messages vs. Exchange streaming.

### Future: Multi-Value Channels (Deferred)

Block A's `SubscribableListener` is inherently single-value (one completion). Multi-value channels
would carry streams of messages over time — needed for streaming patterns, fold-as-join, event
handling. Single-value `send` (completing a `spawn!`) is covered by Block C.

### Future: Exchange as Explicit Orchestration (Deferred)

The compute engine (Page/Block/Exchange) is ES infrastructure that piescript **orchestrates via
channels**, not infrastructure piescript is built on. For scale, the user (or a library) explicitly
sets up an Exchange via a sequence of channel messages — send closure to data node, data node
initializes Exchange sink and sends back metadata, coordinator connects source, Pages stream.
This is a piescript coordination protocol, not hidden runtime magic.

## Theoretical Model: Free Monad over Join Calculus Effects

The Join Calculus coordination primitives (`spawn`, `when`, `query`, `newchan`, `send`) form an
**algebraic effect signature**. The evaluator is an **effect handler** that interprets these
effects. This is the free monad perspective — and it still applies, even though the implementation
uses direct CPS interpretation rather than an explicit plan graph.

The key insight is that the free monad arises naturally as the **residual of partial evaluation**:

1. **Partial evaluation**: the evaluator reduces pure expressions (let-bindings, lambdas,
   application, arithmetic, records) to values. Coordination expressions (`spawn`, `when`, `query`)
   are **stuck** — they cannot be reduced further without performing actual effects.

2. **The residual is the free monad**: what remains after partial evaluation is a tree of
   irreducible coordination operations, each carrying the values and closures produced by step 1.
   This is `Free JoinF Value` — a free monad over the Join Calculus effect signature.

3. **Optimization**: the free monad structure can be inspected and transformed before execution.
   Push-down (fuse lambdas into ESQL queries), combinator fusion, dead-branch elimination — all
   operate on this structure.

4. **Runtime interpretation**: the actual execution (SubscribableListener callbacks, thread pool
   forks, ESQL query dispatch) interprets the optimized free monad.

```
CoreExpr (System F + coordination primitives)
    │
    ▼  partial evaluation / constant folding
    │
Free JoinF (residual: coordination skeleton with attached values/closures)
    │
    ▼  optimization (push-down into ESQL, fusion, etc.)  ← Block D
    │
Optimized Free JoinF
    │
    ▼  runtime interpretation (SubscribableListener, GENERIC thread pool, ESQL engine)
    │
Result
```

This is piescript's **lowering pass**, analogous to GHC's Core → STG → Cmm pipeline or ESQL's
Logical Plan → Physical Plan → Operator Pipeline.

### Block A: No Lowering Pass

In Block A, the evaluator eagerly interprets everything — including coordination primitives —
in one step. There is no explicit free monad data structure. The evaluator goes directly from
`CoreExpr` to runtime effects via ActionListener callbacks (a continuation monad, not a free
monad). This is correct and simple for the initial implementation.

### Future: Lowering Pass (When Push-Down is Implemented)

When push-down compilation is implemented (via typeclasses or direct compilation), the evaluator
splits into two phases:

1. **Partial evaluator**: reduces pure code, gets stuck on coordination effects, produces the
   free monad residual. This is the lowering pass.
2. **Optimizer**: inspects the free monad structure, compiles mobile lambdas into ESQL expressions,
   fuses combinators.
3. **Runtime interpreter**: the SubscribableListener/thread pool machinery from Block A, now
   interpreting the optimized free monad rather than the raw Core IR.

The runtime interpreter from Block A becomes the backend for the free monad interpreter in Block D.
Nothing is thrown away — the lowering pass is an additive change.

The single-value → multi-value channel transition (Block A → B) is orthogonal to this. Whether
`JoinF` includes `Spawn`/`Join` (Block A) or also `NewChan`/`Send` (Block B) doesn't change the
architecture. Both are constructors in the effect signature; the partial evaluator still reduces
pure code; the residual still contains stuck effects; the optimizer still inspects the structure.

## Traveling Code (Code Mobility)

Lambdas and closures attached to `spawn` expressions are "traveling code" — they can move to
wherever the computation is dispatched. In the π-calculus, this is process passing (higher-order
π). In practice:

- **Closed lambdas** (no free variables): serialize the `CoreExpr` subtree.
- **Closures** (captured environment): serialize `(CoreExpr, Map<Name, Value>)`. The language's
  purity guarantees that cloning the captured environment is safe — no aliasing or mutation hazards.
- **Mobility check**: some lambdas cannot travel (they capture non-serializable values like
  channel references). For Block A, all values are simple (integers, strings, booleans, records,
  streams) and trivially serializable. Future phases with channel references will need
  linear/affine types to prevent non-serializable captures.

For Block A (local execution), traveling code is a conceptual model — closures don't actually
travel over the wire. For distributed execution (future), closures will be serialized and shipped
to data nodes via the transport layer.

See [references.md](references.md) — Sangiorgi's agent-passing paper for the theory,
Nomadic Pict for a practical implementation of code mobility in a typed language.
