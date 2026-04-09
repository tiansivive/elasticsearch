---
tags: [infrastructure, serialization, implemented, documentation]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:ValueSerialization.java
  - code:CoreExprSerialization.java
  - code:TypeSerialization.java
---
# Serialization Infrastructure

Three centralized classes:

- `ValueSerialization` (12 `Value` variants with stable byte tags).
- `CoreExprSerialization` (17+ [[core-ir.language|CoreExpr]] variants).
- `TypeSerialization` (`MonoType`, `RowType`, `LitVal`, `Op`, `Kind`).

[[closure-val.language|ClosureVal]] serializes body + env recursively. `BuiltinVal` serializes name + arity + partial args.

**Depends on**: [[core-ir.language]], [[closure-val.language]]
**Enables**: [[code-mobility.coordination]], [[send.coordination]]
**Connections**:
- part-of: [[block-c.roadmap]]
- constrains: no `TransportVersion` guards (tech debt) — deserialized nodes use synthetic `WIRE_SOURCE`
- complements: [[serialization-boundary.infrastructure]] — defines which Value variants can cross the wire
- blocks: [[transport-versioning.infrastructure]] — the missing version guards are tracked there
