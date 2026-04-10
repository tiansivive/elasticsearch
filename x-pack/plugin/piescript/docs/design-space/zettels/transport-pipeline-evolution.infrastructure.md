---
tags: [infrastructure, async, implemented, documentation, decision, es-internals, transport-layer]
refs:
  - adr:D-004
  - code:TransportPiescriptAction.java
  - code:server/src/main/java/org/elasticsearch/transport/TransportService.java
---
# Transport Pipeline Evolution

The progression from Phase 0 query passthrough to the unified parse-elaborate-evaluate pipeline:
- Early implementation used direct ESQL passthrough
- Pipeline evolved to parse piescript source, elaborate (type-check and produce [[core-ir.language]]), then evaluate asynchronously via [[evaluator.language]]
- Includes the [[generic-thread-pool.infrastructure]] deadlock discovery (D-004 revision): blocking on transport threads caused deadlock when the evaluator called `client.execute` synchronously, resolved by forking evaluation to the GENERIC thread pool

**Depends on**: [[generic-thread-pool.infrastructure]]
**Enables**: (none directly)
**Connections**:
- uses: [[transport-layer.es]] — transport threading model vs eval pipeline
- uses: [[generic-thread-pool.infrastructure]] — the GENERIC pool avoids transport-thread deadlocks
- part-of: [[transport-pipeline.infrastructure]] — the transport action that hosts the full pipeline
- uses: [[evaluator.language]] — evaluation is the final async pipeline stage
- uses: [[core-ir.language]] — elaboration produces Core IR consumed by the evaluator
