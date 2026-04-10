---
tags: [types, open, typeclasses, polymorphism, feature, concept, needs-design, someday]
refs:
  - adr:D-019
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
  - thread:type-foundations
---
# Typeclasses

Ad-hoc polymorphism via typeclasses:
- **Num** for arithmetic, **Eq** for equality, **Ord** for comparison
- **Functor/Monad** for map/bind, **Semigroup** for concat
- **Query** for data access backends (see [[query-typeclass.data]])
- Instance resolution interleaves with [[unification-algorithm.types]] via [[deferred-constraints.types]]
- See [[typeclass-instances.types]] for the concrete instance catalog

**Depends on**: [[hindley-milner.types]], [[deferred-constraints.types]]
**Enables**: [[query-typeclass.data]], [[push-down-compilation.performance]], [[comprehension-syntax.language]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- related: [[deferred-constraints.types]] — D-019 notes migration to OutsideIn(X)
- refines: [[prelude.language]] — stream combinators as prelude builtins (D-016) prepare for typeclasses
- subsumes: [[polymorphic-equality.types]] — future Eq typeclass would replace unconstrained polymorphic equality
- enables: [[typeclass-instances.types]] — instances are the concrete realizations of the typeclass interfaces
- related: [[unification-algorithm.types]] — instance resolution interleaves with type unification
