---
tags: [data, design-pattern, pattern, implemented, concept]
refs:
  - adr:D-050
  - adr:D-051
  - code:EvalShard.java
  - code:EvalWrite.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Two-Tier Architecture

Recurring pattern: high-level API + shard-level primitives for both read and write:
- **Read**: ESQL queries (high-level, ESQL handles distribution via [[esql-compilation.esql]]) vs `Shard.open`/`consume`/`read` (shard-level, user controls via [[shard-read.data]])
- **Write**: `Index.bulk` (high-level, Bulk API handles routing/replication via [[index-bulk.data]]) vs `Shard.writer`/`write` (shard-level, direct Engine, primary-only via [[shard-write.data]])
- 95% use high-level; 5% power users go shard-level

**Depends on**: [[shard-read.data]], [[shard-write.data]], [[esql-compilation.esql]], [[index-bulk.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-e.roadmap]]
- prerequisite-for: [[lucene-m.data]] — shard-level tier is the foundation for the future LuceneM free monad
- uses: high-level tier delegates to existing ES infrastructure
- evolved-into: [[data-access-hierarchy]] — the four-level hierarchy formalizes the two-tier pattern
- complements: [[query-typeclass.data]] — Query typeclass makes the tiers composable under a unified interface
- uses: [[esql-compilation.esql]] — high-level read tier delegates to ESQL
- uses: [[index-bulk.data]] — high-level write tier delegates to Bulk API
