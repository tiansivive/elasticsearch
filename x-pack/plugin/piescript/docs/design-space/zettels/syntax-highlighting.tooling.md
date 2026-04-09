---
tags: [tooling, syntax, open, feature, task]
refs:
  - roadmap:phase-8
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Syntax Highlighting

Editor syntax highlighting definitions (VSCode, JetBrains, etc.) for piescript. The [[antlr-grammar.language|ANTLR grammar]] defines the token structure -- TextMate/monarch grammars can be derived from it. Keywords (`let`, `fn`, `in`, `spawn`, `when`, `send`, `use`, `query`), [[qualified-builtin-namespacing.language|qualified builtins]] (`ESQL.from`, `Shard.open`), [[type-annotations.types|type annotations]], and comments all need distinct highlighting.

**Depends on**: [[antlr-grammar.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-8.roadmap]]
- complements: [[lsp.tooling]] — LSP provides richer editor integration; syntax highlighting is the simpler baseline
