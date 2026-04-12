---
tags: [language, syntax, feature, implemented]
refs:
  - adr:D-010
  - thread:language-expressiveness
---
# Match Syntax

ML-style, no `with` keyword:

```
match expr
| pattern1 -> body1
| pattern2 -> body2
| _ -> default
```

`if c then a else b` is sugar for `match c | true -> a | false -> b`. The `IfExpr` rule
stays in the grammar but elaborates to a `CoreMatch` with Boolean literal patterns.

The `|` token serves double duty: arm separator at the top level of a match, and tail
separator inside `{}` / `[]` patterns. No ambiguity — the bracketing context distinguishes
them.

**Depends on**: [[antlr-grammar.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- uses: [[pattern-types.language]] — syntax for each pattern form
