---
tags: [language, implemented]
refs:
  - adr:D-016
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Prelude Module

Prelude module defining all built-in function type schemes. Qualified names: `List.*`, `Math.*`, `Shard.*`, `Index.*`, `Cluster.*`, `ESQL.*`, `Exchange.*`, `Page.*`. Type schemes use pre-allocated negative Rigid IDs. Wired into `ElaborationContext.withModule` at elaboration start.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[esql-combinators.esql]], [[shard-read.data]], [[topology.infrastructure]]
**Connections**:
- related: D-016 — combinators as prelude builtins, not Core IR nodes
- informs: [[typeclasses.types]] — prepares for typeclasses (map -> Functor.fmap)
