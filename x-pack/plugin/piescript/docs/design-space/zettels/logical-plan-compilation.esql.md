---
tags: [esql, performance, compilation, open]
refs:
  - adr:D-052
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Logical Plan Compilation

Compile directly to ESQL's internal `LogicalPlan` IR instead of strings. Enables: arbitrary lambda compilation to ESQL expressions, full ESQL function coverage without per-function builtins, deeper optimizer integration. Piescript already depends on x-pack-esql, so plan API is accessible.

**Depends on**: [[esql-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- related: future optimization — MVP compiles to strings; this is the next step
- related: [[nbe-compilation.esql]] — NbE would produce LogicalPlan nodes instead of string fragments
- related: [[esql-combinators.esql]] — combinators would compile to LogicalPlan nodes instead of text
