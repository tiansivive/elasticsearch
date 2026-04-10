---
tags: [roadmap, coordination, distributed, implemented]
refs:
  - adr:D-045
  - adr:D-046
  - adr:D-047
  - plan:block_c_cross-node_execution_7faf2b07
---
# Block C — Cross-Node Code Execution

Ship closures to remote nodes, get results back. Introduced spawn!, send, fire-and-forget, serialization, transport, inbox, channel registry, and value restriction.

**Depends on**: [[block-b.roadmap]]
**Enables**: [[block-d.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- part-of: [[mvp.roadmap]]
- subsumes: [[spawn-bang.coordination]]
- subsumes: [[send.coordination]]
- subsumes: [[fire-and-forget.coordination]]
- subsumes: [[code-mobility.coordination]]
- subsumes: [[serialization.infrastructure]]
- subsumes: [[transport-layer.es]] — ES `TransportService` and piescript transport actions
- subsumes: [[transport-send.infrastructure]]
- subsumes: [[inbox.infrastructure]]
- subsumes: [[channel-registry.infrastructure]]
- subsumes: [[value-restriction.types]]
- subsumes: [[builder-dsl.language]]
