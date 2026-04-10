---
tags: [external, lifecycle, designed, concept, someday]
refs:
  - vision:fragmentation-problem
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:external-interaction
---
# Transform Unification

Ingest pipelines, enrich processors, transforms, watchers, runtime fields -- five features that all do query->compute->output with separate APIs, configs, and execution models. Piescript subsumes all as a single typed program. This is the [[feature-constellation.external]] problem: fragmented features with overlapping capabilities that piescript unifies under a single [[core-ir.language]].

**Depends on**: [[scheduled-execution.lifecycle]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- related: [[shard-write.data]] — requires Block E (writes)
- related: [[scheduled-execution.lifecycle]] — requires scheduled execution
- related: [[groupby.language]] — requires groupBy combinator for full story
- subsumes: [[watcher-replacement.external]] — watcher replacement is part of the same fragmentation unification
- solves: [[extraction-cliff.external]] — unifying fragments under piescript extends the boundary before extraction is needed
- solves: [[feature-constellation.external]] — directly addresses the fragmented feature landscape
- related: [[core-ir.language]] — single typed IR replaces five separate execution models
