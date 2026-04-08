---
tags: [language, resources, control-flow, open]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Bracket Patterns

Resource bracketing / try-finally patterns for resource lifecycle. A `bracket acquire release use` combinator ensures that resources (Searchers, Writers, file handles) are always cleaned up, even when the use computation fails. This is the functional equivalent of try-with-resources.

**Depends on**: [[pattern-matching.language]]
**Enables**: safe Searcher/Writer cleanup
**Connections**:
- contrasts-with: [[qtt-linearity.types]] — linear types also solve resource safety but differently
- related: [[local-kind.types]] — Local resources need lifecycle management
