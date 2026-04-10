---
tags: [paper-trail, lifecycle, scheduling, es-internals]
refs:
  - session:7200695e-f3af-494b-b6cc-6f02316a3ffa
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transform Scheduling Session

Short session on how piescript could support scheduled execution like ES Transforms. Explored PiescriptPersistentTasksExecutor wrapping piescript in ES persistent tasks. The gap: piescript is one-shot (POST eval -> response), transforms need scheduling + state.

**Connections**:
- produced: [[scheduled-execution.lifecycle]]
- informs: [[transform-unification.external]]
- informs: [[actor-model.lifecycle]] — persistent execution is prerequisite
