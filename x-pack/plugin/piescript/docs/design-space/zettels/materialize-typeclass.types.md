---
tags: [types, typeclasses, data, materialization, open, needs-design, later, concept]
refs:
  - roadmap:block-h
---
# Materialize Typeclass

A `Materialize` typeclass for output format selection: whether piescript values are delivered as
Records (row-by-row piescript values) or Pages/Blocks (columnar compute engine format). Bridges
the piescript value layer to the compute engine's columnar representation.

Distinct from [[type-driven-materialization.esql]] (which determines List vs scalar conversion
for ESQL result columns). The Materialize typeclass would be a user-facing abstraction:
`materialize : Materialize a => Page -> a` with instances for `Record`, `List Record`,
`Page` (identity), or custom output types.

**Use cases:**
- Default: materialize to `List Record` (full conversion, convenient, expensive)
- Streaming: keep as `Page` (zero conversion, work with columnar operators)
- Hybrid: materialize specific columns, leave others columnar

**Depends on**: [[typeclasses.types]], [[type-driven-materialization.esql]]
**Enables**: (none directly)
**Connections**:
- uses: [[typeclasses.types]] -- Materialize is a typeclass with instances per output format
- extends: [[type-driven-materialization.esql]] -- generalizes the current type-driven approach to a typeclass
- informs: [[materialization-boundary.data]] -- typeclass makes the boundary user-controllable
- informs: [[shard-stream.data]] -- shard stream output format could be typeclass-driven
- uses: [[page-opaque-typed.data]] -- Page is one Materialize target
- uses: [[record-type.language]] -- Record is another Materialize target
- related: [[dictionary-passing.types]] -- Materialize dictionaries carry the conversion logic
- related: [[type-stack.data]] -- Materialize operates across the three-level type stack
- informs: [[exchange-streaming.infrastructure]] -- exchange output format selection via typeclass
