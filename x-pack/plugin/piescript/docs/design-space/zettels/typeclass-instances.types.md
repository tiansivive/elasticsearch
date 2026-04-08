---
tags: [types, typeclasses, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Typeclass Instances

Specific typeclass instances: Num, Eq, Ord, Semigroup, Monoid, Functor, Monad, Filterable, Groupable, Traversable, Foldable. These are the concrete instances that make the typeclass system useful — each one unlocks specific combinators and optimizations for the types that implement them.

**Depends on**: [[typeclasses.types]]
**Enables**: [[push-down-compilation.performance]], [[query-typeclass.data]], [[groupby.language]], [[string-concat.language]]
**Connections**:
- related: [[bird-meertens.types]] — Monoid enables parallel reduce
- related: [[traverse.language]] — Traversable instance
