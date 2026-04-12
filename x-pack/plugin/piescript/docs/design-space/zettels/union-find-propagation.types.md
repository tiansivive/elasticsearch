---
tags: [types, inference, unification, implemented, concept]
refs:
  - code:Unifier.java
  - code:ElaborationState.java
---
# Union-Find Propagation for Concrete Rows

Concrete rows (from resolved index mappings) are stored as metadata on metavariables in the union-find (zonker). When the unifier solves a metavar, its concrete-row constraint propagates to the solution. Cross-index type conflicts are detected by observing when two concrete rows -- originating from different `use` declarations -- are unified and have incompatible field types for the same field name.

This is an instance of a general pattern: constraints as metadata on union-find nodes, propagated during path compression. The zonker already serves as the union-find backing store; concrete-row metadata piggybacks on the same structure. No separate constraint solver pass is needed -- conflict detection happens as a side effect of ordinary unification.

**Depends on**: [[unification-algorithm.types]], [[meta-variables.types]]
**Enables**: [[concrete-row-constraints.types]]
**Connections**:
- implements: [[concrete-row-constraints.types]] -- this is the mechanism by which concrete-row constraints are enforced
- uses: [[unification-algorithm.types]] -- propagation happens during Robinson unification
- uses: [[meta-variables.types]] -- metas carry the concrete-row metadata
- uses: [[zonker.types]] -- the zonker map IS the union-find where metadata lives
- extends: [[row-polymorphism.types]] -- concrete rows are a refinement of open rows with provenance tracking
- informs: [[type-errors.types]] -- conflict detection produces TypeError when incompatible rows unify
