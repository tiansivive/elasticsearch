---
tags: [infrastructure, es-internals, implemented]
refs:
  - adr:D-001
  - adr:D-011
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ES Plugin

x-pack `ActionPlugin` with `extendedPlugins = ['x-pack-esql']`. Security: `cluster:compute/piescript` namespace (D-055). Transport: `internal:compute/piescript/send`. `createComponents` for `ChannelRegistry` singleton. Guice injection.

**Depends on**: (none)
**Enables**: [[channel-registry.infrastructure]], [[topology.infrastructure]], [[plugin-spi.external]]
**Connections**:
- related: [[security-namespace.infrastructure]] — removed `CompositeIndicesRequest` (D-055); ESQL queries handle their own index auth
