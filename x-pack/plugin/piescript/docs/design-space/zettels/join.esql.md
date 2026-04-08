---
tags: [esql, data, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL LOOKUP JOIN

ESQL LOOKUP JOIN support via typed combinator. Exposes the ESQL LOOKUP JOIN command as a piescript combinator, enabling cross-index enrichment within compiled ESQL queries. The combinator carries type information about both sides of the join, allowing the elaborator to verify field compatibility.

**Depends on**: [[esql-combinators.esql]]
**Enables**: cross-index enrichment via ESQL
**Connections**:
- related: [[query-typeclass.data]] — JOIN is a key capability for the Query typeclass
- related: [[enrich.esql]] — ENRICH is a related cross-index enrichment mechanism
