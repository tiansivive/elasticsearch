---
tags: [data, streaming, materialization, designed, columnar, concept]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalPage.java
---
# Materialization Boundary

The user controls where materialization happens: `Page.toList` on data node (early) vs streaming Pages to coordinator then materializing (late). [[exchange-streaming.infrastructure]] enables explicit control over the boundary. Three levels:

- **RawData** -- description (not yet materialized)
- **Page** -- columnar ([[page-opaque-typed.data]])
- **Value** -- piescript values

**Depends on**: [[exchange-streaming.infrastructure]], [[shard-stream.data]], [[type-stack.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-g.roadmap]]
- solves: [[eager-materialization.data]] — Block G delivers the mechanism; user/library code chooses the strategy
- complements: [[type-driven-materialization.esql]] — type-driven materialization determines which columns get full materialization vs scalar conversion
- uses: [[type-stack.data]] — the three-level type stack defines the materialization levels
- uses: [[page-opaque-typed.data]] — Page is the intermediate columnar representation
