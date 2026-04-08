---
tags: [external, lifecycle, designed]
refs:
  - vision:fragmentation-problem
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transform Unification

Ingest pipelines, enrich processors, transforms, watchers, runtime fields -- five features that all do query->compute->output with separate APIs, configs, and execution models. Piescript subsumes all as a single typed program.

**Depends on**: [[scheduled-execution.lifecycle]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- related: [[shard-write.data]] — requires Block E (writes)
- related: [[scheduled-execution.lifecycle]] — requires scheduled execution
- related: [[groupby.language]] — requires groupBy combinator for full story
- related: [[watcher-replacement.external]] — watcher replacement is part of the same fragmentation unification
- related: [[extraction-cliff.external]] — unifying fragments under piescript extends the boundary before extraction is needed
