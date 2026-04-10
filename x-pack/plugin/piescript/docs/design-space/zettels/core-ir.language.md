---
tags: [language, implemented, ir, concept]
refs:
  - adr:D-008
  - adr:D-040
  - code:CoreExpr.java
  - code:CoreExprSerialization.java
  - code:piescript.core
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Core IR

`CoreExpr` is a single sealed hierarchy of 17+ variants:

- Functional nodes: `CoreVar`, `CoreFree`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp`, `CoreQuery`/`CoreQueryExec`, `CoreList`
- Coordination nodes: `CoreSpawn`, `CoreWhen`, `CoreSend`
- Extends ES's `Node` infrastructure for tree traversal/rewriting
- [[kind-system.types|types]] on every node

**Depends on**: [[de-bruijn-indices.language]], [[system-f-core.types]]
**Enables**: [[evaluator.language]], [[serialization.infrastructure]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- replaces: [[two-layer-ir.language]] — D-013 separate `CoreProcess` merged into single hierarchy (D-040)
- uses: [[antlr-grammar.language]] — parsing produces CST; [[elaboration-architecture.types]] converts to Core IR
- complements: [[closure-val.language]] — CoreLam produces ClosureVal at evaluation time
- uses: [[de-bruijn-indices.language]] — `CoreVar` uses de Bruijn indices for variable representation
