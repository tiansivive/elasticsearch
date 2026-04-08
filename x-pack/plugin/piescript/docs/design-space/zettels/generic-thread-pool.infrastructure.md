---
tags: [infrastructure, es-internals, concurrency, implemented, async]
refs:
  - adr:D-004
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Generic Thread Pool

`TransportPiescriptAction` runs on `threadPool.executor(ThreadPool.Names.GENERIC)`. Originally `DIRECT_EXECUTOR_SERVICE` (Phase 0). Changed in Phase 2 when synchronous query execution caused deadlock on transport threads.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: [[spawn.coordination]]
**Connections**:
- related: GENERIC threads are safe to block — resolve callbacks also fork to GENERIC; future: dedicated piescript thread pool
- related: [[transport-pipeline.infrastructure]] — the full evaluation pipeline runs on the GENERIC pool
- related: [[spawn.coordination]] — spawn forks computation to the GENERIC pool
