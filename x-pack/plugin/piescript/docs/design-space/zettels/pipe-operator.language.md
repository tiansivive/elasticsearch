---
tags: [language, implemented, syntax]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Pipe Operator

The `|>` pipe operator is syntactic sugar: `x |> f` desugars to `f x`. Enables left-to-right data flow composition: `ESQL.from idx |> ESQL.where pred |> ESQL.limit 10`. Desugared during elaboration -- no pipe node in Core IR. Combined with currying, creates a natural pipeline style for ESQL combinators and list operations.

**Depends on**: [[currying.language]]
**Enables**: [[esql-combinators.esql]]
**Connections**:
- related: [[currying.language]] — pipe + currying combination is what makes the combinator API ergonomic
- related: standard in ML-family languages (F#, Elm, Elixir)
