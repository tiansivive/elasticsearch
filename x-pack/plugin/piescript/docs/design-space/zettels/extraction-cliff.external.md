---
tags: [external, data, problem, motivation]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Extraction Cliff

The moment a user's needs exceed what ESQL can express, they must leave Elasticsearch entirely (extract to Spark/Python).
- That extraction is expensive, slow, operationally complex, and untyped at boundaries
- Piescript extends the boundary of what's possible inside ES via the [[data-access-hierarchy]]

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- complements: [[feature-constellation.external]] — extraction cliff is the outer boundary; the feature constellation is the inner fragmentation
- motivates: [[data-access-hierarchy]] — piescript's layered data access is the response to the extraction cliff
- motivates: [[feature-engineering.data]] — feature engineering is a concrete case where the extraction cliff forces users to Python/Spark
