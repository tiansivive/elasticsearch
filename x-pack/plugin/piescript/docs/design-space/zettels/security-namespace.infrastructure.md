---
tags: [infrastructure, security, es-internals, implemented]
refs:
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Security Namespace

Main action: `cluster:compute/piescript` (cluster-level auth). Send action: `internal:compute/piescript/send` (system-internal). Removed `CompositeIndicesRequest`. Removed `RBACEngine` allowlist entry. ESQL queries handle their own index authorization.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: previous namespace `indices:data/read/piescript` caused assertion failures in `RBACEngine` with security enabled
- related: [[auth-checks.elaboration]] — compile-time auth checks complement the runtime namespace
