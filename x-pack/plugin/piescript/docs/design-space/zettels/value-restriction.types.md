---
tags: [types, implemented, polymorphism, safety]
refs:
  - adr:D-046
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Value Restriction

Only syntactic values generalize: `CoreLit`, `CoreLam`, `CoreVar`, `CoreFree`, `CoreRecord` (if all fields are values), `CoreTypeAbs` (if body is value). Non-values (`CoreApp`, `CoreSpawn`, `CoreSend`, etc.) stay monomorphic. Prevents unsound polymorphism from `let ch = spawn!` generalizing `Channel ?a` to `∀a. Channel a`.

**Depends on**: [[hindley-milner.types]], [[rigid-variables.types]]
**Enables**: [[spawn-bang.coordination]]
**Connections**:
- related: Wright (1995), adopted by OCaml/SML — conservative but safe
- related: future: relaxed generalization for partially-applied builtins
- related: [[binding-levels.types]] — value restriction is an additional guard on top of binding-level generalization
- related: [[effect-systems.types]] — value restriction is a crude syntactic effect approximation; explicit effects would be principled
