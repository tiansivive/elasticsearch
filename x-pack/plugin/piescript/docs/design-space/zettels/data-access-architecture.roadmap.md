---
tags: [hub, data, types, designed]
refs:
  - doc:archive/data-access.pre-threads.md
  - thread:language-expressiveness
---
# Data Access

Piescript's data access landscape — multiple coexisting approaches for getting
data in and out of Elasticsearch, from declarative ESQL delegation to raw
Lucene control.

**Implemented paths:**
- ESQL NbE compilation (Block F) — typed combinators compiled to ESQL strings
- Physical primitives (Block D) — `open`/`consume`/`read` on shards
- Exchange streaming (Block G) — columnar Page transport via `ExchangeService`

**Future paths:**
- `Query a` typeclass — unified declarative surface over ESQL, ShardPlan, List
- `LuceneM` free monad — imperative Lucene control with automatic resource management
- Typeclass-driven push-down — compile piescript lambdas to Lucene queries

**Navigating this hub:**
- [[data-access-hierarchy]] — the 4-level abstraction hierarchy
- [[data-access-rationale]] — why four levels (equational vs sequential)
- [[data-access-diagram]] — architecture diagram (mermaid)
- [[query-typeclass.data]] — `Query f` typeclass design
- [[query-shardplan.data]] — shard-local declarative instance
- [[lucene-m.data]] — imperative Lucene free monad
- [[comprehension-syntax.language]] — uniform query surface syntax
- [[push-down-compilation.performance]] — typeclass-driven compilation
- [[dynamic-index-names.data]] — runtime index names with `Dynamic` type

**Depends on**: (none — navigational hub)
**Enables**: (none directly)
**Connections**:
- includes: [[data-access-hierarchy]]
- includes: [[data-access-rationale]]
- includes: [[data-access-diagram]]
- includes: [[query-typeclass.data]]
- includes: [[query-shardplan.data]]
- includes: [[lucene-m.data]]
- includes: [[comprehension-syntax.language]]
- includes: [[push-down-compilation.performance]]
- includes: [[dynamic-index-names.data]]
- related: [[language-expressiveness.thread]] — Query typeclass and comprehensions tracked there
- related: [[data-completeness.thread]] — ESQL.join, LogicalPlan compilation tracked there
- related: [[esql-data-layer.principle]] — ESQL is the default backend
