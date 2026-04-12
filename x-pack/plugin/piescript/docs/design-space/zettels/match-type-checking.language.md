---
tags: [language, types, inference, implementation, implemented]
refs:
  - thread:language-expressiveness
---
# Match Type Checking

Elaboration algorithm for `match` expressions. Uses the existing unification machinery —
no special pattern-matching logic beyond inferring pattern types.

**Algorithm:**

1. Infer scrutinee type → `τ_scrut`
2. Fresh meta for result type → `?result`
3. For each arm `| pattern -> body`:
   a. Infer pattern type → `τ_pat` (generating metas for pattern variables)
   b. Emit constraint: `τ_scrut ~ τ_pat` (unify scrutinee with pattern)
   c. Extend environment with pattern variables (their metas are now constrained by step b)
   d. Infer body type in extended env → `τ_body`
   e. Emit constraint: `?result ~ τ_body` (all branches agree on result type)
4. Result type of the match expression is `?result`

**Pattern type inference:**

- Literal `42` → `Double`
- Literal `"hello"` → `Keyword`
- Literal `true` / `false` → `Boolean`
- Variable `x` → fresh meta `?a` (bound in env for the arm body)
- Wildcard `_` → fresh meta `?a` (not bound)
- Record `{ name: n, age: 18 }` → `RecordType({ name: ?a, age: Double | ?r })` — open row
  with metas for bound fields and row tail
- Record tail `{ name: n | rest }` → same, but `rest` bound with type `RecordType(?r)`
- List `[x, y]` → `List ?a` with `x : ?a`, `y : ?a`; runtime length check
- List tail `[h | t]` → `List ?a` with `h : ?a`, `t : List ?a`

The constraint `τ_scrut ~ τ_pat` does the real work — unification solves field metas, row
tails, and element types against the scrutinee. Same unifier, same zonker, same constraint
accumulator as the rest of the elaborator.

**De Bruijn binding:** Each `VarPat` introduces a de Bruijn binding. In a record pattern
`{ name: n, age: a }`, the binding order must be deterministic — alphabetical by field name
is the natural choice (consistent, predictable). The body's de Bruijn indices reference these
bindings.

**No exhaustiveness checking in v1.** If no pattern matches at runtime, the evaluator throws
an `EvaluationException`. Exhaustiveness checking is future work — requires tracking which
patterns are covered and producing a compile-time error for incomplete matches.

**Depends on**: [[unification-algorithm.types]], [[deferred-constraints.types]], [[de-bruijn-indices.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- uses: [[unification-algorithm.types]] — pattern/scrutinee unification is standard Robinson unification
- uses: [[deferred-constraints.types]] — pattern constraints emitted to the same accumulator
- uses: [[de-bruijn-indices.language]] — pattern variables become de Bruijn bindings in the arm body
- uses: [[elaboration-architecture.types]] — pattern elaboration extends the existing bidirectional elaborator
