---
tags: [esql, types, rejected, concept, aggregation, nbe]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Agg/StripAgg Wrapper Type (Rejected)

An `Agg a` wrapper type was considered for distinguishing aggregate expressions from scalar expressions in the type system. `StripAgg` would be a type-level operator that unwraps `Agg a` to `a` in the output row of `ESQL.statsBy`. This would make the aggregate/scalar distinction statically visible: `ESQL.avg` would return `Agg Double`, not `Double`, and `statsBy` output type would apply `StripAgg` to extract the underlying types.

Rejected in favor of plain output types with ESQL runtime validation. The current approach types aggregate builtins as returning their output type directly (e.g., `ESQL.avg : a -> Double`) and relies on ESQL itself to validate that aggregates appear only in valid positions. This is less type-safe -- the type system cannot catch "aggregate used outside STATS" errors -- but avoids the complexity of a wrapper type, a strip operator, and the interaction with row operators and NbE compilation.

The [[esql-expression-wrapper.types]] zettel tracks the related tech debt: aggregate builtins produce `Symbol` where the type says `Double`.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: plain output types -- current [[esql-aggregates.esql]] implementation uses plain types
- part-of: [[esql-aggregates.esql]] -- this was a rejected design for the aggregates feature
- informs: [[esql-expression-wrapper.types]] -- the Agg wrapper was one way to address the Symbol/type divergence
- tension-with: [[nbe-compilation.esql]] -- Agg wrapper would complicate NbE; Symbol already carries the ESQL fragment regardless of wrapper
- related: [[row-operators.types]] -- StripAgg would have been another row-level operator alongside Pick/Omit/&
- related: [[f-omega-lite.types]] -- StripAgg would need to be a reducible builtin in force, like &/Pick/Omit
