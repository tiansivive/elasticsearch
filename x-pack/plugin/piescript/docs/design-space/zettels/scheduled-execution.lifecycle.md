---
tags: [lifecycle, open]
refs:
  - roadmap:post-mvp
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Scheduled Execution

`PiescriptPersistentTasksExecutor` wrapping piescript in ES persistent tasks + scheduler infrastructure. Enables long-running and recurring piescript programs. Prerequisite for the Transform replacement story.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: [[actor-model.lifecycle]]
**Connections**:
- related: [[transform-unification.external]] — same persistent task system Transforms use; required for continuous/long-lived computations
- related: [[watcher-replacement.external]] — scheduled execution is a prerequisite for replacing Watcher with piescript
