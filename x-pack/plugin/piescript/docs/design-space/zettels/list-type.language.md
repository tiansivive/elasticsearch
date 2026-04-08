---
tags: [language, implemented, syntax]
refs:
  - adr:D-043
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# List Type

`TCon("List")` type constructor with `ListVal(List<Value>)` runtime representation. Renamed from `Stream`/`StreamVal` (D-043) to reflect finite, eager, in-memory semantics. "Stream" reserved for future lazy/Exchange-backed streaming. List literal syntax `[e1, e2, ...]` via `CoreList`.

**Depends on**: (none)
**Enables**: [[index-bulk.data]]
**Connections**:
- related: [[prelude.language]] — builtins: `List.map`, `filter`, `reduce`, `head`, `tail`, `length`, `isEmpty`
- related: [[cps-evaluation.language]] — `map`/`filter`/`reduce` via `SubscribableListener` chaining for stack safety
