---
tags: [esql, data-processing, open, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL GROK Command

ESQL GROK command for structured parsing. Exposes ESQL's GROK command as a typed combinator via [[esql-combinators.esql]], enabling structured field extraction from string values using grok patterns.
- Like [[dissect.esql]] but with regex-based patterns for more complex parsing scenarios

**Depends on**: [[esql-combinators.esql]]
**Enables**: (none)
**Connections**:
- analogous-to: [[dissect.esql]] — similar string parsing patterns
