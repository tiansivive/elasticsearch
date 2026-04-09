---
tags: [performance, theoretical, reference]
refs:
  - doc:references.md
---
# Nested Data Parallelism

Blelloch's NESL: nested parallel operations are flattened into efficient flat parallelism via a flattening transformation. The programmer writes naturally nested parallel code (map over collections of collections); the compiler transforms it into flat vectors with segment descriptors, enabling GPU-style SIMD execution.

- Relevant to piescript's nested [[spawn.coordination]]/[[when-synchronization.coordination]] patterns
- Potential vectorization of [[list-type.language]] collection operations
- Theoretical foundation for automatic parallelization of nested collection operations

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- related: [[map-reduce.distributed]] — nested data parallelism is the theoretical foundation for automatic parallelization of nested collection operations
- related: [[exchange-streaming.infrastructure]] — exchange streaming flattens piescript's distributed computation graph similarly to how NESL flattens nested parallelism
