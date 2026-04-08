---
tags: [tooling, open]
refs:
  - roadmap:phase-8
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Language Server Protocol

Language Server Protocol implementation for IDE support. An LSP server for piescript would provide autocompletion, type-on-hover, error highlighting, and go-to-definition. The elaboration architecture already produces the type information needed; the LSP layer would expose it incrementally as the user edits.

**Depends on**: (none)
**Enables**: autocompletion, type-on-hover, error highlighting, go-to-definition
**Connections**:
- related: [[elaboration-architecture.types]] — LSP needs incremental elaboration
- related: [[dev-endpoint.tooling]] — dev endpoint already returns type/constraint info
- related: [[syntax-highlighting.tooling]] — LSP provides richer highlighting; syntax highlighting is the simpler baseline
- related: [[repl.tooling]] — both are interactive developer experience tools
