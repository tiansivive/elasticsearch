---
tags: [esql, query-theory, theoretical, reference]
refs:
  - doc:references.md
---
# Query Compilation Theory

Theoretical foundations of query compilation that collectively inform how piescript compiles typed expressions to ESQL and Lucene backends.

- [[t-linq.esql|T-LINQ]] normalization guarantees that host-language composition produces optimal queries.
- [[query-shredding.esql|Query shredding]] handles nested collections that normalization can't flatten.
- [[monoid-comprehensions.esql|Monoid comprehensions]] provide a uniform IR across collection types.
- The [[third-homomorphism.performance|third homomorphism theorem]] identifies parallelizable folds.
- [[compiling-to-categories.performance|Compiling to categories]] gives a denotational framework for translating typed lambdas to categorical combinators (i.e., query plans).

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- subsumes: [[t-linq.esql]], [[query-shredding.esql]], [[monoid-comprehensions.esql]], [[third-homomorphism.performance]], [[compiling-to-categories.performance]]
