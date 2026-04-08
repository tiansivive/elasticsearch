---
tags: [evaluation, infrastructure, implemented]
refs:
  - adr:D-044
  - adr:D-045
  - code:EvalDependencies.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# EvalDependencies

Context record bundling everything the evaluator needs: `Client`, `Executor`, `ClusterService`, `TransportService`, `IndicesService`, `ChannelRegistry`, `localNodeId`, and the `force` function (from `ElaborationState`). Replaces a growing constructor parameter list. Grew over multiple blocks: Block B added `ClusterService`, Block C added `TransportService` + `ChannelRegistry`, Block G added `force` function.

**Depends on**: [[evaluator.language]], [[force-threading.types]]
**Enables**: (none directly)
**Connections**:
- implements: [[evaluator.language]] — the dependency injection mechanism for evaluation
- related: [[generic-thread-pool.infrastructure]] — Executor comes from here
