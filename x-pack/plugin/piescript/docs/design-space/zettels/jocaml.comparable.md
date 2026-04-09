---
tags: [theoretical, language, comparable, prior-art, decision]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: JoCaml

OCaml + [[join-calculus.coordination]] primitives (`def`, `reply`, `spawn`). Three keywords embed process calculus in an ML-family language. Closest practical precedent for what piescript does: embedding process primitives in a functional language.

- `def` / `reply` map to piescript's [[when-synchronization.coordination]]
- `spawn` maps directly to piescript's [[spawn.coordination]]
- Validates that join patterns compose naturally with [[hindley-milner.types]] inference

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- validates: [[join-calculus.coordination]] — validates the approach; join patterns as def/reply work naturally in OCaml's type system
- analogous-to: [[spawn.coordination]] — JoCaml's `spawn` maps directly to piescript's spawn
- analogous-to: [[when-synchronization.coordination]] — JoCaml's `def`/`reply` maps to piescript's `when` synchronization
- validates: [[hindley-milner.types]] — demonstrates that HM inference accommodates process primitives without special extensions
