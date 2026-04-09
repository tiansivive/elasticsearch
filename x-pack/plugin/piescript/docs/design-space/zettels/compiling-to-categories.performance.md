---
tags: [performance, theoretical, push-down]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Compiling to Categories

Elliott (ICFP 2017): same program compiled to different cartesian closed categories produces different outputs (circuit, GPU kernel, Lucene query, ESQL string). Formalizes piescript's typeclass-driven [[push-down-compilation.performance]] pattern.

- Same filter expression in "Lucene category" produces a Lucene query, "ESQL category" produces an ESQL string, "List category" produces iteration

**Depends on**: (none)
**Enables**: [[push-down-compilation.performance]]
**Connections**:
- part-of: [[deferred-push-down.roadmap]]
- informs: [[push-down-compilation.performance]] — CCC framework formalizes the multi-backend compilation pattern
- related: [[typeclasses.types]] — CCC compilation is typeclass-driven; both are peer theoretical concepts
- informs: [[query-typeclass.data]] — Query typeclass is the concrete instance of this pattern
