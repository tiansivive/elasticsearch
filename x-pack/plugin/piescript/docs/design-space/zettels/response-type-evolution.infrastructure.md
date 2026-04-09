---
tags: [infrastructure, es-internals, implemented, decision, documentation]
refs:
  - adr:D-002
  - adr:D-023
  - code:PiescriptResponse.java
---
# Response Type Evolution

- **Phase 0**: returned raw `EsqlQueryResponse` directly (D-002).
- **Phase 1c**: introduced `PiescriptResponse` wrapper (D-023) implementing `ChunkedToXContentObject` and `Releasable`.
- Wrapper chosen over subclass to avoid fragile coupling to ESQL internals (Pages, BlockFactory, ref-counting).
- Expression results serialize as `{"type": ..., "result": ...}`.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[es-plugin.infrastructure]] — response handling is a core plugin responsibility
- uses: [[esql-value-converter.esql]] — converter bridges ESQL results into piescript Values before response serialization
