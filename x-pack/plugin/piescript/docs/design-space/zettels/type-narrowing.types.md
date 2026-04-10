---
tags: [types, open, task, concept, question, needs-design, later]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Type Narrowing

TypeScript-style type refinement via runtime checks:
- if-check narrows the type in the true branch
- Related to [[gadt-rejection.types]] (GADTs) and OutsideIn(X)
- Would enable safe handling of Dynamic types and mixed-type data
- Connected to [[elaboration-architecture.types]] since narrowing info must flow through elaboration

**Depends on**: [[adts.types]], [[pattern-matching.hub]]
**Enables**: [[dynamic-index-names.data]]
**Connections**:
- related: [[result-types.types]] — requires the type system to track control flow; related to exhaustive matching
- related: [[existential-types.types]] — pattern matching on existentials reveals hidden type
- contrasts-with: [[gadt-rejection.types]] — GADTs were rejected, but type narrowing achieves some of the same goals via a simpler mechanism
