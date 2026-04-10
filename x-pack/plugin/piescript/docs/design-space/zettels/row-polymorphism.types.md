---
tags: [types, row-types, implemented, unification, decision, concept]
refs:
  - adr:D-030
  - adr:D-029
  - adr:D-021
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Unifier.java
  - code:piescript.types
---
# Row Polymorphism

Leijen-style open-row [[unification-algorithm.types|unification]] with flat field maps and optional tail variables.

- Rows are `RowType(Map<String, MonoType> fields, Optional<MonoType.Meta> tail)`.
- Projection and update work via unification, not direct field lookup.
- Row variables allow functions to accept [[record-type.language|records]] with extra fields.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[row-operators.types]], [[use-declarations.data]], [[esql-compilation.esql]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- replaces: [[concrete-row-constraints.types]] — replaced D-021 closed-row constraints with open-row unification
- inspired-by: Leijen chosen over Remy-style because piescript's `RowType` is already flat
- specializes: [[unification-algorithm.types]] — row unification is a special case within Robinson unification
- uses: [[rowtype-as-monotype.types]] — RowType implements MonoType; row-kinded meta variables
- complements: [[record-type.language]] — RecordType wraps a row type; primary consumer of row polymorphism
- motivated-by: [[accessor-sugar.language]] — open-row accessor sugar was the motivation for pulling row polymorphism forward (D-029)
