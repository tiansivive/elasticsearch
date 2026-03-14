# Vision

> **Living doc** — revisit when the project's direction shifts or new insights emerge.

## One-Liner

Piescript is a **typed functional language for distributed computation** in Elasticsearch, using
π-calculus process primitives to orchestrate data pipelines that run where the data lives.

## Why Piescript Exists

ESQL is excellent for declarative data retrieval but intentionally stops short of general-purpose
programming. Users who need to transform, branch, or compose query results today must leave
Elasticsearch (client-side code, ingest pipelines, Painless scripts). Each of these has trade-offs:

| Approach | Drawback |
|----------|----------|
| Client-side code | Network round-trips, data leaves the cluster, language fragmentation |
| Painless | Untyped, imperative, no composition with ESQL |
| Ingest pipelines | Write-time only, not query-time |

Piescript fills the gap: a **query-time** language that is **typed**, **functional**, and
**ESQL-native**. It runs server-side, composes naturally with ESQL queries, and provides compile-time
safety via type inference. Crucially, it is designed from the ground up for **distributed
computation** — user-defined transforms travel to the nodes where data resides, rather than pulling
all data to a single coordinator.

### The Fragmentation Problem

Beyond the gap between ESQL and general-purpose programming, Elasticsearch has a deeper problem:
the same fundamental operation — *query some data, compute something, put the result somewhere* —
is implemented as a constellation of separate, incompatible features:

| Feature | When it runs | What it does | How you configure it |
|---------|-------------|-------------|---------------------|
| Ingest pipelines | Write time | Transform documents before indexing | JSON processor chain |
| Enrich processors | Write time (inside ingest) | Look up data from another index, merge into document | Enrich policy + processor config |
| Transforms | Scheduled batch | Query, aggregate, write to destination | JSON config (pivot/latest mode) |
| Watcher | Scheduled/triggered | Query, evaluate condition, take action | JSON watch definition |
| Runtime fields | Query time | Compute derived fields on the fly | Painless script |
| Painless scripts | Various | Ad-hoc computation | Imperative untyped scripts |

Each has its own API, configuration format, execution model, and limitations. When users need
something that crosses boundaries — enrich documents from index A using a lookup against index B,
aggregate the results, write to index C — they chain multiple features together, each with its own
failure modes, no shared type checking, and no unified debugging story.

Piescript can subsume all of these. Every feature in the table above is a specific instantiation of
the same pattern: query → compute → output. A programming language expresses this natively. The plan
graph architecture means the same program can be executed in different contexts (batch, ingest-time,
query-time, triggered) by different executors, without changing the program itself.

## The Distributed Computation Model

Piescript's core insight is the separation of two concerns:

1. **Pure functional expressions** — let-bindings, lambdas, application, records, pattern matching.
   These evaluate locally, on whichever node runs them. They produce values.

2. **Process primitives** — queries, parallel composition, channels, stream combinators. These
   describe distributed work. They are not executed directly by the evaluator; instead, they produce
   a **plan graph** that describes what computation happens where.

The plan graph is the bridge between the language and distributed execution. It is a reification of
the program's effectful structure — a free monad over π-calculus operations. The executor interprets
this plan, dispatching computation to the nodes that hold the relevant data.

**The rule:** pure code evaluates; process code describes; the executor runs the descriptions.

This separation works because the language is **pure and referentially transparent**. The only
effects are π-calculus effects (query execution, channel communication, parallel composition). Since
pure expressions have no side effects, the evaluator can safely reduce them locally and defer all
effectful operations to the plan graph. This is analogous to Haskell's IO monad boundary — but
instead of arbitrary IO, piescript's effects are precisely the π-calculus primitives.

### Code Mobility

When a user writes `query FROM logs-* |> map inc`, the `inc` function must travel to the nodes
holding `logs-*` shards. In a pure language, this is straightforward:

- **Closed lambdas** (no free variables) are self-contained code — serialize and ship anywhere.
- **Closures** (lambdas with captured variables) carry their captured environment. Since the
  language is pure, the captured values are immutable — clone and ship the `(code, env)` pair.

This is similar to delimited continuations: the process primitives are the delimiters, and the
plan graph is the reified continuation tree. Each π-primitive captures "what happens next" as a
continuation that can be dispatched to a remote node.

See [references.md](references.md) — Sangiorgi's agent-passing paper shows that code mobility
reduces to name passing in the π-calculus, requiring no fundamentally new mechanism.

## Design Philosophy

### 1. Types are inferred, not annotated

The programmer writes expressions; the compiler figures out the types via Hindley-Milner inference.
No type annotations are required (but may be supported for documentation purposes in the future).
This keeps the surface syntax lightweight while providing strong safety guarantees.

### 2. ESQL is the data layer

