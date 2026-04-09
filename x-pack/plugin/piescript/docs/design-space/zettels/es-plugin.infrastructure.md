---
tags: [infrastructure, es-internals, implemented, documentation]
refs:
  - adr:D-001
  - adr:D-011
  - adr:D-055
  - code:PiescriptPlugin.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ES Plugin

x-pack `ActionPlugin` with `extendedPlugins = ['x-pack-esql']`.
- Security: `cluster:compute/piescript` namespace (D-055) via [[security-namespace.infrastructure]]
- Transport: `internal:compute/piescript/send` via [[transport-send.infrastructure]]
- `createComponents` for [[channel-registry.infrastructure]] singleton
- Guice injection

**Depends on**: (none)
**Enables**: [[channel-registry.infrastructure]], [[topology.infrastructure]], [[plugin-spi.external]]
**Connections**:
- part-of: [[phase-0.roadmap]]
- uses: [[security-namespace.infrastructure]] — removed `CompositeIndicesRequest` (D-055); ESQL queries handle their own index auth
- uses: [[transport-send.infrastructure]] — registers the PiescriptSendAction transport handler
