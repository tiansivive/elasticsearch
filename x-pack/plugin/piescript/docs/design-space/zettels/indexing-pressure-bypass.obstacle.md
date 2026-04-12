---
tags: [data, write-path, es-internals, obstacle, open, mem-management, concept]
refs:
  - adr:D-051
  - code:EvalShard.java
  - code:WriterState.java
---
# Indexing Pressure Bypass

Shard-level writes via `Shard.write` bypass Elasticsearch's `IndexingPressure` mechanism entirely. `IndexingPressure` tracks in-flight indexing bytes across the node and rejects writes when memory pressure exceeds thresholds -- it's Elasticsearch's backpressure system for the write path.

By calling `IndexShard.applyIndexOperationOnPrimary()` directly, piescript sidesteps this protection. In production, a piescript script performing unbounded writes could consume arbitrary memory without triggering the pressure-based rejection that the standard bulk API uses.

Future integration paths:
- **WriterState lifecycle**: track coordinating/primary bytes in `WriterState.acquire()` / `WriterState.release()`, mirroring what `IndexingPressure` does for transport-level writes.
- **Circuit breaker integration**: hook into the parent circuit breaker instead of `IndexingPressure` directly, since piescript's memory accounting may need a different granularity.
- **Rate limiting at the language level**: expose backpressure as a channel signal -- when pressure is high, the write channel blocks (coordination-native backpressure).

**Depends on**: [[primary-shard-write.data]]
**Enables**: (none directly -- production hardening)
**Connections**:
- implements: [[primary-shard-write.data]] -- bypassing transport also bypasses indexing pressure tracking
- informs: [[es-conventions-debt.infrastructure]] -- another ES convention gap from direct shard access
- informs: [[circuit-breaker.infrastructure]] -- circuit breakers are the general backpressure mechanism; indexing pressure is a specific instance
- tension-with: [[shard-write.data]] -- maximum performance comes at the cost of no memory protection
- informs: [[write-context.data]] -- WriteContext abstraction should include pressure tracking
- related: [[bracket-patterns.language]] -- WriterState resource lifecycle relates to pressure tracking lifecycle
