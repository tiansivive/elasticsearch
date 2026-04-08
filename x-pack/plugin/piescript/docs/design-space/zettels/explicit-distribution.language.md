---
tags: [language, coordination, implemented, distributed, orchestration]
refs:
  - adr:D-042
  - vision:distributed-computation-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Explicit Distribution Philosophy

Piescript's core design stance: **explicit control over distributed computation**, not hidden optimizer magic. ESQL is declarative about data retrieval (user says what, ESQL decides where/how). Piescript is explicit about distributed computation: nodes are values, shards are values, the user sends code to named nodes and coordinates results via channels. Libraries raise the abstraction level when convenience is wanted, but the primitives are always available.

This means: `topology "index"` returns typed cluster topology. `send node.inbox closure` ships code explicitly. `Shard.open` accesses local data explicitly. The user writes the distributed plan. This works because the language is pure — the user can reason about what runs where since pure expressions have no side effects outside coordination primitives.

**Depends on**: [[purity.language]], [[join-calculus.coordination]], [[topology.infrastructure]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- contrasts-with: [[spark.comparable]] — Spark/Ray also have explicit models but are standalone systems
- related: piescript integrates distribution into the language primitives — pure code evaluates, coordination primitives orchestrate, the runtime dispatches
