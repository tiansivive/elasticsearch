---
tags: [infrastructure, serialization, implemented]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Serialization Infrastructure

Three centralized classes: `ValueSerialization` (12 `Value` variants with stable byte tags), `CoreExprSerialization` (17+ `CoreExpr` variants), `TypeSerialization` (`MonoType`, `RowType`, `LitVal`, `Op`, `Kind`). `ClosureVal` serializes body + env recursively. `BuiltinVal` serializes name + arity + partial args.

**Depends on**: [[core-ir.language]], [[closure-val.language]]
**Enables**: [[code-mobility.coordination]], [[send.coordination]]
**Connections**:
- related: no `TransportVersion` guards (tech debt) — deserialized nodes use synthetic `WIRE_SOURCE`
- related: [[serialization-boundary.infrastructure]] — defines which Value variants can cross the wire
- related: [[transport-versioning.infrastructure]] — the missing version guards are tracked there
