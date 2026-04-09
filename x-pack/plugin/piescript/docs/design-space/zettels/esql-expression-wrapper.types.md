---
tags: [types, esql, tech-debt, debugging, task, known-issue]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Expression Wrapper

Aggregate builtins produce `Symbol` where the type says `Double` -- a type-level lie that propagates silently.
- Should wrap in an ESQL expression type for static safety
- The [[nbe-dual-pattern.types]] approach means types and values diverge during [[symbol-partial-evaluation.esql]]

**Depends on**: [[nbe-compilation.esql]], [[esql-aggregates.esql]]
**Enables**: (none directly)
**Connections**:
- constrains: [[esql-aggregates.esql]] — not blocking correctness (ESQL validates at execution time) but prevents the type system from catching invalid aggregate usage
- tension-with: [[nbe-dual-pattern.types]] — the NbE approach causes types and values to diverge during symbolic evaluation
