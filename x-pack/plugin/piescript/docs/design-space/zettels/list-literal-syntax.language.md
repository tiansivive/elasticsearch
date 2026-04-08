---
tags: [syntax, language, implemented]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# List Literal Syntax

`[e1, e2, ...]` constructs a `ListVal` from element expressions. `[]` is polymorphic (`List ?a`). Elaborates to `CoreList` — the 17th `CoreExpr` variant. Elements are elaborated with a shared meta for the element type, so all elements must have the same type (unified via constraints). Added in Block E to unblock `Index.bulk` and general list construction.

**Depends on**: [[list-type.language]], [[core-ir.language]]
**Enables**: [[index-bulk.data]]
**Connections**:
- related: [[currying.language]] — list literals + partial application enable concise data construction
