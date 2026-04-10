---
tags: [meta, principle, es-internals, infrastructure, decision]
refs:
  - doc:vision.md
---
# Elasticsearch-Native

Piescript is an x-pack plugin that follows ES conventions for build, test,
security, and backwards compatibility. It is not a standalone language bolted
onto Elasticsearch — it is designed from the ground up to integrate deeply.
The coordination runtime builds on ES's existing infrastructure:
`ActionListener`/`SubscribableListener` for async channels, the transport
layer for cross-node communication, and ESQL's compute engine for vectorized
data processing.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- related: [[es-plugin.infrastructure]] — the plugin scaffold implements this principle
- related: [[generic-thread-pool.infrastructure]] — uses ES thread pools, not custom ones
- related: [[transport-pipeline.infrastructure]] — uses ES transport, not custom networking
- related: [[es-conventions-debt.infrastructure]] — tech debt items for full ES convention compliance
