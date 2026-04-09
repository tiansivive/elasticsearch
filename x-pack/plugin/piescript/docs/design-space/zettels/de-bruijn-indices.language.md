---
tags: [language, implemented, decision, concept]
refs:
  - adr:D-006
  - adr:D-024
  - code:CoreVar.java
  - code:Evaluator.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# De Bruijn Indices

Variable representation in [[core-ir.language]].

- The surface AST uses named variables; [[elaboration-architecture.types]] converts to de Bruijn indices
- Environment is a `Value[]` indexed by de Bruijn index
- Eliminates alpha-equivalence issues
- Lambda application prepends argument to [[closure-val.language]]'s captured environment

**Depends on**: (none)
**Enables**: [[core-ir.language]], [[evaluator.language]], [[when-synchronization.coordination]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- implements: [[core-ir.language]] — `CoreVar` uses de Bruijn indices for variable lookup
- uses: [[closure-val.language]] — environment array in `ClosureVal` is indexed by de Bruijn index
