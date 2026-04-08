---
tags: [data, esql, designed]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Composite Paging

The composite aggregation paging pattern from risk scoring: use after_key to page through groups, each page adds KQL range filters. In piescript, maps to a recursive loop where each iteration recompiles the ESQL query with a captured after_key via closure. The query recompilation works because NbE evaluates the closure with the new captured value, producing a different ESQL WHERE clause each time.

**Depends on**: [[esql-compilation.esql]], [[recursion.language]]
**Enables**: (none directly)
**Connections**:
- related: [[recursion.language]] — requires recursion (currently missing) for the loop
- related: [[nbe-compilation.esql]] — demonstrates how closures + NbE naturally handle parameterized query iteration
- related: [[risk-score-pattern.data]] — composite paging is part of the risk score query pattern
