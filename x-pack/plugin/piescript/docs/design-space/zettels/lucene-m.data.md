---
tags: [data, open, theoretical, concept]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# LuceneM Free Monad

[[free-monad.types]] over Lucene primitives — the "assembly language" of data access. Each operation is a constructor (building a LuceneM value does nothing; it *describes* what to do). An interpreter executes the description, managing [[searcher-lifecycle.data]] automatically.

Gives full control over every Lucene primitive:
- [[index-searcher.es-internals]] acquisition
- [[lucene-query-builders.es-internals]] compilation
- [[lucene-segments.es-internals]] iteration
- [[doc-values.es-internals]] reading
- [[lucene-collectors.es-internals]]-based push processing. This is the escape hatch for programs that interleave data access with coordination - custom merge joins
- [[segment-parallelism.data]] for user-controlled parallelism
- [[searcher-lifecycle.data]] spanning multiple coordination steps.

Block D's [[shard-read.data]] primitives (`open`/`consume`/`read`) are what LuceneM primitives will eventually compile to.

**Depends on**: [[shard-read.data]], [[free-monad.types]], [[searcher-lifecycle.data]]
**Enables**: [[data-access-hierarchy]], [[segment-parallelism.data]]
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- part-of: [[data-access-hierarchy]] — Level 3 in the data access hierarchy; not yet designed in detail
- related: [[blockloader.data]] — BlockLoader is a Lucene read optimization that LuceneM could expose
- compiles-to: [[shard-read.data]] — currently the physical primitives (Level 4) are exposed directly; LuceneM compiles to them
- related: [[index-searcher.es-internals]] — searcher acquisition is a core LuceneM operation
- related: [[lucene-segments.es-internals]] — segment-level iteration for fine-grained control
- related: [[doc-values.es-internals]] — DocValues reading for field access
- related: [[lucene-collectors.es-internals]] — push-based processing model
- related: [[lucene-query-builders.es-internals]] — query compilation within LuceneM programs
