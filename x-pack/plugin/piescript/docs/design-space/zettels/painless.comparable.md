---
tags: [comparable, es-internals, prior-art, decision]
refs:
  - vision:what-piescript-is-not
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Painless

Both run inside ES. Painless is imperative, untyped, single-document scope. No distribution, no composition with [[esql-compilation.esql|ESQL]]. Piescript is functional, typed, multi-document, distributed. They serve different use cases and may coexist.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- complements: [[ffi-painless.external]] — FFI layer reuses Painless's security allowlist
- contrasts-with: [[purity.language]] — Painless is imperative with mutable state; piescript enforces purity and referential transparency
- contrasts-with: [[explicit-distribution.language]] — Painless is single-document scoped; piescript is multi-document and distributed
- informs: [[painless-push-down.performance]] — push-down for writes is a future possibility (compile piescript closures to Painless for atomic per-document updates)
