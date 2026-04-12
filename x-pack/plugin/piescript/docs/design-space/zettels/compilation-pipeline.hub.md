---
tags: [hub, language, compilation, pipeline, lowering, concept]
refs:
  - code:TransportPiescriptAction.java
  - code:PiescriptParser.java
  - code:IndexResolutionPrePass.java
  - code:Elaborator.java
  - code:Evaluator.java
  - doc:architecture.md
---
# Compilation Pipeline

End-to-end piescript execution pipeline, from source text to evaluated result:

1. **Source** -- piescript program text arrives via REST API
2. **Parser** ([[antlr-grammar.language]]) -- ANTLR lexer/parser produces CST
3. **Index Resolution** ([[async-prepass.technique]]) -- async pre-pass resolves field caps via RefCountingListener fan-out
4. **Elaborator** ([[elaboration-architecture.types]]) -- bidirectional HM type inference, produces typed [[core-ir.language|Core IR]] (System F + coordination nodes)
5. **Evaluator** ([[evaluator.language]]) -- CPS tree-walking interpreter over Core IR with async ActionListener callbacks

Plus future compilation paths branching from Core IR:

- **Lowering** ([[lowering-pass.performance]]) -- Core IR to lower IR (free monad residual, optimizer, then backend)
- **Closure conversion** ([[closure-conversion.compilation]]) -- eliminate free variables from closures
- **Lambda lifting** ([[lambda-lifting.compilation]]) -- lift local functions to top-level
- **Defunctionalization** ([[defunctionalization.compilation]]) -- replace higher-order functions with first-order data + dispatch
- **Monomorphization** ([[monomorphization.compilation]]) -- specialize polymorphic code to concrete types
- **Block-params IR** ([[block-params-ir.compilation]]) -- lower IR with basic block parameters for efficient control flow
- **Bytecode** ([[bytecode-compilation.performance]]) -- JVM bytecode emission for pure fragments
- **Painless push-down** ([[painless-push-down.performance]]) -- compile piescript closures to Painless scripts

The current pipeline is stages 1-5 (all implemented). Future compilation stages branch after step 4 -- Core IR is the pivot point between interpretation and compilation.

**Depends on**: (none -- this is the top-level hub)
**Enables**: (none directly -- hub zettel)
**Connections**:
- includes: [[antlr-grammar.language]], [[async-prepass.technique]], [[elaboration-architecture.types]], [[core-ir.language]], [[evaluator.language]]
- includes: [[lowering-pass.performance]], [[closure-conversion.compilation]], [[monomorphization.compilation]], [[lambda-lifting.compilation]], [[defunctionalization.compilation]], [[block-params-ir.compilation]], [[bytecode-compilation.performance]], [[painless-push-down.performance]]
- tension-with: [[execution-model.question]] -- interpreter vs compiler changes which stages matter
- informs: [[phase-transition-architecture.language]] -- the pipeline evolution from interpreter to compiler
- uses: [[force-threading.types]] -- the force function threads through elaboration and evaluation
- uses: [[de-bruijn-indices.language]] -- Core IR uses de Bruijn indices throughout the pipeline
- related: [[nbe-compilation.esql]] -- NbE for ESQL is a parallel compilation pipeline (Core IR to ESQL text)
