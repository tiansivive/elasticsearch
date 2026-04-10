---
tags: [external, motivation, problem, someday]
refs:
  - vision:fragmentation-problem
  - thread:external-interaction
---
# Feature Constellation

Elasticsearch's "query-compute-output" pattern is implemented as 6+ incompatible features:
- Ingest pipelines (document-level transforms on write)
- Enrich processors (lookup joins during ingest)
- Transforms (continuous aggregation jobs)
- Watchers (alert on query results)
- Runtime fields (per-query computed columns)
- [[painless.comparable]] scripts (embedded in queries, pipelines, and transforms)

Each has its own API surface, configuration format, execution model, scheduling mechanism, and failure semantics. Users who need to combine capabilities (e.g., enrich + aggregate + alert) must wire together multiple features with glue logic. This is distinct from the [[extraction-cliff.external]], which is about leaving ES entirely -- the feature constellation problem is about fragmentation within ES itself.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- motivates: [[transform-unification.external]] — piescript subsumes these fragments under a single typed programming model
- complements: [[extraction-cliff.external]] — extraction cliff is the outer boundary; the feature constellation is the inner fragmentation
