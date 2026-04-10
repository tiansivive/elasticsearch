---
tags: [data, types, tech-debt, task, fix, ready, next]
refs:
  - thread:data-completeness
  - code:IndexResolutionPrePass.java
---
# Empty Mapping Diagnostics

When `buildRowFields` produces an empty row (index exists but field caps returns
no usable fields), the elaborator silently produces `List { }`. Downstream type
errors ("missing fields ... in `{ }`") are confusing when the real issue is a
missing or unmapped index.

Fix: emit a diagnostic on `ElaborationState` when field caps returns an empty
or all-unsupported field set, so the user gets a clear message at the actual
problem site rather than a cryptic structural type error downstream.

**Depends on**: [[field-caps-resolution.data]]
**Enables**: (none directly)
**Connections**:
- solves: [[field-caps-resolution.data]] — addresses the empty mapping tech debt noted there
- related: [[elaboration-architecture.types]] — diagnostic would be emitted during elaboration
