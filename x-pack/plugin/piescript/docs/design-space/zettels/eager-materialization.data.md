---
tags: [data, tech-debt, materialization]
refs:
  - adr:D-039
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Eager Materialization

Every ESQL query result is fully materialized into `List<Value>` on the coordinator. No streaming, no pagination, no back-pressure. Large result sets will OOM. Resolution: Exchange streaming orchestrated explicitly by piescript via channels.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: [[exchange-streaming.infrastructure]] — acceptable for prototype scope; Exchange streaming (Block G) provides the scale path
- related: [[materialization-boundary.data]] — materialization boundary is the design-level solution to eager materialization limits
