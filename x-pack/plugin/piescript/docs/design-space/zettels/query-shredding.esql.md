---
tags: [esql, query-theory, theoretical, reference]
refs:
  - doc:references.md
---
# Query Shredding

Cheney, Lindley, and Wadler's technique for compiling nested queries to flat SQL. Where [[t-linq.esql|T-LINQ]] normalization handles the common case of query composition, shredding handles the hard case: nested collections that normalization alone cannot flatten. It decomposes a nested query into multiple flat queries plus a stitching step that reassembles the nested result. Complementary to T-LINQ -- together they cover the full spectrum from simple projections to deeply nested [[comprehension-syntax.language|comprehensions]].

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- complements: [[t-linq.esql]] — T-LINQ normalizes; shredding handles what normalization leaves nested
- informs: [[comprehension-syntax.language]] — shredding is the compilation target for nested comprehensions over flat backends
