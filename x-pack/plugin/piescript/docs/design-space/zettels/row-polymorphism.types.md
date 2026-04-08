---
tags: [types, row-types, implemented, unification]
refs:
  - adr:D-030
  - adr:D-029
  - adr:D-021
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Row Polymorphism

Leijen-style open-row unification with flat field maps and optional tail variables. Rows are `RowType(Map<String, MonoType> fields, Optional<MonoType.Meta> tail)`. Projection and update work via unification, not direct field lookup. Row variables allow functions to accept records with extra fields.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[row-operators.types]], [[use-declarations.data]], [[esql-compilation.esql]]
**Connections**:
- supersedes: [[concrete-row-constraints.types]] — superseded D-021 closed-row constraints
- related: Leijen chosen over Remy-style because piescript's `RowType` is already flat
- related: [[unification-algorithm.types]] — row unification is a special case within Robinson unification
- related: [[rowtype-as-monotype.types]] — RowType implements MonoType; row-kinded meta variables
- related: [[record-type.language]] — RecordType wraps a row type; primary consumer of row polymorphism
- related: [[accessor-sugar.language]] — open-row accessor sugar was the motivation for pulling row polymorphism forward (D-029)
