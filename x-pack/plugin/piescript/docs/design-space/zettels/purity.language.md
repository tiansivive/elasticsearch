---
tags: [language, implemented, theoretical, safety, concept, motivation]
refs:
  - adr:D-014
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Purity and Referential Transparency

Piescript is pure and referentially transparent. The only effects are coordination primitives ([[spawn.coordination|spawn]], [[when-synchronization.coordination|when]], [[send.coordination|send]]). Captured values are immutable. This is what makes distributed execution safe -- shipping a [[closure-val.language|closure]] to a remote node produces the same result as evaluating locally.

**Depends on**: (none)
**Enables**: [[code-mobility.coordination]], [[join-calculus.coordination]], [[free-monad.types]]
**Connections**:
- complements: [[explicit-distribution.language]] — the functional/coordination boundary is the core architectural insight; pure expressions evaluate anywhere, coordination primitives orchestrate
- complements: [[value-restriction.types]] — purity simplifies generalization; value restriction approximates effect tracking
- motivates: [[effect-systems.types]] — purity is currently implicit; effect systems would make it explicit
- enables: [[purity-enforcement.language]] — enforcement of the implicit purity guarantee
