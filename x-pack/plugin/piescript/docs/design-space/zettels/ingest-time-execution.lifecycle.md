---
tags: [lifecycle, write-path, open, exploration, feature, someday]
refs:
  - thread:external-interaction
---
# Ingest-Time Execution

Running piescript programs at ingest time -- triggered per-document on the write path -- alongside the existing batch, query-time, and (future) [[scheduled-execution.lifecycle]] modes.
- The same piescript program could run in different execution contexts: a validation function used at query time for ad-hoc checks could also run at ingest time to reject malformed documents before they are indexed
- This would replace ingest pipelines and processors with typed, composable piescript functions, eliminating one more piece of the [[feature-constellation.external]] fragmentation problem

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[transform-unification.external]] -- ingest-time execution is one of the five fragmented compute modes piescript aims to subsume
- extends: [[scheduled-execution.lifecycle]] -- adds another execution-context variant alongside batch/scheduled/query-time
- related: [[es-write-internals.infrastructure]] -- ingest-time execution hooks into the same write path that es-write-internals documents
- solves: [[feature-constellation.external]] -- replaces ingest pipelines and processors with typed piescript functions
