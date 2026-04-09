---
tags: [language, syntax, implemented, decision, documentation]
refs:
  - adr:D-033
  - adr:D-028
  - code:PiescriptLexer.java
---
# Ident Case Convention

[[antlr-grammar.language]] lexer splits identifiers by case: `UPPER_IDENT` for type constructors, `LOWER_IDENT` for variables.
- Enforces the type-variable convention (D-028) at the grammar level rather than the [[elaboration-architecture.types]]
- Means `a -> a` and `List a` are structurally unambiguous in the parser

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[antlr-grammar.language]] — case split is a lexer-level rule
- implements: [[rigid-variables.types]] — convention makes Rigids parseable
