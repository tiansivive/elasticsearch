---
tags: [language, syntax, implemented, decision]
refs:
  - adr:D-022
---
# Decimal Literal Ambiguity

The lexer requires digits after the decimal point (42.0 is valid, 42. is invalid) to avoid ambiguity with field projection syntax (42.x would be ambiguous between a decimal literal followed by identifier and an integer with field access). This is standard practice shared by Haskell, OCaml, and Rust. The rule is enforced in the [[antlr-grammar.language]] DECIMAL_LITERAL token definition.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[antlr-grammar.language]] — the DECIMAL_LITERAL token rule in the lexer grammar
