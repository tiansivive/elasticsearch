# Architecture

> **Living doc** — update when adding components, changing data flow, or making structural decisions.
>
> **Revised**: 2026-03-16. The execution model has been redesigned around the Join Calculus (D-040).
> The plan graph / two-layer IR / optimizer / executor architecture is archived in
> `docs/archive/architecture.pre-join-calculus.md`.
>
> **Ref**: [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef)

## System Overview

Piescript is an Elasticsearch x-pack plugin that adds a typed functional scripting language on top of
ESQL. The current architecture (through Phase 2) has a synchronous compilation and evaluation
pipeline: programs are received via REST, parsed, type-checked against index mappings, and evaluated
by a tree-walking interpreter that fires ESQL queries synchronously and operates over materialized
stream results.

Block A extends this with asynchronous coordination via Join Calculus primitives (`spawn`, `when`,
channels), enabling concurrent query execution and multi-way synchronization. The surface keyword
is `when` (not `join`) to avoid collision with SQL/ESQL JOIN terminology — see D-041.

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
- `CoreQuery` fires an ESQL query asynchronously and returns a `StreamVal`.

### Coordination Nodes (Block A)

- `CoreSpawn(CoreExpr body)` — launch `body` asynchronously, return a channel.
  - Type: `τ → Channel τ` (where `τ` is the type of `body`).
  - Evaluation: create a `SubscribableListener<Value>`, fork `body` evaluation to
    `threadPool.executor(GENERIC)`, return `SpawnVal(listener)`.

- `CoreWhen(List<WhenBinding> channels, CoreExpr body)` — synchronize on channels, then evaluate
  `body` with bound values. (Surface keyword is `when` — see D-041.)
  - Each `WhenBinding` specifies a channel expression and a variable binding.
  - Type: the body's type, with channel value types bound to the variables.
  - Evaluation: register callbacks on each channel's `SubscribableListener`. Use a positional
    collector (`AtomicArray<Value>` + `CountDown`) to preserve binding order for de Bruijn
    indexing. When all channels complete, bind the received values and evaluate `body`.

### Prelude Built-ins

`map`, `filter`, `reduce` are prelude built-in functions (D-016), not Core IR nodes. They are
typed as normal polymorphic functions and operate over materialized `StreamVal(List<Value>)` via
`applyFunction` callbacks. This keeps the IR uniform and enables a clean path to typeclasses.

Adding new combinators (`take`, `zip`, `groupBy`, `partition`, etc.) means adding prelude
functions, not extending the Core IR grammar.

## The Evaluator

The evaluator is a uniformly async tree-walking de Bruijn environment machine (D-041). Every
`evaluate` call takes an `ActionListener<Value>`. It handles all `CoreExpr` variants:

- **Functional nodes**: callbacks fire synchronously (inline). `CoreVar` looks up the de Bruijn
  environment. `CoreApp` applies a closure. `CoreLet` extends the environment. `CoreQuery` fires
  an ESQL query asynchronously via `ActionListener` and returns a `StreamVal`.

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
computations. Built-in functions (`map`, `filter`, `reduce`) use an iterative while-loop pattern
for stream processing to avoid stack growth from recursive callbacks.

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
| `StreamVal(List<Value>)` | Eagerly materialized stream of values |
| `ClosureVal` | Lambda closure (code + captured environment) |
| `BuiltinVal` | Curried built-in function (name, arity, partial args) |
| `SpawnVal(SubscribableListener<Value>)` | Channel carrying an async result (Block A) |

## ES Infrastructure Mapping

The Join Calculus model maps directly to Elasticsearch's existing async infrastructure:

| Piescript Concept | ES Infrastructure | Notes |
|------------------|-------------------|-------|
| Single-value channel (`Channel τ`) | `SubscribableListener<Value>` | Single-completion future; other listeners subscribe for the result |
| `spawn` (fork computation) | `threadPool.executor(GENERIC).execute(...)` | Safe to block; used for query execution |
| `when` (channel synchronization) | Positional collector (`AtomicArray` + `CountDown`) | Preserves binding order for de Bruijn indexing; fires when all slots filled |
| Error propagation | `ActionListener.onFailure(Exception)` | `SubscribableListener` propagates failures to all subscribers |
| Query execution | `client.execute(EsqlQueryAction, request, listener)` | Async via ActionListener |

### Future: Multi-Value Channels (Block B)

Block A's `SubscribableListener` is inherently single-value (one completion). Block B introduces
multi-value channels for streaming results:

- **Implementation**: lightweight piescript-native concurrent queue (`Queue<Value>` +
  notification mechanism), not ESQL's `Exchange` (which operates on `Page`/`Block`, a different
  abstraction level).
- **Join automaton**: pattern matching over multi-value channels, firing the join body each time
  the pattern is satisfied.
- **Primitives**: `newchan` (create a multi-value channel), `send` (send a value on a channel).

### Future: Exchange Integration (Block E)

For high-throughput streaming, piescript can participate in ESQL's Exchange pipeline:

- ESQL's `Exchange` is a multi-value, streaming, concurrent FIFO of `Page`s across threads/nodes.
- `ExchangeService` registers transport handlers for cross-node streaming.
- Piescript could operate on `Page`s/`Block`s directly (or convert to `Value`s incrementally),
  becoming a consumer/producer in ESQL's distributed streaming pipeline.

This is a performance optimization for Block E, not required for correctness.

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

### Block D: Lowering Pass Introduced

When Block D (push-down compilation) is implemented, the evaluator splits into two phases:

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
