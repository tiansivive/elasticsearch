---
tags: [types, data, search, theoretical, exploration, concept]
refs:
  - doc:references.md
---
# Nondeterministic MV Semantics

List monad / nondeterministic semantics for multi-value fields. Each MV value represents a set
of possibilities; operations explore the Cartesian product. This is the monadic interpretation:

```
do x <- mv_field_1    -- x ranges over all values in field 1
   y <- mv_field_2    -- y ranges over all values in field 2
   return (x + y)     -- produces all pairwise sums
```

This connects to logic programming: [[backtracking.search|backtracking]] over MV values is
equivalent to the list monad's bind (concatMap). A multi-value field is a nondeterministic
choice point -- exactly like Prolog's disjunction.

**Contrast with APL model:** [[scalar-pervasion.data]] (APL element-wise) zips MV fields
pointwise, requiring same length. Nondeterministic semantics produce the Cartesian product
regardless of lengths. ESQL already uses Cartesian semantics for MV*MV operations, so the
nondeterministic model is actually closer to existing behavior.

**Implications:**
- MV a ~ NonDet a ~ List a (all isomorphic under nondeterministic semantics)
- `guard` and `filter` are natural operations (prune the search space)
- Connects to [[comprehension-syntax.language]] -- list comprehensions ARE nondeterministic computation

**Depends on**: [[multi-value-fields.data]]
**Enables**: (none directly)
**Connections**:
- uses: [[backtracking.search]] -- nondeterministic MV is backtracking over value sets
- contrasts-with: [[scalar-pervasion.data]] -- APL element-wise zip model vs nondeterministic Cartesian product model
- informs: [[logic-programming.hub]] -- MV fields as logic variables connect ES data to logic programming
- related: [[comprehension-syntax.language]] -- list comprehensions express nondeterministic computation
- related: [[mv-type-constructor.types]] -- explicit MV type constructor would reify nondeterministic semantics
- informs: [[mv-scalar-dispatch.data]] -- nondeterministic model changes how MV/scalar dispatch works
- related: [[free-monad.types]] -- nondeterminism is an effect; could be expressed as a free monad
- analogous-to: Haskell List monad, Prolog backtracking, SQL implicit cross-join on multi-value
- related: [[datalog-fixpoint.search]] -- Datalog's set-at-a-time evaluation is bulk nondeterminism
