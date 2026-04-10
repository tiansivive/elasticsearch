---
tags: [data, types, designed, concept]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Data Access Hierarchy

Four nested levels of data access abstraction, from fully declarative to raw
Lucene control.

**Level 1 — ESQL**: Declarative, fully managed. ESQL decides distribution and
optimization. The `Query ESQL` instance of [[query-typeclass.data]]. The 90%
case. See [[esql-compilation.esql]].

**Level 2 — ShardPlan**: Declarative but shard-local. Typeclass-driven
push-down inside shipped closures. See [[query-shardplan.data]],
[[push-down-compilation.performance]].

**Level 3 — LuceneM**: Imperative free monad over Lucene primitives. Full
control over searchers, segments, doc values. Automatic resource management.
See [[lucene-m.data]].

**Level 4 — Physical primitives**: Direct `open`/`consume`/`read`. What
LuceneM compiles to. Block D's vertical slice. See [[shard-read.data]].

**Depends on**: (none — conceptual framework)
**Enables**: [[push-down-compilation.performance]], [[comprehension-syntax.language]]
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- overlaps: [[data-access-rationale]] — rationale explains *why* this structure exists
- subsumes: [[two-tier-architecture.data]]
- uses: [[code-mobility.coordination]] — shipped closures carry Level 2 computations
- related: [[free-monad.types]] — Level 3 is structured as a free monad
- constrains: [[searcher-lifecycle.data]] — resource lifecycle is critical at Levels 3-4
- replaces: [[shard-interaction-layers.data]] — 4-level hierarchy replaced the 3-layer model
