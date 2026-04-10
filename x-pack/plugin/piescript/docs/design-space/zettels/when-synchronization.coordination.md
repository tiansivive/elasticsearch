---
tags: [coordination, language, concurrency, implemented, pi-calculus, documentation]
refs:
  - adr:D-040
  - adr:D-041
  - code:Whens.java
  - code:EvalCoordination.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# When Synchronization

`when (ch1 x) & (ch2 y) -> body` synchronizes on one or more [[channels.infrastructure]]:
- Uses a [[positional-collector.coordination]] (`AtomicArray` + `CountDown`) to preserve [[de-bruijn-indices.language]] binding order
- The surface keyword is `when` (not `join`) to avoid SQL/ESQL JOIN collision (D-041)
- Only works on local channels -- remote channels rejected at runtime
- Part of the [[join-calculus.coordination]] coordination model

**Depends on**: [[join-calculus.coordination]], [[channels.infrastructure]], [[de-bruijn-indices.language]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- part-of: [[block-a.roadmap]]
- uses: [[positional-collector.coordination]] — `GroupedActionListener` was rejected because it stores by arrival order, not binding order (D-041 S3)
- replaces: [[par-blocks.coordination]] — when provides strictly more expressive multi-way synchronization than par blocks
- complements: [[spawn.coordination]] — spawn+when is the core coordination pattern; spawn creates channels that when synchronizes on
- related: [[pattern-reuse.language]] — `when` channel bindings currently bind a single variable per channel; pattern destructuring (`when (ch { name: n }) -> ...`) would desugar to wrapping the body in a `CoreMatch` per binding
