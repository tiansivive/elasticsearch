---
tags: [types, infrastructure, open, concept, known-issue, later]
refs:
  - adr:D-054
  - code:EvalExchange.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:data-completeness
---
# Column Name Derivation

Derive [[exchange-streaming.infrastructure]] column names from [[rowtype-as-monotype.types]] at elaboration time (eliminate runtime List Keyword parameter).

- Currently, the Exchange streaming infrastructure requires an explicit list of column names passed at runtime
- Since the elaborator already knows the full row type, column names can be derived from the elaborated type via [[force-threading.types]], removing this redundant parameter

**Depends on**: [[exchange-streaming.infrastructure]], [[force-threading.types]], [[rowtype-as-monotype.types]]
**Enables**: (none)
**Connections**:
- analogous-to: [[type-driven-materialization.esql]] — similar pattern of using elaborated type at runtime
