# Architecture

> **Living doc** — update when adding components, changing data flow, or making structural decisions.

## System Overview

Piescript is an Elasticsearch x-pack plugin that adds a typed functional scripting language on top of
ESQL. The current architecture (Phase 0) is a thin passthrough layer: programs are received via
REST, validated, and forwarded to the ESQL engine. Future phases will insert parsing, type checking,
and elaboration stages between REST intake and ESQL execution.

```
Client
  │
  ▼
RestPiescriptAction          ← REST layer: parses JSON body, extracts "program" field
  │
  ▼
TransportPiescriptAction     ← Transport layer: validates program structure, delegates to ESQL
  │
  ▼
ESQL Engine                  ← Executes the query, returns EsqlQueryResponse
  │
  ▼
Client (columnar JSON)
```

## Components

### REST Layer — `RestPiescriptAction`

- Registers `POST /_piescript/eval`.
- Parses the request body to extract the `program` string.
- Creates a `PiescriptRequest` and dispatches to the transport layer.
- Uses `RestRefCountedChunkedToXContentListener` for streaming response handling (same pattern as
  ESQL).

### Action Definition — `PiescriptAction`

- `ActionType<EsqlQueryResponse>` registered under `indices:data/read/piescript`.
- The `indices:data/read/` prefix integrates with ES security privilege resolution: users with
  index-level read access can run piescript programs that touch those indices.
- Response type is `EsqlQueryResponse` directly (no piescript-specific wrapper yet).

### Request — `PiescriptRequest`

- Extends `ActionRequest`, implements `CompositeIndicesRequest`.
- `CompositeIndicesRequest` tells the security subsystem that the action touches indices determined
  at runtime (like ESQL), so authorization delegates to the underlying ESQL execution.
- Carries a single `program` string. Validates that it is non-blank.
- Serializable over transport via `StreamInput`/`StreamOutput`.

### Transport Layer — `TransportPiescriptAction`

- `HandledTransportAction` running on `DIRECT_EXECUTOR_SERVICE` (no thread-pool hop — the ESQL
  engine manages its own threading).
- `extractEsqlQuery()`: strips the `query ... ;` wrapper and passes the inner ESQL string to
  `EsqlQueryRequest.syncEsqlQueryRequest()`.
- Error handling: malformed programs fail fast with `IllegalArgumentException` before reaching ESQL.

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

## Future Architecture (Phase 1+)

### Compilation Pipeline

The planned architecture inserts a compilation pipeline between REST intake and execution:

```
Client
  │
  ▼
RestPiescriptAction
  │
  ▼
TransportPiescriptAction
  │
  ├─► Lexer/Parser        ← Phase 1a: ANTLR grammar → CST → AST
  │     │
  │     ▼
  │   Type Checker         ← Phase 1b: bidirectional HM inference, zonker-based elaboration
  │     │
  │     ▼
  │   Core IR              ← Phase 1c: elaborated, fully-typed intermediate representation
  │     │                     (two layers: CoreExpr for functional, CoreProcess for effects)
  │     ▼
  │   Evaluator / Planner  ← Evaluates functional nodes, builds plan graph for process nodes
  │     │
  │     ├─► Pure result    ← Program has no process nodes: return value directly
  │     │
  │     └─► Plan Graph     ← Program has process nodes: plan describes distributed work
  │           │
  │           ▼
  │         Optimizer      ← Push-down, dead-code elimination, fusion
  │           │
  │           ▼
  │         Executor       ← Dispatches plan to ESQL / compute engine / remote nodes
  │
  ▼
ESQL Engine / Compute Engine / Remote Nodes
  │
  ▼
Client
```

Key design choices for the pipeline:

- **De Bruijn indices** for variable binding (no alpha-renaming needed).
- **Zonker-based elaboration**: unification writes solutions to a `Map<Integer, Object>` (the
  zonker). The elaborator never substitutes into the term tree. There is no zonking pass — the
  zonker is carried as a lookup table throughout the pipeline (elaboration, evaluation, lowering).
  Metavars in types are resolved by chain-following lookup when encountered. See D1.7 in the
  Phase 1 plan.
- **Core IR only extends `Node`** (ES AST infrastructure). Types, values, and other structures use
  plain records/sealed interfaces. `Node<T>` provides `Source` for error locations and
  `transformDown`/`transformUp` for the optimizer (Phase 2+). The elaborator and evaluator are
  hand-written recursive descent — they do not use `Node<T>` traversal methods.
- **Null semantics (v0)**: `Null` unifies with any type (behaves like `Any`). Will be refined in
  later phases with proper option types.

### The Two-Layer IR

The Core IR is partitioned into two sealed hierarchies:

- **`CoreExpr`** — functional expressions: `Var`, `Lit`, `Lam`, `App`, `Let`, `PrimOp`, `Record`,
  `Project`, `Update`, `Match`. These evaluate to values. The tree-walking evaluator handles them
  directly.

- **`CoreProcess`** — process descriptions: `Query`, `Par`, and (future) `Send`, `Recv`, `New`.
  These describe distributed effects with dedicated surface syntax. When the evaluator encounters
  them, it builds plan graph nodes instead of executing directly.

