---
tags: [performance, known-issue, problem]
refs: []
---
# Cross-Index Join Cost

The enrichment pattern (filter inner collection per outer element) is O(n*m) nested loop.

- There is no hash join and no index lookup — every outer element scans the full inner collection
- Fine for small datasets but becomes prohibitive at scale
- The real fix is [[push-down-compilation.performance]]: compile the join into ESQL or Lucene where the engine can use its own indexes and join strategies

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- motivates: [[push-down-compilation.performance]] — push-down is how this cost gets eliminated for query-expressible joins
- motivates: [[join.esql]] — the ESQL-level join semantics that push-down would target
