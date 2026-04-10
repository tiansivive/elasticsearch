---
tags: [types, polymorphism, theoretical, someday]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Higher-Rank Polymorphism

Higher-rank / rank-N polymorphism beyond [[system-f-core.types]] rank-1. In rank-1 polymorphism, [[forall-type.types]] quantifiers only appear at the outermost level.
- Higher-rank types allow polymorphic arguments: `(forall a. a -> a) -> Int`
- This enables more expressive abstractions but complicates type inference -- rank-2 and above generally require [[type-annotations.types]]

**Depends on**: [[forall-type.types]], [[hindley-milner.types]]
**Enables**: (none)
**Connections**:
- part-of: [[future-type-system.roadmap]]
- overlaps: [[impredicativity.types]] — often discussed together
- informs: [[typeclasses.types]] — some typeclass encodings need rank-2
- related: [[bidir-checking.types]] — rank-2 and above require type annotations / checking mode
- extends: [[system-f-core.types]] — System F provides the basis for higher-rank types
