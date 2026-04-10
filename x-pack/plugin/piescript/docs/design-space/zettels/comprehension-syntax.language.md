---
tags: [language, esql, open, feature, concept, question, needs-design, later]
refs:
  - vision:long-term
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Comprehension Syntax

Sugar over Query combinators: `from r in idx where pred select projection`. Readable for SQL-familiar users. May coexist with combinator chains (pipe style). Surface syntax question: comprehension keywords vs pure combinators vs both.

Three surface syntax options are under discussion: (a) comprehension keywords (`from r in idx where pred select projection`), readable for SQL-familiar users who expect a declarative query shape; (b) pure combinator chains via pipe (`ESQL.from idx |> ESQL.where pred |> ESQL.select projection`), idiomatic for functional programmers and consistent with how the rest of piescript works; (c) both coexisting, with comprehension syntax desugaring to the same combinator calls. In all cases, desugaring targets the Query typeclass combinators, so the semantics are identical regardless of surface form.

Instance selection is also open: implicit resolution (the data source type determines the backend — an `EsqlIndex` triggers the ESQL instance, a hypothetical `PgConnection` triggers a Postgres instance) or explicit annotation (the user writes `@ESQL` or similar to select the backend). Implicit is more ergonomic; explicit avoids ambiguity when multiple instances could apply.

The ESQL typing problem motivates this zettel directly. The current opaque `query` syntax has a type soundness hole: ESQL transformations (stats, eval, rename) change the result row shape invisibly inside a string literal. Comprehensions solve this by making every operation — filter, projection, aggregation — a typed piescript expression that the elaborator checks, so the output row type flows through naturally.

**Depends on**: [[typeclasses.types]], [[query-typeclass.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- inspired-by: [[t-linq.esql]] — the "language-integrated query" aspect of the name T-LINQ
- uses: [[query-typeclass.data]] — desugaring targets the Query typeclass combinators
- implements: [[esql-compilation.esql]] — comprehensions make every ESQL transformation a typed piescript expression
