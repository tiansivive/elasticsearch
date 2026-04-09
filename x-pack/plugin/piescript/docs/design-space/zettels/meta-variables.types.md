---
tags: [types, unification, inference, implemented, documentation]
refs:
  - code:ElaborationState.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Meta Variables

`MonoType.Meta(int id, MonoType kind)` -- unification holes. Created fresh by the [[elaboration-architecture.types]] elaborator. Solved by [[unification-algorithm.types]] writing to the [[zonker.types]].

- Chain resolution: a meta can point to another meta which points to a solution
- Unsolved metas at program end indicate ambiguous types
- Each meta has a [[binding-levels.types]] for generalization tracking

**Depends on**: [[zonker.types]]
**Enables**: [[binding-levels.types]], [[unification-algorithm.types]]
**Connections**:
- uses: [[zonker.types]] — the zonker is the union-find over metas
- complements: [[rigid-variables.types]] — metas become rigids during generalization; rigids become fresh metas during instantiation
- related: [[f-omega-lite.types]] — f-omega-lite's force normalizer chases meta chains as part of NbE normalization
- used-by: [[elaboration-architecture.types]] — the elaborator creates fresh metas for unknown types
