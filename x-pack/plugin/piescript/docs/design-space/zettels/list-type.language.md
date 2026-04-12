---
tags: [language, implemented, syntax, documentation]
refs:
  - adr:D-043
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Value.java
  - code:EvalBuiltins.java
---
# List Type

`TCon("List")` type constructor with `ListVal(List<Value>)` runtime representation. Renamed from `Stream`/`StreamVal` (D-043) to reflect finite, eager, in-memory semantics. "Stream" reserved for future lazy/[[exchange-streaming.infrastructure]]-backed streaming. List literal syntax `[e1, e2, ...]` via [[list-literal-syntax.language]] `CoreList`.

- Builtins: `List.map`, `filter`, `reduce`, `head`, `tail`, `length`, `isEmpty` via [[prelude.language]]
- Stack-safe via [[cps-evaluation.language]] `SubscribableListener` chaining

**Depends on**: (none)
**Enables**: [[index-bulk.data]]
**Connections**:
- part-of: [[block-b.roadmap]]
- uses: [[prelude.language]] — builtins: `List.map`, `filter`, `reduce`, `head`, `tail`, `length`, `isEmpty`
- uses: [[cps-evaluation.language]] — `map`/`filter`/`reduce` via `SubscribableListener` chaining for stack safety
- complements: [[list-literal-syntax.language]] — syntax for constructing List values
- complements: [[iterative-streaming.language]] — iterative streaming enables lazy processing over List-like sequences
- contrasts-with: [[stream-a.language]] — List is data (eager, finite); the reserved Stream is codata (lazy, potentially infinite). D-043 reserves "Stream" for this future type.
- contrasts-with: [[codata.types]] — List is an ADT (initial algebra); Stream would be codata (final coalgebra)
