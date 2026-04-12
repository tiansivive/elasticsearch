---
tags: [coordination, data, write-path, channels, implemented, concept]
refs:
  - adr:D-051
  - code:EvalWrite.java
---
# Shard Refresh Ordering

`Shard.refresh` returns `Channel RefreshResult` -- a channel that completes when the shard refresh finishes. The caller synchronizes via `when` before opening a post-write searcher, ensuring read-after-write consistency:

```
let writer = Shard.writer shard;
Shard.write writer id doc;
let refreshCh = Shard.refresh shard;
when (refreshCh result) ->
  let searcher = Shard.open shard;
  ...
```

The channel-based synchronization forces the correct ordering at the language level -- you cannot read stale data because `when` blocks until the refresh completes. This is coordination-as-types: the `Channel RefreshResult` value is the proof that a refresh happened.

Future: [[qtt-linearity.types|linear types]] could make `RefreshResult` a linear value that MUST be consumed before opening a post-write searcher. This would enforce the ordering at the type level rather than relying on programmer discipline to `when` on the refresh channel.

**Depends on**: [[when-synchronization.coordination]], [[channels.infrastructure]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- uses: [[when-synchronization.coordination]] -- `when` is the synchronization mechanism for refresh completion
- uses: [[channels.infrastructure]] -- refresh result delivered via channel
- implements: [[shard-write.data]] -- refresh is part of the shard write lifecycle
- informs: [[qtt-linearity.types]] -- linear RefreshResult would enforce read-after-write ordering at the type level
- informs: [[session-types.types]] -- write-refresh-read sequence is a session type protocol
- complements: [[shard-read.data]] -- post-refresh searcher opening bridges the write and read paths
- part-of: [[block-e.roadmap]]
- implements: [[monadic-write-protocol.data]] -- refresh ordering is an instance of sequenced write protocol
