---
tags: [data, designed, concept, needs-design, next]
refs:
  - roadmap:block-h
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:data-completeness
---
# Multi-Value Fields (Block H)

ES fields are inherently multi-value capable. Block H proposes **MV-as-default**: all values from ES indices can hold one or many values. The base types (`Double`, `Keyword`, etc.) are MV-capable at runtime. Explicit narrowing via [[single-a-boxing.types]] marks "I've already dealt with MV." `MV.*` rank-reducing builtins (`first`, `toList`, `sum`, `min`, `max`, `count`, `dedupe`) convert from MV-capable to explicit representations.

- Design draws from APL's [[scalar-pervasion.data]] but diverges on MV x MV semantics: ESQL uses cartesian product where APL uses element-wise zip
- User controls the read/materialization boundary -- choose when MV values become piescript Lists or get reduced to scalars
- Currently, `ESQL.top` and `ESQL.values` return `List a` via [[type-driven-materialization.esql]], while other MV fields take first element only
- Full Block H semantics (pervasion, `Single a`, `MV.*` builtins) are designed but not implemented

**Depends on**: [[shard-read.data]], [[esql-aggregates.esql]], [[scalar-pervasion.data]]
**Enables**: [[type-driven-materialization.esql]]
**Connections**:
- part-of: [[block-h.roadmap]]
- complements: [[single-a-boxing.types]] — two-type-universe model (piescript native vs ES columnar) was the key design insight
- uses: [[esql-value-converter.esql]] — converter handles MV field materialization (first-element vs ListVal)
- complements: [[mv-scalar-dispatch.data]] — runtime dispatch semantics for MV-capable values
- contrasts-with: [[multi-value-channels.coordination]] — different concepts (MV ES fields vs MV channels) sharing the "multiple values under one name" pattern
