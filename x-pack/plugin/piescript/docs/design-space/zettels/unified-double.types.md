---
tags: [types, primitives, implemented]
refs:
  - adr:D-020
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Unified Double

All numbers are `Double` (IEEE 754 64-bit). `Integer`/`Long` literals elaborate to `DoubleLit`. ESQL numeric fields widened to `DoubleVal` at boundary. Whole-number doubles serialize as integers in JSON. `Math` builtins (`abs`, `floor`, `ceil`, etc.) all operate on `Double`.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: [[typeclasses.types]] — future: arithmetic operators become `Num` methods when typeclasses arrive
- related: [[numeric-precision.types]] — resolves D-020
- related: [[datetime.types]] — DateTime is the main unsupported primitive type alongside Double/Keyword/Boolean
- related: [[keyword-string.types]] — part of the primitive type cluster
