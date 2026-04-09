---
tags: [roadmap, data, types, designed]
refs:
  - doc:data-access.md
---
# Data Access Architecture

The `Query a` typeclass vision for unified data access. One query surface (comprehensions or combinators), multiple backend instances (ESQL, ShardPlan, List), with the physical layer (`LuceneM`) as the escape hatch for full Lucene control.

**Connections**:
- part-of: [[vision-hub.roadmap]]
- subsumes: [[data-access-hierarchy.data]]
- subsumes: [[query-typeclass.data]]
- subsumes: [[query-shardplan.data]]
- subsumes: [[lucene-m.data]]
- subsumes: [[comprehension-syntax.language]]
- subsumes: [[push-down-compilation.performance]]
