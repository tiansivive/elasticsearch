---
tags: [types, primitives, open, task, concept, question, someday]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Value.java
  - code:EsqlValueConverter.java
  - thread:data-completeness
---
# Numeric Precision

All numbers are `Double` (IEEE 754 64-bit) via [[unified-double.types]]. Integers/Longs lose precision above 2^53. `Integer`/`Long` still exist in `Value` for serialization but widened to `DoubleVal` at the [[esql-value-converter.esql]] boundary.

- Whole-number doubles serialize as integers in JSON (part of primitive type review)
- Future: separate Integer type, or numeric tower via [[typeclasses.types]]

**Depends on**: [[unified-double.types]]
**Enables**: (none directly)
**Connections**:
- motivates: [[typeclasses.types]] — numeric tower via typeclasses is the future direction for integer/double separation
- tension-with: [[esql-value-converter.esql]] — widening at the boundary loses precision information
