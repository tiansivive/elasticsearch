---
tags: [types, infrastructure, technique, implemented, evaluation, concept]
refs:
  - code:IndexResolutionPrePass.java
  - code:TransportPiescriptAction.java
  - adr:D-050
---
# Async Pre-Pass

Synchronous elaboration with an async pre-pass. `IndexResolutionPrePass` uses a `RefCountingListener` fan-out to resolve field capabilities for all `use` declarations concurrently before elaboration starts. This bridges the async Elasticsearch transport APIs to the synchronous, recursive-descent type checker -- the elaborator never needs to block on I/O.

The pattern is general: any future phase that needs async ES data before a synchronous pipeline stage can follow the same shape -- fan out async requests, collect results, then hand the resolved data to the sync phase. The pre-pass runs on the GENERIC thread pool; the elaborator runs synchronously on the callback thread once all results arrive.

**Depends on**: [[field-caps-resolution.data]], [[generic-thread-pool.infrastructure]]
**Enables**: [[elaboration-architecture.types]]
**Connections**:
- implements: [[field-caps-resolution.data]] -- the pre-pass IS the mechanism that resolves field caps
- uses: [[evaluator.language]] -- the GENERIC thread fork pattern is shared with spawn
- extends: [[elaboration-architecture.types]] -- pre-pass feeds resolved index types into the immutable ElaborationContext
- part-of: [[compilation-pipeline.hub]] -- first async stage in the end-to-end pipeline
- analogous-to: [[nbe-compilation.esql]] -- both are "resolve async, then run sync": pre-pass resolves field caps, NbE resolves symbols
- informs: [[auth-checks.elaboration]] -- future auth pre-pass could use the same fan-out pattern
