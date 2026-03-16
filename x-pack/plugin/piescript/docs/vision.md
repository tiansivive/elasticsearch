# Vision

> **Living doc** — revisit when the project's direction shifts or new insights emerge.
>
> **Revised**: 2026-03-16. The execution model has been redesigned around the Join Calculus
> (Fournet & Gonthier). The previous plan-graph / free-monad-over-π-effects architecture is
> archived in `docs/archive/vision.pre-join-calculus.md`. See D-040 for the decision record.
>
> **Ref**: [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef)

## One-Liner

Piescript is a **typed functional language for distributed computation** in Elasticsearch, using
Join Calculus primitives (`spawn`, `when`, channels) to coordinate asynchronous data pipelines
that run where the data lives.

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
the same pattern: query → compute → output. A programming language expresses this natively.

## The Distributed Computation Model

Piescript's core insight is the separation of two concerns:

1. **Pure functional expressions** — let-bindings, lambdas, application, records, pattern matching.
   These evaluate locally, on whichever node runs them. They produce values.

2. **Coordination primitives** — `spawn` (launch asynchronous computation, producing a channel),
   `when` (synchronize on one or more channels, react when results arrive), and channels (typed
   conduits for asynchronous results). These orchestrate distributed work.

The coordination layer is based on the **Join Calculus** (Fournet & Gonthier, 2000), a variant of
the π-calculus designed specifically for distributed implementation. The key properties:

- **Local synchronization**: a join body fires only when all required channels have delivered their
  values. Synchronization is local — no distributed consensus needed at the primitive level.
- **Asynchronous by construction**: `spawn` launches work without blocking. The spawning
  computation continues immediately. Results arrive on channels.
- **Reaction rules**: `when` patterns are reaction rules — "when channel A has a value AND channel
  B has a value, fire this body." This naturally expresses multi-way synchronization (e.g.,
  "proceed when both query A and query B complete").

**The rule:** pure code evaluates; coordination primitives orchestrate; the runtime dispatches.

This separation works because the language is **pure and referentially transparent**. The only
effects are coordination effects (`spawn`, `when`, channel communication). Since pure expressions
have no side effects, they can be safely evaluated on any node, and closures can be shipped to
remote nodes without changing semantics.

### The Free Monad Perspective

The coordination primitives form an **algebraic effect signature**, and the evaluator is an
**effect handler**. Formally, the evaluator is a partial evaluator: it reduces pure expressions
to values, and gets stuck on coordination effects. The stuck residual — a tree of irreducible
`spawn`/`when`/`query` operations with attached values and closures — is a **free monad** over
the Join Calculus effect signature.

This free monad is piescript's **lowering IR**. In the initial implementation (Block A), the
evaluator eagerly interprets coordination effects via ActionListener callbacks — no explicit free
monad is constructed. When optimization is introduced (Block D), the evaluator splits into a
partial evaluator (producing the free monad residual) and a runtime interpreter (executing the
optimized residual). The free monad structure enables inspection and transformation before
execution: push-down of lambdas into ESQL queries, combinator fusion, dead-branch elimination.
See [architecture.md](architecture.md) for the full pipeline.

### Why Join Calculus, Not Raw π-Calculus

The full π-calculus includes constructs (like input-guarded choice: `c₁?x.P + c₂?y.Q`) that are
notoriously difficult to implement in distributed systems — they require atomic coordination across
multiple nodes. The Join Calculus restricts the π-calculus to primitives that have efficient
distributed implementations while preserving full expressiveness:

- No input-guarded choice (replaced by join patterns with local synchronization)
- Messages travel to a destination and interact only after arrival
- All synchronization is local to a single node

This is precisely what Elasticsearch needs: coordination that can be implemented efficiently using
existing transport and thread-pool infrastructure, without requiring new distributed consensus
protocols.

### Code Mobility

