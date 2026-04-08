---
tags: [types, security, esql, open]
refs:
  - adr:D-003
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Auth Checks at Elaboration Time

Compile-time index permission checks at `use`/ESQL compilation time. Instead of deferring all authorization to ESQL execution, the elaborator can verify index permissions when `use` declarations are elaborated and field-caps are resolved. This enables early auth failure with precise error messages pointing at the source location, rather than opaque runtime errors deep in ESQL execution.

**Depends on**: [[use-declarations.data]], [[field-caps-resolution.data]]
**Enables**: early auth failure, better error messages
**Connections**:
- related: [[security-namespace.infrastructure]] — currently auth is runtime-only via ESQL delegation
