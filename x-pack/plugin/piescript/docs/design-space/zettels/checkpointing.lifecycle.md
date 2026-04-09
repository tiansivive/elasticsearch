---
tags: [lifecycle, scheduling, open, concept, feature]
refs: []
---
# Checkpointing

Incremental and resumable execution for [[scheduled-execution.lifecycle]] piescript jobs.

- Cursor state tracks progress through data, enabling restart from the last checkpoint rather than reprocessing from the beginning
- Uses transform-style checkpoints with `Shard.globalCheckpoint` as the basis for tracking position in the change stream

**Depends on**: [[scheduled-execution.lifecycle]]
**Enables**: (none)
**Connections**:
- related: [[shard-write.data]] — checkpoint state is persisted via shard writes
- complements: [[persistent-task-integration.infrastructure]] — persistent tasks provide the execution frame; checkpointing provides the resume semantics
