---
tags: [data, tech-debt, materialization, task, known-issue]
refs:
  - adr:D-039
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Eager Materialization

Every [[esql-compilation.esql]] query result is fully materialized into `List<Value>` on the coordinator.
- No streaming, no pagination, no back-pressure
- Large result sets will OOM
- Resolution: [[exchange-streaming.infrastructure]] orchestrated explicitly by piescript via [[channels.infrastructure]]

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-2.roadmap]]
- motivates: [[exchange-streaming.infrastructure]] — acceptable for prototype scope; Exchange streaming (Block G) provides the scale path
- motivates: [[materialization-boundary.data]] — materialization boundary is the design-level solution to eager materialization limits
- tension-with: [[type-stack.data]]
- motivates: [[page-opaque-typed.data]]
