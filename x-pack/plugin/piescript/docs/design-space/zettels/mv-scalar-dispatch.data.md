---
tags: [data, types, runtime, designed]
refs:
  - roadmap:block-h
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# MV Scalar Dispatch

Runtime dispatch for MV-capable values: should `+` on an MV Double automatically lift (scalar pervasion), or does the user explicitly call MV.map? The Block H design chose pervasion-by-default: scalar functions automatically lift to work over multi-values. The runtime must inspect whether a value is single or multi at operation time. This is rank polymorphism, distinct from typeclass dictionary passing.

**Depends on**: [[scalar-pervasion.data]], [[multi-value-fields.data]]
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[runtime-dispatch.types]] — split from runtime-dispatch which was conflating two concepts; this is about MV lifting semantics, that is about typeclass implementation strategy
