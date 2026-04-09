---
tags: [types, implemented, kinds, concept]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:MonoType.java
  - code:Prelude.java
  - code:ElaborationState.java
---
# Kind System

The kind system classifies types by their "type of type." Kinds are [[meta-variables.types]] `MonoType` values:

- `TCon("Type")` for value types
- `TCon("Row")` for [[row-polymorphism.types]] row types
- Arrow kinds via `MonoType.Arrow` for type constructors (e.g., `List : Type -> Type`, `ESQL : Row -> Type`, `& : Row -> Row -> Row`)

The same [[unification-algorithm.types]] solves kind constraints -- no separate kind checker. `Prelude.KINDS` maps every builtin type constructor to its kind. Kind errors are caught by unification at type application sites, not by runtime assertions. Formerly a separate `Kind` enum (deleted by D-053).

**Depends on**: [[f-omega-lite.types]]
**Enables**: [[row-operators.types]], [[label-kind.types]]
**Connections**:
- inspired-by: GHC TypeInType-style kind unification — kind constraint improvements (ascription-site checks, record field type kinds, arrow param/result kinds) are future work for better error messages
- uses: [[prelude.language]] — `Prelude.KINDS` maps every builtin type constructor to its kind
- uses: [[unification-algorithm.types]] — same unifier solves kind constraints (D-053)
- constrains: [[rowtype-as-monotype.types]] — row-kindedness assertion prevents unsound type applications
- extends: [[local-kind.types]] — proposed Local kind extends the kind vocabulary
