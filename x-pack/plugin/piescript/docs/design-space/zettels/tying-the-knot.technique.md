---
tags: [language, evaluation, technique, concept, recursion]
refs: []
---
# Tying the Knot

Implementation technique for recursion in strict languages. Allocate a mutable slot,
evaluate the RHS with the slot in scope, then backpatch the slot with the resulting
closure. In Java: a mutable `Value[]` entry; the circular reference is handled by GC.

Used by OCaml (`let rec`), SML, and Scheme. The key insight is that closures capture the
environment array by reference, so when the slot is later filled, existing closures that
closed over it see the updated value.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- implements: [[implicit-recursion.design]] — the backpatch mechanism that makes implicit recursion work
- uses: [[closure-val.language]] — closures capture the mutable environment array
- uses: [[de-bruijn-indices.language]] — self-reference is a de Bruijn index pointing into the pre-allocated slot
- part-of: [[recursion.hub]]
- analogous-to: ML/OCaml `let rec` implementation
