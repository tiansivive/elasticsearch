---
tags: [types, implemented, polymorphism, safety, decision, concept]
refs:
  - adr:D-046
  - code:Polymorphism.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Value Restriction

Only syntactic values generalize:
- `CoreLit`, `CoreLam`, `CoreVar`, `CoreFree`, `CoreRecord` (if all fields are values), `CoreTypeAbs` (if body is value)
- Non-values (`CoreApp`, `CoreSpawn`, `CoreSend`, etc.) stay monomorphic
- Prevents unsound polymorphism from `let ch = spawn!` generalizing `Channel ?a` to `forall a. Channel a`
- Interacts with [[spawn-bang.coordination]] and [[channels.infrastructure]] since channel creation is effectful

**Depends on**: [[hindley-milner.types]], [[rigid-variables.types]]
**Enables**: [[spawn-bang.coordination]]
**Connections**:
- part-of: [[block-c.roadmap]]
- inspired-by: Wright (1995), adopted by OCaml/SML — conservative but safe
- extends: future: relaxed generalization for partially-applied builtins
- refines: [[binding-levels.types]] — value restriction is an additional guard on top of binding-level generalization
- alternative-to: [[effect-systems.types]] — value restriction is a crude syntactic effect approximation; explicit effects would be principled
- constrains: [[channels.infrastructure]] — channel creation is effectful, so `let ch = spawn!` never generalizes
