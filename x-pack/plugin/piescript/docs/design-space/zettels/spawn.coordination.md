---
tags: [coordination, language, concurrency, implemented, pi-calculus, async, documentation]
refs:
  - adr:D-040
  - adr:D-042
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalCoordination.java
  - code:ChannelRegistry.java
---
# Spawn

`spawn expr` forks computation to the [[generic-thread-pool.infrastructure|GENERIC thread pool]], creates a [[channels.infrastructure|channel]] (`SubscribableListener`), auto-sends the result, and returns a `ChannelVal`. Following [[join-calculus.coordination|Join Calculus]] Section 1.3, `spawn` is sugar over channel creation + fork + [[send.coordination|send]]. Implementation: register in [[channel-registry.infrastructure|ChannelRegistry]], fork body evaluation on executor, return `ChannelVal(localNodeId, channelId)`.

**Depends on**: [[join-calculus.coordination]], [[channels.infrastructure]], [[channel-registry.infrastructure]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- part-of: [[block-a.roadmap]]
- refines: [[spawn-bang.coordination]] — `spawn!` is the primitive form (bare channel); `spawn body` = `let ch = spawn! in fork(send ch body) in ch`
- uses: [[generic-thread-pool.infrastructure]] — spawn forks computation to the GENERIC thread pool
- replaces: [[par-blocks.coordination]] — any par block is expressible as spawn + when
- complements: [[when-synchronization.coordination]] — spawn+when is the core coordination pattern (spawn creates channels, when synchronizes on them)
