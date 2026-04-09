---
tags: [coordination, distributed, theoretical, comparable, prior-art, reference]
refs:
  - doc:references.md
---
# WebdamLog

Location-aware Datalog where data placement and distribution are first-class in the language:
- Relations are annotated with their physical location; rules can reference remote relations, and the runtime handles data shipping
- One of few systems that formalize *where* data lives as part of the query semantics rather than treating distribution as an execution-layer concern
- Piescript's [[explicit-distribution.language]] and [[data-locality.distributed]] serve the same role: making placement explicit
- Compare with [[dedalus.coordination]] which adds time rather than location to Datalog

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- contrasts-with: [[dedalus.coordination]] — Dedalus adds time to Datalog; WebdamLog adds location. Both extend Datalog for distributed settings but along different axes
- analogous-to: [[explicit-distribution.language]] — piescript's explicit node/shard placement serves the same role as WebdamLog's location annotations
- analogous-to: [[data-locality.distributed]] — data-aware computation placement mirrors WebdamLog's location-annotated relations
- related: [[locality-property.coordination]] — both address location-awareness but via very different mechanisms (pi-calculus channel routing vs Datalog relation annotations)
