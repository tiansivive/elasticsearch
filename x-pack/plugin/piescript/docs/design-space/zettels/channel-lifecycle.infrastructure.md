---
tags: [infrastructure, channels, resources, tech-debt]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Channel Lifecycle

Channels registered in `ChannelRegistry` as `SubscribableListener`s. Regular channels auto-remove on completion (via callback wrapper). The inbox is persistent (never removed). Leak risk: if a channel is created (`spawn!`) but never completed (no `send`, no `when`), the `SubscribableListener` stays in the registry indefinitely. No explicit `Shard.release` equivalent for channels. Future: scope-based cleanup or bracket patterns.

**Depends on**: [[channel-registry.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: [[bracket-patterns.language]] — similar to Searcher resource leak in Block D; both are "leak if not consumed" patterns needing scope-based or linear-type solutions
- related: [[spawn-bang.coordination]] — spawn! creates channels that are especially leak-prone (no auto-send on completion)
- related: [[persistent-resources.infrastructure]] — both are "resource outlives expected scope" problems needing ownership or lifetime tracking
