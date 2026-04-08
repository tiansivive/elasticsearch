---
tags: [types, language, implemented, inference]
refs:
  - adr:D-005
  - adr:D-035
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Elaboration Architecture

The elaboration pipeline separates immutable context from mutable state. `ElaborationContext` (immutable): typing context G with de Bruijn-indexed local bindings + module-level free variable map + binding level. Passed by value through recursive descent. `ElaborationState` (mutable): metavar supply, zonker, constraint accumulator, force normalizer. The split ensures recursive elaboration can't accidentally mutate context while allowing shared mutable state (zonker, constraints) across the traversal.

**Depends on**: [[hindley-milner.types]], [[zonker.types]]
**Enables**: [[deferred-constraints.types]]
**Connections**:
- related: standard technique in type checker implementation — immutable/mutable split aligns with the coding guideline: never mix immutable context with mutable state
- related: [[meta-variables.types]] — mutable state includes the metavar supply
- related: [[binding-levels.types]] — immutable context carries the current binding level
- related: [[antlr-grammar.language]] — elaboration consumes the ANTLR CST as input
- related: [[bidir-checking.types]] — the elaborator supports synthesis and checking modes
