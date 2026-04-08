---
tags: [data, open, theoretical]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# LuceneM Free Monad

Free monad over Lucene primitives — the "assembly language" of data access. Each operation is a constructor (building a LuceneM value does nothing; it *describes* what to do). An interpreter executes the description, managing resource lifecycle automatically.

Gives full control over every Lucene primitive: searcher acquisition, query compilation, segment iteration, DocValues reading, Collector-based push processing. Resource management is automatic (interpreter releases searchers on completion). This is the escape hatch for programs that interleave data access with coordination logic — custom merge joins, per-segment spawn for user-controlled parallelism, searcher lifecycle spanning multiple coordination steps.

Block D's `open`/`consume`/`read` are what LuceneM primitives will eventually compile to. Once LuceneM exists, those become internal implementation details.

**Depends on**: [[shard-read.data]], [[free-monad.types]]
**Enables**: [[data-access-hierarchy.data]]
**Connections**:
- related: [[data-access-hierarchy.data]] — Level 3 in the data access hierarchy; not yet designed in detail
- related: currently the physical primitives (Level 4) are exposed directly
- related: [[blockloader.data]] — BlockLoader is a Lucene read optimization that LuceneM could expose
