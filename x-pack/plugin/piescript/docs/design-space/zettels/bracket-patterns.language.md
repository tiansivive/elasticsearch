---
tags: [language, resources, control-flow, open, concept, needs-design, later]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
---
# Bracket Patterns

Resource bracketing / try-finally patterns for resource lifecycle. A `bracket acquire release use` combinator ensures that resources ([[searcher-lifecycle.data]], Writers, file handles) are always cleaned up, even when the use computation fails. This is the functional equivalent of try-with-resources.

**Depends on**: [[pattern-matching.hub]]
**Enables**: safe Searcher/Writer cleanup
**Connections**:
- contrasts-with: [[qtt-linearity.types]] — linear types also solve resource safety but differently
- related: [[local-kind.types]] — Local resources need lifecycle management
- solves: [[searcher-lifecycle.data]] — ensures searchers are always released
- solves: [[channel-lifecycle.infrastructure]] — bracket patterns address the channel leak problem
