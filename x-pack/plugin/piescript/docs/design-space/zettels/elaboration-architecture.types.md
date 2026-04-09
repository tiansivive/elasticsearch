---
tags: [types, language, implemented, inference, concept]
refs:
  - adr:D-005
  - adr:D-035
  - code:piescript.elab
  - code:ElaborationContext.java
  - code:ElaborationState.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Elaboration Architecture

The elaboration pipeline separates immutable context from mutable state.
- `ElaborationContext` (immutable): typing context G with [[de-bruijn-indices.language]]-indexed local bindings + module-level free variable map + [[binding-levels.types]]
- Passed by value through recursive descent
- `ElaborationState` (mutable): [[meta-variables.types]] supply, [[zonker.types]], constraint accumulator, [[force-threading.types]] normalizer
- The split ensures recursive elaboration can't accidentally mutate context while allowing shared mutable state (zonker, constraints) across the traversal

**Depends on**: [[hindley-milner.types]], [[zonker.types]]
**Enables**: [[deferred-constraints.types]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- inspired-by: standard technique in type checker implementation — immutable/mutable split aligns with the coding guideline: never mix immutable context with mutable state
- uses: [[meta-variables.types]] — mutable state includes the metavar supply
- uses: [[binding-levels.types]] — immutable context carries the current binding level
- uses: [[de-bruijn-indices.language]] — context uses de Bruijn-indexed local bindings
- uses: [[antlr-grammar.language]] — elaboration consumes the ANTLR CST as input
- uses: [[force-threading.types]] — mutable state includes the force normalizer
- implements: [[bidir-checking.types]] — the elaborator supports synthesis and checking modes
