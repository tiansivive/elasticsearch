---
tags: [theoretical, data, coordination, push-down]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# CALM Theorem

Consistency As Logical Monotonicity: monotone computations (map, filter) distributable without coordination. Non-monotone (aggregation with negation) requires synchronization. Provides the formal line for which piescript operations can be pushed to shards safely.

**Depends on**: (none)
**Enables**: [[push-down-compilation.performance]]
**Connections**:
- inspired-by: [[dedalus.coordination]] — Alvaro et al. (CIDR 2011); Dedalus/Bloom is the language where CALM was developed
- complements: [[bird-meertens.types]] — both provide formal criteria for safe parallelization; BMF focuses on algebraic structure, CALM on monotonicity
- complements: [[datalog-fixpoint.search]] — Datalog computes the monotone fragment that CALM identifies as coordination-free
- complements: [[join-calculus.coordination]] — JC handles the non-monotone fragment that CALM identifies as requiring coordination
