---
tags: [types, tech-debt]
refs:
  - adr:D-038
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Forall Type

`MonoType` has no `Forall` variant, so `CoreTypeAbs` cannot express its own type (∀a. τ). Polytype ascription produces orphaned rigids. Fix: rename `MonoType` → `Type`, add `Forall(rigidId, kind, body)`. Related tests `@AwaitsFix`. The annotated-let path works because the scheme bypasses `generalize`.

**Depends on**: [[system-f-core.types]], [[rigid-variables.types]]
**Enables**: [[bidir-checking.types]]
**Connections**:
- related: [[unification-algorithm.types]] — `CoreExpr.type()` return type changes, unifier must handle `Forall`, `generalize` detects `Forall` in RHS
- related: [[type-scheme.types]] — dual representation: TypeScheme for let-bindings, Forall for expression-level polytypes
- related: [[higher-rank.types]] — Forall variant enables higher-rank polymorphism
