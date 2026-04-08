---
tags: [types, polymorphism, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Existential Types

Existential types for abstract type signatures (hiding implementation). An existential type `exists a. T a` packages a type together with a value, hiding the concrete type from consumers. This enables abstract module interfaces where the implementation type is opaque, enforcing encapsulation at the type level.

**Depends on**: [[forall-type.types]]
**Enables**: abstract module interfaces
**Connections**:
- related: [[gadt-rejection.types]] — GADTs generalize existentials
- related: [[type-narrowing.types]] — pattern matching on existentials reveals hidden type
- related: [[module-system.tooling]] — existentials enable abstract module interfaces with opaque types
