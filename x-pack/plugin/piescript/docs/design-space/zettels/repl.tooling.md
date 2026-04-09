---
tags: [tooling, open, feature, task]
refs:
  - roadmap:phase-8
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# REPL

Interactive REPL for piescript evaluation.

- Would enable exploratory data analysis and rapid iteration.
- Requires: persistent evaluation context across inputs, incremental [[elaboration-architecture.types|elaboration]], and a way to display intermediate types and values.
- The [[dev-endpoint.tooling|dev endpoint]] already returns pipeline stages -- a REPL would build on that.

**Depends on**: [[dev-endpoint.tooling]], [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-8.roadmap]]
- extends: [[dev-endpoint.tooling]] — REPL builds on the same pipeline inspection the dev endpoint provides
- complements: [[lsp.tooling]] — both are interactive developer experience tools; LSP for editors, REPL for terminal
