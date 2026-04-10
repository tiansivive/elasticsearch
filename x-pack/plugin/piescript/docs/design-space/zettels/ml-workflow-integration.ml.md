---
tags: [ml, external, hub, exploration, concept]
refs: []
---
# ML Workflow Integration

ML-adjacent workloads that piescript makes ES-native. Piescript is not "ML in
ES" — it is a general-purpose distributed computation language that happens to
eliminate the [[extraction-cliff.external]] that currently forces ML workflows
out of Elasticsearch into external systems like Spark or Airflow.

- [[feature-engineering.data]] — compute derived features without ETL
- [[model-evaluation.ml]] — score and compare models against live data
- [[data-preparation.ml]] — sampling, normalization, train/test splits
- [[inference-orchestration.ml]] — call external models and join results back
- [[es-ml-integration.ml]] — route closures to ES ML nodes
- [[ml-non-goals.ml]] — what piescript does NOT attempt (GPUs, training, tensors)

ML pipeline automation chains these steps via [[scheduled-execution.lifecycle]].

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- includes: [[feature-engineering.data]]
- includes: [[model-evaluation.ml]]
- includes: [[data-preparation.ml]]
- includes: [[inference-orchestration.ml]]
- includes: [[es-ml-integration.ml]]
- includes: [[ml-non-goals.ml]]
- solves: [[extraction-cliff.external]]
- analogous-to: [[spark.comparable]] — Spark is the current destination for ML pipelines extracted from ES
- related: [[scheduled-execution.lifecycle]] — ML pipeline automation requires scheduled execution
