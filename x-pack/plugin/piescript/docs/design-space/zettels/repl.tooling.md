---
tags: [tooling, open]
refs:
  - roadmap:phase-8
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# REPL

Interactive REPL for piescript evaluation. Would enable exploratory data analysis and rapid iteration. Requires: persistent evaluation context across inputs, incremental elaboration, and a way to display intermediate types and values. The dev endpoint already returns pipeline stages — a REPL would build on that.

**Depends on**: [[dev-endpoint.tooling]], [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- related: [[dev-endpoint.tooling]] — REPL builds on the same pipeline inspection the dev endpoint provides
- related: [[lsp.tooling]] — both are interactive developer experience tools; LSP for editors, REPL for terminal
