---
tags: [esql, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# T-LINQ

Cheney, Lindley & Wadler (ICFP 2013): embed query expressions in typed functional language, represent as quotations, normalize via evaluation, compile to SQL. Key result: restricted sublanguage normalizes to flat queries. Piescript's query surface directly applies this.

**Depends on**: (none)
**Enables**: [[esql-compilation.esql]], [[query-typeclass.data]]
**Connections**:
- related: [[nbe-compilation.esql]] — piescript does runtime compilation (not elaboration-time normalization) but same principles apply
- related: [[esql-combinators.esql]] — combinators are the concrete T-LINQ surface in piescript
- related: [[compiling-to-categories.performance]] — Elliott's CCC compilation generalizes T-LINQ's typed-compilation-to-backends pattern
