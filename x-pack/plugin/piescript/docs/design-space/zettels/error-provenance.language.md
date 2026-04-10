---
tags: [language, tech-debt, fault-tolerance, debugging, task, problem, ready, next]
refs:
  - code:EvaluationException.java
  - code:ElaborationException.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
---
# Error Provenance

`EvaluationException` carries only a message string -- no source location. Builtin failures cannot point to call site.
- `ElaborationException` has line/column but info is lost at evaluation
- Fix: thread `Source` through [[evaluator.language]] via [[core-ir.language]] nodes, `BuiltinVal` stamps, or provenance stack

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- tension-with: [[code-mobility.coordination]] — critical for distributed debugging where errors occur inside shipped closures on remote nodes
- constrains: [[evaluator.language]] — evaluator currently has no source location threading
- related: [[core-ir.language]] — source locations would be attached to CoreExpr nodes
