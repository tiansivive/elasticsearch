---
tags: [data, types, designed, concept]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Data Access Hierarchy

Four nested levels of data access abstraction:

**Level 1 — [[esql-compilation.esql]]**: Declarative, fully managed. ESQL decides distribution and optimization via the [[query-typeclass.data]] `Query ESQL` instance. The 90% case.
**Level 2 — [[query-shardplan.data]]**: Declarative but shard-local. Typeclass-driven [[push-down-compilation.performance]] inside shipped closures via [[code-mobility.coordination]].
**Level 3 — [[lucene-m.data]]**: Imperative [[free-monad.types]] over Lucene primitives. Full control over [[index-searcher.es-internals]], [[lucene-segments.es-internals]], [[doc-values.es-internals]]. Automatic resource management via [[searcher-lifecycle.data]].
**Level 4 — [[shard-read.data]]**: Direct `open`/`consume`/`read` primitives. What [[lucene-m.data]] compiles to. Block D's vertical slice.

Levels 1-2 are *descriptions of what data you want* (equational, rewritable). 
Levels 3-4 are *descriptions of what to do with Lucene* (sequential, order matters).
This is a fundamental semantic difference, not a convenience gradient.

**Depends on**: [[query-typeclass.data]], [[shard-read.data]], [[esql-compilation.esql]], [[lucene-m.data]], [[query-shardplan.data]]
**Enables**: [[push-down-compilation.performance]], [[comprehension-syntax.language]]
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- subsumes: [[two-tier-architecture.data]] — two-tier pattern is the concrete implementation of this hierarchy
- uses: [[code-mobility.coordination]] — shipped closures carry Level 2 computations to shards
- related: [[free-monad.types]] — Level 3 is structured as a free monad
- constrains: [[searcher-lifecycle.data]] — resource lifecycle is critical at Levels 3-4
- supersedes: [[shard-interaction-layers.data]]
