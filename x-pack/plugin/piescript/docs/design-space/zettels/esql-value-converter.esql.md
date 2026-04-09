---
tags: [esql, evaluation, materialization, implemented, documentation]
refs:
  - code:EsqlValueConverter.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Value Converter

Bridges ESQL Java types to piescript Values. `instanceof` dispatch:
- `Integer`/`Long`/`Double` -> `DoubleVal`
- `String` -> `KeywordVal` (via `BytesRef`) -- see [[keyword-string.types]]
- `Boolean` -> `BooleanVal`, `null` -> `NullVal`
- `List` -> first element (scalar) or `ListVal` when [[type-driven-materialization.esql]] identifies `List`-typed columns via the [[force-threading.types]] function

**Depends on**: [[type-driven-materialization.esql]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-2.roadmap]]
- complements: [[serialization.infrastructure]] — three distinct serialization targets: `ValueSerialization` (binary wire), `PiescriptResponse` (REST JSON), `EsqlValueConverter` (ESQL->piescript bridge)
- uses: [[multi-value-fields.data]] — MV field handling (first-element vs ListVal) is core to value conversion
- uses: [[keyword-string.types]] — converter handles BytesRef->String boundary for keywords
- uses: [[force-threading.types]] — force function identifies which columns are List-typed
