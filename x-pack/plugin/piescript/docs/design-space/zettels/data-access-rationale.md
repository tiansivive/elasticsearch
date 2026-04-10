---
tags: [data, types, concept, motivation]
refs:
  - doc:archive/data-access.pre-threads.md
---
# Data Access Rationale

Why the [[data-access-hierarchy]] has four levels instead of one unified
abstraction.

Levels 1-2 (`Query a`) are **equational** — `filter p (filter q xs)` equals
`filter (fn x -> p x && q x) xs`. The optimizer can rewrite, fuse, and push
down freely. But they can only express things the typeclass interface defines.

Levels 3-4 (`LuceneM`, physical primitives) are **sequential** — acquire,
compile, iterate, read. Order matters. They can express anything Lucene can do,
including things that aren't queries: interleaving data access with
coordination logic, custom merge joins, per-segment parallelism, resource
lifecycles spanning multiple coordination steps.

This is a fundamental semantic difference, not a convenience gradient. The
equational levels exist because most data access is declarative. The sequential
levels exist because some programs need to be the query planner.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- overlaps: [[data-access-hierarchy]] — hierarchy describes *what*, rationale explains *why*
- motivates: [[lucene-m.data]] — Level 3 exists because queries can't express programs
- motivates: [[query-typeclass.data]] — Levels 1-2 exist because programs shouldn't have to be queries
