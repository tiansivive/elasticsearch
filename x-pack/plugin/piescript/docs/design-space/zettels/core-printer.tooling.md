---
tags: [tooling, debugging, tech-debt, implemented, task]
refs:
  - code:CorePrinter.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# CorePrinter

Debug printing for [[core-ir.language]]: `printExpr` (zonked types), `printExprRaw` (bare metas/rigids), `printConstraints`, `printZonker`.

- Used by the [[dev-endpoint.tooling]] for pipeline inspection
- Still uses [[resolve-deep.types]] from `TypeWalker` for type display — a known tech debt item (D-032 deviation)
- The long-term fix is environment-based Rigid resolution

**Depends on**: [[core-ir.language]]
**Enables**: [[dev-endpoint.tooling]]
**Connections**:
- uses: [[resolve-deep.types]] — `CorePrinter` is the last consumer of `resolveDeep`
- implements: [[dev-endpoint.tooling]] — provides the core/core_raw/constraints/zonker output
