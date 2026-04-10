---
tags: [paper-trail, data, columnar, streaming, performance]
refs:
  - session:67a8c7c5-6f6e-49b4-8e96-470508de6862
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Compute Engine Integration Session

Detailed discussion on integrating ESQL's compute engine with piescript. Key decisions: expose primitives individually (not wrapped in high-level scan), piescript orchestrates exchange setup via channels. Blocks are I/O containers not computation units — piescript handles computation, Blocks handle efficient columnar reads. Whole-pure-fragment bytecode compilation (not delimited by operator type).

**Connections**:
- produced: [[exchange-streaming.infrastructure]] — exchange design emerged here
- produced: [[shard-stream.data]] — Shard.stream replacing Shard.read for batches
- produced: [[materialization-boundary.data]] — user-controlled materialization
- informs: [[bytecode-compilation.performance]] — whole-fragment compilation insight
- informs: [[block-g-impl.session]] — this design session preceded Block G implementation
