---
tags: [language, superseded]
refs:
  - adr:D-013
  - adr:D-040
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Two-Layer IR

`CoreExpr` (functional) and `CoreProcess` (process descriptions) as separate sealed hierarchies. Superseded by D-040: `CoreSpawn` and `CoreWhen` are `CoreExpr` variants, not a separate `CoreProcess`. Effect boundary maintained at runtime level, not IR level.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- superseded-by: [[core-ir.language]] — pure/IO boundary analogy still holds, just not as separate hierarchies
