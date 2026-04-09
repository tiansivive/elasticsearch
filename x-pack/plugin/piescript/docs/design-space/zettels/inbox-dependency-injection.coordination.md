---
tags: [coordination, design-pattern, pattern, distributed, implemented, concept]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Inbox Dependency Injection

The [[inbox.infrastructure]] closure receives local node info as its lambda argument -- dependency injection via the most fundamental language mechanism: lambda abstraction.
- No `local_node` primitive needed
- As piescript evolves, the inbox argument type can widen (shard handles, local capabilities, services) without changing the transport protocol
- This is capability passing via [[closure-val.language]]

**Depends on**: [[inbox.infrastructure]], [[closure-val.language]]
**Enables**: (none directly)
**Connections**:
- implements: [[inbox.infrastructure]] — the DI mechanism for inbox closures
- uses: [[closure-val.language]] — capability passing via closures
- analogous-to: [[plugin-spi.external]] — same pattern: capabilities passed as arguments, not globals
- uses: [[topology.infrastructure]] — node info passed as the inbox lambda argument originates from topology
- prerequisite-for: [[data-locality.distributed]] — DI via lambda abstraction is what makes shipped closures useful at the target node