When a user writes `query FROM logs-* |> map inc`, the `inc` function must travel to the nodes
holding `logs-*` shards. In a pure language, this is straightforward:

- **Closed lambdas** (no free variables) are self-contained code — serialize and ship anywhere.
- **Closures** (lambdas with captured variables) carry their captured environment. Since the
  language is pure, the captured values are immutable — clone and ship the `(code, env)` pair.

See [references.md](references.md) — Sangiorgi's agent-passing paper shows that code mobility
reduces to name passing in the π-calculus, requiring no fundamentally new mechanism.

## Design Philosophy

### 1. Types are inferred, not annotated

The programmer writes expressions; the compiler figures out the types via Hindley-Milner inference.
No type annotations are required (but may be supported for documentation purposes in the future).
This keeps the surface syntax lightweight while providing strong safety guarantees.

### 2. ESQL is the data layer

Piescript does not reinvent query execution. `query` expressions delegate to ESQL. The language
adds computation *around* queries — binding results, transforming values, branching on conditions —
not *inside* them.

### 3. Functional by default, distributed by design

Immutable bindings, expressions over statements, pattern matching over if-else chains. Purity is
not just an aesthetic choice — it is what makes distributed execution safe. Referential transparency
guarantees that shipping a closure to a remote node produces the same result as evaluating it
locally. The language's only effects are coordination primitives (`spawn`, `when`, channel
communication), which are modeled explicitly.

### 4. Incremental delivery

The language is built in phases, each self-contained and testable. Phase 0 is a passthrough;
Phase 1 adds the core expression language; Phase 2 adds query typing and evaluation; later phases
add asynchronous coordination and distributed execution. Early phases execute everything locally
on the coordinator node; later phases distribute work to data nodes. The key architectural
abstractions (the functional/coordination boundary, the channel model) are introduced early so
that the transition from local to distributed execution is incremental, not architectural.

### 5. Elasticsearch-native

Piescript is an x-pack plugin that follows ES conventions for build, test, security, and backwards
compatibility. It is not a standalone language bolted onto Elasticsearch — it is designed from the
ground up to integrate deeply. The coordination runtime builds on ES's existing infrastructure:
`ActionListener` / `SubscribableListener` for asynchronous single-value channels, the transport
layer for cross-node communication, and ESQL's compute engine for vectorized data processing.

### 6. Grounded in process algebra

The Join Calculus (Fournet & Gonthier) provides the theoretical foundation for piescript's
coordination primitives. This is not decoration — it gives us a formal framework for reasoning
about asynchronous coordination, channel communication, and code mobility. The restriction to
locally-synchronizable primitives guarantees that every coordination pattern in piescript has an
efficient distributed implementation. See [references.md](references.md) for the full theoretical
lineage.

## MVP: Unified Data Pipelines

The MVP goal is a piescript program that can express the equivalent of — and more than — ES
Transforms, enrich policies, and ingest pipeline chains, as a single typed program.

### What the MVP demonstrates

1. **Query data** from one or more indices via ESQL.
2. **Transform, filter, and aggregate** the results using typed, composable functions.
3. **Run multiple queries concurrently** via `spawn` + `when` — no sequential blocking.
4. **Join / enrich** by querying a second index and merging fields — no separate enrich policy
   or processor configuration needed.
5. **Write results** to a target index via `writeTo`.
6. **Run on a schedule** as an async persistent task within Elasticsearch.

### Why this proves the use case

- **Expressiveness**: a piescript transform is a *program*, not a configuration. Users can write
  arbitrary logic — not just the fixed aggregation modes the Transform API anticipated.
- **Type safety**: the entire pipeline — source query, transforms, joins, output — is type-checked
  as one unit. If an enrich join references a field that doesn't exist, the error is caught at
  compile time, not at 3 AM on the 10 millionth document.
- **Concurrency**: multiple queries execute in parallel via `spawn` + `when`, with the type system
  ensuring that `when` bodies receive the correct types from each channel.
