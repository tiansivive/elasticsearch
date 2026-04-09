---
tags: [data, data-processing, external, designed, concept]
refs:
  - vision:ml-workflows
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Feature Engineering

The highest-value ML-adjacent piescript use case. Pattern:
- Query ES data via [[esql-compilation.esql]], compute derived features (cross-index joins, time-series aggregations, normalized scores, windowed statistics)
- Write feature vectors back to an index via [[index-bulk.data]]
- Today requires extraction to Python/Spark (the [[extraction-cliff.external]])
- Piescript's [[query-typeclass.data]] + write primitives make it ES-native
- Not an ML-specific feature -- it's query/transform/write, which piescript handles generically

**Depends on**: [[query-typeclass.data]], [[shard-write.data]], [[esql-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- subsumes: [[risk-score-pattern.data]] — risk scoring is a specific instance of feature engineering
- overlaps: [[transform-unification.external]] — feature engineering subsumes many Transform use cases
- example-of: [[extraction-cliff.external]] — feature engineering is the canonical extraction cliff use case
- related: [[index-bulk.data]] — feature vectors written back via bulk API
