---
tags: [infrastructure, coordination, concurrency, channels, implemented, pi-calculus]
refs:
  - adr:D-040
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Channels

`Channel t` is a type constructor backed by `SubscribableListener<Value>` — single-value, future-like. `ChannelVal(nodeId, channelId)` is the serializable reference; the actual listener lives in the per-node `ChannelRegistry`. Channels carrying channels (`Channel (Channel a)`) enables the pi-calculus name-passing pattern.

**Depends on**: [[join-calculus.coordination]]
**Enables**: [[spawn.coordination]], [[spawn-bang.coordination]], [[when-synchronization.coordination]], [[send.coordination]], [[channel-registry.infrastructure]]
**Connections**:
- enables: [[name-passing.coordination]] — `Channel (Channel a)` enables the pi-calculus name-passing pattern
- related: [[multi-value-channels.coordination]] — single-value only in current implementation; multi-value channels are deferred