- **Unification**: one program replaces what today requires chaining a Transform, an enrich policy,
  an enrich processor, an ingest pipeline, and the glue between them. One language, one type system,
  one error model, one debugging story.

### Conceptual example

What today requires an enrich policy + enrich processor + ingest pipeline + transform:

```
let orders_ch = spawn (query `FROM incoming-orders | WHERE @timestamp > now() - 1h`);
let customers_ch = spawn (query `FROM customer-database`);

when (orders_ch orders) & (customers_ch customers) -> {
  let enriched = orders |> map (fn order ->
    let customer = customers
      |> filter (fn c -> c.id == order.customer_id)
      |> reduce { name: "", tier: "" } (fn _ c -> { name: c.name, tier: c.tier });
    { order | customer_name: customer.name, tier: customer.tier });

  let summary = enriched
    |> groupBy .tier
    |> reduce { count: 0, revenue: 0 } (fn acc row ->
         { count: acc.count + 1, revenue: acc.revenue + row.amount });

  summary |> writeTo "order-summary-by-tier"
}
```

One typed program. Both queries run concurrently. The `when` fires when both complete. The type
checker verifies field compatibility across the entire pipeline before anything runs.

### MVP scope (mapped to blocks)

The MVP requires completing these blocks from the [roadmap](roadmap.md):

- **Block A**: `spawn` + single-value `when` (async coordination) — concurrent query execution
- **Block B**: Multi-value channels (full Join Calculus runtime) — streaming results
- **Block C**: `writeTo` sink + scheduler — persistence and scheduled execution

Push-down compilation of piescript transforms into ESQL expressions (Block D) is a post-MVP
optimization. The MVP achieves correctness and concurrency; performance optimization follows.

## What Piescript is Not

- **Not a replacement for ESQL.** ESQL remains the primary query language. Piescript *wraps* ESQL,
  not the other way around.
- **Not Painless 2.0.** Painless is imperative and untyped. Piescript is functional and typed. They
  serve different use cases and may coexist.
- **Not a general-purpose language.** Piescript is purpose-built for distributed data computation
  within Elasticsearch. It deliberately omits arbitrary I/O, mutation, and class definitions. Its
  only effects are coordination primitives.
- **Not just an orchestrator.** While early phases execute locally (coordinator-based), the
  long-term goal is true distributed execution where user-defined transforms travel to data nodes.
  The Join Calculus coordination model is designed for this from the start.

## Long-Term Aspirations

These are directional, not committed:

- **Distributed coordination** — `spawn`ed computations dispatched to data nodes, `when` patterns
  synchronizing results across nodes, leveraging ES's transport layer for cross-node channels.
- **ESQL Exchange integration** — piescript as a consumer/producer in ESQL's Exchange pipeline for
  high-throughput streaming data flow.
- **Push-down compilation** — compiling mobile piescript lambdas into ESQL expressions (map → EVAL,
  filter → WHERE) for vectorized execution on data nodes. Significant compiler work (closure
  conversion, lambda lifting, defunctionalization).
- **Linearity for channels** — QTT-style multiplicities (0, 1, ω) on bindings. Channel endpoints
  are linear (multiplicity 1), enabling session types with deadlock-freedom guarantees. Most values
  remain unrestricted (ω). See [references.md § Linear Haskell](references.md).
- **Session types for channels** — type-checked communication protocols on channels, providing
  deadlock-freedom guarantees from the type system (see Wadler's "Propositions as Sessions").
- **Algebraic data types** — user-defined sum and product types for modeling domain concepts.
- **Pattern matching** — exhaustive, type-safe destructuring as the primary control flow mechanism.
- **Module system** — named, reusable piescript definitions stored in the cluster (like stored
  scripts, but typed and composable).
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
significant performance win. Similarly, linear channel edges guarantee single-consumer data flow,
simplifying buffer management.
