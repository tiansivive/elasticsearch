---
tags: [types, row-types, implemented, nbe, kinds, decision, concept]
refs:
  - adr:D-053
  - code:piescript.types
  - code:Prelude.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# F-omega-lite

Extends [[hindley-milner.types]] + [[row-polymorphism.types]] toward F-omega-lite.
- Kinds are `MonoType` values (`TCon("Type")`, `TCon("Row")`), not a separate enum -- see [[kind-system.types]]
- Arrow kinds use `MonoType.Arrow`; the same [[unification-algorithm.types]] solves kind constraints
- `force` NbE normalizer chases meta chains AND reduces built-in type operators -- see [[force-threading.types]]
- Types after `force` are in head-normal form

**Depends on**: [[hindley-milner.types]], [[zonker.types]], [[row-polymorphism.types]]
**Enables**: [[row-operators.types]], [[esql-aggregates.esql]]
**Connections**:
- part-of: [[block-f.roadmap]]
- inspired-by: GHC TypeInType-style — `Prelude.KINDS` maps each builtin type constructor to its kind
- extends: [[nbe-dual-pattern.types]] — new reducible builtins can be added to `force` without changing the unifier
- uses: [[kind-system.types]] — kinds are MonoType values; kind system is the foundation
- uses: [[unification-algorithm.types]] — same unifier solves kind constraints
- implements: [[force-threading.types]] — force function bridges elaboration and evaluation
- makes-redundant: [[gadt-rejection.types]] — F-omega-lite solved the same typing problem (STATS output) without GADTs (D-053)
- validates: [[maplist-operator.types]] — demonstrates extensibility of the force/reducible-builtin pattern
