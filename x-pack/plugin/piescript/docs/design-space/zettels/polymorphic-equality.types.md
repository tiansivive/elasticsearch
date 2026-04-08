---
tags: [types, implemented, polymorphism]
refs:
  - adr:D-049
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Polymorphic Equality

`==` and `!=` are fully polymorphic: `∀a. a → a → Boolean`. Both operands unify to the same type but that type is unconstrained. Uses Java `Object.equals` on `Value` records. Ordering operators (`<`, `>`, etc.) remain `Double → Double → Boolean`.

**Depends on**: [[hindley-milner.types]]
**Enables**: (none directly)
**Connections**:
- related: [[typeclasses.types]] — comparing closures is allowed but meaningless; future `Eq` typeclass could restrict to sensible types
