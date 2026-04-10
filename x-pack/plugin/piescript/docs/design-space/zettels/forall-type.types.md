---
tags: [types, tech-debt, task, ready, next]
refs:
  - adr:D-038
  - code:MonoType.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
  - thread:type-foundations
---
# Forall Type

`MonoType` has no `Forall` variant, so [[core-ir.language]] `CoreTypeAbs` cannot express its own type (forall a. t).
- Polytype ascription produces orphaned [[rigid-variables.types]]
- Fix: rename `MonoType` -> `Type`, add `Forall(rigidId, kind, body)`
- Related tests `@AwaitsFix`
- The annotated-let path works because the [[type-scheme.types]] bypasses `generalize`

**Depends on**: [[system-f-core.types]], [[rigid-variables.types]]
**Enables**: [[bidir-checking.types]]
**Connections**:
- constrains: [[unification-algorithm.types]] — `CoreExpr.type()` return type changes, unifier must handle `Forall`, `generalize` detects `Forall` in RHS
- complements: [[type-scheme.types]] — dual representation: TypeScheme for let-bindings, Forall for expression-level polytypes
- prerequisite-for: [[higher-rank.types]] — Forall variant enables higher-rank polymorphism
