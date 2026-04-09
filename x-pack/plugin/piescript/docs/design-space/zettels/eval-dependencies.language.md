---
tags: [evaluation, infrastructure, implemented, documentation]
refs:
  - adr:D-044
  - adr:D-045
  - code:EvalDependencies.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# EvalDependencies

Context record bundling everything the [[evaluator.language]] needs: `Client`, `Executor`, `ClusterService`, `TransportService`, `IndicesService`, [[channel-registry.infrastructure]], `localNodeId`, and the [[force-threading.types]] function (from `ElaborationState`).
- Replaces a growing constructor parameter list
- Grew over multiple blocks: Block B added `ClusterService`, Block C added `TransportService` + `ChannelRegistry`, Block G added `force` function

**Depends on**: [[evaluator.language]], [[force-threading.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-b.roadmap]]
- implements: [[evaluator.language]] — the dependency injection mechanism for evaluation
- uses: [[generic-thread-pool.infrastructure]] — Executor comes from here
- uses: [[channel-registry.infrastructure]] — ChannelRegistry singleton bundled for coordination primitives
- uses: [[force-threading.types]] — force function bundled for type-driven materialization
