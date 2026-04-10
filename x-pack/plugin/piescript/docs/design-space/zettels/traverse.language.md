---
tags: [language, effects, data-processing, open, concept, needs-design, later]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Traverse Combinator

Traverse/mapM combinator for [[effect-systems.types]] sequencing over [[list-type.language]]:
- `map` applies a pure function to each element; `traverse` applies an effectful function and sequences the effects
- Essential for patterns like "for each shard, perform a write" where each step is effectful and results must be collected
- Related to [[typeclasses.types]] (Traversable instance) and [[monadic-write.data]] (effectful write sequencing)

**Depends on**: [[list-type.language]]
**Enables**: (none)
**Connections**:
- example-of: [[monadic-write.data]] — `List.map` over `Shard.write` is semantically `traverse`
- related: [[typeclass-instances.types]] — Traversable instance
- related: [[effect-systems.types]] — traverse sequences effects, requiring an effect model
- prerequisite-for: [[typeclasses.types]] — Traversable as a typeclass
