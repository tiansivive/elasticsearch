---
tags: [theoretical, concept]
refs:
  - doc:references.md
---
# Church-Rosser Property

Confluence property: if a term can be rewritten two ways, both paths lead to the same normal form. Lambda calculus has this (Church-Rosser theorem). Guarantees evaluation order doesn't affect results. Piescript's purity means pure expressions should satisfy Church-Rosser. Coordination effects (spawn/when/send) break it -- order of channel operations matters.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[term-rewriting.theory]]
- uses: [[purity.language]] -- purity is what gives Church-Rosser for pure expressions
- tension-with: [[join-calculus.coordination]] -- coordination effects are non-confluent; order matters
- informs: [[f-omega-lite.types]] -- type-level reduction should be confluent
- informs: [[knuth-bendix.theory]] -- K-B completion achieves confluence
