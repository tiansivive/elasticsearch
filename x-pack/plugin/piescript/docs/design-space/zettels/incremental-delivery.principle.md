---
tags: [meta, principle, decision]
refs:
  - doc:vision.md
---
# Incremental Delivery

The language is built in phases, each self-contained and testable. Early phases
execute everything locally on the coordinator node; later phases distribute
work to data nodes. The key architectural abstractions (the
functional/coordination boundary, the channel model) are introduced early so
that the transition from local to distributed execution is incremental, not
architectural.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- related: [[phase-transition-architecture.language]] — the architecture supports incremental phase transitions
