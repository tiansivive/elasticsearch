---
tags: [data, types, designed]
refs:
  - roadmap:block-h
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Scalar Pervasion

APL-style scalar pervasion: scalar functions automatically lift to work over arrays. `1 + [3,4]` = `[4,5]`. This is the core semantics for MV field handling — scalar ops permeate through multi-values transparently. Diverges from APL on MV×MV: ESQL uses cartesian product (all pairs) where APL uses element-wise zip (requires same length). The cartesian semantics match ESQL's existing behavior and are semantically richer but produce larger results.

**Depends on**: [[multi-value-fields.data]]
**Enables**: [[runtime-dispatch.types]]
**Connections**:
- related: APL calls this "pervasion" — well-studied, clean semantics
- related: key design insight from Block H session: piescript inherits ESQL's cartesian MV*MV semantics, not APL's zip semantics
