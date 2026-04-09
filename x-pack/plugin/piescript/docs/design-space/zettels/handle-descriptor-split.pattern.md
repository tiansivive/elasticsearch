---
tags: [pattern, safety, serialization, implemented, concept]
refs:
  - adr:D-050
  - adr:D-051
  - adr:D-054
---
# Handle-Descriptor Split

The pattern of separating a [[serialization.infrastructure]] descriptor (can travel in [[closure-val.language]], cross node boundaries) from a [[non-serializable-types.types]] node-local handle that provides actual I/O capability.
- `Exchange r` is a descriptor; `Sink r` and `Source r` are node-local handles instantiated from it (see [[exchange-streaming.infrastructure]])
- `Searcher`/`DocRef` are descriptors acquired from an `IndexSearcher` handle (see [[shard-read.data]])
- `Writer` wraps an `IndexShard` handle (see [[shard-write.data]])
- The descriptor carries enough information to reconstruct or locate the handle on the correct node, but holds no JVM resources itself
- This pattern is what makes piescript's [[code-mobility.coordination]] safe: closures capture descriptors freely, and the type system (future [[local-kind.types]]) or runtime wire boundary prevents handles from escaping their node

**Depends on**: [[serialization.infrastructure]], [[purity.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-g.roadmap]]
- example-of: [[exchange-streaming.infrastructure]] — Exchange r vs Sink/Source is the canonical instance
- example-of: [[shard-read.data]] — Searcher/DocRef vs IndexSearcher
- example-of: [[shard-write.data]] — Writer vs IndexShard
- uses: [[non-serializable-types.types]] — non-serializable types are the "handle" side of this split
- prerequisite-for: [[local-kind.types]] — the Local kind would enforce the handle/descriptor boundary at the type level
- complements: [[code-mobility.coordination]] — code mobility is safe because closures capture descriptors, not handles
