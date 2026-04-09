---
tags: [esql, implemented, documentation]
refs:
  - adr:D-052
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Combinators

1:1 mapping from piescript combinators to ESQL commands: `ESQL.from`->`FROM`, `ESQL.where`->`WHERE`, `ESQL.eval`->`EVAL`, `ESQL.keep`->`KEEP`, `ESQL.drop`->`DROP`, `ESQL.limit`->`LIMIT`, `ESQL.sort`->`SORT ASC`, `ESQL.sortDesc`->`SORT DESC`, `ESQL.rename`->`RENAME`, `ESQL.explain`->debug.
- Schema-preserving combinators use one [[row-polymorphism.types]] var; schema-changing use two
- Compiled via [[nbe-compilation.esql]] (T-LINQ style)

**Depends on**: [[esql-compilation.esql]]
**Enables**: [[esql-aggregates.esql]]
**Connections**:
- part-of: [[block-f.roadmap]]
- uses: [[f-omega-lite.types]] — `ESQL.keep`/`drop` updated from `List Keyword` to closure-based with `Pick`/`Omit` output types (D-053)
- uses: [[dotted-field-paths.esql]] — dotted field path fix for nested fields
- subsumes: [[dissect.esql]] — DISSECT is an ESQL combinator
- subsumes: [[grok.esql]] — GROK is an ESQL combinator
- subsumes: [[join.esql]] — LOOKUP JOIN is a future combinator addition
