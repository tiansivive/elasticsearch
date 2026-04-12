---
tags: [hub, language, syntax, concept]
refs:
  - doc:architecture.md
  - doc:vision.md
---
# Syntax

Surface syntax design space for piescript. ML-inspired core (let-bindings, lambda, match, record
literals) with piescript-specific additions (pipe operator, accessor/update sugar, coordination
primitives). The grammar is defined in ANTLR and is the source of truth for what parses.

Syntax choices are constrained by the target audience ([[target-users.principle]]) -- security
analysts and data engineers, not PL researchers. Familiarity and readability trump cleverness.
Desugaring happens during [[elaboration-architecture.types|elaboration]]; [[core-ir.language]]
is the post-sugar representation.

**Includes**: [[primops.language]], [[precedence.language]], [[shadowing.language]], [[where-clauses.language]], [[match-syntax.language]], [[antlr-grammar.language]], [[accessor-sugar.language]], [[update-sugar.language]], [[pipe-operator.language]], [[currying.language]], [[block-expressions.language]], [[list-literal-syntax.language]], [[record-spread.language]]

**Depends on**: [[antlr-grammar.language]]
**Enables**: [[core-ir.language]]
**Connections**:
- part-of: [[design-principles.hub]] -- syntax is shaped by design principles (inferred types, familiarity)
- constrains: [[elaboration-architecture.types]] -- elaboration must handle every surface form
- informs: [[target-users.principle]] -- syntax is the user-facing surface; audience shapes syntax choices
- uses: [[de-bruijn-indices.language]] -- surface names are resolved to de Bruijn indices during elaboration
- informs: [[pattern-matching.hub]] -- match syntax is a core syntactic form
- informs: [[syntax-highlighting.tooling]] -- syntax definition drives editor support
- informs: [[lsp.tooling]] -- LSP completion and navigation depend on grammar structure
