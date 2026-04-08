---
tags: [types, infrastructure, open]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Column Name Derivation

Derive Exchange column names from row type at elaboration time (eliminate runtime List Keyword parameter). Currently, the Exchange streaming infrastructure requires an explicit list of column names passed at runtime. Since the elaborator already knows the full row type, column names can be derived from the elaborated type, removing this redundant parameter.

**Depends on**: [[exchange-streaming.infrastructure]], [[force-threading.types]], [[rowtype-as-monotype.types]]
**Enables**: (none)
**Connections**:
- related: [[type-driven-materialization.esql]] — similar pattern of using elaborated type at runtime
