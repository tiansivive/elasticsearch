---
tags: [data, types, designed]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Data Access Hierarchy

Four nested levels of data access abstraction, each serving different user needs:

**Level 1 — `Query ESQL`**: Declarative, fully managed. ESQL decides distribution and optimization. The 90% case. **Level 2 — `Query ShardPlan`**: Declarative but shard-local. Typeclass-driven push-down. Inside shipped closures. **Level 3 — `LuceneM`**: Imperative free monad over Lucene primitives. Full control, automatic resource management. For programs that interleave data access with coordination. **Level 4 — Physical primitives**: Direct `open`/`consume`/`read`. What `LuceneM` compiles to. Block D's vertical slice.

The key architectural distinction: Levels 1-2 are *descriptions of what data you want* (equational, rewritable by the optimizer). Levels 3-4 are *descriptions of what to do with Lucene* (sequential, order matters). This isn't a convenience gradient — it's a fundamental semantic difference.

**Depends on**: [[query-typeclass.data]], [[shard-read.data]], [[esql-compilation.esql]]
**Enables**: [[push-down-compilation.performance]]
**Connections**:
- related: [[lucene-m.data]] — Levels 3-4 can do things no Query interface covers: hold a searcher open across coordination steps, iterate segments in lockstep, interleave reads with sends
- related: [[two-tier-architecture.data]] — two-tier pattern is the concrete implementation of this hierarchy
