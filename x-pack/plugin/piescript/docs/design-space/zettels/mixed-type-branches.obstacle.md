---
tags: [language, types, pattern-matching, obstacle, open, recursion, iteration]
refs:
  - thread:language-expressiveness
---
# Mixed-Type Branches

Standard HM match/if requires all arms to have the same codomain type. This prevents
mixing normal returns with `repeat` in the same match:

```piescript
-- FAILS: Keyword ~ Repeat Double
loop 0
| n -> match (n > 10) | true -> "done" | false -> repeat (n + 1)
```

This is a well-understood limitation of the `Repeat a` TCon approach ([[repeat-tcon.types]]).
The program is valid at runtime (repeat jumps, "done" returns) but the type system can't
express the disjunction.

## Future solutions (ordered by priority)

1. **Pattern guards** ([[pattern-guards.language]]) — separate the condition into the arm
   pattern: `| n when n > 10 -> "done"` / `| n -> repeat (n + 1)`. Each arm has one type.
   No type system changes. Highest priority.

2. **Variant-based arm typing** ([[variant-arm-typing.language]]) — internally wrap arm
   codomains in `< #return: R | #repeat: S >`. Both arms have the same variant type.
   Requires row-based Variants.

3. **ATP-inspired dual-type tracking** — `repeat` acts as both `R` and `Repeat S` depending
   on context. Most general but not fully designed. See [[repeat-design-exploration.note]].

4. **Union types** — TypeScript-style `R | Repeat S`. Powerful but complex in HM.

**Depends on**: [[repeat-tcon.types]]
**Enables**: (none directly — this is a problem, not a solution)
**Connections**:
- caused-by: [[repeat-tcon.types]] — the Repeat a approach creates this limitation
- caused-by: [[unification-algorithm.types]] — Robinson unification can't express disjunctions
- solved-by: [[pattern-guards.language]] (future, highest priority)
- solved-by: [[variant-arm-typing.language]] (future, after Variants)
- informs: [[answer-type-polymorphism.types]] — ATP was explored as a potential solution
- informs: [[exhaustiveness-checking.types]] — mixed-type branches interact with exhaustiveness
- tension-with: [[fused-loop-match.language]] — limits the expressiveness of loop arms
