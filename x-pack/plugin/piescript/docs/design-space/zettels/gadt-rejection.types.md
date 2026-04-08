---
tags: [types, superseded]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# GADT Rejection

GADTs were an active candidate for the ESQL stats typing problem (how to type `STATS ... BY` output as the merge of aggregate results and group keys). Evaluated during the F-omega design session (846bd5a8) and rejected in favor of type-level computation via `force`/`&`/`Pick`/`Omit` operators. Reasons: GADTs require OutsideIn(X) constraint solving (significant complexity), while the TS-style reducible type operator approach reuses the existing unifier and is extensible (new builtins in `force` without changing the solver).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[f-omega-lite.types]] — D-053 solved the same problem with less type system machinery
- related: PureScript-style Union constraint also rejected (relational style doesn't fit)
- related: "rejected alternative" item — important for preventing re-proposal
- related: [[row-operators.types]] — &, Pick, Omit are the operators that replaced what GADTs would have provided
- related: [[higher-order-unification.types]] — GADTs would have required higher-order unification or OutsideIn(X)
