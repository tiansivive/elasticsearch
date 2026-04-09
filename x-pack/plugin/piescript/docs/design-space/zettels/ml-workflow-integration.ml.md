---
tags: [ml, external, exploration, concept]
refs: []
---
# ML Workflow Integration

ML-adjacent workloads that piescript makes ES-native:

- **Feature engineering** -- compute derived features without ETL (see [[feature-engineering.data]])
- **Model evaluation** -- score and compare models against live data
- **Data preparation** -- sampling, normalization, train/test splits
- **Inference orchestration** -- call external models and join results back
- **ML pipeline automation** -- schedule and chain these steps via [[scheduled-execution.lifecycle]]

Piescript is not "ML in ES" -- it is a general-purpose distributed computation language that happens to eliminate the [[extraction-cliff.external]] that currently forces ML workflows out of Elasticsearch into external systems like Spark or Airflow.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- extends: [[feature-engineering.data]] — feature engineering is the most immediate ML use case; this zettel covers the broader ML workflow surface
- solves: [[extraction-cliff.external]] — the extraction cliff is the core problem that makes ML workflows painful today; piescript dissolves it
- analogous-to: [[spark.comparable]] — Spark is the current destination for ML pipelines extracted from ES; piescript aims to make that extraction unnecessary
- related: [[scheduled-execution.lifecycle]] — ML pipeline automation requires scheduled execution for chaining steps
