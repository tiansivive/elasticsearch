---
tags: [types, row-types, implemented]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Row Operators

Three built-in row operators: `&` (merge, right-biased on overlap), `Pick` (keep fields in intersection), `Omit` (remove fields in intersection). All have kind `Row → Row → Row`. Reduce in `force` when both operands are concrete `RowType`s. Used by `ESQL.statsBy` (output `s & t`), `ESQL.keep` (`Pick`), `ESQL.drop` (`Omit`).

**Depends on**: [[f-omega-lite.types]], [[row-polymorphism.types]]
**Enables**: [[esql-aggregates.esql]]
**Connections**:
- related: [[gadt-rejection.types]] — PureScript-style Union constraint was rejected; TS-style reducible operators preferred
- related: [[row-merge-semantics.types]] — & merge semantics (right-biased overlap)
- related: [[nbe-dual-pattern.types]] — operators are reducible builtins in force
- related: [[force-threading.types]] — reduced by the force normalizer at point of use
- related: [[maplist-operator.types]] — proposed MapList follows the same reducible-builtin pattern
