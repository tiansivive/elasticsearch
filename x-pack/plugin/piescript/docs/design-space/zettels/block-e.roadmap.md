---
tags: [roadmap, data, write-path, implemented]
refs:
  - adr:D-051
  - plan:block_e_write_primitives_f1e74ffb
---
# Block E — Write Primitives

Two-tier write: shard-level Engine writes + Bulk API. List literal syntax, record-to-XContent conversion.

**Depends on**: [[block-d.roadmap]]
**Enables**: [[block-f.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- subsumes: [[shard-write.data]]
- subsumes: [[write-primitive-design.data]]
- subsumes: [[index-bulk.data]]
- subsumes: [[list-literal-syntax.language]]
- subsumes: [[recordval-to-xcontent.data]]
- subsumes: [[two-tier-architecture.data]]
