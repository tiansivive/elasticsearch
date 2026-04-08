---
tags: [esql, implemented]
refs:
  - adr:D-052
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Combinators

1:1 mapping from piescript combinators to ESQL commands: `ESQL.from`→`FROM`, `ESQL.where`→`WHERE`, `ESQL.eval`→`EVAL`, `ESQL.keep`→`KEEP`, `ESQL.drop`→`DROP`, `ESQL.limit`→`LIMIT`, `ESQL.sort`→`SORT ASC`, `ESQL.sortDesc`→`SORT DESC`, `ESQL.rename`→`RENAME`, `ESQL.explain`→debug. Schema-preserving combinators use one row type var; schema-changing use two.

**Depends on**: [[esql-compilation.esql]]
**Enables**: [[esql-aggregates.esql]]
**Connections**:
- related: [[f-omega-lite.types]] — `ESQL.keep`/`drop` updated from `List Keyword` to closure-based with `Pick`/`Omit` output types (D-053)
- related: [[dotted-field-paths.esql]] — dotted field path fix for nested fields
- related: [[dissect.esql]] — DISSECT is an ESQL combinator
- related: [[grok.esql]] — GROK is an ESQL combinator
- related: [[join.esql]] — LOOKUP JOIN is a future combinator addition
