---
tags: [types, security, esql, open, concept]
refs:
  - adr:D-003
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Auth Checks at Elaboration Time

Compile-time index permission checks at `use`/ESQL compilation time.

- Instead of deferring all authorization to ESQL execution, the [[elaboration-architecture.types]] can verify index permissions when [[use-declarations.data]] are elaborated and [[field-caps-resolution.data]] is resolved
- Enables early auth failure with precise error messages pointing at the source location, rather than opaque runtime errors deep in ESQL execution

**Depends on**: [[use-declarations.data]], [[field-caps-resolution.data]]
**Enables**: early auth failure, better error messages
**Connections**:
- related: [[security-namespace.infrastructure]] — currently auth is runtime-only via ESQL delegation
- extends: [[elaboration-architecture.types]] — adds permission checking to the elaboration pipeline
- uses: [[field-caps-resolution.data]] — field caps resolution is the point where permissions can be checked
