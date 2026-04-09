---
tags: [meta]
refs:
  - resource:https://en.wikipedia.org/wiki/Resource_Description_Framework
  - resource:https://en.wikipedia.org/wiki/Faceted_classification
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Tags as Triples

The design space knowledge base is a property graph where everything reduces to subject-predicate-object triples:
- A zettel tagged `[types, implemented]` is two triples: `(zettel, hasTag, types)` and `(zettel, hasTag, implemented)`
- A connection `informs: [[other]]` is `(zettel, informs, other)`
- A tag group "Maturity" with role `universal` is `(maturity-group, hasRole, universal)`

The same tagging mechanism works at every level -- zettels are tagged, tag groups are tagged with roles, edges are labeled with actions. This parallels piescript's [[kind-system.types]] (D-053): types classify values, kinds classify types, but kinds ARE types. Here, tags classify zettels, roles classify groups, but roles ARE tags. It's faceted classification (Ranganathan) meets RDF triples.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[universal-vs-topic.meta]] — the RDF model explains why universal/topic roles work
- analogous-to: [[f-omega-lite.types]] — same "collapse the meta-level" pattern (kinds as types)
- analogous-to: [[kind-system.types]] — tags classify zettels the way kinds classify types
