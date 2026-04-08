---
tags: [coordination, channels, open]
refs:
  - roadmap:block-b-old
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Multi-Value Channels

Channels that carry streams of messages over time, not just single completion. Would require channel message stores (multiset/bag semantics) and a join automaton for pattern matching over accumulated messages.

The Exchange system (D-054, Block G) handles **data-plane** streaming — large columnar `Page` batches with backpressure via `ExchangeService`. Multi-value channels address a different layer: **control-plane** coordination where message volume is small (tens to hundreds) but reaction semantics are rich. Example: 10 workers sending results back to the same coordinator channel over time. That's not an Exchange use case — it's value-level messaging.

**Depends on**: [[channels.infrastructure]]
**Enables**: [[cham-patterns.coordination]], [[fold-as-join.coordination]], [[sse-streaming.external]], [[watcher-replacement.external]]
**Connections**:
- contrasts-with: [[exchange-streaming.infrastructure]] — Exchanges handle columnar data-plane streaming; MV channels handle value-level control-plane messaging
- enables: [[actor-model.lifecycle]] — persistent actors receiving input streams need multi-value inbox
- contrasts-with: [[multi-value-fields.data]] — different concepts (MV channels vs MV ES fields) sharing the "multiple values under one name" pattern
- informs: [[cham-patterns.coordination]] — CHAM functional-pattern matching operates over channel message stores
