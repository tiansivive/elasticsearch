---
tags: [ml, data-processing, exploration, concept]
refs: []
---
# Data Preparation

Sampling, stratification, train/test splitting, normalization, one-hot
encoding. These are map/filter/reduce operations over query results — already
in piescript's wheelhouse via `Query a` combinators. No ML-specific features
needed; the general-purpose list processing builtins handle these patterns.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[ml-workflow-integration.ml]]
- related: [[feature-engineering.data]] — data prep feeds into feature engineering
