---
tags: [infrastructure, es-internals, open, task]
refs:
  - adr:D-044
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Topology Wildcards

Wildcard, alias, and data stream pattern resolution in [[topology.infrastructure]]:
- The topology layer currently resolves concrete index names only
- Elasticsearch supports wildcards (`logs-*`), aliases, and data streams that resolve to multiple concrete indices
- Supporting these patterns requires integrating with the `IndexNameExpressionResolver` at topology resolution time
- Related to how [[use-declarations.data]] resolves index names at elaboration time

**Depends on**: [[topology.infrastructure]]
**Enables**: (none)
**Connections**:
- related: [[dynamic-index-names.data]] — similar runtime resolution problem, both about resolving non-concrete index references
- extends: [[topology.infrastructure]] — adds pattern resolution to the existing concrete-only topology layer
