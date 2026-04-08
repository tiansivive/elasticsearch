---
tags: [theoretical, language, comparable]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: JoCaml

JoCaml: OCaml + Join Calculus primitives (def, reply, spawn). Three keywords embed process calculus in ML-family language. Closest practical precedent for what piescript does: embedding process primitives in a functional language.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: [[join-calculus.coordination]] — validates the approach; join patterns as def/reply work naturally in OCaml's type system
- related: [[spawn.coordination]] — JoCaml's `spawn` maps directly to piescript's spawn
- related: [[when-synchronization.coordination]] — JoCaml's `def`/`reply` maps to piescript's `when` synchronization
