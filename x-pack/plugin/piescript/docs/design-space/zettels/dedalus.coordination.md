---
tags: [coordination, distributed, theoretical, comparable, prior-art]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dedalus

Dedalus/Bloom distributed declarative logic programming.

- Dedalus extends Datalog with a temporal dimension — facts have timestamps, and rules can reference "next tick" or "async" (network-delayed) derivations
- Bloom is its Ruby DSL
- This is the language in which the [[calm-theorem.types]] (Consistency As Logical Monotonicity) was developed, proving that monotone programs are eventually consistent without coordination

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- informs: [[calm-theorem.types]] — Dedalus is the language Bloom/CALM was developed in
- contrasts-with: [[join-calculus.coordination]] — different approach to distributed coordination
- part-of: [[distributed-data-systems.comparable]] — one of the key distributed data systems bracketing the design space
