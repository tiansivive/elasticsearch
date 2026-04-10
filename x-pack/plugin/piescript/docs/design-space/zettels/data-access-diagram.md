---
tags: [data, types, documentation, diagram]
refs:
  - doc:archive/data-access.pre-threads.md
---
# Data Access Diagram

Architecture diagram for the [[data-access-hierarchy]]. See
[[data-access-rationale]] for why the equational/sequential split exists.

```mermaid
graph TD
    Surface["Comprehension / Combinator Syntax<br/>(from/where/select or combinators)"]

    Surface --> QueryESQL["Query ESQL<br/>instance"]
    Surface --> QueryShard["Query ShardPlan<br/>instance"]
    Surface --> QueryList["Query List<br/>instance"]

    QueryESQL --> ESQLEngine["ESQL engine<br/>(distributed, optimized)"]
    QueryShard --> LuceneM["LuceneM<br/>(shard-local, typed)"]
    QueryList --> MapFilter["map/filter/reduce<br/>over ListVal"]

    LuceneM --> Open["open/consume/read<br/>(pull)"]
    LuceneM --> Collect["collect<br/>(push)"]
    LuceneM --> RawLucene["raw Lucene<br/>primitives"]

    Physical["Physical layer<br/>(escape hatch — skip Query,<br/>talk to Lucene directly)"]
    Physical -.-> Open
    Physical -.-> Collect
    Physical -.-> RawLucene
```

**Depends on**: [[data-access-hierarchy]]
**Enables**: (none directly)
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- documents: [[data-access-hierarchy]]
- documents: [[query-typeclass.data]]
