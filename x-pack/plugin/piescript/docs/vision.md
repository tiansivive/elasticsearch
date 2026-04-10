# Vision

> **Living doc** — revisit when the project's direction shifts or new insights emerge.
>
> **Revised**: 2026-04-09. Aspirational and speculative sections moved to the design space
> knowledge base as atomic zettels. This document now covers identity, motivation, and the
> core computation model. Forward-looking plans live in thread hub zettels — run
> `python3 scripts/roadmap_status.py` for the full dashboard.
>
> Previous revision (2026-03-17): distributed execution strategy refined (D-042).

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

Each has its own API, configuration format, execution model, and limitations. Piescript can
subsume all of these. Every feature in the table above is a specific instantiation of the same
pattern: query -> compute -> output. A programming language expresses this natively.

See [[feature-constellation.external]] and [[extraction-cliff.external]] for the full problem
analysis. See [[external-interaction.thread]] for the plan to unify these features.

## The Distributed Computation Model

Piescript's core insight is the separation of two concerns:

1. **Pure functional expressions** — let-bindings, lambdas, application, records, pattern matching.
   These evaluate locally, on whichever node runs them. They produce values.

2. **Coordination primitives** — channels (typed conduits for async results), `send` (send a
   message to a channel — message travels to the channel's definition site), `when` (synchronize
   on one or more channels, react when results arrive), and parallel composition. These orchestrate
   distributed work.

The coordination layer is based on the **Join Calculus** (Fournet & Gonthier, 2000), a variant of
the pi-calculus designed specifically for distributed implementation. The key properties:

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

**The rule:** pure code evaluates; coordination primitives orchestrate; the runtime dispatches.

This separation works because the language is **pure and referentially transparent**. The only
effects are coordination effects (`send`, `when`, channel communication). Since pure expressions
have no side effects, they can be safely evaluated on any node, and closures can be shipped to
remote nodes without changing semantics.

See [[explicit-distribution.language]], [[code-mobility.coordination]], and
[[join-calculus.coordination]] for the detailed design.

### The Free Monad Perspective

The coordination primitives form an **algebraic effect signature**, and the evaluator is an
**effect handler**. Formally, the evaluator is a partial evaluator: it reduces pure expressions
to values, and gets stuck on coordination effects. The stuck residual — a tree of irreducible
`spawn`/`when`/`query` operations with attached values and closures — is a **free monad** over
the Join Calculus effect signature. See [[free-monad.types]] and
[[partial-evaluation-lowering.performance]] for the theory.

### Why Join Calculus, Not Raw pi-Calculus

The full pi-calculus includes constructs (like input-guarded choice) that are notoriously
difficult to implement in distributed systems. The Join Calculus restricts the pi-calculus to
primitives that have efficient distributed implementations while preserving full expressiveness.
This is precisely what Elasticsearch needs: coordination that can be implemented efficiently using
existing transport and thread-pool infrastructure, without requiring new distributed consensus
protocols. See [[join-calculus.coordination]].

### Code Mobility

Lambdas and closures travel to wherever computation is dispatched. In a pure language, this is
straightforward: closed lambdas are self-contained code; closures carry their immutable captured
environment. See [[code-mobility.coordination]] and [references.md](references.md).

## Design Philosophy

See [[design-principles.hub]] for the full set. The six principles:

1. **Types are inferred, not annotated** [[inferred-types.principle]]
2. **ESQL is the data layer** [[esql-data-layer.principle]]
3. **Functional by default, distributed by design** [[functional-distributed.principle]]
4. **Incremental delivery** [[incremental-delivery.principle]]
5. **Elasticsearch-native** [[es-native.principle]]
6. **Grounded in process algebra** [[join-calculus.coordination]]

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

## Where Piescript is Going

The forward-looking plan is organized as parallel work threads. Each thread is a sequenced path
through the design space knowledge base. Run `python3 scripts/roadmap_status.py` for the full
dashboard, or explore the thread hubs directly:

- [[error-handling.thread]] — error provenance to OTP supervision
- [[language-expressiveness.thread]] — recursion to module system
- [[data-completeness.thread]] — DateTime to ESQL.join
- [[distributed-coordination.thread]] — MV channels to long-lived computations
- [[type-foundations.thread]] — Forall type to session types
- [[external-interaction.thread]] — plugin SPI to Transform/Watcher unification
- [[ownership-resources.thread]] — ownership to incremental computation

See also:

- [[design-principles.hub]] — the 6 foundational design constraints
- [[data-access-architecture.roadmap]] — the `Query a` typeclass vision
- [[ml-workflow-integration.ml]] — ML-adjacent workloads piescript makes ES-native
- [[risk-scoring-incremental.example]] — end-to-end example combining actor model + Kafka + SSE
- [archive/data-access.pre-threads.md](archive/data-access.pre-threads.md) — full data access architecture design document (archived — see [[data-access-architecture.roadmap]])
- [references.md](references.md) — theoretical foundations
- [archive/mvp.md](archive/mvp.md) — completed MVP (archived 2026-04-06)
