# Design Space

A structured knowledge base of piescript's design landscape. Each zettel is an atomic note in `zettels/`, tagged and linked. 

Compatible with [Obsidian](https://obsidian.md) — open `docs/design-space/` as a vault
for graph navigation.

## Model

The knowledge base is a graph of **nodes** connected by **labeled edges**.

**Nodes** are zettels — atomic notes in `zettels/`. Each zettel has tags and refs.

**Edges** connect zettels to each other via labeled actions: `depends-on`, `enables`,
`informs`, `supersedes`, `related`, or any verb that describes the relationship.

**Tags** name things. A tag on a zettel says "this zettel is about X." The same tagging
mechanism works at every level — tags can be grouped, and groups themselves can be
tagged with roles. This is the same pattern as piescript's kinds-as-types (D-053):
types classify values, kinds classify types, but kinds ARE types. Here, tags classify
zettels, roles classify tag groups, but roles ARE tags.

Concretely: a tag like `implemented` classifies a zettel. A group like "Maturity"
collects related tags (`implemented`, `designed`, `open`, ...). A role like `universal`
classifies that group (meaning: expected on most zettels). It's nodes and labeled edges
all the way down — [faceted classification](https://en.wikipedia.org/wiki/Faceted_classification)
meets [RDF triples](https://en.wikipedia.org/wiki/Resource_Description_Framework).

## File naming

`thing.qualifier.md` — the primary concept first, optional qualifier second.
The qualifier is for disambiguation when many files relate to a similar area — it's
not a category or taxonomy, just a hint that helps group related files visually and
in sort order. Examples: `topby.esql.md`, `numerical.typeclass.md`,
`spark.comparable.md`, `transport-channels.infrastructure.md`.

When no qualifier is needed: `recursion.md`, `error-handling.md`.

## Zettel format

```markdown
---
tags: [language, types, open, runtime, fault-tolerance]
refs:
  - adr:D-051
  - adr:D-047
  - session:e3b83171-9476-43b2-bbc3-fde5725a57a0
  - plan:compute_engine_streaming_f5db78f2
  - roadmap:block-h
  - vision:external-interaction-model
  - doc:data-access.md
  - code:EvalExchange.java
  - code:Evaluator.java
---
# Title

One-paragraph description of the concept.

**Depends on**: [[pattern-matching.language]], [[adts.types]]
**Enables**: [[actor-model.lifecycle]], [[plugin-spi.external]]
**Connections**:
- informs: [[nbe-compilation.esql]] — NbE is piescript's instantiation of this
- supersedes: [[plan-graph.language]]
- related: [[bird-meertens.types]]
```

### Frontmatter fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `tags` | yes | list | Flat list of tags — use as many as apply, freely |
| `refs` | no | list | References using prefixed links. Multiple refs of any type. |

### Ref prefixes

References use a `prefix:value` convention. Multiple refs of the same or different
prefixes are normal. New prefixes can be added freely — just document them here.

| Prefix | Points to | Example |
|--------|-----------|---------|
| `adr` | Decision record in `docs/decisions.md` | `adr:D-051` |
| `roadmap` | Section in `docs/roadmap.md` | `roadmap:block-h` |
| `vision` | Section in `docs/vision.md` | `vision:external-interaction-model` |
| `plan` | Plan file in `.cursor/plans/` | `plan:compute_engine_streaming_f5db78f2` |
| `session` | Chat session ID (any agent — Claude, Cursor, etc.) | `session:e3b83171-...` |
| `doc` | Other documentation file | `doc:data-access.md` |
| `code` | Source file | `code:EvalExchange.java` |
| `resource` | Any URI — web link, local file, PDF, GitHub | `resource:https://arxiv.org/pdf/1306.6032.pdf` |

### Edges

Zettels connect via labeled edges using `[[filename-without-extension]]` links
(Obsidian-compatible).

**Depends on** and **Enables** are structural edges — they form the dependency DAG
that [metrics.md](metrics.md) computes on. They get their own sections in the zettel:

```
**Depends on**: [[hindley-milner.types]], [[row-polymorphism.types]]
**Enables**: [[esql-compilation.esql]], [[query-typeclass.data]]
```

**Connections** are all other edges, written as `action: [[link]] — optional note`:

```
**Connections**:
- informs: [[nbe-compilation.esql]] — NbE is piescript's instantiation of this
- supersedes: [[plan-graph.language]]
- related: [[bird-meertens.types]]
```

Actions are verbs describing what the edge does. Common ones:

| Action | Meaning |
|--------|---------|
| `informs` | Theoretical basis or influence |
| `inspired-by` | Creative influence (softer than `informs`) |
| `supersedes` | Replaces a previous approach |
| `rejected-in-favor-of` | Was considered but not chosen |
| `contrasts-with` | Different approach to same problem (adversarial) |
| `alternative-to` | Different approach, both viable (non-adversarial) |
| `complements` | Works well together with |
| `part-of` | This concept is a component of the linked concept |
| `subsumes` | Generalization that includes the linked concept |
| `specializes` | Specific case of a more general concept |
| `overlaps` | Partial conceptual overlap — shared territory |
| `duplicates` | Same concept tracked separately — should be merged |
| `implements` | Concrete realization of the linked design |
| `extends` | Builds on top of the linked item |
| `refines` | Narrows or improves the linked item |
| `uses` | Practical dependency — X uses Y at runtime or build time |
| `example-of` | Concrete instance of a pattern or concept |
| `validates` | Provides evidence that the linked approach works |
| `evolved-into` | Changed over time and became the linked item |
| `constrains` | Imposes constraints on the linked item |
| `tension-with` | Design tension — pulling in different directions |
| `tradeoff-with` | Choosing one affects the other |
| `prerequisite-for` | Must be done before (implementation ordering) |
| `blocks` | Prevents the linked item from proceeding |
| `unblocks` | Removes a blocker for the linked item |
| `closes` | Resolves or completes the linked item |
| `motivates` | This problem/need is why the linked item exists |
| `solves` | Addresses the problem described by the linked item |
| `workaround-for` | Temporary fix for the linked item |
| `analogous-to` | Same pattern in a different domain |
| `related` | General relationship (use sparingly — prefer a specific verb) |

**Convention:** active voice — "this zettel [verb] that zettel." If you need the
inverse direction, write the edge on the other zettel. Avoid inverse pairs like
`implements`/`implemented-by`.

New actions appear as needed — just use a verb that describes the relationship.

## Tags

Tags are **free-form**. Liberally use as many as apply. A tag is useful if it helps
find related zettels or see connections. New tags appear as needed — add them to the
table for vocabulary consistency.

| Tag | Description |
|-----|-------------|
| `aggregation` | STATS, GROUP BY, aggregates, fold/reduce patterns |
| `async` | Asynchronous execution, ActionListener, CPS |
| `channels` | Channel mechanism, messaging, coordination |
| `columnar` | Column-store representation: Page/Block, Block builders. For Elasticsearch’s columnar **runtime** (`org.elasticsearch.compute`), see **`compute-engine`**. |
| `comparable` | Comparison with external systems (Spark, Flink, Ray, Painless) |
| `compilation` | Bytecode, JVM codegen, ESQL compilation, pure fragment detection |
| `compute` | Computation model, distributed execution (piescript-level; not the ES `org.elasticsearch.compute` stack) |
| `compute-engine` | Elasticsearch columnar runtime: `org.elasticsearch.compute` — data `Page`/`Block`/`BlockFactory`, `Driver`/`Operator` chains, exchange. Distinct from `compute`. |
| `concurrency` | Parallel execution, synchronization |
| `control-flow` | Loops, recursion, branching |
| `coordination` | Join Calculus, spawn/when/send patterns |
| `data` | Direct data access, Lucene, shard ops, MV fields |
| `data-processing` | Data transformation, aggregation, enrichment |
| `debugging` | Dev endpoint, CorePrinter, diagnostics, explain |
| `design-pattern` | Recurring architectural patterns (two-tier, NbE dual, CPS) |
| `designed` | Documented design exists, not yet implemented |
| `distributed` | Cross-node execution, distributed algorithms, data locality |
| `effects` | Effect systems, algebraic effects, monadic effect tracking |
| `es-internals` | Elasticsearch infrastructure, transport layer, plugin system |
| `esql` | ESQL compilation, T-LINQ combinators |
| `evaluation` | Evaluator, environment machine, builtins, primops |
| `external` | Outside-world interaction, plugins, FFI, SSE, HTTP |
| `fault-tolerance` | Error recovery, crash handling, supervision |
| `implemented` | In the codebase, tested, working |
| `inference` | Type inference, elaboration, bidirectional checking |
| `infrastructure` | Channels, exchange, transport, compute engine |
| `ir` | Intermediate representation, Core IR, System F nodes |
| `kinds` | Kind system, kind constraints, Type/Row/arrow kinds |
| `language` | Core language features, syntax, semantics, control flow |
| `lifecycle` | Script lifecycle, actor model, scheduling, persistence |
| `lucene` | Lucene interaction, doc values, searchers, segments |
| `materialization` | Page-to-Value conversion, MV handling |
| `meta` | About the knowledge base itself — how we organize and reason about design |
| `mobility` | Code shipping, closure serialization, remote evaluation |
| `nbe` | Normalization by evaluation, partial evaluation, symbolic execution |
| `open` | Identified need, no settled design yet |
| `orchestration` | Distributed coordination, topology-based routing |
| `performance` | Compilation, optimization, push-down, bytecode |
| `pi-calculus` | Pi-calculus theory, Join Calculus, process algebra |
| `polymorphism` | Parametric, let-, ad-hoc, rank-1/higher-rank polymorphism |
| `primitives` | Base types, literals, built-in values |
| `push-down` | Query optimization, predicate push-down |
| `resources` | Resource management, cleanup, lifecycle |
| `roadmap` | Implementation milestone, phase, block, or delivery unit |
| `row-types` | Row polymorphism, row operators, record types |
| `runtime` | Runtime behavior, evaluation, dispatch, polymorphism |
| `safety` | Type safety, purity guarantees, serialization boundary |
| `security` | Auth, permissions, sandboxing, capabilities |
| `serialization` | Wire format, cross-node data transfer |
| `streaming` | Streaming data, backpressure, incremental output |
| `superseded` | Was planned, now replaced by a different approach |
| `syntax` | Surface syntax, grammar, desugaring, ANTLR |
| `tech-debt` | Exists but needs fixing, refactoring, or hardening |
| `theoretical` | Informing our thinking, not a direct implementation target |
| `tooling` | IDE, modules, stored programs, developer experience |
| `typeclasses` | Typeclass system and specific typeclass designs |
| `types` | Type system, inference, checking, kinds |
| `unification` | Robinson algorithm, occurs check, row/kind unification |
| `task` | Actionable work item — something to build, fix, or implement |
| `concept` | Design idea, pattern, or architectural choice |
| `decision` | A settled choice, often linked to an ADR |
| `prior-art` | How external systems solved this problem |
| `motivation` | Problem statement, why something exists |
| `exploration` | Open-ended thinking, not yet committed |
| `pattern` | Reusable approach or technique |
| `fix` | Something broken to repair |
| `feature` | Concrete capability to build |
| `epic` | Large multi-step work effort |
| `bug` | Known defect |
| `known-issue` | Documented limitation, not necessarily a bug |
| `workaround` | Temporary solution |
| `question` | Open question to resolve |
| `problem` | Identified problem needing a solution |
| `solution` | An answer to a problem or question |
| `interface` | API boundary, contract, protocol surface |
| `protocol` | Communication or coordination protocol |
| `capability` | System capability or affordance |
| `report` | Analysis or findings |
| `documentation` | Documents what exists |
| `blocker` | Currently blocks other work (state tag — remove when unblocked) |
| `deferred` | Explicitly postponed (state tag — remove or update when addressed) |
| `migration` | Requires migration or transition plan |
| `category-theory` | Mathematical foundations — catamorphisms, bananas, homomorphisms, CCC |
| `mutability` | Mutable state, ownership, shared state concerns |
| `scheduling` | Task scheduling, fairness, preemption, persistent tasks |
| `ml` | Machine learning workflows, inference, model evaluation |
| `mem-management` | Memory lifecycle, GC, ref counting, circuit breakers |
| `write-path` | Write concerns — shard write, bulk, ingest, replication, indexing pressure |
| `query-theory` | Query compilation theory — shredding, normalization, comprehensions |
| `beam` | BEAM/Erlang runtime lessons — scheduling, GC, supervision, hot code |

## Tag aliases

Strict equivalences — different spellings or abbreviations for the same concept.
When searching or filtering, treat aliased tags as interchangeable.

| Alias | Canonical |
|-------|-----------|
| `design-pattern` | `pattern` |
| `machine-learning` | `ml` |
| `intermediate-representation` | `ir` |
| `infra` | `infrastructure` |
| `erlang` | `beam` |
| `gc` | `mem-management` |
| `memory` | `mem-management` |
| `cat-theory` | `category-theory` |
| `writes` | `write-path` |
| `es-compute` | `compute-engine` |

New aliases appear as needed — add them when the same concept genuinely goes by two names.

## Tag groups

Tags can be collected into named groups. Groups themselves can have roles —
tags that describe the group's purpose. A tag can appear in multiple groups or none.

| Group | Meaning | Role | Tags |
|-------|---------|------|------|
| Concern | What area of the project does this touch? | universal | `language`, `types`, `esql`, `data`, `infrastructure`, `lifecycle`, `external`, `tooling`, `performance`, `security`, `es-internals` |
| Maturity | How baked is this concept? | universal | `implemented`, `designed`, `open`, `theoretical`, `tech-debt`, `superseded` |
| Foundations | What theory or technique underpins this? | topic | `pi-calculus`, `nbe`, `polymorphism`, `unification`, `inference`, `effects` |
| Distribution | How do things move and coordinate across nodes? | topic | `distributed`, `coordination`, `mobility`, `orchestration`, `serialization`, `channels`, `async`, `concurrency` |
| Data path | How does data flow from storage to piescript values? | topic | `lucene`, `columnar`, `compute-engine`, `materialization`, `streaming`, `aggregation`, `push-down`, `data-processing` |
| Purpose | What kind of artifact is this? | topic | `task`, `concept`, `decision`, `documentation`, `report` |
| Workflow | Function in the work process? | workflow | `prior-art`, `motivation`, `exploration`, `pattern`, `fix`, `feature`, `epic`, `bug`, `known-issue`, `workaround`, `question`, `problem`, `solution`, `interface`, `protocol`, `capability`, `blocker`, `deferred`, `migration` |

`universal` — expected on most zettels. 
`topic` — use when relevant.
`workflow` — describes the zettel's function in work processes (design, implementation, debugging).

New groups and roles appear as patterns emerge.

## Thread & Queue

The design space includes a work layer on top of the zettelkasten:

- **[thread.md](thread.md)** — append-only paper trail of work across sessions.
  Edge lines (`[[A]] -- verb -> [[B]]`) record knowledge graph traversal.
  Action lines (`ENQUEUE`, `RESOLVED`, `SPAWN`) record workflow events.
- **[queue.md](queue.md)** — flat FIFO list of pending work items referencing zettels.

Zettels are the atoms. Threads are paths through the graph. Queues are pending edges.
See [[thread-queue-system.meta]] for the full design and future MCP vision.

## Scope

The design space includes everything about piescript's design landscape.

See [metrics.md](metrics.md) for derived metrics.
