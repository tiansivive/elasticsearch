---
tags: [performance, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Combinator Fusion

Fusing adjacent map/filter/reduce into single passes.

- Wu & Schrijvers (MPC 2015) on fusing [[free-monad.types]] handlers
- `filter p . filter q = filter (p && q)`; `map f . map g = map (f . g)`
- In-memory: reduces traversal count
- In ESQL: combines WHERE clauses or EVAL expressions

**Depends on**: [[lowering-pass.performance]], [[bird-meertens.types]]
**Enables**: (none directly)
**Connections**:
- optimizes: [[esql-compilation.esql]] — fusing WHERE/EVAL in compiled ESQL queries
- complements: [[push-down-compilation.performance]] — fusion is a complementary optimization to push-down
- related: [[free-monad.types]] — Wu & Schrijvers show fusion of free monad handlers; same theoretical lineage
