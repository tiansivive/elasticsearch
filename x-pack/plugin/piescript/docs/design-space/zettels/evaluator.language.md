---
tags: [language, runtime, implemented, evaluation, concept]
refs:
  - adr:D-024
  - adr:D-025
  - adr:D-041
  - code:piescript.eval
  - code:Evaluator.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Evaluator

Uniformly async tree-walking [[de-bruijn-indices.language]] environment machine. Every `evaluate` call takes an `ActionListener<Value>`.
- Pure expressions fire callbacks synchronously (zero overhead)
- Split across 7+ classes: `Evaluator`, `EvalPrimOps`, `EvalBuiltins`, `EvalCoordination`, `EvalTopology`, `EvalShard`, `EvalExchange`
- Trusts the type checker (D-025) -- see [[evaluator-trusts-typechecker.language]]

**Depends on**: [[core-ir.language]], [[de-bruijn-indices.language]], [[channels.infrastructure]], [[evaluator-trusts-typechecker.language]]
**Enables**: [[nbe-compilation.esql]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- implements: [[cps-evaluation.language]] — CPS transformation where `SubscribableListener` callbacks serve as continuations
- uses: [[eval-dependencies.language]] — `EvalDependencies` bundles `Client`, `Executor`, `ClusterService`, `TransportService`, `ChannelRegistry`, `localNodeId`
- implements: [[effect-handlers.types]] — the evaluator IS an effect handler for coordination primitives
- uses: [[force-threading.types]] — force function threaded to evaluator for type-driven materialization
- uses: [[iterative-streaming.language]] — while-loop pattern for stack-safe list builtins
