---
tags: [types, primitives, tech-debt, task, question, needs-design, later]
refs:
  - adr:D-026
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Value.java
  - code:EsqlValueConverter.java
  - thread:data-completeness
---
# Keyword String

`KeywordVal` uses `String`, not `BytesRef`. Conversion from `BytesRef` at [[core-ir.language]] `CoreLit` boundary. Reverse conversion (`String` to `BytesRef`) needed when piescript values flow into ESQL query parameters. Deliberate deviation from D-009 (type alignment with ESQL `DataType`).

**Depends on**: (none)
**Enables**: [[string-concat.language]]
**Connections**:
- related: [[unified-double.types]] — part of the primitive type cluster alongside Double and Boolean
- related: [[datetime.types]] — another unsupported primitive type requiring boundary conversion
- informs: [[esql-value-converter.esql]] — converter handles BytesRef->String boundary for keywords
- constrains: narrow conversion boundary (one line in `litToValue`) — part of the broader primitive type review
