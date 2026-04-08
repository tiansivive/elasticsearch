---
tags: [types, unification, theoretical]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Higher-Order Unification

First-order unification (Robinson) is what piescript uses — decidable, efficient, well-understood. Higher-order unification (undecidable in general, semi-decidable with pattern restrictions) would be needed for higher-rank polymorphism, type families, or full GADT inference. Deliberately avoided — the F-omega-lite approach (reducible builtins in force) provides type-level computation without higher-order unification.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: [[gadt-rejection.types]] — D-053 is partly motivated by avoiding higher-order unification
- related: Miller patterns (restricted higher-order unification) are a middle ground not yet explored
- contrasts-with: [[unification-algorithm.types]] — piescript uses first-order Robinson unification
- related: [[f-omega-lite.types]] — force/reducible-builtin approach avoids higher-order unification entirely
