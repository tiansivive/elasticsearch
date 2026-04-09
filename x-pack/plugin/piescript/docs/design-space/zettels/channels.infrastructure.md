---
tags: [infrastructure, coordination, concurrency, channels, implemented, pi-calculus, concept]
refs:
  - adr:D-040
  - adr:D-045
  - code:ChannelRegistry.java
  - code:Value.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Channels

`Channel t` is a type constructor backed by `SubscribableListener<Value>` — single-value, future-like. `ChannelVal(nodeId, channelId)` is the serializable reference; the actual listener lives in the per-node [[channel-registry.infrastructure]]. Channels carrying channels (`Channel (Channel a)`) enables the pi-calculus [[name-passing.coordination]] pattern.

**Depends on**: [[join-calculus.coordination]]
**Enables**: [[spawn.coordination]], [[spawn-bang.coordination]], [[when-synchronization.coordination]], [[send.coordination]], [[channel-registry.infrastructure]]
**Connections**:
- part-of: [[block-a.roadmap]]
- enables: [[name-passing.coordination]] — `Channel (Channel a)` enables the pi-calculus name-passing pattern
- extends: [[multi-value-channels.coordination]] — single-value only in current implementation; multi-value channels are deferred
