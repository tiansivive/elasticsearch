---
tags: [types, tech-debt, task, ready, next]
refs:
  - adr:D-036
  - code:Elaborator.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Bidirectional Checking

Bidirectional checking mode partially implemented. The [[elaboration-architecture.types]] has elaborate (synthesis) and check modes, but polytype ascription at expression level doesn't work (blocked on [[forall-type.types]]). Full implementation needs forall-CHECK rule, lambda-against-arrow, ascription-as-check. Current approach is synthesis-only (Algorithm J with [[deferred-constraints.types]]).

**Depends on**: [[hindley-milner.types]], [[forall-type.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-1.roadmap]]
- extends: [[elaboration-architecture.types]] — adds checking mode alongside existing synthesis mode in the elaborator
- uses: [[type-annotations.types]] — annotations provide the expected types that feed checking mode
- extends: [[deferred-constraints.types]] — current approach is synthesis-only (Algorithm J with deferred solving)
- cites: [[dunfield-krishnaswami.paper]]
