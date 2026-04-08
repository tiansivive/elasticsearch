---
tags: [coordination, language, concurrency, implemented]
refs:
  - adr:D-042
  - adr:D-045
  - adr:D-046
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Spawn Bang

`spawn!` creates a bare channel without executing a body. Returns `ChannelVal(localNodeId, channelId)`. The user completes it via explicit `send`. Subject to the value restriction (D-046): `let ch = spawn!` stays monomorphic. This is the true primitive — `spawn` is sugar over it.

**Depends on**: [[channels.infrastructure]], [[channel-registry.infrastructure]], [[value-restriction.types]]
**Enables**: [[send.coordination]], [[code-mobility.coordination]]
**Connections**:
- related: [[core-ir.language]] — Core IR: `CoreSpawn` with null body; Grammar: `SPAWN BANG`
- related: [[channel-lifecycle.infrastructure]] — spawn! channels are especially leak-prone (no auto-send on completion)
- related: [[name-passing.coordination]] — spawn! creates channels for the pi-calculus name-passing pattern
