---
tags: [types, esql, kinds, open, needs-design, concept]
refs:
  - adr:D-053
  - thread:type-foundations
---
# Liftable Kind

Kind constraint for types that can be serialized as ESQL literals. Distinguishes types valid in
ESQL expressions (`Double`, `Keyword`, `Boolean`) from piescript-only types that have no ESQL
representation (closures, channels, records with row tails, multi-value wrappers).

**Motivation:**
When compiling piescript to ESQL via [[nbe-compilation.esql]], environment values are spliced
into ESQL strings as literals. But not all piescript values can be ESQL literals -- a closure
cannot appear in `WHERE x = <closure>`. Currently this is a runtime error in the NbE compiler.
`Liftable` would catch it at elaboration time.

**Design:**
- `Liftable` is a kind constraint, not a typeclass -- it's about what types CAN appear in ESQL,
  not about method dispatch
- Liftable types: `Double`, `Keyword`, `Boolean`, `DateTime` (base ESQL types)
- Non-liftable types: `a -> b` (closures), `Channel a`, `Record { ... | r }` (open rows),
  `SearcherVal`, `WriterVal`, `DocRefVal`
- In the [[f-omega-lite.types|force]] vocabulary: `Liftable` would be a new reducible builtin
  that normalizes to `Type` for liftable base types and fails for non-liftable types

**Depends on**: [[kind-system.types]], [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- uses: [[kind-system.types]] -- Liftable extends the kind vocabulary with a new constraint
- extends: [[f-omega-lite.types]] -- new kind constraint in the force vocabulary (new reducible builtin)
- informs: [[esql-compilation.esql]] -- Liftable constraint prevents non-serializable values from reaching ESQL
- constrains: [[non-serializable-types.types]] -- Liftable formalizes what non-serializable-types enforces at runtime
- informs: [[nbe-compilation.esql]] -- NbE compiler currently does runtime checks; Liftable moves them to elaboration
- related: [[local-kind.types]] -- Local kind prevents cross-node travel; Liftable prevents ESQL embedding; both are "where can this type go?" constraints
- related: [[compilation-boundary.esql]] -- Liftable defines the type-level boundary between piescript and ESQL
- uses: [[unified-double.types]] -- Double is a Liftable base type
- uses: [[keyword-string.types]] -- Keyword is a Liftable base type
- informs: [[closure-val.language]] -- closures are NOT Liftable (they can travel between nodes but not into ESQL)
- related: [[code-mobility.coordination]] -- Liftable is orthogonal to mobility: a closure can travel (mobile) but not lift (non-Liftable)
