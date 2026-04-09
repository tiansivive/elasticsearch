---
tags: [language, implemented, documentation]
refs:
  - adr:D-016
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Prelude.java
  - code:ElaborationContext.java
---
# Prelude Module

Prelude module defining all built-in function [[type-scheme.types|type schemes]]. [[qualified-builtin-namespacing.language|Qualified names]]: `List.*`, `Math.*`, `Shard.*`, `Index.*`, `Cluster.*`, `ESQL.*`, `Exchange.*`, `Page.*`. Type schemes use pre-allocated negative [[rigid-variables.types|Rigid]] IDs. Wired into `ElaborationContext.withModule` at [[elaboration-architecture.types|elaboration]] start.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[esql-combinators.esql]], [[shard-read.data]], [[topology.infrastructure]]
**Connections**:
- part-of: [[phase-2.roadmap]]
- implements: D-016 — combinators as prelude builtins, not Core IR nodes
- related: [[typeclasses.types]] — prepares for typeclasses (map -> Functor.fmap)
- uses: [[qualified-builtin-namespacing.language]] — all builtins use the Namespace.name convention
