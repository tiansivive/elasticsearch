---
tags: [roadmap, performance, open]
refs: []
---
# Deferred: Typeclass-Driven Push-Down

Typeclass-driven push-down to Lucene via `Query a` instances and compiling-to-categories. Deprioritized because the typeclass approach (specialize `filter`/`map` per backend instance) is more general than ESQL text compilation.

**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- subsumes: [[push-down-compilation.performance]]
- subsumes: [[query-typeclass.data]]
- subsumes: [[compiling-to-categories.performance]]
- subsumes: [[bird-meertens.types]]
