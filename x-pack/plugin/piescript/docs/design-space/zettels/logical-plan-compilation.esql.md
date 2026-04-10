---
tags: [esql, performance, compilation, open, concept, needs-design, later]
refs:
  - adr:D-052
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:data-completeness
---
# Logical Plan Compilation

Compile directly to ESQL's internal `LogicalPlan` IR instead of strings. Enables:

- Arbitrary lambda compilation to ESQL expressions
- Full ESQL function coverage without per-function builtins
- Deeper optimizer integration

Piescript already depends on x-pack-esql, so the plan API is accessible. The current [[nbe-compilation.esql]] MVP compiles to ESQL strings; this is the next step.

**Depends on**: [[esql-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- extends: [[nbe-compilation.esql]] — NbE would produce LogicalPlan nodes instead of string fragments
- extends: [[esql-combinators.esql]] — combinators would compile to LogicalPlan nodes instead of text
- solves: [[expression-evaluator-compilation.performance]] — LogicalPlan compilation enables deeper integration with ESQL's expression evaluator
