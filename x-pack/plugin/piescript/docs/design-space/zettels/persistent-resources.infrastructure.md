---
tags: [infrastructure, resources, lifecycle, theoretical]
refs:
  - vision:speculative
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Persistent Resources

Shared in-memory resources (counters, lookup tables, caches) with safe lifecycle. Resources that outlive a single script execution, shared across invocations or between concurrent scripts. Requires ownership tracking to ensure cleanup when no script references the resource, and safe concurrent access patterns.

**Depends on**: [[ownership.types]], [[qtt-linearity.types]]
**Enables**: (none)
**Connections**:
- related: [[incremental-computation.performance]] — persistent state enables incremental
- related: [[actor-model.lifecycle]] — resources need lifecycle management
- related: [[channel-lifecycle.infrastructure]] — both are "resource outlives expected scope" problems needing ownership or lifetime tracking
