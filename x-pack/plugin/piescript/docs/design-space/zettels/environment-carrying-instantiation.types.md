---
tags: [types, inference, open, concept, exploration, later]
refs:
  - adr:D-037
  - thread:type-foundations
---
# Environment-Carrying Instantiation

Proposed alternative to walk-and-substitute type instantiation.
- Instead of rewriting the type tree to replace [[rigid-variables.types]] with fresh [[meta-variables.types]], thread an environment of Rigid-to-Meta mappings through the evaluator and optimizer
- Lookups resolve through the environment, avoiding O(n) substitution walks over the type structure
- This is the type-level analogue of the "no zonking pass" principle: resolve lazily via indirection rather than eagerly rewriting

**Depends on**: [[hindley-milner.types]]
**Enables**: (none directly)
**Connections**:
- refines: [[hindley-milner.types]] — a more efficient instantiation strategy for HM inference
- alternative-to: [[resolve-deep.types]] — resolveDeep walks and substitutes; environment-carrying avoids the walk
- uses: [[rigid-variables.types]] — maps rigids to metas via environment indirection
- uses: [[meta-variables.types]] — fresh metas are the targets of the Rigid-to-Meta mappings
