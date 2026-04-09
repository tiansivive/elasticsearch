---
tags: [syntax, language, implemented, documentation]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:CoreList.java
  - code:piescript.elab
---
# List Literal Syntax

`[e1, e2, ...]` constructs a `ListVal` from element expressions. `[]` is polymorphic (`List ?a`). Elaborates to `CoreList` -- the 17th [[core-ir.language]] variant. Elements are elaborated with a shared [[meta-variables.types]] for the element type, so all elements must have the same type (unified via constraints). Added in Block E to unblock [[index-bulk.data]] and general list construction.

**Depends on**: [[list-type.language]], [[core-ir.language]]
**Enables**: [[index-bulk.data]]
**Connections**:
- part-of: [[block-e.roadmap]]
- complements: [[currying.language]] — list literals + partial application enable concise data construction
- uses: [[elaboration-architecture.types]] — element type unification via shared meta variable
