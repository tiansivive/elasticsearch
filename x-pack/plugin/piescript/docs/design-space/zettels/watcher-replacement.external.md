---
tags: [external, lifecycle, designed]
refs:
  - vision:fragmentation-problem
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Watcher Replacement

Watcher implements condition evaluation + action triggering via JSON watch definitions. In piescript: `when` coordination maps to Watcher's condition semantics (fire when channels satisfy a predicate). `send` maps to action triggering. Scheduled execution provides the trigger mechanism. The semantic mapping: Watcher input -> piescript query, Watcher condition -> piescript `when` pattern, Watcher action -> piescript `send` / write.

**Depends on**: [[scheduled-execution.lifecycle]], [[when-synchronization.coordination]]
**Enables**: (none directly)
**Connections**:
- related: [[transform-unification.external]] — part of the broader fragmentation problem unification
- related: [[actor-model.lifecycle]] — long-running watchers map to persistent actors
