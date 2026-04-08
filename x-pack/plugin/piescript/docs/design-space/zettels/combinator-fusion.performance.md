---
tags: [performance, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Combinator Fusion

Fusing adjacent map/filter/reduce into single passes. Wu & Schrijvers (MPC 2015) on fusing free monad handlers. filter p . filter q = filter (p && q). map f . map g = map (f . g). Relevant to both in-memory list processing and ESQL compilation.

**Depends on**: [[lowering-pass.performance]], [[bird-meertens.types]]
**Enables**: (none directly)
**Connections**:
- related: in-memory reduces traversal count; in ESQL combines WHERE clauses or EVAL expressions
- related: [[esql-compilation.esql]] — fusing WHERE/EVAL in compiled ESQL queries
- related: [[push-down-compilation.performance]] — fusion is a complementary optimization to push-down