Piescript does not reinvent query execution. `query` expressions compile directly to ESQL requests.
The language adds computation *around* queries — binding results, transforming values, branching on
conditions — not *inside* them.

### 3. Functional by default, distributed by design

Immutable bindings, expressions over statements, pattern matching over if-else chains. Purity is
not just an aesthetic choice — it is what makes distributed execution safe. Referential transparency
guarantees that shipping a closure to a remote node produces the same result as evaluating it
locally. The language's only effects are π-calculus process primitives (query, par, send, recv),
which are modeled explicitly and handled by the plan graph executor.

### 4. Incremental delivery

The language is built in phases, each self-contained and testable. Phase 0 is a passthrough;
Phase 1 adds the core expression language; later phases add the plan graph and distributed
execution. Early phases execute everything locally on the coordinator node; later phases distribute
plan fragments to data nodes. The key architectural abstractions (the functional/process boundary,
the plan graph IR) are introduced early so that the transition from local to distributed execution
is incremental, not architectural.

### 5. Elasticsearch-native

Piescript is an x-pack plugin that follows ES conventions for build, test, security, and backwards
compatibility. It is not a standalone language bolted onto Elasticsearch — it is designed from the
ground up to integrate deeply. The plan graph executor builds on ES's existing infrastructure:
ESQL's compute engine for vectorized data processing, the transport layer for cross-node
communication, and the exchange mechanism for distributed data flow.

### 6. Grounded in process algebra

The π-calculus provides the theoretical foundation for piescript's process primitives. This is not
decoration — it gives us a formal framework for reasoning about parallel composition, channel
communication, and code mobility. The join calculus variant (Fournet & Gonthier) specifically
informs which primitives are efficiently implementable in a distributed setting. See
[references.md](references.md) for the full theoretical lineage.

## MVP: Unified Data Pipelines

The MVP goal is a piescript program that can express the equivalent of — and more than — ES
Transforms, enrich policies, and ingest pipeline chains, as a single typed program.

### What the MVP demonstrates

1. **Query data** from one or more indices via ESQL.
2. **Transform, filter, and aggregate** the results using typed, composable functions.
3. **Join / enrich** by querying a second index and merging fields — no separate enrich policy
   or processor configuration needed.
4. **Write results** to a target index.
5. **Run on a schedule** as an async persistent task within Elasticsearch.
6. **Execute distributed** — the plan graph optimizer pushes compatible transforms into the ESQL
   query (map → EVAL, filter → WHERE, groupBy + fold → STATS), so they run on data nodes via
   ESQL's existing distributed engine.

### Why this proves the use case

- **Expressiveness**: a piescript transform is a *program*, not a configuration. Users can write
  arbitrary logic — not just the fixed aggregation modes the Transform API anticipated.
- **Type safety**: the entire pipeline — source query, transforms, joins, output — is type-checked
  as one unit. If an enrich join references a field that doesn't exist, the error is caught at
  compile time, not at 3 AM on the 10 millionth document.
- **Performance**: piescript transforms that compile to ESQL expressions run on data nodes,
  vectorized, parallel across shards — leveraging ESQL's compute engine rather than pulling data
  to the coordinator. The plan graph optimizer can fuse adjacent transforms and push computation
  down, achieving performance that a rigid API configuration cannot match.
- **Unification**: one program replaces what today requires chaining a Transform, an enrich policy,
  an enrich processor, an ingest pipeline, and the glue between them. One language, one type system,
  one error model, one debugging story.

### Conceptual example

What today requires an enrich policy + enrich processor + ingest pipeline + transform:

```
let orders = query FROM incoming-orders | WHERE @timestamp > now() - 1h;
let customers = query FROM customer-database;

let enriched = orders |> map (fn order ->
  let customer = customers
    |> filter (fn c -> c.id == order.customer_id)
    |> first;
  { order | customer_name: customer.name, tier: customer.tier });

let summary = enriched
  |> groupBy .tier
  |> fold { count: 0, revenue: 0 } (fn acc -> fn row ->
       { count: acc.count + 1, revenue: acc.revenue + row.amount });

summary |> writeTo "order-summary-by-tier"
```

One typed program. The optimizer pushes the filter and field projections into the ESQL queries.
The join, aggregation, and write-back are plan graph nodes executed by the runtime. The type
checker verifies field compatibility across the entire pipeline before anything runs.

### MVP scope (mapped to phases)

The MVP requires completing these phases from the [roadmap](roadmap.md):

- **Phase 1e**: Pattern matching (control flow in transforms) — *deferred; not blocking Phases 2–4*
- **Phase 2**: Index resolution + query typing (typed query results)
- **Phase 3**: Stream runtime + plan graph + map/filter/fold + ExpressionEvaluator compiler +
  push-down optimizer + `writeTo` sink primitive + `groupBy` combinator
