---
tags: [performance, types, theoretical]
refs:
  - vision:speculative
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Zero-Copy Linear Transfer

If a closure is linear (used exactly once), the executor can move it rather than clone it — zero-copy transfer between nodes. For large captured environments traveling to remote nodes, this eliminates allocation overhead. Linear channel edges guarantee single-consumer data flow, simplifying buffer management. Requires QTT linearity (Phase 6).

**Depends on**: [[qtt-linearity.types]], [[code-mobility.coordination]]
**Enables**: (none directly)
**Connections**:
- informs: [[serialization.infrastructure]] — linear values don't need defensive cloning
- related: [[exchange-streaming.infrastructure]] — single-consumer exchanges could exploit linearity
- related: [[data-locality.distributed]] — data locality reduces the need for transfers; linearity optimizes unavoidable ones
