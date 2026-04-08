---
tags: [types, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dependent Types

Idris 2 and Granule demonstrate QTT with dependent types. Piescript uses QTT *without* dependent types (D-018) — multiplicities are static annotations, no term-level multiplicity computation. Full dependent types are not planned but inform the linearity story and the Label kind design.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: [[label-kind.types]] — Label kind for type-safe field projection is the closest piescript gets to dependent types (type-level string singletons)
- related: [[qtt-linearity.types]] — QTT originates from dependent type theory; piescript uses multiplicities without dependent types
