---
tags: [types, row-types, implemented, nbe, kinds]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# F-omega-lite

Extends HM + rows toward F-omega-lite. Kinds are `MonoType` values (`TCon("Type")`, `TCon("Row")`), not a separate enum. Arrow kinds use `MonoType.Arrow`. The same unifier solves kind constraints. `force` NbE normalizer chases meta chains AND reduces built-in type operators. Types after `force` are in head-normal form.

**Depends on**: [[hindley-milner.types]], [[zonker.types]], [[row-polymorphism.types]]
**Enables**: [[row-operators.types]], [[esql-aggregates.esql]]
**Connections**:
- related: GHC TypeInType-style — `Prelude.KINDS` maps each builtin type constructor to its kind
- related: [[nbe-dual-pattern.types]] — new reducible builtins can be added to `force` without changing the unifier
- related: [[kind-system.types]] — kinds are MonoType values; kind system is the foundation
- related: [[force-threading.types]] — force function bridges elaboration and evaluation
- related: [[gadt-rejection.types]] — F-omega-lite was chosen over GADTs for type-level computation
- related: [[maplist-operator.types]] — demonstrates extensibility of the force/reducible-builtin pattern
