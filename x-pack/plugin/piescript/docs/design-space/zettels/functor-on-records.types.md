---
tags: [types, typeclasses, row-types, concept, needs-design, exploration]
refs:
  - doc:references.md
---
# Functor on Records

Key typeclass motivation: `Functor` over row-polymorphic records. `fmap` transforms each field
uniformly. Example: `fmap show { x: 1, y: 2 }` produces `{ x: "1", y: "2" }`.

This requires either:
- **Higher-kinded types:** `Functor f` where `f = Record r` -- but `Record` has kind `Row -> Type`,
  so `fmap : (a -> b) -> Record r -> Record ???` needs a way to express "apply this function to
  every field type in the row"
- **Type-level MapList:** [[maplist-operator.types]] lifts a type function over each field in a row:
  `fmap f : Record (show :: Double, count :: Double) -> Record (show :: String, count :: String)`

The challenge is that `fmap` over records is inherently rank-2: the function must be
polymorphic in the field type (`forall a. a -> b` or `forall a. Show a => a -> String`).
This connects to [[higher-rank.types]] -- field-polymorphic record fmap may need rank-2 types.

Practical motivation: ESQL result transformation, where a record of typed columns needs
uniform conversion (e.g., all fields to strings for display, or all numeric fields to
percentages).

**Depends on**: [[typeclasses.types]], [[row-polymorphism.types]]
**Enables**: (none directly)
**Connections**:
- uses: [[maplist-operator.types]] -- MapList lifts a function type over each field in a row
- informs: [[higher-rank.types]] -- field-polymorphic fmap may require rank-2 polymorphism
- extends: [[typeclass-instances.types]] -- Functor instance for Record is a key motivating instance
- uses: [[record-type.language]] -- operates on piescript's record values
- informs: [[row-operators.types]] -- may need new row-level operators for field-wise transformation
- related: [[traverse.language]] -- Traversable on records generalizes Functor with effects
- related: [[bird-meertens.types]] -- record fmap is a catamorphism over record structure
- informs: [[f-omega-lite.types]] -- may need a new reducible builtin in force for row-level mapping
