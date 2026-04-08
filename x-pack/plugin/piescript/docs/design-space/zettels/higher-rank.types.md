---
tags: [types, polymorphism, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Higher-Rank Polymorphism

Higher-rank / rank-N polymorphism beyond System F rank-1. In rank-1 polymorphism, forall quantifiers only appear at the outermost level. Higher-rank types allow polymorphic arguments: `(forall a. a -> a) -> Int`. This enables more expressive abstractions but complicates type inference — rank-2 and above generally require type annotations.

**Depends on**: [[forall-type.types]], [[hindley-milner.types]]
**Enables**: (none)
**Connections**:
- related: [[impredicativity.types]] — often discussed together
- informs: [[typeclasses.types]] — some typeclass encodings need rank-2
- related: [[bidir-checking.types]] — rank-2 and above require type annotations / checking mode
- related: [[system-f-core.types]] — System F provides the basis for higher-rank types
