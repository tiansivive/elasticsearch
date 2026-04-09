---
tags: [performance, compilation, open, concept, feature]
refs: []
---
# Painless Push-Down

Compile piescript closures TO [[painless.comparable|Painless]] via closure conversion. Distinct from [[ffi-painless.external|FFI]] (calling Painless from piescript) -- this is using Painless as a compilation target. For example, `Write.update shard id (fn doc -> ...)` where the lambda compiles to a Painless script that runs natively on the shard, avoiding [[serialization.infrastructure|serialization]] round-trips for update-by-query operations.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- contrasts-with: [[ffi-painless.external]] — FFI calls Painless; push-down compiles piescript to Painless
- related: [[code-mobility.coordination]] — push-down is a form of code mobility where the target runtime is Painless
- related: [[painless.comparable]] — extends the Painless relationship from coexistence to compilation target
