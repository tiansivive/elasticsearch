---
tags: [paper-trail, comparable, pi-calculus, distributed]
refs:
  - session:ca0f522f-6232-48c4-b135-8e9d39644734
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Spark and Join Calculus Session

Exploration of whether Spark is based on Join Calculus. Answer: Spark's RDD lineage model is functional (map/filter/reduce over immutable collections) but not process-algebraic. Spark's distribution is implicit (optimizer decides); piescript's is explicit (user decides). The comparison clarified piescript's positioning.

**Connections**:
- informs: [[spark.comparable]]
- contrasts-with: [[explicit-distribution.language]] — Spark implicit vs piescript explicit
