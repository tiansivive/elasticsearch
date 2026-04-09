---
tags: [data, columnar, materialization, implemented, concept]
refs:
  - adr:D-054
  - code:EvalPage.java
  - code:Value.java
---
# Page as Opaque Typed Value

- `Page r` is a [[non-serializable-types.types|non-serializable]], node-local opaque type carrying the row type parameter `r` from the `Index r` / `Searcher r` context it was produced in.
- Pages hold columnar compute-engine data (real ESQL `Page` instances) but the user cannot inspect columns directly.
- Materialization to piescript values happens explicitly via `Page.toList`, converting the columnar representation to a `List {r}`.
- This user-controlled [[materialization-boundary.data|materialization boundary]] means the system never silently converts between columnar and row representations; the programmer decides where that cost is paid.

**Depends on**: [[materialization-boundary.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-g.roadmap]]
- uses: [[shard-stream.data]] -- Shard.stream produces Page values from DocRef batches
- complements: [[type-stack.data]] -- Page r is one level in the type stack (between raw Lucene data and piescript Values)
- uses: [[non-serializable-types.types]] -- PageVal is node-local only
