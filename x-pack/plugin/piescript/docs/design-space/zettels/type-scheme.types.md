---
tags: [types, polymorphism, implemented]
refs:
  - adr:D-034
  - adr:D-038
  - code:TypeScheme.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Type Scheme

`TypeScheme`: quantified map (rigid ID -> kind) + monomorphic body. Produced by `generalize` (unsolved metas -> Rigids). Consumed by `instantiate` (Rigids -> fresh Metas). Retained for future qualified types (typeclass constraints like `forall a. Num a => a -> a`). Not removed even after `MonoType` gets `Forall` variant.

**Depends on**: [[rigid-variables.types]]
**Enables**: [[type-annotations.types]], [[system-f-core.types]]
**Connections**:
- related: [[forall-type.types]] — dual representation (`TypeScheme` for let-bindings, future `Forall` in `MonoType` for expression-level polytypes) is deliberate; schemes carry constraint metadata that `Forall` doesn't
- related: [[meta-variables.types]] — generalize converts unsolved metas to rigids in the scheme body
- related: [[binding-levels.types]] — generalization collects metas at the current binding level
