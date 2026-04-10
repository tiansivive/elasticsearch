---
tags: [security, lifecycle, open, concept, needs-design, someday]
refs:
  - thread:external-interaction
---
# Token Capability Security

Token-based capability security for the [[actor-model.lifecycle]]:
- Script submission returns an opaque token; possession of the token grants access to the script's [[channels.infrastructure]] for sending messages, polling results, and lifecycle management
- This is object-capability security: authority derives from holding a reference, not from identity or ACLs
- Related to [[non-serializable-types.types]] -- tokens may need to be non-serializable to prevent capability leakage across nodes
- Complements [[security-namespace.infrastructure]] at a finer granularity: namespace controls submission, tokens control interaction

**Depends on**: [[actor-model.lifecycle]], [[named-channels.lifecycle]]
**Enables**: (none)
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- complements: [[security-namespace.infrastructure]] — namespace controls who can submit scripts; tokens control who can interact with running scripts
- related: [[channels.infrastructure]] — tokens grant access to specific channels
- related: [[named-channels.lifecycle]] — named channels are the externally-visible endpoints that tokens protect
- constrains: [[non-serializable-types.types]] — tokens may need to be non-serializable to prevent capability leakage
