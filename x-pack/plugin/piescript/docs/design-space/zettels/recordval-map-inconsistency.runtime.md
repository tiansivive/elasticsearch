---
tags: [runtime, tech-debt, known-issue, fix, task]
refs:
  - code:Value.java
---
# RecordVal Map Inconsistency

Some `RecordVal` construction sites use `Map.of()` (nondeterministic iteration order) while others use `LinkedHashMap` (ordered).

- This causes inconsistent [[serialization.infrastructure|serialization]] and debugging output across JVM runs.
- The same [[record-type.language|record]] may render its fields in different orders depending on which code path created it.
- Fix: standardize on `LinkedHashMap` everywhere `RecordVal` fields are constructed.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- part-of: [[evaluator.language]] — RecordVal is a core evaluator data structure
- constrains: [[serialization.infrastructure]] — nondeterministic field order breaks stable serialization output
- tension-with: [[schema-permutation.types]] — field order inconsistency is visible at runtime
