---
tags: [types, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Type Narrowing

TypeScript-style type refinement via runtime checks. if-check narrows the type in the true branch. Related to GADTs and OutsideIn(X). Would enable safe handling of Dynamic types and mixed-type data.

**Depends on**: [[adts.types]], [[pattern-matching.language]]
**Enables**: [[dynamic-index-names.data]]
**Connections**:
- related: [[result-types.types]] — requires the type system to track control flow; related to exhaustive matching
- related: [[existential-types.types]] — pattern matching on existentials reveals hidden type
