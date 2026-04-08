---
tags: [data, design-pattern, implemented]
refs:
  - adr:D-050
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Two-Tier Architecture

Recurring pattern: high-level API + shard-level primitives for both read and write. Read: ESQL queries (high-level, ESQL handles distribution) vs Shard.open/consume/read (shard-level, user controls). Write: Index.bulk (high-level, Bulk API handles routing/replication) vs Shard.writer/write (shard-level, direct Engine, primary-only). 95% use high-level; 5% power users go shard-level.

**Depends on**: [[shard-read.data]], [[shard-write.data]], [[esql-compilation.esql]], [[index-bulk.data]]
**Enables**: (none directly)
**Connections**:
- related: [[lucene-m.data]] — shard-level tier is the foundation for the future LuceneM free monad
- related: high-level tier delegates to existing ES infrastructure
- related: [[data-access-hierarchy.data]] — the four-level hierarchy formalizes the two-tier pattern
- related: [[query-typeclass.data]] — Query typeclass makes the tiers composable under a unified interface
