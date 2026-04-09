---
tags: [types, designed, concept]
refs:
  - roadmap:block-h
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Single a Boxing

`Single a` type for explicitly scalar values -- the "I've already dealt with MV" marker.

- In the MV-as-default model, base types can hold one or many values.
- `Single a` is a type-level guarantee of exactly one value.
- `MV.first`, `MV.min`, `MV.max` etc. narrow from potentially-MV to `Single a`.
- This creates a controlled boundary between MV-capable ES data and guaranteed-scalar piescript native values.

**Depends on**: [[multi-value-fields.data]], [[scalar-pervasion.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-h.roadmap]]
- analogous-to: Haskell's `Identity` monad or a "definitely not null" wrapper
- complements: [[multi-value-fields.data]] — the two-type-universe boundary: ES data (MV-capable) vs piescript native (always single)
- complements: [[mv-scalar-dispatch.data]] — MV.* rank-reducing builtins produce Single a values
