---
tags: [external, lifecycle, designed, concept, someday]
refs:
  - vision:fragmentation-problem
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:external-interaction
---
# Watcher Replacement

Watcher implements condition evaluation + action triggering via JSON watch definitions. In piescript:
- [[when-synchronization.coordination]] maps to Watcher's condition semantics (fire when channels satisfy a predicate)
- [[send.coordination]] maps to action triggering
- [[scheduled-execution.lifecycle]] provides the trigger mechanism

The semantic mapping:
- Watcher input -> piescript query ([[esql-compilation.esql]])
- Watcher condition -> piescript `when` pattern
- Watcher action -> piescript `send` / write

**Depends on**: [[scheduled-execution.lifecycle]], [[when-synchronization.coordination]]
**Enables**: (none directly)
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- part-of: [[transform-unification.external]] — part of the broader fragmentation problem unification
- related: [[actor-model.lifecycle]] — long-running watchers map to persistent actors
- related: [[when-synchronization.coordination]] — `when` provides condition evaluation semantics
- related: [[send.coordination]] — `send` provides action triggering semantics
- related: [[esql-compilation.esql]] — Watcher inputs map to piescript queries
