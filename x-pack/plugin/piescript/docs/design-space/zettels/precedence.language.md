---
tags: [language, syntax, operator, implemented, concept]
refs:
  - code:PiescriptAntlrParser.g4
  - code:PiescriptLexer.g4
---
# Operator Precedence

Operator precedence levels in the ANTLR grammar, from lowest to highest:

1. **Pipe** `|>` -- lowest precedence, enables left-to-right composition
2. **Boolean** `&&`, `||` -- logical connectives
3. **Comparison** `<`, `>`, `<=`, `>=`, `==`, `!=` -- relational operators
4. **Additive** `+`, `-` -- addition and subtraction
5. **Multiplicative** `*`, `/`, `%` -- multiplication, division, modulo
6. **Unary** `-`, `!` -- prefix negation and logical not
7. **Application** (juxtaposition) -- highest precedence, `f x` is function application

Precedence is encoded structurally in the ANTLR grammar via rule nesting -- higher-precedence
operators are nested deeper in the grammar rules. This is the standard technique for
precedence-climbing in recursive descent parsers.

**Depends on**: [[antlr-grammar.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[syntax.hub]]
- implements: [[antlr-grammar.language]] -- precedence is encoded in the grammar rule structure
- uses: [[pipe-operator.language]] -- pipe is explicitly lowest precedence so pipelines compose naturally
- uses: [[primops.language]] -- precedence governs how primop expressions are parsed
- informs: [[elaboration-architecture.types]] -- elaboration receives a pre-precedence-resolved CST from the parser
