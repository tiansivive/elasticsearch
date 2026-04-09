---
tags: [esql, query-theory, category-theory, theoretical, reference]
refs:
  - doc:references.md
---
# Monoid Comprehensions

Fegaras and Maier's uniform query IR over multiple collection types (lists, bags, sets). A monoid comprehension normalizes to the same form regardless of the underlying collection monad -- the collection type is a parameter, not hardcoded.

- Single intermediate representation for queries over heterogeneous backends
- Each backend has different bag/set/list semantics
- Connects to [[query-compilation-theory.esql]] as a foundational reference

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- informs: [[comprehension-syntax.language]] — monoid comprehensions provide a theoretical foundation for piescript's query syntax
- alternative-to: [[t-linq.esql]] — where T-LINQ is LINQ-specific, monoid comprehensions generalize across collection monoids
- informs: [[query-compilation-theory.esql]] — part of the theoretical foundation for query compilation
