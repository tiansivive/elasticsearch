# Data Access Architecture

> **Living doc** — update as the data access story evolves.
>
> **Created**: 2026-03-19. Captures the unified `Query a` typeclass design, the levels of control
> for data access, use cases, comparable systems, and rationale.
>
> **Status**: Exploratory. These ideas emerged from design discussions during Block D planning and
> are not committed to a specific implementation timeline. The architecture described here is the
> long-term direction; Block D's vertical slice implements the physical layer primitives as the
> foundation.
>
> **Ref**: [Data access design discussion](<!-- insert chat UUID when available -->)

## The Core Idea: `Query` as a Typeclass

Piescript has multiple ways to access data — ESQL delegation, shard-local Lucene access,
in-memory list processing — and they all share the same fundamental operations: filter, project,
join, group, aggregate. The insight is that these operations form a **typeclass**, and the
different data access backends are **instances**:

```
class Query f where
  from   : Index r → f (Stream r)
  where  : (a → Bool) → f (Stream a) → f (Stream a)
  select : (a → b) → f (Stream a) → f (Stream b)
  group  : (a → k) → (k → List a → b) → f (Stream a) → f (Stream b)
  join   : f (Stream a) → f (Stream b) → (a → b → Bool) → f (Stream (a, b))
  fold   : b → (b → a → b) → f (Stream a) → f b
  take   : Int → f (Stream a) → f (Stream a)
  ...
```

The user writes one query surface — comprehensions or combinator chains. The typeclass instance
determines how it executes:

| Instance | Compiles to | Distribution model | When to use |
|----------|------------|-------------------|-------------|
| `Query ESQL` | ESQL query string | ESQL decides (declarative) | "Give me data from the cluster" — the 90% case |
| `Query ShardPlan` | Shard-local Lucene plan | User-controlled (inside shipped closures) | "I'm on a data node, operating on local data" |
| `Query List` | In-memory `map`/`filter`/`reduce` | Already materialized | "I already have the data in hand" |
| `Query (LuceneM a)` | Raw Lucene primitives | User-controlled | "I need full control over searchers and segments" |

### Uniform syntax, different backends

The same piescript expression:

```
from r in idx
where r.status == "active"
select { id: r.id, status: r.status }
```

compiles to different things depending on context:

- **On the coordinator, targeting ESQL**: produces `FROM idx | WHERE status == "active" | KEEP id, status`
- **On a data node, inside a shipped closure**: produces a Lucene `TermQuery` + DocValues read
- **Over an already-materialized list**: produces `filter` + `map` over `ListVal`

The typeclass instance is selected by the type of the data source. An `Index r` bound via `use`
might default to `ESQL`; a shard reference inside a `send` closure might default to `ShardPlan`.
The user could also select explicitly.

### What each instance supports

Not all instances support all operations. The T-LINQ normalization result (Cheney, Lindley,
Wadler, ICFP 2013) tells you which expressions are valid for which backend:

- `Query List`: accepts everything — arbitrary higher-order functions, closures, side effects.
  It's just in-memory iteration.
- `Query ESQL`: accepts the subset that normalizes to flat ESQL — first-order predicates,
  field projections, built-in aggregations. Higher-order predicates or piescript-specific
  operations are rejected at elaboration time.
- `Query ShardPlan`: accepts what can be compiled to Lucene queries + DocValues reads +
  collectors. A `filter` with a simple predicate pushes down to a Lucene query; a `filter`
  with a complex closure applies post-materialization.
- `Query (LuceneM a)`: accepts everything that Lucene can express — the "assembly language"
  with no restrictions.

This could be modeled as finer-grained typeclasses (`Filterable f`, `Groupable f`, `Joinable f`)
so the type signature of an expression determines which backends are available. Or as a single
`Query f` class where unsupported operations produce elaboration errors for specific instances.

## Levels of Control

Piescript provides a hierarchy of data access abstractions. Each level is built on the one below.
The user picks the level that matches their need — higher for convenience, lower for control.

```
More declarative ─────────────────────────────────── More control

  Query ESQL     Query ShardPlan     LuceneM        open/consume/read
      │                │                │                   │
  "what data"    "what data,       "what to do       "I'll drive the
                  on this shard"    with Lucene"       iterator myself"
      │                │                │                   │
  ESQL decides    typeclass         sequential         raw Lucene API
  everything      push-down         imperative         (searcher, scorer,
                  & fusion          steps               segment, docref)
```

