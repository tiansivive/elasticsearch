---
tags: [paper, data, columnar, materialization, theoretical, performance, compute-engine, lucene, reference]
refs:
  - doc:references.md
  - resource:https://ieeexplore.ieee.org/document/4221659
---
# Abadi et al. -- Materialization Strategies in a Column-Oriented DBMS

Daniel Abadi, Samuel Madden, and Nikos Hachem. "Materialization Strategies in a Column-Oriented DBMS." *ICDE 2007*.

Defines early materialization (construct tuples at the leaves, pass them up) vs late materialization (operate on column positions as long as possible, construct tuples only when needed). Late materialization is dramatically better for selective queries because it avoids constructing tuples that will be filtered out.

Directly applicable to piescript: Lucene's doc values are columnar storage, and the same trade-off applies to piescript's `scan`/`read` primitives. Block D's `Shard.read ref "*"` (read all fields) is early materialization; the future `RawData` type with deferred field access would be late materialization. The paper provides the formal framework for deciding when to convert from Page/Block columnar representation to piescript `RecordVal` values.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[materialization-boundary.data]] -- the early/late materialization trade-off at piescript's Page-to-Value boundary
- informs: [[type-stack.data]] -- type stack governs when columnar data becomes typed values
- informs: [[shard-stream.data]] -- shard stream is the point where columnar data enters piescript
- informs: [[blockloader.data]] -- BlockLoader is the mechanism that reads columnar data; materialization strategy determines when
- informs: [[composite-paging.data]] -- paging strategy interacts with materialization timing
- part-of: [[papers.hub]]
- related: [[push-down-compilation.performance]] -- late materialization is a form of push-down: defer work until needed
