---
tags: [language, evaluation, compilation, open, question]
refs:
  - code:Evaluator.java
---
# Execution Model

Is the tree-walking interpreter piescript's long-term execution model, or a stepping stone to compilation (Painless, bytecode, interaction nets)? Affects whether to invest in CEK/trampoline (interpreter path) or compilation infrastructure (compiler path). The trampoline deferral grows costlier over time either way -- more code depends on the callback-nesting pattern.

Three potential paths:

1. **Interpreter stays** -- invest in [[cek-machine.evaluation|CEK]], [[trampolining.technique|trampoline]], reified continuations
2. **Compile to Painless** -- invest in [[closure-conversion.compilation|closure conversion]], [[monomorphization.compilation|monomorphization]], Painless codegen
3. **Compile to bytecode** -- invest in [[zam.abstract-machine|ZAM-style]] stack machine, JVM bytecode emission
4. **Compile to interaction nets** -- speculative, invest in graph reduction on JVM

Not mutually exclusive -- the tree-walking interpreter can coexist with compiled fast paths for hot code.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- constrains: [[cek-machine.evaluation]] -- CEK investment only justified if interpreter stays
- constrains: [[trampolining.technique]] -- trampoline investment only justified if interpreter stays
- constrains: [[stack-depth.language]] -- the right fix depends on the chosen path
- constrains: [[bytecode-compilation.performance]] -- bytecode path only if we commit to compilation
- constrains: [[painless-push-down.performance]] -- Painless target only if we compile to it
- constrains: [[interaction-nets.computation]] -- net reduction only if we compile to interaction nets
- constrains: [[closure-conversion.compilation]] -- needed for all compilation paths
- constrains: [[monomorphization.compilation]] -- needed for efficient compiled code
- informs: [[recursion.hub]] -- recursion safety strategy depends on evaluator architecture
- informs: [[delimited-continuations.hub]] -- continuation representation depends on execution model
