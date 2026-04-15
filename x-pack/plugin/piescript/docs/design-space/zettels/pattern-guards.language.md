---
tags: [language, syntax, control-flow, pattern-matching, open, needs-design, next]
refs:
  - thread:language-expressiveness
---
# Pattern Guards

Guards on match and loop arms: `| pat when cond -> body`. The guard expression is evaluated
after the pattern matches but before the arm body. If the guard is false, the next arm is
tried.

Solves the conditional-repeat problem without mixed-type branches:

```piescript
loop { acc: [], key: "" }
| { acc, key } when List.isEmpty (query_page key) -> acc
| { acc, key } -> repeat { acc: acc ++ query_page key, key: ... }
```

Without guards, this requires either separate patterns that distinguish the cases structurally,
or mixed-type branches (which fail with `Repeat a` TCon approach). Guards decouple the
condition from the pattern, making loop arms more expressive.

Higher priority than [[variant-arm-typing.language]] for solving the conditional-repeat
problem — simpler, no type system changes needed, familiar syntax (Haskell, Erlang, OCaml).

**Depends on**: [[pattern-matching.hub]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- solves: [[mixed-type-branches.obstacle]] — guards avoid mixed-type branches entirely by separating conditions into distinct arms
- enhances: [[fused-loop-match.language]] — conditional repeat becomes natural with guarded arms
- enhances: [[match-syntax.language]] — extends the arm syntax with `when` clause
- uses: [[match-type-checking.language]] — guard is elaborated as Boolean, same type checking
- informs: [[exhaustiveness-checking.types]] — guards complicate exhaustiveness analysis (guard may always be false)
- contrasts-with: [[variant-arm-typing.language]] — guards solve the problem at the syntax level; variants solve it at the type level
