---
tags: [lifecycle, open, feature, concept, needs-design, later]
refs:
  - roadmap:post-mvp
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:distributed-coordination
---
# Scheduled Execution

`PiescriptPersistentTasksExecutor` wrapping piescript in ES [[persistent-task-integration.infrastructure|persistent tasks]] + scheduler infrastructure. Enables [[long-lived-computations.lifecycle|long-running]] and recurring piescript programs. Prerequisite for the [[transform-unification.external|Transform replacement]] story.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: [[actor-model.lifecycle]]
**Connections**:
- part-of: [[future-coordination.roadmap]]
- related: [[transform-unification.external]] — same persistent task system Transforms use; required for continuous/long-lived computations
- prerequisite-for: [[watcher-replacement.external]] — scheduled execution is a prerequisite for replacing Watcher with piescript
- implements: [[persistent-task-integration.infrastructure]] — persistent tasks are the ES mechanism for scheduled execution
