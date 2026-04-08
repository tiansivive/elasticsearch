---
tags: [data, types, typeclasses, push-down, open]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Query Typeclass

Unified `Query f` typeclass where instances (ESQL, ShardPlan, List, LuceneM) determine how filter/project/join/group/aggregate execute. Same piescript expression compiles to different backends based on instance. T-LINQ normalization determines which expressions are valid per backend.

**Depends on**: [[typeclasses.types]], [[esql-compilation.esql]]
**Enables**: [[push-down-compilation.performance]], [[comprehension-syntax.language]]
**Connections**:
- related: [[data-access-hierarchy.data]] — the long-term data access architecture; see data-access.md for the full design
- supersedes: old push-down-to-ESQL-text approach
- related: [[t-linq.esql]] — T-LINQ normalization determines valid expressions per Query instance
- related: [[two-tier-architecture.data]] — Query typeclass formalizes the two-tier read architecture
