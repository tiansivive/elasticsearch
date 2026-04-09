---
tags: [coordination, language, concurrency, implemented, documentation]
refs:
  - adr:D-042
  - adr:D-045
  - adr:D-046
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalCoordination.java
  - code:ChannelRegistry.java
---
# Spawn Bang

`spawn!` creates a bare [[channels.infrastructure|channel]] without executing a body. Returns `ChannelVal(localNodeId, channelId)`. The user completes it via explicit [[send.coordination|send]]. Subject to the [[value-restriction.types|value restriction]] (D-046): `let ch = spawn!` stays monomorphic. This is the true primitive -- [[spawn.coordination|spawn]] is sugar over it.

**Depends on**: [[channels.infrastructure]], [[channel-registry.infrastructure]], [[value-restriction.types]]
**Enables**: [[send.coordination]], [[code-mobility.coordination]]
**Connections**:
- part-of: [[block-c.roadmap]]
- implements: [[core-ir.language]] — Core IR: `CoreSpawn` with null body; Grammar: `SPAWN BANG`
- tension-with: [[channel-lifecycle.infrastructure]] — spawn! channels are especially leak-prone (no auto-send on completion)
- enables: [[name-passing.coordination]] — spawn! creates channels for the pi-calculus name-passing pattern
