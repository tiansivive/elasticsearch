---
tags: [coordination, channels, open, feature, concept, needs-design, next]
refs:
  - roadmap:block-b-old
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:distributed-coordination
---
# Multi-Value Channels

[[channels.infrastructure]] that carry streams of messages over time, not just single completion. Would require channel message stores (multiset/bag semantics) and a join automaton for pattern matching over accumulated messages.

The [[exchange-streaming.infrastructure]] system (D-054, Block G) handles **data-plane** streaming -- large columnar `Page` batches with backpressure. Multi-value channels address a different layer: **control-plane** coordination where message volume is small (tens to hundreds) but reaction semantics are rich. Example: 10 workers sending results back to the same coordinator channel over time via [[send.coordination]]. That's not an Exchange use case -- it's value-level messaging.

**Depends on**: [[channels.infrastructure]]
**Enables**: [[cham-patterns.coordination]], [[fold-as-join.coordination]], [[sse-streaming.external]], [[watcher-replacement.external]]
**Connections**:
- part-of: [[future-coordination.roadmap]]
- contrasts-with: [[exchange-streaming.infrastructure]] — Exchanges handle columnar data-plane streaming; MV channels handle value-level control-plane messaging
- enables: [[actor-model.lifecycle]] — persistent actors receiving input streams need multi-value inbox
- contrasts-with: [[multi-value-fields.data]] — different concepts (MV channels vs MV ES fields) sharing the "multiple values under one name" pattern
- related: [[cham-patterns.coordination]] — CHAM functional-pattern matching operates over channel message stores
