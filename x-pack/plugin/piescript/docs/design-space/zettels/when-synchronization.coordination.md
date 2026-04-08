---
tags: [coordination, language, concurrency, implemented, pi-calculus]
refs:
  - adr:D-040
  - adr:D-041
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# When Synchronization

`when (ch1 x) & (ch2 y) -> body` synchronizes on one or more channels. Uses a positional collector (`AtomicArray` + `CountDown`) to preserve de Bruijn binding order. The surface keyword is `when` (not `join`) to avoid SQL/ESQL JOIN collision (D-041). Only works on local channels — remote channels rejected at runtime.

**Depends on**: [[join-calculus.coordination]], [[channels.infrastructure]], [[de-bruijn-indices.language]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- related: [[positional-collector.coordination]] — `GroupedActionListener` was rejected because it stores by arrival order, not binding order (D-041 S3)
- related: [[spawn.coordination]] — spawn+when is the core coordination pattern; spawn creates channels that when synchronizes on
