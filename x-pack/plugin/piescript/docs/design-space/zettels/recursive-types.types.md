---
tags: [types, open, control-flow]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Recursive Types

Not currently supported. Equi-recursive (infinite type trees, relax occurs check) vs iso-recursive (explicit fold/unfold, preserve occurs check). Would enable: recursive data structures (linked lists, trees), recursive functions (let rec / fix), and recursive type aliases. The occurs check in the unifier currently rejects all recursive types.

**Depends on**: [[unification-algorithm.types]]
**Enables**: [[recursion.language]]
**Connections**:
- related: [[adts.types]] — iso-recursive approach requires ADTs (fold/unfold are constructors)
- related: [[stack-depth.language]] — either approach makes stack depth critical
- related: [[recursion.language]] — recursive types and recursive functions are co-dependent features
