---
tags: [types, theoretical, concept, codata, category-theory]
refs:
  - doc:references.md
---
# Anamorphisms

Unfolds — the dual of [[catamorphisms.types]] (folds). Where a catamorphism consumes a
recursive structure (algebra: `F a → a`), an anamorphism produces one from a seed
(coalgebra: `a → F a`). Generalizes list generation to arbitrary coinductive types.

`unfold : (s → (a, s)) → s → Stream a` — given a seed and a step function that produces
a value and a new seed, generate a (potentially infinite) stream.

**Connection to piescript:**
- [[fused-loop-match.language]] IS an anamorphism with early termination: the loop state is
  the seed, each `repeat` produces the next seed, base cases terminate the unfold.
- [[composite-paging.data]] is an unfold: seed = initial `after_key`, step = query one page
  and produce `(page, new_after_key)`, terminate when page is empty.
- A hylomorphism (unfold-then-fold) would express: generate pages via anamorphism, then
  reduce them via catamorphism. This is the pagination-then-aggregate pattern.

**Depends on**: [[codata.types]]
**Enables**: (none directly)
**Connections**:
- dual-of: [[catamorphisms.types]] — fold consumes (algebra), unfold produces (coalgebra)
- instantiated-by: [[fused-loop-match.language]] — loop-match is an anamorphism with early termination
- instantiated-by: [[composite-paging.data]] — pagination is an unfold over query pages
- part-of: [[codata.types]] — anamorphisms are the canonical eliminators for codata
- informs: [[recursion.hub]] — unfolds are the safe, structured way to produce sequences (vs general recursion)
