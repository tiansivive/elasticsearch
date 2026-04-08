---
tags: [language, effects, data-processing, open]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Traverse Combinator

Traverse/mapM combinator for effect sequencing over lists. While `map` applies a pure function to each element, `traverse` applies an effectful function and sequences the effects. This is essential for patterns like "for each shard, perform a write" where each step is effectful and the results must be collected.

**Depends on**: [[list-type.language]]
**Enables**: (none)
**Connections**:
- related: [[monadic-write.data]] — `List.map` over `Shard.write` is semantically `traverse`
- informs: [[typeclass-instances.types]] — Traversable instance