Note: `map`, `filter`, `fold` are **not** `CoreProcess` nodes. They are prelude built-in
functions whose runtime implementations construct plan graph nodes when applied to `StreamVal`s.
The Core IR for `stream |> map f` is `CoreApp(CoreApp(CoreVar("map"), f), stream)` — standard
function application. This keeps the IR uniform and enables a clean path to typeclasses
(`map` → `Functor.fmap`). See D-016 in [decisions.md](decisions.md).

This separation mirrors the fundamental distinction in the π-calculus between expressions (which
compute values) and processes (which perform communication). It is also analogous to the pure/IO
boundary in Haskell — `CoreExpr` is the pure layer, `CoreProcess` is the effectful layer. See
[vision.md](vision.md) for the conceptual model.

The boundary between the two layers is the most important seam in the architecture. Process nodes
may contain functional subexpressions (e.g., the ESQL string in `Query`), but functional nodes
never contain process nodes. Effects do not leak inward.

### The Plan Graph

When a program contains process nodes, evaluation produces a **plan graph** — a DAG of distributed
operations. The plan graph is a free monad over π-calculus effects:

```
data PiF next
  = Query String (Stream -> next)           -- CoreProcess node
  | Par [(Name, PiF next)] next             -- CoreProcess node
  | MapPlan (a -> b) (Stream a) next        -- from built-in `map`
  | FilterPlan (a -> Bool) (Stream a) next  -- from built-in `filter`
  | FoldPlan (b -> a -> b) b (Stream a) next -- from built-in `fold`
  | Send Channel Value next                 -- future CoreProcess node
  | Recv Channel (Value -> next)            -- future CoreProcess node
```

Plan nodes come from two sources: `CoreProcess` IR nodes (Query, Par) produce plan nodes directly
during evaluation. Built-in prelude functions (map, filter, fold) produce plan nodes when applied
to `StreamVal` values at runtime. Both paths produce the same plan graph IR. See D-016.

Each plan node carries:
- **Typed edges** (channels) — data flows between nodes along these edges.
- **Code** (optional) — a `CoreExpr` subtree (lambda/closure) to be executed at the plan node's
  location. The code is already type-checked and elaborated; it travels with the plan node.
- **Captured environment** — for closures, the `(code, env)` pair where `env` is a snapshot of
  captured bindings. Since the language is pure, captured values are immutable and safe to clone.

The plan graph is a **DAG, not a tree**. A `StreamVal` wraps a plan node description; using a
stream twice creates fan-out — two downstream nodes referencing the same source. No query is
re-executed. The executor handles fan-out via Exchange operators and reference-counted pages.
See D-017 in [decisions.md](decisions.md).

The plan graph is built by the evaluator (not a separate compilation pass). The evaluator walks
the Core IR, evaluates functional nodes eagerly, and suspends at process nodes — constructing plan
fragments and wiring them together. Built-in functions participate in the same process: when `map`
is applied to a `StreamVal`, it constructs a new plan node referencing the source. This is
analogous to how delimited continuations reify the "rest of the computation" at each effect
boundary.

### The Optimizer

The optimizer transforms the plan graph before execution:

- **Push-down**: a `MapPlanNode` with a simple lambda (field projection, arithmetic) can be fused
  into the upstream `QueryPlanNode` as an ESQL `EVAL` clause. A `FilterPlanNode` with a simple
  predicate becomes a `WHERE` clause. The "simplicity check" is really a **mobility check**: can
  this code be expressed in the target execution context (ESQL evaluators, compute operators)?
- **Dead-code elimination**: `Par` branches whose bindings are never referenced in the
  continuation are removed. Fan-out edges with unreachable consumers are pruned.
- **Fusion**: adjacent `MapPlanNode`s are fused into a single node with a composed lambda.

### The Executor

The executor interprets the optimized plan graph:

- **v0 (local)**: all plan nodes execute on the coordinator node. `Query` nodes fire
  `EsqlQueryRequest` via the node client. `Par` nodes spawn concurrent async queries
  (ActionListeners) and join. Stream combinators apply transforms locally (via ExpressionEvaluators
  for simple lambdas, via the tree-walking interpreter for complex ones).
- **Future (distributed)**: plan fragments are dispatched to data nodes. `Query` + downstream
  transforms are co-located with the shards they read. Channels between plan nodes on different
  nodes are implemented as Exchange operators. The executor leverages ESQL's existing shard
  routing, transport layer, and exchange mechanism.

The executor is behind an interface, so the transition from local to distributed execution is a
swap of the executor implementation, not an architectural change.

### Traveling Code (Code Mobility)

Lambdas and closures attached to plan nodes are "traveling code" — they move to wherever the plan
executor dispatches the node. In the π-calculus, this is process passing (higher-order π). In
practice:

- **Closed lambdas** (no free variables): serialize the `CoreExpr` subtree.
- **Closures** (captured environment): serialize `(CoreExpr, Map<Name, Value>)`. The language's
  purity guarantees that cloning the captured environment is safe — no aliasing or mutation hazards.
- **Mobility check**: some lambdas cannot travel (they capture non-serializable values like stream
  handles). The optimizer flags these and keeps them on the coordinator. For v0, all values are
  simple (integers, strings, booleans, records) and trivially serializable. Future phases with
  streams-as-values or channel references will need linear/affine types to prevent non-serializable
  captures.

See [references.md](references.md) — Sangiorgi's agent-passing paper for the theory,
Nomadic Pict for a practical implementation of code mobility in a typed language.
