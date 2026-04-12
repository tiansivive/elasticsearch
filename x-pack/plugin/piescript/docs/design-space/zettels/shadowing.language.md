---
tags: [language, syntax, open, question, concept]
refs: []
---
# Variable Shadowing

Variable shadowing: allowed (OCaml-style) or disallowed? Currently allowed -- the
[[de-bruijn-indices.language|de Bruijn]] environment representation naturally shadows: an inner
binding at a lower index masks an outer binding at a higher index. The elaborator does not warn
or error when a new binding shadows an existing one.

This was not explicitly decided as a design choice -- it falls out of the representation.

**Risk:** silent bugs when users accidentally shadow a binding. Particularly dangerous in
[[block-expressions.language|block expressions]] where sequential `let` bindings can
inadvertently reuse a name. Also relevant in [[match-syntax.language|match arms]] where pattern
variables can shadow outer bindings.

**Mitigation options** (not yet designed):
- Lint warning (not error) on shadow -- informational, no breakage
- Explicit `let shadow x = ...` syntax to indicate intent
- IDE highlighting (via [[lsp.tooling]]) of shadowed bindings

**Depends on**: [[de-bruijn-indices.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[syntax.hub]]
- uses: [[de-bruijn-indices.language]] -- shadowing falls out naturally from index-based lookup
- informs: [[block-expressions.language]] -- sequential let-bindings can shadow earlier bindings in the same block
- informs: [[match-syntax.language]] -- pattern variables can shadow outer bindings
- informs: [[target-users.principle]] -- shadowing bugs hurt non-expert users disproportionately
- informs: [[lsp.tooling]] -- IDE could highlight or warn on shadowed variables
- related: [[elaboration-architecture.types]] -- elaboration resolves names to indices; shadowing happens here
