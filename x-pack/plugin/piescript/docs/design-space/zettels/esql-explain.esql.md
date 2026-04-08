---
tags: [esql, debugging, implemented]
refs:
  - adr:D-052
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Explain

`ESQL.explain : ESQL r -> Keyword` — returns the compiled ESQL string without executing the query. Debugging builtin for inspecting NbE compilation output. The ESQL plan is compiled to a string by walking the `EsqlPlan` ADT, then returned as a `KeywordVal` instead of being sent to `EsqlQueryAction`.

**Depends on**: [[esql-compilation.esql]], [[nbe-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- related: [[nbe-compilation.esql]] — essential for debugging NbE compilation; shows the ESQL that would be sent to the engine
- related: [[dev-endpoint.tooling]] — both serve debugging: explain shows compiled ESQL, dev endpoint shows full pipeline
