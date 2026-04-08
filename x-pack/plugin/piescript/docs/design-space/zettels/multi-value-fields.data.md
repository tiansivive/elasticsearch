---
tags: [data, designed]
refs:
  - roadmap:block-h
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Multi-Value Fields (Block H)

ES fields are inherently multi-value capable. Block H proposes **MV-as-default**: all values from ES indices can hold one or many values. The base types (`Double`, `Keyword`, etc.) are MV-capable at runtime. Explicit narrowing via `Single a` boxing marks "I've already dealt with MV." `MV.*` rank-reducing builtins (`first`, `toList`, `sum`, `min`, `max`, `count`, `dedupe`) convert from MV-capable to explicit representations.

The design draws from APL's scalar pervasion (see [[scalar-pervasion.data]]) but diverges on MV×MV semantics: ESQL uses cartesian product where APL uses element-wise zip. The user controls the read/materialization boundary — choose when MV values become piescript Lists or get reduced to scalars.

Currently, MV fields from ESQL are handled pragmatically: `ESQL.top` and `ESQL.values` return `List a` via type-driven materialization (see [[type-driven-materialization.esql]]), while other MV fields take first element only. Full Block H semantics (pervasion, `Single a`, `MV.*` builtins) are designed but not implemented.

**Depends on**: [[shard-read.data]], [[esql-aggregates.esql]], [[scalar-pervasion.data]]
**Enables**: [[type-driven-materialization.esql]]
**Connections**:
- related: [[single-a-boxing.types]] — two-type-universe model (piescript native vs ES columnar) was the key design insight
- related: design from Block H session 2026-03-25
- related: [[esql-value-converter.esql]] — converter handles MV field materialization (first-element vs ListVal)
- related: [[mv-scalar-dispatch.data]] — runtime dispatch semantics for MV-capable values
