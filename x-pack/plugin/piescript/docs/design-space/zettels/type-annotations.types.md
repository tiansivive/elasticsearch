---
tags: [types, syntax, inference, polymorphism, implemented, concept]
refs:
  - adr:D-034
  - adr:D-028
  - code:TypeAnnotations.java
  - code:TypeScheme.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Type Annotations

`let f : a -> a = ...` -- annotation elaborates to [[type-scheme.types]] via `resolveTypeAnnotation`:
- Lowercase identifiers are type variables (D-028), uppercase are constructors
- The `TypeScheme` from annotation is stored directly in the environment -- no generalization step needed
- Unannotated definitions infer via [[meta-variables.types]] then generalize

**Depends on**: [[hindley-milner.types]], [[rigid-variables.types]]
**Enables**: [[system-f-core.types]]
**Connections**:
- complements: [[forall-type.types]] — annotated-let path bypasses the `Forall` limitation (D-038) because the scheme is constructed directly from the annotation
- uses: [[type-scheme.types]] — annotation elaborates directly to a TypeScheme
- uses: [[antlr-grammar.language]] — UPPER_IDENT/LOWER_IDENT split enforces type variable vs constructor convention at syntax level
- uses: [[meta-variables.types]] — unannotated definitions infer via metas then generalize
