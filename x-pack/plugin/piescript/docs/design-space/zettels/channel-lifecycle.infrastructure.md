---
tags: [infrastructure, channels, resources, tech-debt, task, problem, needs-design, later]
refs:
  - code:ChannelRegistry.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
  - thread:distributed-coordination
---
# Channel Lifecycle

Channels registered in [[channel-registry.infrastructure]] as `SubscribableListener`s:

- Regular channels auto-remove on completion (via callback wrapper)
- The [[inbox.infrastructure]] is persistent (never removed)
- Leak risk: if a channel is created ([[spawn-bang.coordination]]) but never completed (no [[send.coordination]], no [[when-synchronization.coordination]]), the `SubscribableListener` stays in the registry indefinitely
- No explicit `Shard.release` equivalent for channels
- Future: scope-based cleanup or [[bracket-patterns.language]]

**Depends on**: [[channel-registry.infrastructure]]
**Enables**: (none directly)
**Connections**:
- analogous-to: [[bracket-patterns.language]] — similar to Searcher resource leak in Block D; both are "leak if not consumed" patterns needing scope-based or linear-type solutions
- tension-with: [[spawn-bang.coordination]] — spawn! creates channels that are especially leak-prone (no auto-send on completion)
- analogous-to: [[persistent-resources.infrastructure]] — both are "resource outlives expected scope" problems needing ownership or lifetime tracking
