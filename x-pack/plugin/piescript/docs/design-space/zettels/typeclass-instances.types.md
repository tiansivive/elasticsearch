---
tags: [types, typeclasses, open, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Typeclass Instances

Specific [[typeclasses.types]] instances:
- **Num, Eq, Ord** -- primitive dispatch for arithmetic, equality, comparison
- **Semigroup, Monoid** -- concatenation and parallel reduce (see [[bird-meertens.types]])
- **Functor, Monad** -- map/bind for effectful composition
- **Filterable, Groupable** -- data-processing combinators
- **Traversable, Foldable** -- effect sequencing over collections (see [[traverse.language]])

These are the concrete instances that make the typeclass system useful -- each one unlocks specific combinators and optimizations for the types that implement them.

**Depends on**: [[typeclasses.types]]
**Enables**: [[push-down-compilation.performance]], [[query-typeclass.data]], [[groupby.language]], [[string-concat.language]]
**Connections**:
- related: [[bird-meertens.types]] — Monoid enables parallel reduce
- implements: [[traverse.language]] — Traversable instance
- extends: [[typeclasses.types]] — instances make the typeclass system concrete
