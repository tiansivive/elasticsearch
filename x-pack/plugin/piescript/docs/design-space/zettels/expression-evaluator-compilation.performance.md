---
tags: [performance, compilation, columnar, open, concept]
refs: []
---
# Expression Evaluator Compilation

Compiling mobile [[core-ir.language]] lambdas to ESQL `ExpressionEvaluator` trees for vectorized Block-level execution.
- Where [[symbol-partial-evaluation.esql]] produces ESQL query strings, this path would produce `ExpressionEvaluator` ASTs that operate directly on columnar `Block` data
- The "fast path" for scalar transforms over Pages from [[shard-stream.data]]
- A piescript lambda like `\r -> r.x + r.y * 2` would compile to an `AddDoublesEvaluator(field("x"), MulDoublesEvaluator(field("y"), constant(2)))` tree, executing at Block granularity without per-row interpretation overhead

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- extends: [[nbe-compilation.esql]] -- same NbE principle (evaluate with symbolic input, read back into target) but targeting ExpressionEvaluator ASTs instead of strings
- related: [[shard-stream.data]] -- expression evaluators operate on the same Page/Block data that shard streaming produces
- related: [[core-ir.language]] -- compiles mobile Core IR lambdas to ExpressionEvaluator trees
- contrasts-with: [[symbol-partial-evaluation.esql]] -- string compilation (ESQL queries) vs AST compilation (expression evaluators); complementary targets
