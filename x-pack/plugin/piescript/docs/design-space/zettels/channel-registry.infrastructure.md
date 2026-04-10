---
tags: [infrastructure, coordination, resources, channels, implemented, documentation, es-internals, transport-layer]
refs:
  - adr:D-045
  - code:ChannelRegistry.java
  - code:TransportPiescriptSendAction.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Channel Registry

Per-node `ConcurrentHashMap<String, ActionListener<Value>>` mapping channel IDs to local listeners. Singleton shared across transport actions via Guice injection. Remote **`send`** completes entries via [[transport-send.infrastructure]] on the channel owner node. Regular [[channels.infrastructure]] are `SubscribableListener`s that auto-remove on completion. The [[inbox.infrastructure]] is a persistent, reusable `ActionListener`.

**Depends on**: [[channels.infrastructure]], [[es-plugin.infrastructure]]
**Enables**: [[spawn.coordination]], [[send.coordination]], [[when-synchronization.coordination]], [[inbox.infrastructure]]
**Connections**:
- uses: [[transport-layer.es]] — registry listeners completed via transport on owner node
- uses: [[transport-send.infrastructure]] — remote channel completion path
- part-of: [[block-c.roadmap]]
- implements: [[join-calculus.coordination]] — locality property: messages travel to their channel's definition site
