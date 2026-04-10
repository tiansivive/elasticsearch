---
tags: [esql, data, open, concept, needs-design, later]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:data-completeness
---
# ESQL LOOKUP JOIN

ESQL LOOKUP JOIN support via typed combinator. Exposes the ESQL LOOKUP JOIN command as a piescript combinator, enabling cross-index enrichment within compiled ESQL queries. The combinator carries type information about both sides of the join, allowing the [[elaboration-architecture.types]] to verify field compatibility.

- Compiled via [[nbe-compilation.esql]] -- the join combinator produces [[symbol-partial-evaluation.esql]] fragments
- Type-safe: row types ensure field compatibility at elaboration time

**Depends on**: [[esql-combinators.esql]]
**Enables**: cross-index enrichment via ESQL
**Connections**:
- part-of: [[query-typeclass.data]] — JOIN is a key capability for the Query typeclass
- complements: [[enrich.esql]] — ENRICH is a related cross-index enrichment mechanism; JOIN is index-to-index, ENRICH is index-to-enrich-policy
- uses: [[nbe-compilation.esql]] — join combinator compiles via NbE Symbol evaluation
- uses: [[elaboration-architecture.types]] — elaborator verifies field compatibility across join sides
