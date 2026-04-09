---
tags: [types, inference, polymorphism, implemented, concept]
refs:
  - code:ElaborationState.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Binding Levels

Binding-level-based let-generalization. Each let-binding introduces a new level. Metas created at a binding level are generalizable at that level — they don't escape to outer scope. `collectMetas` gathers unsolved metas at the current level; `generalize` converts them to Rigids. The binding level prevents inner metas from being generalized at outer let boundaries.

**Depends on**: [[hindley-milner.types]], [[meta-variables.types]]
**Enables**: [[value-restriction.types]]
**Connections**:
- prerequisite-for: [[value-restriction.types]] — value restriction (D-046) is an additional guard on top of binding levels
- uses: [[meta-variables.types]] — `collectMetas` gathers unsolved metas at the current level for generalization
- inspired-by: standard technique from Odersky/Laufer
