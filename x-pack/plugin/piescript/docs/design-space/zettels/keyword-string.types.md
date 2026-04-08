---
tags: [types, primitives, tech-debt]
refs:
  - adr:D-026
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Keyword String

`KeywordVal` uses `String`, not `BytesRef`. Conversion from `BytesRef` at `CoreLit` boundary. Reverse conversion (`String` → `BytesRef`) needed when piescript values flow into ESQL query parameters. Deliberate deviation from D-009 (type alignment with ESQL `DataType`).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: narrow conversion boundary (one line in `litToValue`) — part of the broader primitive type review
