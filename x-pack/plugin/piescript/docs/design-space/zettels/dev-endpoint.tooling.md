---
tags: [tooling, debugging, implemented, documentation]
refs:
  - code:RestPiescriptDevAction.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dev Endpoint

`POST /_piescript/dev` returns all pipeline stages:

- CST (`tree`), elaborated [[core-ir.language]] (`core`, `core_raw`), `constraints`, `zonker` substitutions, resolved type, and eval result
- Parse errors -> `parse_error`; Type errors -> `tree` + `type_error`; Eval errors -> `eval_error`
- `core_raw` shows bare metas/rigids before zonking; `constraints` shows [[deferred-constraints.types]]; `zonker` shows the union-find state

**Depends on**: [[transport-pipeline.infrastructure]]
**Enables**: (none directly)
**Connections**:
- uses: [[core-printer.tooling]] — relies on `CorePrinter` for output
- uses: [[deferred-constraints.types]] — displays constraint state for debugging
- uses: [[zonker.types]] — displays union-find substitution state
- extends: [[eval-endpoint.infrastructure]] — dev endpoint adds pipeline inspection on top of the eval pipeline
