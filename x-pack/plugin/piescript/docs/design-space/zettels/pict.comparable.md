---
tags: [comparable, theoretical, pi-calculus, prior-art, decision]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Pict

Pict: statically-typed concurrent language compiling directly from pi-calculus. [[channels.infrastructure|Channels]] first-class, processes compose, type system works in practice. Closest language to piescript's theoretical foundations. [[nomadic-pict.coordination|Nomadic Pict]] extends with locations and mobile agents.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- validates: [[channels.infrastructure]] — Pict's channels-first model validates piescript's channel-centric design
- evolved-into: [[nomadic-pict.coordination]] — Nomadic Pict extends Pict with locations and mobile agents
- contrasts-with: [[join-calculus.coordination]] — Pict compiles from pi-calculus; join calculus restricts pi-calculus for efficient distributed implementation