- **Parts of Phase 4**: `par` for merging multiple query results
- **New**: Persistent task wrapper for scheduled async execution

Full distributed execution (Phase 5 — serializing closures and shipping them to data nodes for
logic that cannot be expressed as ESQL) is a post-MVP enhancement. The MVP achieves distributed
performance for common cases through push-down into ESQL's engine.

## What Piescript is Not

- **Not a replacement for ESQL.** ESQL remains the primary query language. Piescript *wraps* ESQL,
  not the other way around.
- **Not Painless 2.0.** Painless is imperative and untyped. Piescript is functional and typed. They
  serve different use cases and may coexist.
- **Not a general-purpose language.** Piescript is purpose-built for distributed data computation
  within Elasticsearch. It deliberately omits arbitrary I/O, mutation, and class definitions. Its
  only effects are π-calculus process primitives.
- **Not just an orchestrator.** While early phases execute locally (coordinator-based orchestration),
  the long-term goal is true distributed execution where user-defined transforms travel to data
  nodes. The plan graph architecture is designed for this from the start.

## Long-Term Aspirations

These are directional, not committed:

- **Distributed plan execution** — plan graph fragments dispatched to data nodes, transforms
  co-located with the data they operate on, leveraging ESQL's exchange mechanism for cross-node
  data flow.
- **Linearity for channels** — QTT-style multiplicities (0, 1, ω) on bindings. Channel endpoints
  are linear (multiplicity 1), enabling session types with deadlock-freedom guarantees. Most values
  remain unrestricted (ω). See [references.md § Linear Haskell](references.md).
- **Session types for channels** — type-checked communication protocols on channels, providing
  deadlock-freedom guarantees from the type system (see Wadler's "Propositions as Sessions").
- **Explicit channels** — user-visible `new`, `send`, `recv` primitives for advanced orchestration
  patterns (fan-out, dynamic routing, producer-consumer).
- **Algebraic data types** — user-defined sum and product types for modeling domain concepts.
- **Pattern matching** — exhaustive, type-safe destructuring as the primary control flow mechanism.
- **Module system** — named, reusable piescript definitions stored in the cluster (like stored
  scripts, but typed and composable).
- **Plan optimization** — push-down of compatible piescript transforms into ESQL queries (map
  becomes EVAL, filter becomes WHERE), dead-code elimination of unused `par` branches, fusion of
  adjacent stream combinators. Linear closures/continuations (multiplicity 1) can be moved without
  cloning, avoiding allocation in the executor.
- **IDE support** — language server protocol for autocompletion, type-on-hover, and error
  highlighting.

## Speculative: Potential Future Directions

> Design rationale: `6c10d690-5758-49da-88f5-4c38f2f9cd72`

> **Caveat:** The ideas below are exploratory. They represent potential directions that the type
> system foundations (QTT multiplicities, session types) could unlock, but they are not planned,
> not committed, and may turn out to be impractical or unnecessary. They are recorded here to
> inform long-term design choices — not as promises.

### Mutable Shared State via Ownership

If QTT multiplicities are introduced for channels, the same machinery could in principle support
**safe mutable references** — owned, linear values that can be exclusively mutated by one process
at a time. This would enable:

- **Persistent in-memory resources** — shared counters, lookup tables, caches that live beyond a
  single query pipeline.
- **Incremental computation** — update an aggregation incrementally as new data arrives, rather
  than recomputing from scratch.
- **Cross-stream communication** — one stream populates a resource, another reads from it, with
  type-level guarantees of safe access.
- **Safe write-back** — linearly-owned write buffers for eventual index writes.

This approaches Rust-like ownership semantics, but from a functional starting point. The type
system would enforce exclusivity (no data races) at compile time. The open questions are
substantial: borrow checking vs. QTT alone, distributed ownership protocols, resource
reclamation across nodes, and user ergonomics for a non-PL-specialist audience. These would need
significant research and prototyping before any commitment.

### Continuous / Long-Lived Computations

With mutable references, piescript could support long-running computations that persist across
query invocations — materialized views, running aggregations, event-driven processing. This would
move piescript from "run a query, get results" toward "run persistent distributed computations
with safe shared state." The supervision and fault tolerance patterns from Erlang/OTP would
inform this design (see [references.md § BEAM / Erlang](references.md)).

### Linear Optimization Opportunities

Linearity enables executor optimizations: if a closure or continuation is linear (used exactly
once), the executor can **move** it rather than clone it — zero-copy transfer between plan nodes,
no allocation overhead. For large captured environments traveling to remote nodes, this is a
significant performance win. Similarly, linear stream edges in the plan graph guarantee
single-consumer data flow, simplifying buffer management and page lifecycle.
