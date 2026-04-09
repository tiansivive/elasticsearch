---
tags: [esql, data-processing, open, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL DISSECT Command

ESQL DISSECT command for pattern extraction.

- Exposes ESQL's DISSECT command as a typed combinator via [[esql-combinators.esql]], enabling structured field extraction from string values using a dissect pattern
- The [[elaboration-architecture.types]] can verify the output fields at compile time based on the pattern

**Depends on**: [[esql-combinators.esql]]
**Enables**: (none)
**Connections**:
- analogous-to: [[grok.esql]] — similar string parsing patterns
- uses: [[esql-combinators.esql]] — exposed as a typed combinator
