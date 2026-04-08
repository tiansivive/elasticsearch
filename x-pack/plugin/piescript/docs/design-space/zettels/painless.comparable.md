---
tags: [comparable, es-internals]
refs:
  - vision:what-piescript-is-not
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Painless

Both run inside ES. Painless is imperative, untyped, single-document scope. No distribution, no composition with ESQL. Piescript is functional, typed, multi-document, distributed. They serve different use cases and may coexist.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: [[ffi-painless.external]] — FFI layer reuses Painless's security allowlist
- related: Painless push-down for writes is a future possibility (compile piescript closures to Painless for atomic per-document updates)
