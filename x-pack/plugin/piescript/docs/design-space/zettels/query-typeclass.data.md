---
tags: [data, types, typeclasses, push-down, open, concept]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Query Typeclass

Unified `Query f` [[typeclasses.types|typeclass]] where instances ([[esql-compilation.esql]], [[query-shardplan.data]], `List`, [[lucene-m.data]]) determine how filter/project/join/group/aggregate execute. Same piescript expression compiles to different backends based on instance. [[t-linq.esql]] normalization determines which expressions are valid per backend. Enables uniform [[comprehension-syntax.language]] over all data access levels. Grounded in [[parametricity.types|parametricity]] -- the same expression produces correct results regardless of which instance interprets it.

**Depends on**: [[typeclasses.types]], [[esql-compilation.esql]], [[t-linq.esql]]
**Enables**: [[push-down-compilation.performance]], [[comprehension-syntax.language]], [[data-access-hierarchy.data]]
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- part-of: [[deferred-push-down.roadmap]]
- implements: [[data-access-hierarchy.data]] — the long-term data access architecture; see data-access.md for the full design
- supersedes: old push-down-to-ESQL-text approach
- uses: [[t-linq.esql]] — T-LINQ normalization determines valid expressions per Query instance
- refines: [[two-tier-architecture.data]] — Query typeclass formalizes the two-tier read architecture
- subsumes: [[query-shardplan.data]] — ShardPlan is one Query instance (Level 2)
- subsumes: [[lucene-m.data]] — LuceneM is one Query instance (Level 3)
- subsumes: [[esql-compilation.esql]] — ESQL is one Query instance (Level 1)
- validated-by: [[parametricity.types]] — parametricity guarantees semantic preservation across instances
