---
tags: [types, open, typeclasses, polymorphism]
refs:
  - adr:D-019
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Typeclasses

Ad-hoc polymorphism via typeclasses. Num for arithmetic, Eq for equality, Ord for comparison, Functor/Monad for map/bind, Semigroup for concat, Query for data access backends. Instance resolution interleaves with unification via deferred constraints.

**Depends on**: [[hindley-milner.types]], [[deferred-constraints.types]]
**Enables**: [[query-typeclass.data]], [[push-down-compilation.performance]], [[comprehension-syntax.language]]
**Connections**:
- related: [[deferred-constraints.types]] — D-019 notes migration to OutsideIn(X)
- related: [[prelude.language]] — stream combinators as prelude builtins (D-016) prepare for typeclasses
- related: [[polymorphic-equality.types]] — future Eq typeclass would replace unconstrained polymorphic equality
