---
tags: [types, implemented, inference, concept, ready, later]
refs:
  - adr:D-005
  - adr:D-032
  - code:Unifier.java
  - code:piescript.elab
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Zonker

The zonker is a `Map<Integer, MonoType>` (metavar ID -> solution):
- [[unification-algorithm.types]] writes solutions to it
- There is NO zonking pass that rewrites the AST -- [[meta-variables.types]] resolve by chain-following lookup at point of use
- This keeps the [[core-ir.language]] immutable and avoids repeated substitution cost
- [[force-threading.types]] subsumes the old `zonkOrKeep` (D-053)

**Depends on**: [[hindley-milner.types]]
**Enables**: [[f-omega-lite.types]]
**Connections**:
- constrains: no-substitution principle is a core design invariant
- tension-with: [[resolve-deep.types]] — `resolveDeep` still used by `CorePrinter` (tech debt)
- implements: [[meta-variables.types]] — the zonker is the union-find backing store for metas
- complements: [[unification-algorithm.types]] — unification writes solutions to the zonker
- evolved-into: [[force-threading.types]] — force subsumes the old zonkOrKeep (D-053)
