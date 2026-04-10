---
tags: [language, superseded, concept]
refs:
  - adr:D-013
  - adr:D-040
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Two-Layer IR

`CoreExpr` (functional) and `CoreProcess` (process descriptions) as separate sealed hierarchies:
- Superseded by D-040: [[core-ir.language]] uses a single hierarchy where `CoreSpawn` and `CoreWhen` are `CoreExpr` variants, not a separate `CoreProcess`
- Effect boundary maintained at runtime level, not IR level
- The pure/IO boundary analogy from [[effect-systems.types]] still holds conceptually, just not as separate hierarchies

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- replaced-by: [[core-ir.language]] — pure/IO boundary analogy still holds, just not as separate hierarchies
- alternative-to: [[effect-systems.types]] — explicit effect system could provide the pure/effectful boundary that separate IR layers attempted
- informs: [[spawn.coordination]] — CoreSpawn lives in the single CoreExpr hierarchy instead of a separate CoreProcess
