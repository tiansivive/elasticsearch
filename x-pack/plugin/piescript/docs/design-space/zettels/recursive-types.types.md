---
tags: [types, open, control-flow, task, concept, needs-design, later, recursion]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
  - thread:type-foundations
---
# Recursive Types

Not currently supported. Two approaches:

- **Equi-recursive**: infinite type trees, relax occurs check.
- **Iso-recursive**: explicit fold/unfold, preserve occurs check.

Would enable: recursive data structures (linked lists, trees), recursive functions (`let rec` / fix), and recursive type aliases. The occurs check in the [[unification-algorithm.types|unifier]] currently rejects all recursive types.

**Depends on**: [[unification-algorithm.types]]
**Enables**: [[recursion.hub]]
**Connections**:
- related: [[adts.types]] — iso-recursive approach requires ADTs (fold/unfold are constructors)
- tension-with: [[stack-depth.language]] — either approach makes stack depth critical
- complements: [[recursion.hub]] — recursive types and recursive functions are co-dependent features
