---
tags: [paper, types, esql, query-theory, theoretical, data-processing, language, category-theory, reference]
refs:
  - doc:references.md
  - resource:https://ncatlab.org/nlab/files/WadlerMonads.pdf
---
# Wadler -- Comprehending Monads

Philip Wadler. "Comprehending Monads." *Mathematical Structures in Computer Science*, 1992.

Shows that list comprehensions generalize to any monad, and that SQL's `SELECT-FROM-WHERE` is a monad comprehension over the "set monad." Establishes the formal basis for why `map`/`filter`/`reduce` are the right query primitives -- they are the monadic operations specialized to collections. The paper unifies three perspectives: Haskell list comprehensions, SQL queries, and monadic bind -- all are the same algebraic structure.

For piescript, this paper grounds the query surface design: piescript's combinators over streams (`map`, `filter`, `reduce`) are monad comprehensions. The same expression, interpreted in different monads (list, set, bag, ESQL plan), produces different execution strategies -- the algebraic interface remains the same.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[comprehension-syntax.language]] -- comprehension syntax is monadic bind in disguise
- informs: [[monoid-comprehensions.esql]] -- generalizes monadic comprehensions to monoid comprehensions
- informs: [[query-typeclass.data]] -- the `Query a` typeclass is a monadic interface over data sources
- informs: [[stream-a.language]] -- `Stream a` is a monad; map/filter/reduce are monadic operations
- informs: [[bird-meertens.types]] -- BMF list homomorphisms are the algebraic view; monads are the categorical view
- part-of: [[papers.hub]]
- related: [[fegaras-maier-monoid-comprehensions.paper]] -- Fegaras & Maier extend monadic comprehensions to monoid comprehensions
- related: [[compiling-to-categories.performance]] -- same expression compiled to different categories / monads
