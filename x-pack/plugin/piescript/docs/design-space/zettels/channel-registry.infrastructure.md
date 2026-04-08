---
tags: [infrastructure, coordination, resources, channels, implemented]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Channel Registry

Per-node `ConcurrentHashMap<String, ActionListener<Value>>` mapping channel IDs to local listeners. Singleton shared across transport actions via Guice injection. Regular channels are `SubscribableListener`s that auto-remove on completion. The inbox is a persistent, reusable `ActionListener`.

**Depends on**: [[channels.infrastructure]], [[es-plugin.infrastructure]]
**Enables**: [[spawn.coordination]], [[send.coordination]], [[when-synchronization.coordination]], [[inbox.infrastructure]]
**Connections**:
- implements: [[join-calculus.coordination]] — locality property: messages travel to their channel's definition site
