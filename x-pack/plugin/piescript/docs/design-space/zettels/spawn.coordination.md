---
tags: [coordination, language, concurrency, implemented, pi-calculus, async]
refs:
  - adr:D-040
  - adr:D-042
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Spawn

`spawn expr` forks computation to the GENERIC thread pool, creates a channel (`SubscribableListener`), auto-sends the result, and returns a `ChannelVal`. Following Join Calculus Section 1.3, `spawn` is sugar over channel creation + fork + send. Implementation: register in `ChannelRegistry`, fork body evaluation on executor, return `ChannelVal(localNodeId, channelId)`.

**Depends on**: [[join-calculus.coordination]], [[channels.infrastructure]], [[channel-registry.infrastructure]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- related: [[spawn-bang.coordination]] — `spawn!` is the primitive form (bare channel); `spawn body` = `let ch = spawn! in fork(send ch body) in ch`
- related: [[generic-thread-pool.infrastructure]] — spawn forks computation to the GENERIC thread pool
- related: [[when-synchronization.coordination]] — spawn+when is the core coordination pattern (spawn creates channels, when synchronizes on them)
