---
tags: [types, tech-debt]
refs:
  - adr:D-036
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Bidirectional Checking

Bidirectional checking mode partially implemented. The elaborator has elaborate (synthesis) and check modes, but polytype ascription at expression level doesn't work (blocked on [[forall-type.types]]). Full implementation needs ∀-CHECK rule, lambda-against-arrow, ascription-as-check.

**Depends on**: [[hindley-milner.types]], [[forall-type.types]]
**Enables**: (none directly)
**Connections**:
- related: current approach is synthesis-only (Algorithm J with deferred solving), not true bidirectional HM
- related: [[type-annotations.types]] — annotations provide the expected types that feed checking mode
- related: [[elaboration-architecture.types]] — elaborate (synthesis) and check modes in the elaborator
