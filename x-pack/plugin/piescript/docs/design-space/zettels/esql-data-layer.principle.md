---
tags: [meta, principle, esql, decision]
refs:
  - doc:vision.md
---
# ESQL Is the Data Layer

Piescript does not reinvent query execution. `query` expressions delegate to
ESQL. The language adds computation *around* queries — binding results,
transforming values, branching on conditions — not *inside* them. The
`extendedPlugins = ['x-pack-esql']` relationship is intentional and permanent.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- related: [[esql-compilation.esql]] — the NbE compilation path is how piescript talks to ESQL
- related: [[query-typeclass.data]] — the Query typeclass generalizes this: ESQL is one instance
- constrains: [[logical-plan-compilation.esql]] — even internal compilation targets ESQL's plan IR
