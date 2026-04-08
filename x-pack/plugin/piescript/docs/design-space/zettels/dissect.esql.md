---
tags: [esql, data-processing, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL DISSECT Command

ESQL DISSECT command for pattern extraction. Exposes ESQL's DISSECT command as a typed combinator, enabling structured field extraction from string values using a dissect pattern. The elaborator can verify the output fields at compile time based on the pattern.

**Depends on**: [[esql-combinators.esql]]
**Enables**: (none)
**Connections**:
- related: [[grok.esql]] — similar string parsing patterns
