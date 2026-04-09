---
tags: [performance, category-theory, theoretical, reference]
refs:
  - doc:references.md
---
# Third Homomorphism Theorem

Gibbons' theorem: any function that can be computed both as a left fold and a right fold can be computed as a parallel divide-and-conquer (a list homomorphism):
- The theorem provides a mechanical procedure for discovering parallelizable computations
- Given both fold directions, the associative combining operator can be derived automatically
- This is the theoretical bridge from sequential folds to parallel [[map-reduce.distributed]]
- Builds on [[bird-meertens.types]] which identifies which operations are homomorphisms

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- extends: [[bird-meertens.types]] — BMF identifies which operations are homomorphisms; the Third Homomorphism Theorem shows how to *construct* the homomorphism from left/right folds
- informs: [[map-reduce.distributed]] — the theorem justifies when a fold can be safely distributed across shards as a parallel reduce
- related: [[catamorphisms.types]] — list homomorphisms are a specific class of catamorphisms
