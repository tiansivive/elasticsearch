---
tags: [roadmap, language, types, implemented]
refs:
  - adr:D-005
  - adr:D-006
  - adr:D-007
  - adr:D-008
  - adr:D-009
  - adr:D-010
  - adr:D-011
  - adr:D-012
  - adr:D-013
  - adr:D-014
  - adr:D-015
  - adr:D-016
  - adr:D-017
  - adr:D-018
  - adr:D-019
  - adr:D-020
  - adr:D-021
  - adr:D-022
  - adr:D-023
  - adr:D-024
  - adr:D-025
  - adr:D-026
  - adr:D-027
  - adr:D-028
  - adr:D-029
  - adr:D-030
  - adr:D-031
  - adr:D-032
  - adr:D-033
  - adr:D-034
  - adr:D-035
  - adr:D-036
  - adr:D-037
  - adr:D-038
  - plan:phase1_expression_language
  - plan:phase_1d_open_rows_bb2dae6a
---
# Phase 1 — Expression Language

Parser (1a), type checker (1b), evaluator (1c), open rows (1d), System F core (D-035). Full typed functional language with row-polymorphic records.

**Depends on**: [[phase-0.roadmap]]
**Enables**: [[phase-2.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- subsumes: [[antlr-grammar.language]]
- subsumes: [[hindley-milner.types]]
- subsumes: [[elaboration-architecture.types]]
- subsumes: [[evaluator.language]]
- subsumes: [[de-bruijn-indices.language]]
- subsumes: [[core-ir.language]]
- subsumes: [[row-polymorphism.types]]
- subsumes: [[system-f-core.types]]
- subsumes: [[deferred-constraints.types]]
- subsumes: [[bidir-checking.types]]
