---
tags: [meta, principle, language, distributed, decision]
refs:
  - doc:vision.md
---
# Functional by Default, Distributed by Design

Immutable bindings, expressions over statements, pattern matching over if-else
chains. Purity is not an aesthetic choice — it is what makes distributed
execution safe. Referential transparency guarantees that shipping a closure to
a remote node produces the same result as evaluating it locally. The language's
only effects are coordination primitives (`spawn`, `when`, channel
communication), which are modeled explicitly.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- implements: [[purity.language]] — purity enforcement is the mechanism
- implements: [[explicit-distribution.language]] — explicit topology control is the distribution model
- related: [[code-mobility.coordination]] — purity enables safe code shipping
