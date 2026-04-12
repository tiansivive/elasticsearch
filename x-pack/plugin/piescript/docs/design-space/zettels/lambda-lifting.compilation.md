---
tags: [compilation, lowering, technique, concept, mobility]
refs:
  - doc:references.md
---
# Lambda Lifting

Lift local functions to top-level by adding free variables as explicit parameters. A nested `fn x -> x + y` where `y` is free becomes a top-level `fn y x -> x + y`, and the original site becomes a partial application. This eliminates nested scopes -- all functions are top-level with fully explicit parameter lists.

Lambda lifting is related to but distinct from [[closure-conversion.compilation|closure conversion]]:
- **Lambda lifting** moves functions to top-level, adding free vars as parameters. The function itself changes location.
- **Closure conversion** keeps functions in place but makes the environment explicit (as a record/tuple). The function stays nested but captures are reified.

Both eliminate free variables; they differ in where the function lives afterward. Lambda lifting produces simpler output (no closures at all), but may duplicate code when the same function is lifted from multiple call sites. Closure conversion preserves sharing but requires environment passing.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- complements: [[closure-conversion.compilation]] -- two approaches to the same problem (eliminating free variables); can be combined
- informs: [[mobility-check.performance]] -- lifted functions have no free variables, so mobility is trivially safe
- informs: [[push-down-compilation.performance]] -- lifted functions are easier to push down (no captured state)
- informs: [[bytecode-compilation.performance]] -- JVM methods are top-level; lambda lifting aligns piescript functions with JVM structure
- informs: [[painless-push-down.performance]] -- Painless has no closures; lifted functions map directly to Painless functions
- extends: [[lowering-pass.performance]] -- lambda lifting is a lowering pass
- part-of: [[compilation-pipeline.hub]] -- a stage in the compilation pipeline
- uses: [[de-bruijn-indices.language]] -- lifting changes de Bruijn indices (additional parameters shift existing bindings)
