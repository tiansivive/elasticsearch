---
tags: [infrastructure, es-internals, scheduling, open, concept, feature]
refs: []
---
# Persistent Task Integration

Using ES's persistent task infrastructure (`PersistentTasksExecutor`) for [[scheduled-execution.lifecycle|scheduled]] piescript execution.

- Provides task allocation across nodes, status reporting via cluster state, and failure recovery through automatic reassignment.
- Bridges piescript's [[actor-model.lifecycle|actor model]] with ES's existing distributed task management.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- implements: [[scheduled-execution.lifecycle]] — persistent tasks are the ES mechanism for scheduled execution
- complements: [[checkpointing.lifecycle]] — persistent tasks provide the execution frame; checkpointing provides the resume semantics
- related: [[actor-model.lifecycle]] — bridges piescript actors with ES task management
