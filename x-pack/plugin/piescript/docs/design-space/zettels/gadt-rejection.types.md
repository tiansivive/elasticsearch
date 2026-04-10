---
tags: [types, archived, decision, concept]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# GADT Rejection

GADTs were an active candidate for the [[esql-aggregates.esql]] typing problem (how to type `STATS ... BY` output as the merge of aggregate results and group keys). Evaluated during the [[f-omega-lite.types]] design session and rejected in favor of type-level computation via `force`/`&`/`Pick`/`Omit` operators.
- GADTs require OutsideIn(X) constraint solving (significant complexity)
- The TS-style reducible type operator approach reuses the existing [[unification-algorithm.types]] and is extensible (new builtins in `force` without changing the solver)

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[f-omega-lite.types]] — D-053 solved the same problem with less type system machinery
- contrasts-with: [[esql-aggregates.esql]] — GADTs were considered for typing STATS output but rejected
- alternative-to: [[row-operators.types]] — &, Pick, Omit are the operators that replaced what GADTs would have provided
- tradeoff-with: [[higher-order-unification.types]] — GADTs would have required higher-order unification or OutsideIn(X)
