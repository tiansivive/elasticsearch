---
tags: [language, runtime, implemented, evaluation]
refs:
  - adr:D-024
  - adr:D-025
  - adr:D-041
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Evaluator

Uniformly async tree-walking de Bruijn environment machine. Every `evaluate` call takes an `ActionListener<Value>`. Pure expressions fire callbacks synchronously (zero overhead). Split across 7+ classes: `Evaluator`, `EvalPrimOps`, `EvalBuiltins`, `EvalCoordination`, `EvalTopology`, `EvalShard`, `EvalExchange`. Trusts the type checker (D-025).

**Depends on**: [[core-ir.language]], [[de-bruijn-indices.language]], [[channels.infrastructure]]
**Enables**: [[nbe-compilation.esql]]
**Connections**:
- related: [[cps-evaluation.language]] — CPS transformation where `SubscribableListener` callbacks serve as continuations
- related: [[eval-dependencies.language]] — `EvalDependencies` bundles `Client`, `Executor`, `ClusterService`, `TransportService`, `ChannelRegistry`, `localNodeId`
- related: [[effect-handlers.types]] — the evaluator IS an effect handler for coordination primitives
- related: [[force-threading.types]] — force function threaded to evaluator for type-driven materialization
- related: [[iterative-streaming.language]] — while-loop pattern for stack-safe list builtins
