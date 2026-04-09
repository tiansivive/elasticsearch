---
tags: [roadmap, data, implemented]
refs:
  - adr:D-050
  - plan:block_d_local_data_ca4b90be
---
# Block D — Local Data Access

Read data on data nodes without ESQL. Use declarations, index types, shard reads, searcher lifecycle, and query-as-record pattern.

**Depends on**: [[block-c.roadmap]]
**Enables**: [[block-e.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- part-of: [[mvp.roadmap]]
- subsumes: [[use-declarations.data]]
- subsumes: [[index-type.data]]
- subsumes: [[shard-read.data]]
- subsumes: [[searcher-lifecycle.data]]
- subsumes: [[non-serializable-types.types]]
- subsumes: [[qualified-builtin-namespacing.language]]
- subsumes: [[queries-as-records.data]]
