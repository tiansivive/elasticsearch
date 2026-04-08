---
tags: [esql, language, superseded, implemented]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Query Syntax Evolution

The query surface evolved through three stages: (1) Phase 0: raw ESQL passthrough. (2) Phase 2: backtick syntax `query \`FROM idx WHERE ...\`` -- ESQL text opaque to type system, schema evolution untracked. (3) Block F: `query ESQL.from idx |> ESQL.where pred ;` -- typed combinators compiled via NbE. Each stage was a deliberate stepping stone. The backtick syntax is now removed.

**Depends on**: [[esql-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- related: [[esql-compilation.esql]] — backtick stage (Phase 2) was an intentional compromise; replaced with typed compilation in Block F
- related: [[t-linq.esql]] — T-LINQ is the theoretical basis for the typed combinator stage
- related: [[nbe-compilation.esql]] — NbE is the implementation mechanism for stage 3
