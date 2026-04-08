---
tags: [tooling, debugging, implemented]
refs:
  - code:RestPiescriptDevAction.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dev Endpoint

`POST /_piescript/dev` returns all pipeline stages: CST (`tree`), elaborated Core IR (`core`, `core_raw`), `constraints`, `zonker` substitutions, resolved type, and eval result. Parse errors -> `parse_error`. Type errors -> `tree` + `type_error`. Eval errors -> `eval_error`. Essential for debugging the full pipeline.

**Depends on**: [[transport-pipeline.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: [[core-printer.tooling]] — relies on `CorePrinter` for output
- related: core_raw shows bare metas/rigids before zonking; constraints shows deferred constraints; zonker shows the union-find state
