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
- `topology "index-name"` returns cluster topology as typed records (nodes, shards) — D-044
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
let topo = topology "my-index"
in let target = head topo.shards
in let ch = spawn!
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
- **Block B**: ES topology as typed values (`topology`, node/shard records) :white_check_mark:
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

- **Language-integrated query (comprehensions)** — replace the opaque `query \`ESQL\`` syntax with
  piescript-native query expressions via the `Query a` typeclass. See
  [§ Data Access Architecture](#data-access-architecture-query-as-a-typeclass) below and
  [data-access.md](data-access.md) for the full design.
- **Typeclass-driven push-down** — specialize generic functions (`filter`, `map`) based on data
  representation via typeclasses. `filter pred rawdata` compiles to a Lucene query; `filter pred
  stream` iterates values. Replaces the old push-down-to-ESQL-text approach with a principled,
  type-directed optimization that works with piescript's own `scan` data access path. Closely
  related to the comprehension layer — the same piescript expression, interpreted via different
  typeclass instances, compiles to different backends (ESQL, Lucene, in-memory list).
- **Exchange streaming via explicit orchestration** — for large data volumes, piescript orchestrates
  Exchange setup via channels (send closure to data node → data node initializes Exchange sink →
  coordinator connects source → Pages stream with back-pressure). The Exchange is ES infrastructure
  that piescript talks to, not infrastructure piescript is built on.
- **Multi-value channels** — streaming patterns, fold-as-join, event handling. Single-value `send`
  is covered by Block C; multi-value channels extend this to repeated messages over time.
- **Functional-pattern matching on channels** — generalize `when` reaction rules from simple
  presence ("fire when each channel has a message") to Curry-style functional patterns ("fire
  when the channel's accumulated messages satisfy this function"). Narrowing over the message
  store naturally enumerates all satisfying assignments, enabling maximal parallel firing (CHAM
  semantics). The programmer declares the shape of a match; the runtime discovers and concurrently
  executes all non-overlapping matches. Requires multi-value channels as a prerequisite.
  See [references.md § CHAM, Functional-Logic](references.md).
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

## Data Access Architecture: `Query` as a Typeclass

> **Status**: Exploratory. See [data-access.md](data-access.md) for the full design document
> covering levels of control, use cases, comparable systems, and rationale.

Piescript's data access story is unified by a single insight: filter, project, join, group, and
aggregate are operations that form a **typeclass** (`Query f`), and the different data access
backends — ESQL, shard-local Lucene, in-memory lists — are **instances**. The user writes one
query surface (comprehensions or combinators); the instance determines how it executes.

| Instance | What it compiles to | When to use |
|----------|-------------------|-------------|
| `Query ESQL` | ESQL query string | Cluster-wide declarative queries — the 90% case |
| `Query ShardPlan` | Shard-local Lucene plan | Inside shipped closures, data-local computation |
| `Query List` | In-memory iteration | Already-materialized data |

Below the typeclass sits the **physical layer** (`LuceneM` free monad / `open`/`consume`/`read`
primitives) — the escape hatch for programs that interleave data access with coordination logic,
custom iteration patterns, or anything the `Query` interface doesn't cover. These are programs
that happen to access data, not queries.

This architecture means:
- **One query syntax** (comprehensions or combinators) works across all backends
- **Typeclass instances** determine compilation targets and optimization strategies
- **The physical layer** provides full Lucene control when the declarative levels aren't enough
- **ESQL coexists** via both the `Query ESQL` instance and the opaque `query \`ESQL\`` escape hatch

### The ESQL typing problem

The current opaque `query \`ESQL\`` syntax has a type soundness hole: piescript infers types from
index field caps, but ESQL transformations inside the backticks (`KEEP`, `RENAME`, `STATS`, `EVAL`)
change the result shape invisibly. Comprehensions solve this — every operation is a typed piescript
expression, so the result type is computed correctly throughout.

### Dynamic index names

Static index names (via `use` / literal `from`) get full type safety from field caps at
elaboration time. Dynamic index names — where the index is a runtime value — cannot be fully
typed because the schema depends on external cluster state. The honest type is `Dynamic`, with
explicit narrowing to concrete types via GADT-based type refinement (OutsideIn(X)), CPS-style
validation, or a `Reflect` typeclass.

See [data-access.md](data-access.md) for the full discussion including the architectural diagram,
comparable systems analysis, and open questions.

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

### Functional-Pattern Matching on Channels (CHAM + Curry Narrowing)

Multi-value channels (deferred) accumulate messages in a store. The current `when` design fires
when each participating channel has at least one message (standard join calculus presence check).
A natural generalization: allow **functional patterns** (Curry-style) as `when` reaction rules.

The programmer defines a function that describes what a "match" looks like over a channel's
message store. The runtime inverts the function via narrowing — searching for subsets of
accumulated messages that satisfy the pattern. This is fundamentally different from view patterns
(deterministic extraction procedures): the programmer writes the *specification* of a match, not
the *implementation* of the search.

**Key properties:**

- **Multi-variable binding from a single channel.** A functional pattern can destructure a channel's
  message store into multiple bindings — e.g., "three ready tasks and a remainder" binds three
  variables and a rest-list from one channel.
- **Maximal parallel firing (CHAM semantics).** Narrowing naturally produces all satisfying
  assignments. Instead of committing to one match, the runtime fires a `when` body for each
  non-overlapping match concurrently. Multiple `when` clauses watching the same channels can fire
  simultaneously from a single scheduler run. This is Berry & Boudol's Chemical Abstract Machine
  (CHAM) semantics — the original model underlying the join calculus before Fournet & Gonthier
  restricted it to simple presence for implementation efficiency.
- **Non-deterministic workflows.** The combination of functional patterns and maximal parallel
  firing enables declarative concurrent workflow definition. The programmer writes reaction rules;
  the runtime discovers and executes the maximal set of concurrent reactions. This is hard to
  express with deterministic pattern matching — view patterns return one result, requiring explicit
  loops for multiple firings. Narrowing's multi-solution enumeration provides this for free.
- **Control-plane performance model.** Narrowing over large data would be prohibitive, but channels
  are coordination mechanisms (control plane), not data pipes (data plane). The Exchange/compute
  engine handles high-throughput streaming. Channel message stores are small (tens to hundreds of
  coordination messages), making narrowing-based search tractable. The performance constraint that
  normally kills narrowing in production systems does not apply here.

**Conceptual example:**

```
when (tasks | readyBatch 3) (t1, t2, t3) & (workers | available 3) (w1, w2, w3) ->
  assign t1 w1; assign t2 w2; assign t3 w3
```

If there are 9 ready tasks and 9 available workers, the scheduler fires 3 concurrent `when`
bodies (3 batches of 3), each consuming its matched messages atomically.

**Prerequisites:** multi-value channels, channel message stores (multiset/bag semantics),
a narrowing runtime for functional patterns, atomic consumption across channels in a `when`
clause, scheduler re-evaluation on message arrival.

**Theoretical lineage:** Berry & Boudol (CHAM, 1992) for multiset reactions and maximal
parallelism. Antoy & Hanus (Curry) for functional patterns and narrowing. Frühwirth (CHR)
for multi-headed rule scheduling. Fournet & Gonthier (join calculus) for the channel-based
coordination substrate that this generalizes.

**Open questions:** consumption semantics (the functional pattern implicitly partitions the store
into consumed and remaining messages — this needs precise specification). Determinism of match
selection when multiple overlapping matches exist (oldest-first bias? fairness rotation?).
Surface syntax for functional patterns in `when` clauses. Whether full narrowing or a restricted
combinator language provides the right trade-off between expressiveness and predictability.

**Ref**: [references.md § CHAM, Functional-Logic, CHR](references.md)

### Linear Optimization Opportunities

Linearity enables executor optimizations: if a closure or continuation is linear (used exactly
once), the executor can **move** it rather than clone it — zero-copy transfer between plan nodes,
no allocation overhead. For large captured environments traveling to remote nodes, this is a
significant performance win. Similarly, linear channel edges guarantee single-consumer data flow,
simplifying buffer management.

## Brainstorming: Impact on ML Workflows

> **Caveat:** Exploratory notes. Not a commitment to ML-specific features — these are observations
> about how piescript's general-purpose data access + coordination model intersects with ML
> workflows that currently require leaving ES.

### The ML extraction problem

ML workflows over ES data today follow a common pattern: query ES → extract to Python/Spark →
compute → maybe write back. Every step in that chain is a serialization boundary, a network hop,
and a type-safety gap. The extraction exists not because ES can't store or serve the data, but
because ESQL can't express the computation. Piescript's data access architecture directly
addresses the computation gap.

### What piescript handles naturally (no ML-specific features needed)

These are ML-adjacent workloads that fall out of piescript's general-purpose design:

**Feature engineering** — the highest-value ML workload for piescript. Query ES data, compute
derived features (cross-index joins, time-series aggregations, normalized scores, windowed
statistics), write feature vectors back to an index. Today this requires extracting to a
DataFrame in Python or Spark. Piescript's `Query a` + `writeTo` makes it ES-native:

```
use .user-events as events
use .user-profiles as profiles
use .feature-store as features

let userEvents = from e in events where e.timestamp > cutoff
let userProfiles = from p in profiles
in userEvents
  |> groupBy (fn e -> e.user_id) (fn uid evts ->
    let profile = from p in userProfiles where p.id == uid select p
    in {
      user_id: uid,
      event_count: length evts,
      avg_session: mean (map (fn e -> e.duration) evts),
      account_age: profile.created_at,
      risk_score: profile.risk_score
    })
  |> writeTo features
```

**Model evaluation** — compute precision/recall/F1/AUC over predictions vs ground truth. This is
a custom aggregation — ship a fold to each shard, merge partial results on the coordinator.
Exactly the pattern that `Query ShardPlan` with a custom fold enables.

**Data preparation** — sampling, stratification, train/test splitting, normalization, one-hot
encoding. These are map/filter/reduce operations over query results. Already in piescript's
wheelhouse via `Query a` combinators.

**Inference orchestration** — fan out model application across nodes, collect and post-process
results. ES already has trained model inference (the `_inference` API and ML nodes). Piescript
would not implement inference kernels, but could coordinate them: query data, route batches to
inference endpoints, collect predictions, join with source data, write enriched results.

**ML pipeline automation** — combine feature engineering + inference + evaluation + write-back in
one typed program. Today this is a Jupyter notebook or Airflow DAG calling the ES REST API in a
loop. Piescript collapses it into a single program with type safety across all stages.

### What piescript should not attempt

**Model training** — gradient descent, backpropagation, matrix multiplication. Requires GPUs,
CUDA, automatic differentiation, and massive parallelism over parameter space. ES's JVM is
fundamentally wrong for this. Not a gap to close — a different problem entirely.

**Heavy inference** — running large transformer models, billion-parameter embedding computation.
ES's native inference API and external inference providers handle this. Piescript orchestrates
calling them; it doesn't reimplement them.

**Tensor operations** — piescript's type system is records and lists, not multi-dimensional arrays
with broadcasting semantics. Adding tensor primitives would be a different language.

### Integration with ES's ML infrastructure

One genuinely interesting direction: ES already has ML nodes with dedicated thread pools and
trained model inference capabilities. Piescript's `topology` + `send` could potentially route
closures to ML nodes specifically, combining piescript's coordination model with ES's inference
infrastructure:

```
let mlNodes = topology "cluster" |> nodes |> filter (fn n -> n.role == "ml")
let target = head mlNodes
let resultCh = spawn!
in send target.inbox (fn () ->
  let data = from r in idx where r.needs_scoring
  let scored = infer "my-model" data
  in send resultCh scored
)
in when (resultCh results) -> writeTo scored_index results
```

This is an integration story, not an implementation story — piescript orchestrates, ES's ML
infrastructure executes. Worth exploring once the distributed vertical slice and `Query ESQL`
are complete.

### The framing

Piescript is not "ML in ES." It's a general-purpose distributed computation language that
happens to make ML-adjacent workflows ES-native. The advantage comes from the data access
architecture, not from ML-specific primitives. Custom aggregations are custom aggregations
whether they compute a t-digest or an F1 score. Feature engineering is query → transform →
write, regardless of whether the downstream consumer is an ML model or a dashboard.

The risk of explicitly targeting ML is scope creep and mismatched expectations. Users will expect
GPU support, tensor operations, and training capabilities that ES cannot provide. The safer
position: piescript is general-purpose, and ML pipelines benefit significantly because they're
data-heavy workflows that today require unnecessary extraction from ES.

---

## External Interaction Model

Piescript today is a closed box: POST a program, get a result. This section describes the
long-term model for how piescript programs interact with the outside world — other services,
users, and systems beyond Elasticsearch.

### Layer 1: Actor Model (script lifecycle)

A piescript program is a **persistent actor** with an identity. You submit it, it runs
asynchronously, and you interact with it through channels exposed as HTTP endpoints.

```
PUT  _piescript/run           → { id: "uuid", token: "..." }   -- submit, returns immediately
GET  _piescript/{id}/result   → value or 202                    -- poll for return value
POST _piescript/{id}/inbox    → send a value into the script    -- external input
DELETE _piescript/{id}        → cancel                          -- kill and clean up
```

The script can **expose named channels** on specific nodes. The token is the capability —
possession grants access to all the script's channels. Internally, everything is channels, same
as piescript's existing Join Calculus model. The REST layer is just an HTTP skin over `send`/`when`.

For streaming output, an exposed channel can be consumed via SSE (Server-Sent Events):
```
GET _piescript/{id}/channels/{name}/stream   → SSE event stream
```

The script `send`s values to the channel; the SSE connection drains them incrementally.

**Use cases:**
- Long-running risk score computation — submit, check back later
- Interactive data exploration — send queries to a running script, get results back
- Orchestration — one script manages a pipeline, external systems feed it data via inbox
- Incremental output — Kibana subscribes to a channel, receives scored batches as they're computed

### Layer 2: Plugin SPI (typed builtins from Java)

Any ES plugin can register piescript builtins with type schemes, arities, and Java
implementations. The plugin author writes Java, piescript users get type-safe builtins.

```java
// In a kafka-piescript-bridge ES plugin
public class KafkaPiescriptExtension implements PiescriptExtension {
    typeSchemes() → { "Kafka.produce": ∀a. Keyword → a → Channel Null, ... }
    arities()     → { "Kafka.produce": 2, ... }
    execute("Kafka.produce", args, listener) → // Java Kafka client code
}
```

The piescript user writes:
```
let scored = List.map (fn r -> { ... }) raw;
Kafka.produce "risk-scores" scored
```

The Java plugin handles the Kafka client, connection pooling, serialization. Piescript provides
the orchestration and type safety.

**Use cases:**
- **Kafka/RabbitMQ** — `Kafka.produce`/`Kafka.consume` backed by Java Kafka client
- **HTTP webhooks** — `Http.post url body` backed by ES's HTTP client
- **Custom storage** — S3, HDFS, database connectors as ES plugins
- **ML inference** — wrap a model as `ML.predict model input`
- **Domain-specific ops** — a security team wraps proprietary scoring logic as typed builtins

Each integration is a **typed, sandboxed extension point**. No changes to piescript core per
integration.

### Layer 3: FFI (JVM access via Painless allowlist)

For ad-hoc JVM access without writing a full plugin, piescript exposes a Foreign Function
Interface gated by Painless's security allowlist — the same curated set of classes/methods
that Painless already audits and maintains.

```
let sqrt = Java.call "java.lang.Math" "sqrt" 25.0
let encoded = Java.call "java.util.Base64.getEncoder" "encodeToString" data
```

Piescript reuses the allowlist definitions via Painless's SPI (`WhitelistLoader`, the `.txt`
files), builds its own lookup, and checks every `Java.call` against it. Plugins can extend the
allowlist via ServiceLoader (same as Painless's `PainlessExtension`).

**Use cases:**
- **Quick prototyping** — try a Java API without writing a plugin
- **Math/crypto/encoding** — `java.lang.Math`, `Base64`, `MessageDigest` (already allowlisted)
- **String manipulation** — full `java.lang.String` API, regex via `java.util.regex.Pattern`
- **Bridging to bytecode compilation** — when piescript compiles to JVM bytecode, FFI calls
  become direct Java method invocations with zero overhead

**Security model:**
- Painless allowlist (compile-time) — which Java classes/methods are callable
- ES entitlement system (runtime) — JVM-level instrumentation for dangerous operations
- Two independent layers, both already maintained by ES

### Example: Incremental Risk Scoring with Multi-Channel Output

This example combines all three layers. A persistent piescript actor paginates through alerts,
computes risk scores incrementally, and pushes each batch to three destinations simultaneously:
ES (for persistence), Kafka (for downstream systems), and an outbound channel (for Kibana
real-time display).

```
-- Submitted via PUT _piescript/run
-- Kibana connects via GET _piescript/{id}/channels/scores/stream (SSE)

use ".alerts-security" as idx;

let pseries_weighted_sum = fn s values ->
  List.reduce (fn acc v ->
    { sum: acc.sum + v / Math.pow acc.i s, i: acc.i + 1 }
  ) { sum: 0.0, i: 1.0 } values
  |> (fn state -> state.sum)

in let scores_out = expose! "scores"   -- Kibana subscribes here via SSE
in let done_out = expose! "done"       -- signals completion

in let process = fn self after_key ->
  let raw = query ESQL.from idx
    |> ESQL.where (fn r -> r.user.name > after_key)
    |> ESQL.statsBy
         (fn r -> {
           top_scores: ESQL.top r.kibana.alert.risk_score 10000 "desc",
           alert_count: ESQL.count "*"
         })
         (fn r -> { user_name: r.user.name })
    |> ESQL.limit 1000;

  in if List.isEmpty raw then send done_out { status: "complete" }
  else
    let scored = List.map (fn user -> {
      user_name: user.user_name,
      score: pseries_weighted_sum 1.5 user.top_scores,
      alert_count: user.alert_count
    }) raw

    -- Write to ES (existing builtin)
    in let u1 = Index.bulk "risk-scores" scored
    -- Publish to Kafka (Layer 2: plugin SPI)
    in let u2 = Kafka.produce "risk-score-updates" scored
    -- Push to Kibana (Layer 1: actor channel → SSE)
    in let u3 = send scores_out scored
    -- Next page
    in let last = List.head (List.tail raw)
    in self self last.user_name

in process process ""
```

**What happens:**
1. User submits the script → gets back an ID and token
2. Kibana opens an SSE connection to `GET _piescript/{id}/channels/scores/stream`
3. The script paginates through alerts, computing risk scores in batches of 1000 users
4. Each batch is simultaneously: written to the `risk-scores` index, published to Kafka, and
   pushed to Kibana's SSE stream
5. Kibana displays scores incrementally as they arrive — no waiting for the full run
6. When done, the `done` channel fires, Kibana shows completion
7. The script's result channel completes with the final summary

One program replaces: a Transform (pagination + writes), a Watcher (scheduling), a Kafka
connector (event publishing), and client-side orchestration (progress tracking).

### Implementation Layers

| Layer | Dependencies | Status |
|-------|-------------|--------|
| Actor model (`PUT`/`GET`/`DELETE`, result channel) | Channels (done), REST endpoints (new) | Design |
| Named channels + `expose!` | Multi-value channels (Block H prerequisite) | Design |
| SSE streaming | Named channels + HTTP chunked response | Design |
| Plugin SPI for typed builtins | ServiceLoader, `PiescriptExtension` interface | Design |
| FFI via Painless allowlist | Painless SPI (exists), piescript lookup (new) | Design |
| Bytecode compilation (FFI zero-cost) | Pure fragment detection, JVM codegen | Long-term |

### Security

| Layer | Mechanism | Source |
|-------|-----------|--------|
| Actor channels | Token-based capability (returned at submit time) | New |
| Plugin SPI | ES plugin security model, plugin author controls exposure | Existing |
| FFI | Painless allowlist (compile-time) + entitlement system (runtime) | Existing |
| Script submission | ES action-level auth (`indices:data/read/piescript`) | Existing |
