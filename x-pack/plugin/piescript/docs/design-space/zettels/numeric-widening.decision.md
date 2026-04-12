---
tags: [types, primitives, decided, superseded, concept]
refs:
  - adr:D-009
  - adr:D-020
---
# Numeric Widening Rules (Superseded)

Original numeric widening rules for piescript's multi-numeric-type system:
- `Integer + Long -> Long`
- `Integer + Double -> Double`
- `Long + Double -> Double`

These rules mirrored ESQL's `commonType` logic, promoting to the wider type on mixed-type arithmetic. The design assumed piescript would maintain separate Integer, Long, and Double types.

Now moot: D-020 introduced unified Double, collapsing all numeric types to IEEE 754 64-bit floating point. Widening rules are unnecessary when there is only one numeric type. Preserved here for historical context and to document the decision trail.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- superseded-by: [[unified-double.types]] -- unified Double eliminates the need for widening rules entirely
- informs: [[numeric-precision.types]] -- precision concerns that motivated separate types remain as future work
- related: [[typeclasses.types]] -- a future Num typeclass could reintroduce polymorphic arithmetic without explicit widening
- related: [[esql-value-converter.esql]] -- the converter still handles Integer/Long from ESQL, widening to Double at the boundary
