---
tags: [data, streaming, materialization, designed, columnar]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Materialization Boundary

The user controls where materialization happens: Page.toList on data node (early) vs streaming Pages to coordinator then materializing (late). Exchange enables explicit control over the boundary. Three levels: RawData (description), Page (columnar), Value (piescript).

**Depends on**: [[exchange-streaming.infrastructure]], [[shard-stream.data]]
**Enables**: (none directly)
**Connections**:
- related: [[eager-materialization.data]] — Block G delivers the mechanism; user/library code chooses the strategy
- related: [[type-driven-materialization.esql]] — type-driven materialization determines which columns get full materialization vs scalar conversion
