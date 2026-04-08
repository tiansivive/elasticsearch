---
tags: [performance, theoretical, push-down]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Compiling to Categories

Elliott (ICFP 2017): same program compiled to different cartesian closed categories produces different outputs (circuit, GPU kernel, Lucene query, ESQL string). Formalizes piescript's typeclass-driven push-down pattern.

**Depends on**: (none)
**Enables**: [[push-down-compilation.performance]]
**Connections**:
- related: same filter expression in "Lucene category" -> Lucene query, "ESQL category" -> ESQL string, "List category" -> iteration
- related: [[typeclasses.types]] — CCC compilation is typeclass-driven
- related: [[query-typeclass.data]] — Query typeclass is the concrete instance of this pattern
