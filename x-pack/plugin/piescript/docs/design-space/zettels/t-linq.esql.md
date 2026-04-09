---
tags: [esql, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# T-LINQ

Cheney, Lindley & Wadler (ICFP 2013):
- Embed query expressions in a typed functional language, represent as quotations, normalize via evaluation, compile to SQL
- Key result: a restricted sublanguage normalizes to flat queries
- Piescript's query surface directly applies this via [[esql-combinators.esql]] and [[nbe-compilation.esql]]
- Related to [[query-compilation-theory.esql]] and [[query-shredding.esql]] as part of the broader language-integrated query literature

**Depends on**: (none)
**Enables**: [[esql-compilation.esql]], [[query-typeclass.data]]
**Connections**:
- informs: [[nbe-compilation.esql]] — piescript does runtime compilation (not elaboration-time normalization) but same principles apply
- implements: [[esql-combinators.esql]] — combinators are the concrete T-LINQ surface in piescript
- specializes: [[compiling-to-categories.performance]] — Elliott's CCC compilation generalizes T-LINQ's typed-compilation-to-backends pattern
- informs: [[query-compilation-theory.esql]] — T-LINQ is a foundational paper in the query compilation theory lineage
- informs: [[query-shredding.esql]] — shredding extends T-LINQ's normalization to nested queries