### Level 1: `Query ESQL` — fully managed

The declarative path. The user says what data they want; ESQL handles distribution,
optimization, and execution. Piescript adds type safety (via comprehensions) and composition
(via the host language) on top of ESQL's existing capabilities.

**What it gives you**: full cluster querying (including cross-cluster), ESQL's optimizer,
all ESQL features (STATS, ENRICH, DISSECT, GROK, etc.).

**What it doesn't give you**: control over where computation runs, custom algorithms over data,
iterative/adaptive pipelines, interleaving data access with coordination logic.

**Who uses this**: most piescript users, most of the time.

### Level 2: `Query ShardPlan` — shard-local, declarative

The explicit distributed path. The user writes declarative query combinators, but they execute
on a specific shard inside a shipped closure. Typeclass instances decide the execution strategy:
push predicates into Lucene queries, compile folds into Collectors, fuse map/filter chains.

**What it gives you**: data locality (compute where data lives), typeclass-driven optimization,
composable shard-local pipelines.

**What it doesn't give you**: control over individual Lucene primitives, custom iteration
patterns, interleaving with coordination mid-query.

**Who uses this**: users writing distributed piescript programs (fan-out to shards, collect
results). In practice, often used via library functions that abstract the `send` + `ShardPlan`
pattern.

### Level 3: `LuceneM` — imperative Lucene interaction

The free monad over Lucene primitives. Each operation is a constructor — building a `LuceneM`
value does nothing; it describes what to do. An interpreter executes the description, managing
resource lifecycle automatically.

**What it gives you**: full control over every Lucene primitive (searcher acquisition, query
compilation, segment iteration, DocValues reading, Collector-based push processing). Resource
management is automatic (the interpreter releases searchers on completion).

**What it doesn't give you**: nothing — this is the complete Lucene API surfaced in piescript.

**Who uses this**: power users implementing custom algorithms, building new `ShardPlan` typeclass
instances, or doing things that the declarative levels can't express.

### Level 4: Physical primitives — `open`/`consume`/`read`

Block D's vertical slice. Java-backed builtins that directly wrap Lucene operations. This is
what `LuceneM` primitives will eventually compile to — but in the vertical slice, they're
exposed directly as builtins.

**What it gives you**: direct access with structured patterns (pull-based iteration).

**What it doesn't give you**: automatic resource management, push-based processing, segment
visibility. These come with `LuceneM`.

**Who uses this**: Block D vertical slice only. Once `LuceneM` exists, these become internal
implementation details.

## Why All Four Levels Exist

The levels serve fundamentally different purposes:

**`Query a` (Levels 1–2) is a description of what data you want.** It's equational —
`filter p (filter q xs)` equals `filter (fn x -> p x && q x) xs`. The optimizer can rewrite,
fuse, and push down freely. But it can only express things the typeclass interface defines.

**`LuceneM` (Level 3) is a description of what to do with Lucene.** It's sequential — acquire,
compile, iterate, read. The order matters. It can express anything Lucene can do, including
things that aren't "queries":

- Consume 100 docs, send partial results on a channel, decide whether to continue based on a
  message from the coordinator — interleaving data access with coordination logic.
- Open searchers on two shards, iterate them in lockstep, correlate docs by a shared key —
  a custom merge join driven by the program.
- Per-segment `spawn` — fan out piescript computations per Lucene segment for user-controlled
  parallelism.
- Hold a searcher open, do external work (send results, receive instructions), then continue —
  resource lifecycle spanning multiple coordination steps.

None of these fit in a `Query` typeclass because they're not queries. They're **programs that
happen to access data** — programs where data access is interleaved with coordination,
branching, and communication. This is piescript's core differentiator: the user can be the
query planner.

## Use Cases and Value Proposition

### The core problem: the extraction cliff

Today, the moment a user's needs exceed what ESQL can express, they have to **leave
Elasticsearch entirely**. Extract the data to Spark, Flink, Python, or a client application,
process it externally, and maybe write results back. That extraction is:

