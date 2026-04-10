---
tags: [coordination, superseded, concept]
refs:
  - adr:D-040
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Par Blocks

`par { a = expr1, b = expr2 } in body` — independent concurrent bindings. Replaced by [[spawn.coordination|spawn]] + [[when-synchronization.coordination|when]], which are strictly more expressive (multi-way synchronization, reaction rules, streaming coordination).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- replaced-by: [[spawn.coordination]] — any par block expressible as spawns + when; was original Phase 4
- replaced-by: [[when-synchronization.coordination]] — when provides strictly more expressive multi-way synchronization
- replaced-by: [[join-calculus.coordination]] — join calculus model replaced the par block approach (D-040)
