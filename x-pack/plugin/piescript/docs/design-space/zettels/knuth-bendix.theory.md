---
tags: [theoretical, concept]
refs:
  - doc:references.md
---
# Knuth-Bendix Completion

Completion procedure for term rewriting systems. Given rewrite rules, computes additional rules to make the system confluent (order of rule application doesn't affect result). If completion succeeds, the TRS is decision-procedure-complete. Relevant if piescript ever needs to verify that pattern matching + type-level reduction is confluent -- e.g., are the force reduction rules for &/Pick/Omit confluent?

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[term-rewriting.theory]]
- uses: [[church-rosser.theory]] -- completion aims to achieve confluence
- informs: [[f-omega-lite.types]] -- are the force reduction rules confluent?
- informs: [[type-level-matching.types]] -- user-defined type families need confluence checking
