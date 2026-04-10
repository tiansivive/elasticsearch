---
tags: [esql, data-processing, open, concept]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL ENRICH Command

ESQL ENRICH command binding. Exposes ESQL's ENRICH command as a typed piescript combinator via [[esql-combinators.esql]], allowing scripts to enrich query results with data from enrich policies. Part of the broader story of unifying Transform, enrich, and ingest pipelines under piescript's coordination model ([[transform-unification.external]]).

**Depends on**: [[esql-combinators.esql]]
**Enables**: (none)
**Connections**:
- part-of: [[transform-unification.external]] — part of Transform/enrich/ingest unification story
