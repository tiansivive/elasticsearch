---
tags: [types, primitives, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Numeric Precision

All numbers are `Double` (IEEE 754 64-bit). Integers/Longs lose precision above 2^53. `Integer`/`Long` still exist in `Value` for serialization but widened to `DoubleVal` at ESQL boundary. Future: separate Integer type, or numeric tower via typeclasses.

**Depends on**: [[unified-double.types]]
**Enables**: (none directly)
**Connections**:
- related: whole-number doubles serialize as integers in JSON — part of primitive type review
- related: [[typeclasses.types]] — numeric tower via typeclasses is the future direction for integer/double separation