- **Expensive** — network transfer, serialization overhead, storage duplication
- **Slow** — data movement is the bottleneck, not computation
- **Operationally complex** — two systems to deploy, monitor, secure, and keep in sync
- **Untyped at the boundary** — JSON over REST, no shared type system across the pipeline

Piescript extends the boundary of what's possible **inside** Elasticsearch. It doesn't replace
Spark or Flink — it makes a significant class of their workloads unnecessary to leave ES for.
Every workload piescript handles natively is one fewer extraction pipeline, one fewer external
system, one fewer operational headache.

### What piescript keeps ES-native

| Pattern | Without piescript | With piescript |
|---------|------------------|---------------|
| **Cross-index query-time enrichment** | Extract both indices, join in Spark/Python, or pre-build an ENRICH policy | Query A, query B, correlate in piescript — ad-hoc, typed, no preprocessing |
| **Iterative / convergent algorithms** | Extract to Spark. ESQL is single-pass only. | Ship closure, iterate, re-query, converge. PageRank, graph traversal, iterative clustering — all ES-native. |
| **Conditional / adaptive pipelines** | Multiple API calls from client code, no type safety across boundaries | Query A; based on results, decide what to query next. One typed program. |
| **Custom aggregations** | Extract to Python/Spark, or wait for ESQL to add the function | Ship a custom fold to each shard — HLL sketch, t-digest, anomaly score — merge on coordinator |
| **Heterogeneous fan-out** | Not possible in ESQL (uniform pipeline on every shard) | Different closures to different nodes. Node A runs algorithm X, node B runs algorithm Y |
| **Pipeline unification** | Transform config + enrich policy + ingest pipeline — separate features, separate APIs | One typed program replaces the entire chain |

### The Transform/enrich/ingest unification story

Today, "query data → compute → write results" is fragmented across five incompatible features,
each with its own config format, execution model, and failure modes. A piescript program can
express all of these as a single typed pipeline:

```
-- What today requires: Transform config + enrich policy + ingest pipeline
-- What piescript enables: one program
use .source-index as src
use .enrich-index as enrich
use .dest-index as dest

from r in src
where r.status == "active"
let enriched = from e in enrich where e.key == r.lookup_key select e.extra_field
in writeTo dest { ...r, extra: enriched }
```

Type-checked across the full pipeline. One execution model. One debugging story.

### What still belongs outside ES

Piescript doesn't aim to replace dedicated compute systems for workloads that are fundamentally
beyond a search engine's scope:

- **Petabyte-scale ML training** — GPU clusters, distributed gradient descent, model serving
  infrastructure. Spark/Ray's domain.
- **Complex event processing with exactly-once guarantees** — Flink's persistent state and
  checkpointing are purpose-built for this.
- **Multi-system data federation** — joining ES data with PostgreSQL, S3, Kafka in a single
  query. Trino/Spark's domain.

The line is: if the workload is **about ES data** and the main reason to extract it is that
ESQL can't express the computation, piescript is the answer. If the workload fundamentally
requires infrastructure ES doesn't have (GPUs, exactly-once state, cross-system federation),
external systems remain the right choice.

## Comparable Systems

| System | Model | Relation to piescript |
|--------|-------|---------------------|
| **ESQL** | Declarative SQL-like query | `Query ESQL` wraps ESQL. Piescript adds types, composition, coordination, and programmability. ESQL remains the backend for declarative queries. |
| **Apache Spark** | Ship compute to data (RDD/DataFrame) | Same "bring compute to data" pattern (`send` + `scan`). Piescript makes a class of Spark workloads ES-native — no extraction needed. Spark retains advantages for ML libraries, GPU, multi-system federation. |
| **Apache Flink** | Stateful stream processing | Piescript's channels enable streaming patterns. Flink retains advantages for persistent state, exactly-once, and complex windowing. |
| **Ray** | Distributed tasks/actors + futures | `spawn`/`when`/`send` map closely to Ray's task model. Piescript is the ES-native equivalent for data-centric distributed tasks. |
| **Presto/Trino** | Distributed SQL only | Same expressiveness ceiling as ESQL — query-only, no programming model. Both keep adding UDFs and stored procedures as escape hatches. |
| **BigQuery stored procedures** | Imperative escape from SQL | Piescript is the typed functional equivalent — but with distributed coordination and type safety. |
| **Painless** | ES scripting language | Both run inside ES. Painless is imperative, untyped, single-document scope. No distribution, no composition. |

