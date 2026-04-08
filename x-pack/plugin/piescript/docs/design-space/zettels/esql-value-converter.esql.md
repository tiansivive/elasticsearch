---
tags: [esql, evaluation, materialization, implemented]
refs:
  - code:EsqlValueConverter.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Value Converter

Bridges ESQL Java types to piescript Values. `instanceof` dispatch: `Integer`->`DoubleVal`, `Long`->`DoubleVal`, `Double`->`DoubleVal`, `String`->`KeywordVal` (via `BytesRef`), `Boolean`->`BooleanVal`, `null`->`NullVal`, `List`->first element (scalar) or `ListVal` (when type-driven materialization identifies `List`-typed columns). The `force` function identifies which columns are `List`-typed.

**Depends on**: [[type-driven-materialization.esql]]
**Enables**: (none directly)
**Connections**:
- related: [[serialization.infrastructure]] — three distinct serialization targets: `ValueSerialization` (binary wire), `PiescriptResponse` (REST JSON), `EsqlValueConverter` (ESQL->piescript bridge)
- related: [[multi-value-fields.data]] — MV field handling (first-element vs ListVal) is core to value conversion
- related: [[keyword-string.types]] — converter handles BytesRef→String boundary for keywords
