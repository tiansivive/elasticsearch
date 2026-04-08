---
tags: [language, implemented, theoretical, safety]
refs:
  - adr:D-014
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Purity and Referential Transparency

Piescript is pure and referentially transparent. The only effects are coordination primitives (spawn, when, send). Captured values are immutable. This is what makes distributed execution safe — shipping a closure to a remote node produces the same result as evaluating locally.

**Depends on**: (none)
**Enables**: [[code-mobility.coordination]], [[join-calculus.coordination]], [[free-monad.types]]
**Connections**:
- related: [[explicit-distribution.language]] — the functional/coordination boundary is the core architectural insight; pure expressions evaluate anywhere, coordination primitives orchestrate
- related: [[value-restriction.types]] — purity simplifies generalization; value restriction approximates effect tracking
- related: [[effect-systems.types]] — purity is currently implicit; effect systems would make it explicit
