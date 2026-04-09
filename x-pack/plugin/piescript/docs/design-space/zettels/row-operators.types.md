---
tags: [types, row-types, implemented, concept]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:piescript.types
---
# Row Operators

Three built-in row operators: `&` ([[row-merge-semantics.types|merge, right-biased]] on overlap), `Pick` (keep fields in intersection), `Omit` (remove fields in intersection). All have kind `Row -> Row -> Row`. Reduce in [[force-threading.types|force]] when both operands are concrete `RowType`s. Used by [[esql-aggregates.esql|ESQL.statsBy]] (output `s & t`), `ESQL.keep` (`Pick`), `ESQL.drop` (`Omit`).

**Depends on**: [[f-omega-lite.types]], [[row-polymorphism.types]]
**Enables**: [[esql-aggregates.esql]]
**Connections**:
- part-of: [[block-f.roadmap]]
- contrasts-with: [[gadt-rejection.types]] — PureScript-style Union constraint was rejected; TS-style reducible operators preferred
- subsumes: [[row-merge-semantics.types]] — & merge semantics (right-biased overlap)
- example-of: [[nbe-dual-pattern.types]] — operators are reducible builtins in force
- uses: [[force-threading.types]] — reduced by the force normalizer at point of use
- analogous-to: [[maplist-operator.types]] — proposed MapList follows the same reducible-builtin pattern