### Key observation

Systems that have **both** a query layer **and** a programming layer (Spark, Flink, Ray) are
used for workloads that query languages can't express. Systems that are **query-only** (Presto,
BigQuery, Snowflake) keep adding escape hatches (UDFs, stored procedures, ML integration)
because users hit the expressiveness ceiling.

Piescript builds both layers from the start — the query layer (`Query a`) and the programming
layer (coordination + `LuceneM`) — with a clean typeclass boundary between them. The goal is
not to compete with Spark; it's to push the "what's possible inside ES" boundary far enough that
most users never need to extract their data to an external compute system.

## Architecture Diagram

```
┌─────────────────────────────────────────────┐
│       Comprehension / Combinator Syntax     │  ← user writes this (one surface)
│       (from/where/select or combinators)    │
└──────────────────┬──────────────────────────┘
                   │
      ┌────────────┼────────────┐
      │            │            │
      ▼            ▼            ▼
 ┌─────────┐ ┌──────────┐ ┌──────────┐
 │  Query   │ │  Query   │ │  Query   │
 │  ESQL    │ │ ShardPlan│ │  List    │
 │ instance │ │ instance │ │ instance │
 └────┬─────┘ └────┬─────┘ └────┬─────┘
      │            │            │
      ▼            ▼            ▼
  ESQL engine   LuceneM      map/filter/
  (distributed,  (shard-local, reduce over
   optimized)    typed)       ListVal
                   │
          ┌────────┼────────┐
          │        │        │
          ▼        ▼        ▼
      open/     collect   raw Lucene
      consume/  (push)    primitives
      read
      (pull)

                                    ┌──────────────────┐
                                    │  Physical layer   │
                                    │  (escape hatch —  │
                                    │   skip Query,     │
                                    │   talk to Lucene  │
                                    │   directly)       │
                                    └──────────────────┘
```

## Relation to Other Piescript Docs

- **[vision.md](vision.md)**: the "why" — design philosophy, MVP goal, long-term aspirations
  including comprehensions, typeclass push-down, and dynamic index typing
- **[architecture.md](architecture.md)**: the "how" — system components, Core IR, evaluator,
  serialization, transport, free monad perspective
- **[Block D plan](../.cursor/plans/block_d_local_data_ca4b90be.plan.md)**: the vertical slice —
  `open`/`consume`/`read` as Java-backed builtins, three-layer evolution path
- **[roadmap.md](roadmap.md)**: the "when" — phased delivery, deferred items including query
  language evolution and typeclass push-down
- **[references.md](references.md)**: the formal foundations — T-LINQ, Compiling to Categories,
  CALM theorem, materialization strategies, OutsideIn(X) for GADTs

## Open Questions

- **Surface syntax**: comprehension keywords (`from`/`where`/`select`) vs pure combinator style
  (`scan |> filter |> map`) vs both. Comprehensions are more readable for SQL-familiar users;
  combinators are more composable.
- **Instance selection**: implicit (based on the type of the data source) vs explicit (user
  annotates which backend to target). Implicit is more ergonomic; explicit is more predictable.
- **ESQL feature coverage**: how much of ESQL's feature set to cover in `Query ESQL` vs leave
  to the opaque `query \`ESQL\`` escape hatch. ENRICH, DISSECT, GROK are hard to express as
  typeclass methods.
- **Typeclass granularity**: one `Query f` class vs many (`Filterable f`, `Groupable f`,
  `Joinable f`). Finer granularity lets the type system tell you which backends support your
  expression; coarser granularity is simpler.
- **`LuceneM` as user-facing vs internal**: whether to expose `LuceneM` to users or keep it as
  infrastructure that `ShardPlan` compiles to. Exposing it adds power but complexity; hiding it
  simplifies the user model but creates an artificial ceiling.
- **Priority**: the Transform/enrich/ingest unification story (`Query ESQL` + coordination +
  `writeTo`) may deliver more practical value sooner than the full distributed computing stack
  (`ShardPlan` + `LuceneM`).
