---
tags: [esql, compilation, nbe, concept, decision]
refs:
  - adr:D-052
---
# Compilation Boundary

The `query ... ;` syntax marks the quotation/anti-quotation boundary where piescript closures compile to ESQL strings and fire.

- Inside `query`, piescript expressions become [[t-linq.esql]] quotations: the [[evaluator.language]] partially evaluates with Symbol inputs, and the resulting Symbol tree is read back as an ESQL string
- Outside `query`, piescript is normal evaluation
- This boundary is the concrete realization of T-LINQ's `<@ @>` brackets
- The [[nbe-compilation.esql]] approach means the boundary is not a syntactic transformation but a mode switch in the evaluator: the same evaluator handles both sides, but inside the query boundary it operates on Symbols rather than concrete values

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[block-f.roadmap]]
- part-of: [[esql-compilation.esql]] — the compilation boundary is where ESQL compilation begins
- implements: [[t-linq.esql]] — T-LINQ's quotation mechanism realized as piescript syntax
- uses: [[nbe-compilation.esql]] — NbE is the compilation strategy within the boundary
