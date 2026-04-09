---
tags: [tooling, ir, theoretical]
refs:
  - roadmap:phase-7
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Content-Addressed Code

Unison-inspired content-addressed AST storage via [[core-ir.language]] hashing.

- Instead of naming functions by path, store them by the hash of their `CoreExpr` representation
- Enables automatic deduplication, fearless refactoring (rename without breaking references), and robust versioning
- If the implementation changes, the hash changes

**Depends on**: [[stored-functions.tooling]], [[core-ir.language]]
**Enables**: (none)
**Connections**:
- part-of: [[phase-7.roadmap]]
- informs: [[module-system.tooling]] — deduplication and versioning
- related: [[serialization.infrastructure]] — `CoreExpr` already serializable, making hashing straightforward
