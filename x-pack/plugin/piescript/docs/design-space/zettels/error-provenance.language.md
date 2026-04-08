---
tags: [language, tech-debt, fault-tolerance, debugging]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Error Provenance

`EvaluationException` carries only a message string — no source location. Builtin failures cannot point to call site. `ElaborationException` has line/column but info is lost at evaluation. Fix: thread `Source` through evaluator via `CoreExpr` nodes, `BuiltinVal` stamps, or provenance stack.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- related: [[code-mobility.coordination]] — critical for distributed debugging where errors occur inside shipped closures on remote nodes
