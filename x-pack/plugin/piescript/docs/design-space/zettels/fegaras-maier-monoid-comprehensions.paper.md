---
tags: [paper, esql, query-theory, theoretical, types, data-processing, compilation, performance, reference]
refs:
  - doc:references.md
  - resource:https://dl.acm.org/doi/10.1145/357775.357783
---
# Fegaras & Maier -- Optimizing Object Queries Using an Effective Calculus

Leonidas Fegaras and David Maier. "Optimizing Object Queries Using an Effective Calculus." *ACM Transactions on Database Systems (TODS)*, 2000.

Defines monoid comprehensions as a uniform intermediate representation for queries over different collection types (sets, bags, lists, arrays). The optimizer normalizes monoid comprehension expressions and then specializes them based on the target collection. This is formally what piescript's typeclass approach does: normalize the expression, then specialize via typeclass instance.

The paper's calculus provides algebraic rewrite rules for query optimization: unnesting nested queries, pushing predicates into comprehensions, and fusing adjacent comprehensions. These are the same transformations piescript's query compiler would perform when lowering piescript combinators to ESQL plans.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[monoid-comprehensions.esql]] -- the paper defines monoid comprehensions as piescript's query IR
- informs: [[comprehension-syntax.language]] -- comprehension syntax for different collection types
- informs: [[nbe-compilation.esql]] -- normalization of comprehension expressions parallels NbE
- informs: [[query-typeclass.data]] -- typeclass-based specialization is the same as monoid-based specialization
- informs: [[push-down-compilation.performance]] -- predicate push-down is a monoid comprehension rewrite
- part-of: [[papers.hub]]
- related: [[wadler-comprehending-monads.paper]] -- Wadler establishes monadic comprehensions; Fegaras & Maier generalize to monoids
- related: [[bird-meertens.types]] -- monoid homomorphisms connect BMF to monoid comprehensions
