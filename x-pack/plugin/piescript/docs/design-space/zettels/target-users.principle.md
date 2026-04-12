---
tags: [principle, meta, concept]
refs:
  - doc:vision.md
  - doc:architecture.md
---
# Target Users

Security analysts, observability engineers, data engineers. Not PL researchers. Not
Haskell/OCaml experts. Users who currently write Painless scripts, Watcher JSON, Transform
configs, and ESQL queries.

This audience constraint shapes everything:

- **Syntax:** no exposed fix combinator, no shift/reset, no explicit forall. Match syntax must
  feel familiar (ML-style, not dependent-type-style). Pipe operator for left-to-right flow.
- **Abstraction level:** explicit is better than clever. A user should be able to read a
  piescript program without knowing lambda calculus. Named functions over point-free style.
- **Error messages:** type errors must explain what went wrong in domain terms ("field 'name'
  not found in the record"), not type theory terms ("row unification failure").
- **Documentation:** examples use security/observability scenarios (risk scoring, alert
  correlation, SLO computation), not abstract data structures.
- **Progressive disclosure:** basic pipelines are simple; advanced features (typeclasses,
  coordination, distribution) are available but not required for common use cases.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- constrains: [[recursion.hub]] -- no exposed fix combinator; users write named recursive functions or loop-match
- constrains: [[delimited-continuations.hub]] -- shift/reset too abstract for target audience; effects must be implicit
- informs: [[pattern-matching.hub]] -- match syntax must feel familiar to non-PL-experts
- informs: [[syntax.hub]] -- syntax choices optimized for readability by target audience
- informs: [[type-errors.types]] -- error messages must speak the user's domain language
- informs: [[type-aliases.language]] -- aliases make types readable for non-experts
- constrains: [[higher-rank.types]] -- rank-2+ requires annotations; keep implicit where possible
- informs: [[value-proposition.principle]] -- target users define the value proposition
- informs: [[painless.comparable]] -- Painless users are a primary migration audience
- informs: [[dev-endpoint.tooling]] -- dev endpoint UX must serve non-expert users
- related: [[extraction-cliff.external]] -- target users are the ones hitting the extraction cliff today
