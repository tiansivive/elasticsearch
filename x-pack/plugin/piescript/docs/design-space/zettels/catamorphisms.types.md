---
tags: [types, category-theory, theoretical, reference]
refs:
  - doc:references.md
---
# Catamorphisms (Bananas, Lenses, Envelopes, Barbed Wire)

Meijer, Fokkinga, and Paterson's recursion schemes:

- Catamorphisms (folds), anamorphisms (unfolds), hylomorphisms (unfold-then-fold), and paramorphisms (folds with access to the original structure)
- Generalizes the concept of folding from lists to arbitrary [[adts.types]] via initial algebra semantics
- Foundation for typed generic traversals — any ADT's eliminator is a catamorphism
- Composition of recursion schemes enables principled tree/graph transformations

**Depends on**: [[adts.types]]
**Enables**: (none)
**Connections**:
- extends: [[bird-meertens.types]] — BMF covers list homomorphisms; catamorphisms generalize to arbitrary inductive types
- informs: [[recursive-types.types]] — iso-recursive types need fold/unfold, which are exactly catamorphism/anamorphism at the type level
- dual-of: [[anamorphisms.types]] — catamorphisms consume (fold/algebra); anamorphisms produce (unfold/coalgebra)
- dual-of: [[codata.types]] — catamorphisms are for data (initial algebras); codata uses anamorphisms (final coalgebras)
