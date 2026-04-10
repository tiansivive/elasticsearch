---
tags: [performance, types, push-down, open, concept, needs-design, someday]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Push-Down Compilation

[[typeclasses.types|Typeclass]]-driven push-down: `filter pred rawdata` -> [[lucene-query-builders.es-internals|Lucene query]], `filter pred stream` -> iteration. Replaces the old Block D (push-down to ESQL text). Requires typeclasses. Principled, type-directed optimization.

**Depends on**: [[typeclasses.types]], [[query-typeclass.data]], [[lowering-pass.performance]]
**Enables**: (none directly)
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- part-of: [[deferred-push-down.roadmap]]
- implements: [[compiling-to-categories.performance]] — Elliott (ICFP 2017) formalizes this: same expression, different backends via CCC
- uses: [[esql-compilation.esql]] — ESQL compilation is the current push-down target
- complements: [[data-access-hierarchy]] — push-down enables the data access hierarchy's declarative levels
