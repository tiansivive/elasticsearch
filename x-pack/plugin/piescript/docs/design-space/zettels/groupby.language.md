---
tags: [language, data-processing, open, concept, needs-design, later]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# GroupBy Combinator

groupBy combinator for [[list-type.language]] partitioning by key function.
- Given a key-extracting function and a list, produces a grouped structure (e.g., Map from key to list of values)
- This enables piescript-level aggregation and grouping without delegating to ESQL, useful for post-query data reshaping

**Depends on**: [[list-type.language]]
**Enables**: piescript-level aggregation without ESQL
**Connections**:
- contrasts-with: [[esql-aggregates.esql]] — ESQL.statsBy handles ESQL-level grouping; this is piescript-level
