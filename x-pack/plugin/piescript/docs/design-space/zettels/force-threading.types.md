---
tags: [types, implemented, evaluation]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Force Threading

The `force` function from `ElaborationState` is threaded to the evaluator via `EvalDependencies` as `Function<MonoType, MonoType>`. Captured after elaboration, it provides lazy type resolution on demand at runtime. First used for type-driven materialization (identifying List-typed columns in MV aggregates). Respects the no-zonking-pass principle — types resolve lazily at point of use, never by rewriting the AST.

**Depends on**: [[f-omega-lite.types]], [[evaluator.language]]
**Enables**: [[type-driven-materialization.esql]]
**Connections**:
- related: [[zonker.types]] — force function bridges elaboration and evaluation without violating the zonker's lazy resolution model
- related: first case where the evaluator needs type information
- related: [[nbe-dual-pattern.types]] — force is the type-level half of the NbE dual pattern
- related: [[meta-variables.types]] — force chases meta chains as part of normalization
- related: [[eval-dependencies.language]] — force function bundled in EvalDependencies for the evaluator
