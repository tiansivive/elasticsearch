---
tags: [compilation, lowering, technique, concept]
refs:
  - doc:references.md
  - resource:https://doi.org/10.1145/800194.805852
---
# Defunctionalization

Reynolds' technique (1972): replace higher-order functions with first-order data constructors plus a centralized `apply` dispatch. Each lambda in the program becomes a data constructor carrying its free variables; function application becomes a case dispatch on the constructor tag.

Example: if the program has two lambdas `fn x -> x + 1` and `fn x -> x * y`, defunctionalization produces:
- Data constructors: `Lam1` (no fields) and `Lam2(y)` (captures `y`)
- A single `apply(f, x) = case f of Lam1 -> x + 1 | Lam2(y) -> x * y`

This eliminates ALL higher-order functions from the program. The result is purely first-order code -- no closures, no function pointers, no environment passing. This is more radical than [[closure-conversion.compilation|closure conversion]] (which keeps functions as values with explicit environments) or [[lambda-lifting.compilation|lambda lifting]] (which keeps functions as functions with extra parameters).

Trade-offs:
- **Pro**: first-order code is trivially compilable to JVM bytecode, Painless, interaction nets
- **Pro**: all call sites are statically known (enables whole-program optimization)
- **Con**: requires whole-program analysis (can't defunctionalize separately compiled modules)
- **Con**: the centralized `apply` function grows with program size

**Depends on**: [[pattern-matching.hub]]
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[closure-conversion.compilation]] -- closure conversion keeps functions as values; defunctionalization eliminates them entirely
- complements: [[lambda-lifting.compilation]] -- lifting + defunctionalization combined produce maximally first-order code
- informs: [[bytecode-compilation.performance]] -- first-order code maps directly to JVM invocations
- informs: [[interaction-nets.computation]] -- first-order code maps to interaction net agents without closure ports
- uses: [[pattern-matching.hub]] -- the centralized apply function IS a case dispatch (pattern matching)
- extends: [[lowering-pass.performance]] -- defunctionalization is a lowering pass
- part-of: [[compilation-pipeline.hub]] -- a stage in the compilation pipeline
- informs: [[monomorphization.compilation]] -- defunctionalization + monomorphization together eliminate all polymorphism and higher-order functions
