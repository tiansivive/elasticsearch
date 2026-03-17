# Vision

> **Living doc** — revisit when the project's direction shifts or new insights emerge.
>
> **Revised**: 2026-03-17. The distributed execution strategy has been refined: piescript gives
> **explicit control** over distributed computing. Nodes, shards, and topology are first-class
> values. `spawn` is sugar over channel creation + send. The compute engine (Page/Block/Exchange)
> is ES infrastructure that piescript orchestrates via channels, not infrastructure piescript is
> built on. See D-042 for the decision record.
>
> Previous revision (2026-03-16): execution model redesigned around Join Calculus (D-040).
>
> **Ref**: [Distributed execution discussion](14bf4826-a39e-4012-ab4c-d73ad902a95f),
> [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef)

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

2. **Coordination primitives** — channels (typed conduits for async results), `send` (send a
   message to a channel — message travels to the channel's definition site), `when` (synchronize
   on one or more channels, react when results arrive), and parallel composition. These orchestrate
   distributed work.

The coordination layer is based on the **Join Calculus** (Fournet & Gonthier, 2000), a variant of
the π-calculus designed specifically for distributed implementation. The key properties:

- **Local synchronization**: a join body fires only when all required channels have delivered their
  values. Synchronization is local — no distributed consensus needed at the primitive level.
- **Asynchronous by construction**: coordination launches work without blocking. Results arrive on
  channels.
- **Reaction rules**: `when` patterns are reaction rules — "when channel A has a value AND channel
  B has a value, fire this body." This naturally expresses multi-way synchronization.
- **Locality property**: messages travel to their channel's definition site. A `send ch value` on
  a remote node routes the value back to wherever `ch` was created. This is the mechanism that
  makes distributed coordination work without distributed consensus.

### `spawn` is Sugar

Following the Join Calculus asynchronous core (Section 1.3), the true primitives are: channel
creation, `send`, parallel composition, and `when`. `spawn body` desugars to:

```
let ch = channel () in fork (send ch body) in ch
```

Create a channel, fork the body, auto-send the result. `spawn!` creates a bare channel without
a body — the user completes it via explicit `send`. This distinction is what makes distributed
execution work: `spawn` handles local concurrency (auto-return), while `spawn!` + explicit `send`
handles the distributed case where a remote node must explicitly route results back.

### Explicit Control over Distributed Computing

**ESQL is declarative about data retrieval.** You say what you want, ESQL decides where and how.

**Piescript is explicit about distributed computation.** Nodes are values. Shards are values.
The user sends code to named nodes and coordinates results via channels. The user orchestrates
the distributed plan — libraries raise the abstraction level when convenience is wanted.

| | ESQL | Piescript |
|--|------|----------|
| Model | Declarative query | Explicit distributed program |
| Distribution | Automatic (optimizer decides) | User-controlled (name nodes, send code) |
| Topology | Hidden | First-class typed values |
| Optimization | ESQL's planner | The user's program (+ libraries) |

This means:
- `index_topology "pattern"` returns cluster topology as typed records (nodes, shards)
- `send node.inbox closure` ships code to a remote node
- `scan shard` accesses local data on a data node (Lucene queries)
- `query \`ESQL\`` defers to ESQL when its declarative optimization is what you want
- The two approaches coexist — ESQL for declarative data retrieval, piescript for explicit
  distributed computation

**The rule:** pure code evaluates; coordination primitives orchestrate; the runtime dispatches.

This separation works because the language is **pure and referentially transparent**. The only
effects are coordination effects (`send`, `when`, channel communication). Since pure expressions
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

## MVP: Distributed Vertical Slice

> See [mvp.md](mvp.md) for concrete examples of what piescript can do today and what's aspirational,
> including a real-world risk scoring case study and the distributed vertical slice target.

The MVP goal is a piescript program that demonstrates **explicit distributed computation**:
discover cluster topology, ship code to data nodes, access local data, and coordinate results
via channels. This proves the core value proposition: user-controlled distributed computing with
code mobility.

### What the MVP demonstrates

1. **Discover topology** — query the cluster for nodes and shards as typed piescript values.
2. **Create channels** — bare channel creation via `spawn!` for explicit coordination.
3. **Ship code to data nodes** — `send node.inbox closure` delivers a closure to a remote node.
4. **Access local data** — `scan shard` runs Lucene queries on the data node.
5. **Coordinate results** — remote closures explicitly `send` results back to coordinator-owned
   channels. `when` patterns synchronize on the results.
6. **Query via ESQL** — `query \`ESQL\`` remains the easy path for declarative data retrieval.

### Why this proves the use case

- **Distributed computing**: piescript code runs on data nodes, not just the coordinator. This is
  the fundamental capability that no other Elasticsearch feature provides at the language level.
- **Code mobility**: closures (code + captured environment) travel to where data lives. Purity
  guarantees this is safe.
- **Explicit control**: the user decides what runs where. No hidden optimizer. Libraries can
  provide higher-level abstractions, but the primitives are always available.
- **Type safety**: the entire pipeline — topology discovery, closure construction, channel
  coordination, result processing — is type-checked as one unit.
- **Join Calculus foundation**: channels, send, and when are the only coordination primitives.
  Everything else (including Exchange streaming at scale) is orchestrated through them.

### Conceptual example

The distributed vertical slice:

```
let topo = index_topology "my-index"
in let ch = spawn!
in let target = head topo
in send target.node.inbox (fn () ->
  let data = scan target |> filter (fn r -> r.status == "active")
  in send ch data
)
in when (ch results) ->
  results |> map (fn r -> { id: r.id, status: r.status })
```

The coordinator discovers topology, creates a bare channel, ships a closure to a data node.
The data node scans its local shard, filters, and explicitly sends results back. The coordinator
receives and processes them. Genuine distributed computing — verified by the remote closure
returning the node name it ran on.

### MVP scope (mapped to blocks)

The MVP requires completing these blocks from the [roadmap](roadmap.md):

- **Block A**: `spawn` + single-value `when` (local async coordination) :white_check_mark:
- **Block B**: ES topology as typed values (`index_topology`, node/shard records)
- **Block C**: Cross-node code execution (`send`, `spawn!`, closure serialization, channel registry)
- **Block D**: Local data access (`scan` on data nodes)

Stretch goal: **Block E** (`writeTo` — persist results to indices). Not required for the vertical
slice, but enables the Transform replacement story.

### The unified data pipelines story

The original MVP vision — replacing ES Transforms, enrich policies, and ingest pipeline chains
with a single typed program — remains valid and is now a **superset** of the distributed vertical
slice. It requires `writeTo` (Block E), `groupBy` combinator, and scheduled execution. The
distributed vertical slice comes first because it proves the harder, more foundational capability.

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

- **Typeclass-driven push-down** — specialize generic functions (`filter`, `map`) based on data
  representation via typeclasses. `filter pred rawdata` compiles to a Lucene query; `filter pred
  stream` iterates values. Replaces the old push-down-to-ESQL-text approach with a principled,
  type-directed optimization that works with piescript's own `scan` data access path.
- **Exchange streaming via explicit orchestration** — for large data volumes, piescript orchestrates
  Exchange setup via channels (send closure to data node → data node initializes Exchange sink →
  coordinator connects source → Pages stream with back-pressure). The Exchange is ES infrastructure
  that piescript talks to, not infrastructure piescript is built on.
- **Multi-value channels** — streaming patterns, fold-as-join, event handling. Single-value `send`
  is covered by Block C; multi-value channels extend this to repeated messages over time.
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
