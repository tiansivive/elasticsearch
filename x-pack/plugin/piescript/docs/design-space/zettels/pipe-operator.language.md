---
tags: [language, implemented, syntax, documentation]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Elaborator.java
---
# Pipe Operator

The `|>` pipe operator is syntactic sugar: `x |> f` desugars to `f x`. Enables left-to-right data flow composition: `ESQL.from idx |> ESQL.where pred |> ESQL.limit 10`. Desugared during [[elaboration-architecture.types|elaboration]] -- no pipe node in [[core-ir.language|Core IR]]. Combined with [[currying.language|currying]], creates a natural pipeline style for [[esql-combinators.esql|ESQL combinators]] and list operations.

**Depends on**: [[currying.language]]
**Enables**: [[esql-combinators.esql]]
**Connections**:
- complements: [[currying.language]] — pipe + currying combination is what makes the combinator API ergonomic
- inspired-by: standard in ML-family languages (F#, Elm, Elixir)
