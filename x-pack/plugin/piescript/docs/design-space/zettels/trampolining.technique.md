---
tags: [evaluation, technique, concept]
refs:
  - resource:https://blog.higher-order.com/assets/trampolines.pdf
---
# Trampolining

Reify the continuation stack as data, iterate instead of recurse. Eliminates JVM stack growth. The evaluator becomes a loop over Step values: `Done(value)` or `More(expr, env, continuation)`. Async operations (query, spawn) suspend to `ActionListener` as today. The proper fix for [[stack-depth.language]] IF the interpreter is long-term.

Resources: "Stackless Scala" (Bjarnason), general trampolining literature.

**Depends on**: [[evaluator.language]]
**Enables**: [[recursion.hub]] -- makes all recursion stack-safe
**Connections**:
- solves: [[stack-depth.language]] -- eliminates unbounded JVM stack growth for pure recursion
- refines: [[cps-evaluation.language]] -- makes the implicit CPS continuation chain explicit as data
- enables: [[cek-machine.evaluation]] -- trampolining is the first step toward a reified CEK machine
- tension-with: [[execution-model.question]] -- investment wasted if piescript compiles to bytecode
- tension-with: [[bytecode-compilation.performance]] -- if we compile, the trampoline is unnecessary
- uses: [[evaluator.language]] -- refactoring the evaluate() dispatch loop
