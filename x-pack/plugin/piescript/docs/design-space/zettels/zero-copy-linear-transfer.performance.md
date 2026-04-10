---
tags: [performance, types, theoretical, someday]
refs:
  - vision:speculative
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:ownership-resources
---
# Zero-Copy Linear Transfer

If a [[closure-val.language]] is linear (used exactly once), the executor can move it rather than clone it -- zero-copy transfer between nodes:
- For large captured environments traveling to remote nodes, this eliminates allocation overhead
- Linear channel edges guarantee single-consumer data flow, simplifying buffer management
- Requires [[qtt-linearity.types]] (Phase 6)
- Relates to [[ownership.types]] and [[borrow-checking.types]] for resource tracking

**Depends on**: [[qtt-linearity.types]], [[code-mobility.coordination]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-6.roadmap]]
- related: [[serialization.infrastructure]] — linear values don't need defensive cloning
- related: [[exchange-streaming.infrastructure]] — single-consumer exchanges could exploit linearity
- complements: [[data-locality.distributed]] — data locality reduces the need for transfers; linearity optimizes unavoidable ones
- related: [[closure-val.language]] — closures are the values being transferred zero-copy
- related: [[ownership.types]] — ownership semantics provide the theoretical foundation for move-vs-clone decisions
