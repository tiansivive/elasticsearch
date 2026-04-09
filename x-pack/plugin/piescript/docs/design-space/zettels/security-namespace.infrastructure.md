---
tags: [infrastructure, security, es-internals, implemented, decision]
refs:
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:PiescriptAction.java
  - code:PiescriptSendAction.java
---
# Security Namespace

- Main action: `cluster:compute/piescript` (cluster-level auth).
- Send action: `internal:compute/piescript/send` (system-internal).
- Removed `CompositeIndicesRequest`. Removed `RBACEngine` allowlist entry.
- [[esql-compilation.esql|ESQL]] queries handle their own index authorization.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-0.roadmap]]
- solves: previous namespace `indices:data/read/piescript` caused assertion failures in `RBACEngine` with security enabled
- complements: [[auth-checks.elaboration]] — compile-time auth checks complement the runtime namespace
- constrains: [[transport-send.infrastructure]] — send action uses the internal namespace
