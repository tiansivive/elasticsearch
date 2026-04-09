---
tags: [types, implemented, polymorphism, decision]
refs:
  - adr:D-049
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalPrimOps.java
  - code:Prelude.java
---
# Polymorphic Equality

- `==` and `!=` are fully polymorphic: `forall a. a -> a -> Boolean`.
- Both operands [[unification-algorithm.types|unify]] to the same type but that type is unconstrained.
- Uses Java `Object.equals` on [[evaluator.language|Value]] records.
- Ordering operators (`<`, `>`, etc.) remain `Double -> Double -> Boolean`.

**Depends on**: [[hindley-milner.types]]
**Enables**: (none directly)
**Connections**:
- tension-with: [[typeclasses.types]] — comparing closures is allowed but meaningless; future `Eq` typeclass could restrict to sensible types
