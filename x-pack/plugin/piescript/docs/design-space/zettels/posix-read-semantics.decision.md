---
tags: [data, language, decided, iteration, concept, technique]
refs:
  - adr:D-050
  - code:EvalShard.java
---
# POSIX Read Semantics for Shard.consume

`Shard.consume` returns fewer results than requested to signal end-of-data, following POSIX `read()` semantics. When `read(fd, buf, n)` returns fewer than `n` bytes, the caller knows the stream is exhausted. Similarly, when `Shard.consume searcher n` returns a list shorter than `n`, the caller knows iteration is complete.

This design avoids needing `Maybe`/`Option` types (which piescript lacks -- no ADTs yet) for end-of-iteration signaling. Instead of returning `Some(results)` or `None`, the primitive returns a `List` whose length encodes the termination signal. The fused loop-match construct detects this via pattern matching on list length.

The tradeoff: sentinel-based signaling is less type-safe than `Maybe`. When ADTs arrive, `Shard.consume` could return `Option (List DocRef)` or use a dedicated iterator protocol. For now, POSIX read semantics are a pragmatic workaround.

**Depends on**: [[shard-read.data]]
**Enables**: (none directly)
**Connections**:
- implements: [[shard-read.data]] -- `Shard.consume` uses this signaling convention
- workaround-for: [[adts.types]] -- absence of sum types forces length-based end-of-stream signaling
- informs: [[fused-loop-match.language]] -- loop termination relies on pattern-matching the returned list length
- informs: [[recursion.hub]] -- iteration termination pattern for shard-level data access
- complements: [[composite-paging.data]] -- composite paging uses the same consume-until-short pattern
- uses: [[list-type.language]] -- the returned `List DocRef` encodes both data and termination signal
- analogous-to: POSIX `read()` -- same fewer-than-requested convention for end-of-stream
- tension-with: [[result-types.types]] -- future Result/Option types would replace this convention
