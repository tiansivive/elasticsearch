---
tags: [language, implemented, ir]
refs:
  - adr:D-008
  - adr:D-040
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Core IR

`CoreExpr` is a single sealed hierarchy of 17+ variants: functional nodes (`CoreVar`, `CoreFree`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp`, `CoreQuery`/`CoreQueryExec`, `CoreList`) and coordination nodes (`CoreSpawn`, `CoreWhen`, `CoreSend`). Extends ES's `Node` infrastructure for tree traversal/rewriting. `MonoType` on every node.

**Depends on**: [[de-bruijn-indices.language]], [[system-f-core.types]]
**Enables**: [[evaluator.language]], [[serialization.infrastructure]]
**Connections**:
- supersedes: D-013 separate `CoreProcess` — single hierarchy (D-040)
- related: `CoreFree` for module-level free variables (builtins)
- related: [[antlr-grammar.language]] — parsing produces CST; elaboration converts to Core IR
- related: [[closure-val.language]] — CoreLam produces ClosureVal at evaluation time
