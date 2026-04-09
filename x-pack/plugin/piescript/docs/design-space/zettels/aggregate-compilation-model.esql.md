---
tags: [esql, compilation, aggregation, decision, known-issue]
refs:
  - adr:D-052
  - adr:D-053
  - code:EvalBuiltins.java
---
# Aggregate Compilation Model

Why `Agg a` typed aggregate descriptors were rejected in favor of Symbol-based aggregate compilation:

- An `Agg Double` wrapper type would give static safety (the type system could distinguish "a Double value" from "a Double that came from an aggregate") but it would infect the entire type system with aggregate awareness
- Instead, aggregate builtins (stats, avg, sum) produce `Symbol` values where the piescript type says `Double` — a deliberate type-level lie
- The type tracks the output type of the aggregate, but the runtime value is a Symbol carrying the ESQL fragment `"AVG(field)"`
- The lie is safe because ESQL validates the compiled query at execution time, but piescript's type checker cannot catch invalid aggregate compositions statically

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[block-f.roadmap]]
- tension-with: [[esql-expression-wrapper.types]] — the expression wrapper proposal would add static safety but faces the same type-infection tradeoff
- uses: [[nbe-compilation.esql]] — aggregates compile via the same NbE Symbol mechanism as expressions
- uses: [[f-omega-lite.types]] — kind-level computation determines aggregate output types
