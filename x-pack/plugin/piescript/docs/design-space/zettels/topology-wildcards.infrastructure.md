---
tags: [infrastructure, es-internals, open]
refs:
  - adr:D-044
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Topology Wildcards

Wildcard, alias, and data stream pattern resolution in topology. The topology layer currently resolves concrete index names, but Elasticsearch supports wildcards (`logs-*`), aliases, and data streams that resolve to multiple concrete indices. Supporting these patterns requires integrating with the `IndexNameExpressionResolver` at topology resolution time.

**Depends on**: [[topology.infrastructure]]
**Enables**: (none)
**Connections**:
- related: [[dynamic-index-names.data]] — similar runtime resolution problem
