---
tags: [language, syntax, concept, rejected, recursion]
refs: []
---
# Let Rec Syntax

ML-style `let rec f x = ...`. Requires a `rec` keyword, a separate `CoreLetRec` IR node
or desugaring to fix. Rejected — implicit recursion (Haskell-style) is simpler: no keyword,
no separate IR node, same semantics. The value restriction already prevents unsound
polymorphism over side effects.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[recursion.hub]]
- rejected-in-favor-of: [[implicit-recursion.design]] — implicit recursion is simpler with identical semantics
- uses: [[value-restriction.types]] — value restriction unchanged regardless of `let rec` vs implicit
- contrasts-with: [[implicit-recursion.design]] — explicit keyword vs implicit scope extension
