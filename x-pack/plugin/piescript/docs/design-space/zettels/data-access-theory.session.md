---
tags: [paper-trail, data, query-theory, esql]
refs:
  - session:0cc7ef96-75fb-451a-a57b-6e842375f009
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Data Access Theory Session

Discussion about formal foundations for data accessing/materializing stored data (not query representation). Led to the data-access.md document, the Query a typeclass design, the 4-level hierarchy, comparable systems analysis, and ML workflows brainstorming. Key insight: piescript extends the "extraction cliff" — when ESQL can't express the computation, users leave ES entirely. Also explored dynamic index typing (GADT vs CPS vs Reflect approaches).

**Connections**:
- produced: [[data-access-hierarchy]] — the 4-level hierarchy crystallized here
- produced: [[query-typeclass.data]] — Query a typeclass emerged from this discussion
- produced: [[extraction-cliff.external]] — the "extraction cliff" framing
- produced: [[dynamic-index-names.data]] — GADT/CPS/Reflect discussion for dynamic indices
- produced: [[comprehension-syntax.language]] — LINQ-style query surface ideas
- produced: [[feature-engineering.data]] — ML workflows brainstorming
