---
tags: [types, implemented, inference]
refs:
  - adr:D-005
  - adr:D-032
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Zonker

The zonker is a `Map<Integer, MonoType>` (metavar ID → solution). Unification writes solutions to it. There is NO zonking pass that rewrites the AST — metavars resolve by chain-following lookup at point of use. This keeps the Core IR immutable and avoids repeated substitution cost. `force` subsumes the old `zonkOrKeep` (D-053).

**Depends on**: [[hindley-milner.types]]
**Enables**: [[f-omega-lite.types]]
**Connections**:
- related: no-substitution principle is a core design invariant
- related: [[resolve-deep.types]] — `resolveDeep` still used by `CorePrinter` (tech debt)
- related: [[meta-variables.types]] — the zonker is the union-find backing store for metas
- related: [[unification-algorithm.types]] — unification writes solutions to the zonker
- related: [[force-threading.types]] — force subsumes the old zonkOrKeep (D-053)
