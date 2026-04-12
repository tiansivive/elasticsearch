---
tags: [principle, meta, concept]
refs:
  - doc:vision.md
  - vision:fragmentation-problem
  - vision:what-piescript-is-not
---
# Value Proposition

Composable type-safe pipelines replacing fragmented Elasticsearch features: Transforms, enrich
processors, ingest pipelines, Watcher, runtime fields, Painless scripting. Six separate systems
with overlapping capabilities, different APIs, different execution models, no composition.

**What piescript adds:**
- **Composition:** pipe operator, currying, higher-order functions -- chain operations naturally
- **Types:** row-polymorphic records, type inference, compile-time safety for data pipelines
- **Distribution:** Join Calculus coordination, code mobility, topology-aware execution

**What piescript does not redo:**
- ESQL query execution -- piescript compiles to ESQL, doesn't replace it
- Compute engine (Page/Block/Driver) -- piescript uses it for columnar streaming
- Painless scripting -- coexists for single-document scope; piescript handles multi-document

The value is measured by how much of the [[extraction-cliff.external|extraction cliff]] piescript
pushes back: workflows that today require extracting data to Spark/Python can instead run
inside Elasticsearch, typed, distributed, and composable.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- uses: [[extraction-cliff.external]] -- the extraction cliff defines the problem piescript solves
- contrasts-with: [[painless.comparable]] -- Painless is single-document/imperative; piescript is multi-document/functional/distributed
- informs: [[transform-unification.external]] -- transforms are a concrete fragmented feature piescript unifies
- informs: [[watcher-replacement.external]] -- watcher is another fragmented feature piescript subsumes
- informs: [[enrich-unification.external]] -- enrich processors are another fragmented feature
- uses: [[feature-constellation.external]] -- the feature constellation is the fragmentation problem
- informs: [[target-users.principle]] -- value proposition is defined in terms of target users' pain
- related: [[esql-data-layer.principle]] -- ESQL-as-data-layer is a key architectural choice enabling the value prop
- related: [[functional-distributed.principle]] -- functional + distributed is what differentiates piescript from existing features
- related: [[es-native.principle]] -- running inside ES (not extracting) is core to the value prop
