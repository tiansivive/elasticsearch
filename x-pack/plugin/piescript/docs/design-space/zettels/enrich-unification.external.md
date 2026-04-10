---
tags: [external, esql, open, concept, feature, someday]
refs:
  - thread:external-interaction
---
# Enrich Unification

Replacing ES enrich policies and enrich processors with in-language cross-index joins.
- The [[enrich.esql]] command is a single-index lookup backed by a pre-built enrich policy
- Piescript enables ad-hoc typed joins across any indices without requiring policy creation
- This subsumes the enrich processor pipeline and unifies the lookup pattern under the language's coordination and [[query-typeclass.data]] model

**Depends on**: [[query-typeclass.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[transform-unification.external]] — part of the broader Transform/enrich/ingest unification story
- extends: [[enrich.esql]] — generalizes ESQL's ENRICH command from policy-backed single-index lookup to ad-hoc cross-index joins
- related: [[query-typeclass.data]] — cross-index joins compile through the Query typeclass
