---
tags: [types, primitives, implemented, decision, concept]
refs:
  - adr:D-009
  - adr:D-020
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Unified Double

All numbers are `Double` (IEEE 754 64-bit):
- `Integer`/`Long` literals elaborate to `DoubleLit`
- ESQL numeric fields widened to `DoubleVal` at the [[esql-value-converter.esql]] boundary
- Whole-number doubles serialize as integers in JSON
- `Math` builtins (`abs`, `floor`, `ceil`, etc.) all operate on `Double` (see [[prelude.language]])
- Future: [[typeclasses.types]] (Num typeclass) will make arithmetic operators polymorphic

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- prerequisite-for: [[typeclasses.types]] — future: arithmetic operators become `Num` methods when typeclasses arrive
- solves: [[numeric-precision.types]] — resolves D-020
- related: [[datetime.types]] — DateTime is the main unsupported primitive type alongside Double/Keyword/Boolean
- related: [[keyword-string.types]] — part of the primitive type cluster
- uses: [[esql-value-converter.esql]] — ESQL numeric fields widened to DoubleVal at boundary
- uses: [[prelude.language]] — Math builtins operate on Double
