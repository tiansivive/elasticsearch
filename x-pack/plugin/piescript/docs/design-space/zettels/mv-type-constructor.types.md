---
tags: [types, data, open, needs-design, later, concept]
refs:
  - roadmap:block-h
  - thread:data-completeness
---
# MV Type Constructor

Explicit `MV a` type constructor for multi-value fields. Makes multi-valuedness visible in the
type system rather than implicit at runtime.

**Current approach (implicit MV):** all base types are MV-capable at runtime but the type system
does not distinguish `Double` (single) from `Double` (multi-value). The runtime handles MV
transparently via [[scalar-pervasion.data]]. The `List a` type from [[type-driven-materialization.esql]]
is the closest thing to explicit MV, but it conflates "ES multi-value field" with "piescript list."

**Explicit MV would enable:**
- Type-safe MV operations: `MV.first : MV a -> a`, `MV.toList : MV a -> List a`
- Distinguishing single from multi-value at compile time -- prevents accidentally treating MV as scalar
- Type-level documentation of which fields are multi-valued
- Integration with [[kind-system.types]] -- `MV : Type -> Type` is a proper type constructor

**Trade-offs:**
- Explicit MV adds type noise for the common case (most fields are single-valued)
- Implicit MV is simpler but hides a runtime distinction that can cause surprises
- The [[single-a-boxing.types]] design (Single/MV distinction) is a related approach

**Depends on**: [[multi-value-fields.data]], [[kind-system.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[multi-value-fields.data]]
- uses: [[kind-system.types]] -- MV is a type constructor with kind Type -> Type
- informs: [[scalar-pervasion.data]] -- explicit MV changes how pervasion is typed
- contrasts-with: implicit MV (current approach where all base types are MV-capable at runtime)
- complements: [[single-a-boxing.types]] -- Single a / MV a is the two-type-universe from Block H design
- informs: [[type-driven-materialization.esql]] -- explicit MV would refine how ESQL columns are typed
- informs: [[esql-value-converter.esql]] -- converter would use MV type to decide materialization strategy
- related: [[list-type.language]] -- MV a vs List a: both are "collection of a" but with different semantics
- informs: [[runtime-dispatch.types]] -- MV/scalar dispatch would be type-directed, not runtime-guessed
