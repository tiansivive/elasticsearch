---
tags: [language, implemented]
refs:
  - adr:D-006
  - adr:D-024
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# De Bruijn Indices

Variable representation in Core IR. The surface AST uses named variables; elaboration converts to de Bruijn indices. Environment is a `Value[]` indexed by de Bruijn index. Eliminates alpha-equivalence issues. Lambda application prepends argument to closure's captured environment.

**Depends on**: (none)
**Enables**: [[core-ir.language]], [[evaluator.language]], [[when-synchronization.coordination]]
**Connections**:
- related: standard for evaluation (count inward from binding site, matching stack discipline) — de Bruijn levels were considered but indices are more standard
