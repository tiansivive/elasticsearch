---
tags: [types, esql, decided, implemented, primitives, concept]
refs:
  - code:DataTypeMapping.java
  - adr:D-009
  - adr:D-053
---
# ES DataType to Piescript TCon Mapping

Elasticsearch `DataType` values map to piescript `TCon(String)` type constructors via the `DataTypeMapping` utility class. This is a curated subset -- not all ES types surface in piescript.

Key mapping decisions:
- **TEXT -> Keyword collapse**: TEXT and KEYWORD both map to `TCon("Keyword")`. Piescript doesn't distinguish full-text from exact-match at the type level.
- **Meta-field exclusion**: `_id`, `_index`, `_source` etc. are filtered out during field caps resolution. They don't appear in index row types.
- **Unsupported fallback**: Unmapped or unrecognized ES types (GEO_POINT, NESTED, IP, VERSION, etc.) map to `TCon("Unsupported")`, which blocks field access at elaboration time.
- **Numeric widening**: INTEGER, LONG, FLOAT, DOUBLE all map to `TCon("Double")` via [[unified-double.types]].

The mapping is intentionally lossy -- piescript's type system is not a mirror of ES's DataType enum. It's a curated surface where each piescript type has clear semantics and evaluation support.

**Depends on**: [[kind-system.types]], [[field-caps-resolution.data]]
**Enables**: [[esql-value-converter.esql]]
**Connections**:
- informs: [[esql-value-converter.esql]] -- the TCon mapping determines which Value types the converter must handle
- informs: [[datetime.types]] -- DATE maps to Unsupported today; DateTime type would add a new mapping entry
- informs: [[numeric-precision.types]] -- the widening decision (all numerics -> Double) creates the precision gap
- informs: [[keyword-string.types]] -- TEXT/KEYWORD collapse means piescript can't distinguish full-text from exact-match
- uses: [[kind-system.types]] -- each mapped TCon must be registered in Prelude.KINDS with kind Type
- constrains: [[nested-record-types.data]] -- OBJECT fields get recursive row types; NESTED/FLATTENED get Unsupported
- part-of: [[phase-2.roadmap]]
