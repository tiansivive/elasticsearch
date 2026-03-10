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
  adjacent stream combinators.
- **IDE support** — language server protocol for autocompletion, type-on-hover, and error
  highlighting.
