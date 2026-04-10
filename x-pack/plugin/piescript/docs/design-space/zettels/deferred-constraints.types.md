---
tags: [types, implemented, inference, concept]
refs:
  - adr:D-035
  - adr:D-019
  - code:Constraint.java
  - code:Unifier.java
  - code:ElaborationState.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Deferred Constraints

The [[elaboration-architecture.types]] emits `Constraint(left, right, line, column)` records into an accumulator. Constraints are solved incrementally at generalization boundaries (so `collectMetas` sees through solved metas) and at end of program. Decouples constraint generation from solving.

**Depends on**: [[hindley-milner.types]], [[system-f-core.types]]
**Enables**: [[typeclasses.types]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- replaces: D-019 eager unification — D-035 introduced deferred solving
- informs: [[typeclasses.types]] — migration to OutsideIn(X) when typeclasses arrive
- uses: [[unification-algorithm.types]] — constraints are solved via unification
- uses: [[binding-levels.types]] — constraints solved incrementally at generalization boundaries
- uses: [[meta-variables.types]] — constraints reference metas; solving writes to the zonker
