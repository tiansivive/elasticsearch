---
tags: [paper-trail, types, polymorphism]
refs:
  - session:00339533-8170-4e5c-9087-c154cc90decb
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Mode-Dependent Types Session

How to support mode-dependent return types (open with Read mode -> Searcher, open with Write mode -> Writer) without dependent types. Options: phantom types + overloading, GADTs, type families, separate functions. Conclusion: dependent types' complexity-to-payoff ratio is poor for piescript's domain. Phantom types or separate functions suffice.

**Connections**:
- informs: [[shard-read.data]], [[shard-write.data]] — separate open vs writer builtins chosen over mode-dependent dispatch
- related: [[dependent-types.types]] — explicitly evaluated and found not worth the complexity
- related: [[gadt-rejection.types]] — same pattern of rejecting complex type machinery
