---
tags: [types, unification, inference, implemented]
refs:
  - code:ElaborationState.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Meta Variables

`MonoType.Meta(int id, MonoType kind)` — unification holes. Created fresh by the elaborator. Solved by unification writing to the zonker. Chain resolution: a meta can point to another meta which points to a solution. Unsolved metas at program end indicate ambiguous types. Each meta has a binding level for generalization tracking.

**Depends on**: [[zonker.types]]
**Enables**: [[binding-levels.types]], [[unification-algorithm.types]]
**Connections**:
- related: [[zonker.types]] — the zonker is the union-find over metas
- related: metas become Rigids during generalization; `force()` chases meta chains AND reduces type operators
- related: [[rigid-variables.types]] — metas become rigids during generalization; rigids become fresh metas during instantiation
- related: [[f-omega-lite.types]] — force chases meta chains as part of NbE normalization
