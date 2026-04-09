---
tags: [language, syntax, implemented, documentation]
refs:
  - adr:D-022
  - adr:D-033
  - code:PiescriptLexer.g4
  - code:PiescriptAntlrParser.g4
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ANTLR Grammar

Lexer and parser grammar. `UPPER_IDENT`/`LOWER_IDENT` split (D-033) for type variable convention. `DECIMAL_LITERAL` requires digit after dot (D-022). `ESQL_MODE` lexer mode for query syntax. `SpawnExpr`/`WhenExpr` rules for coordination. `PiescriptParser.java` is the entry point. Grammar is the source of truth for syntax.

**Depends on**: (none)
**Enables**: [[core-ir.language]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- informs: [[core-ir.language]] — parsing produces CST; elaboration converts to Core IR
- constrains: [[ident-case-convention.language]] — grammar enforces case conventions at the syntax level, not the elaborator
- enables: [[type-annotations.types]] — UPPER_IDENT/LOWER_IDENT split enables type annotation parsing
- informs: [[elaboration-architecture.types]] — CST from ANTLR is consumed by the elaborator
- constrains: [[decimal-literal-ambiguity.language]] — DECIMAL_LITERAL token rule lives in this grammar
