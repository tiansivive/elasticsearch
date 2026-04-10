---
tags: [types, runtime, tech-debt, known-issue, task, later]
refs:
  - adr:D-026
  - code:EsqlValueConverter.java
  - code:Value.java
  - thread:data-completeness
---
# Keyword BytesRef Conversion

`KeywordVal` uses `String` internally but ESQL represents keywords as `BytesRef`. Conversion happens at the [[core-ir.language]] `CoreLit` boundary when ESQL values enter piescript. The reverse conversion is also needed when piescript values flow back into ESQL (e.g., as query parameters or write payloads).

- Forward: `BytesRef` to `String` at the [[esql-value-converter.esql]] boundary
- Reverse: `String` to `BytesRef` when flowing into ESQL query parameters
- Recurring friction point at the language/engine boundary

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- part-of: [[keyword-string.types]] — the broader keyword representation design
- uses: [[esql-value-converter.esql]] — the component responsible for bridging ESQL and piescript value representations
- tension-with: [[core-ir.language]] — CoreLit boundary is where the impedance mismatch surfaces
