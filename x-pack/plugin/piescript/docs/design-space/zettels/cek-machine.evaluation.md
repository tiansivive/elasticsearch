---
tags: [evaluation, abstract-machine, theoretical, concept]
refs:
  - resource:https://doi.org/10.1007/BF01018741
---
# CEK Machine

Control, Environment, Continuation. Piescript's [[evaluator.language|evaluator]] is an implicit CEK machine (Control = `CoreExpr`, Environment = `Value[]`, Continuation = `ActionListener` chain). Making it explicit yields: trampolining, reified continuations (serializable suspended computations), and a path to shift/reset.

Relevant if the tree-walking interpreter is long-term. The key insight is that the evaluator already has these three components -- the refactoring is about making them first-class data structures rather than implicit JVM call-stack frames and callback closures.

Resources: Felleisen & Friedman, "Control operators, the SECD machine, and the lambda-calculus."

**Depends on**: [[trampolining.technique]]
**Enables**: [[delimited-continuations.hub]] -- reified continuations are the prerequisite
**Connections**:
- refines: [[evaluator.language]] -- makes the implicit CEK structure explicit as data
- extends: [[cps-evaluation.language]] -- CPS callbacks become reified continuation data structures
- enables: [[shift-reset.control]] -- shift/reset need first-class reified continuations
- tension-with: [[execution-model.question]] -- may not be the right investment if piescript compiles
- tension-with: [[bytecode-compilation.performance]] -- compilation targets a different machine model
- enables: [[distributed-continuations.obstacle]] -- serializable continuations for cross-node suspend/resume
- uses: [[de-bruijn-indices.language]] -- the Environment component is `Value[]` indexed by de Bruijn levels
