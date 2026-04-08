---
tags: [infrastructure, es-internals, open]
refs:
  - adr:D-044
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Multi-Project Index Resolution

Multi-project index resolution (currently uses `ProjectId.DEFAULT`). Elasticsearch's multi-project support means index operations need a project context. Piescript currently hardcodes `ProjectId.DEFAULT`, which works for single-project clusters but needs proper project resolution for multi-project deployments.

**Depends on**: [[topology.infrastructure]], [[field-caps-resolution.data]]
**Enables**: (none)
**Connections**:
- related: [[security-namespace.infrastructure]] — project context affects authorization scope for piescript actions
