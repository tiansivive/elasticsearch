---
tags: [language, syntax, open, later, concept]
refs:
  - doc:references.md
---
# Where Clauses

Haskell-style `where` for post-hoc local definitions. The user writes the result expression
first, then defines helpers below:

```
result = f x
  where
    f y = y + 1
    threshold = 10
```

Noted as future work in the design plan (section 2.3). Syntactic sugar over `let`/`in` with
reordered scope -- the `where` block introduces bindings visible in the preceding expression.
Desugaring: elaborate the `where` bindings as `let` nodes wrapping the result expression.

Particularly useful for piescript pipelines where the "what" (pipeline shape) matters more
than the "how" (helper definitions). Lets users read top-down intent without being blocked by
boilerplate definitions.

**Depends on**: [[block-expressions.language]], [[antlr-grammar.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[syntax.hub]]
- extends: [[block-expressions.language]] -- where is a block expression with inverted scope order
- uses: [[elaboration-architecture.types]] -- desugaring happens during elaboration
- complements: [[pipe-operator.language]] -- pipe reads left-to-right, where reads top-to-bottom; both emphasize result-first flow
- informs: [[target-users.principle]] -- result-first reading order is friendlier for non-PL-expert users
- analogous-to: Haskell `where` clauses, OCaml `let ... in` (inverse order)
