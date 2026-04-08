---
tags: [data, data-processing, external, designed]
refs:
  - vision:ml-workflows
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Feature Engineering

The highest-value ML-adjacent piescript use case. Pattern: query ES data, compute derived features (cross-index joins, time-series aggregations, normalized scores, windowed statistics), write feature vectors back to an index. Today requires extraction to Python/Spark. Piescript's `Query a` + write primitives make it ES-native. Not an ML-specific feature — it's query/transform/write, which piescript handles generically.

**Depends on**: [[query-typeclass.data]], [[shard-write.data]], [[esql-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- related: [[risk-score-pattern.data]] — risk scoring is a specific instance of feature engineering
- related: [[transform-unification.external]] — feature engineering subsumes many Transform use cases
- related: [[extraction-cliff.external]] — feature engineering is the canonical extraction cliff use case
- related: [[index-bulk.data]] — feature vectors written back via bulk API
