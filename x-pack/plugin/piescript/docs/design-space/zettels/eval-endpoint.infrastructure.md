---
tags: [infrastructure, es-internals, implemented, documentation]
refs:
  - adr:D-002
  - adr:D-023
  - adr:D-055
  - code:RestPiescriptAction.java
  - code:TransportPiescriptAction.java
  - code:PiescriptResponse.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Eval Endpoint

`POST /_piescript/eval` — the main entry point. Accepts `{"program": "..."}`, runs the full [[transport-pipeline.infrastructure]] (parse → index-resolve → elaborate → evaluate), returns `{"type": "...", "result": ...}`.

- Registered by `RestPiescriptAction` using `RestRefCountedChunkedToXContentListener` (same streaming response pattern as ESQL)
- Action name: `cluster:compute/piescript` (D-055, [[security-namespace.infrastructure]])
- Response: `PiescriptResponse` wrapping a `Value` + type string (D-023). `ListVal` serialized as JSON arrays. Whole-number doubles as integers for clean output.
- Request validation: empty/blank programs rejected with 400

**Depends on**: [[transport-pipeline.infrastructure]], [[security-namespace.infrastructure]], [[es-plugin.infrastructure]]
**Enables**: [[dev-endpoint.tooling]]
**Connections**:
- complements: [[dev-endpoint.tooling]] — dev endpoint adds pipeline inspection on top of the same pipeline
- evolved-into: [[actor-model.lifecycle]] — future: persistent actors replace single request/response with submit/poll/inbox/cancel
- uses: [[evaluator.language]] — the evaluate stage of the pipeline
- uses: [[esql-compilation.esql]] — ESQL queries fire via `EsqlQueryAction` during evaluation
