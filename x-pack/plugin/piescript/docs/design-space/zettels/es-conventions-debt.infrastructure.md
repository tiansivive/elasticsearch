---
tags: [infrastructure, es-internals, tech-debt, task, known-issue]
refs:
  - doc:roadmap.md
  - code:TransportPiescriptAction.java
  - code:RestPiescriptAction.java
  - code:RestPiescriptDevAction.java
  - code:Evaluator.java
---
# ES Conventions Tech Debt

Five outstanding items related to Elasticsearch plugin conventions and production readiness:

- **No logging** — `TransportPiescriptAction`, [[evaluator.language]], and supporting classes have no `Logger`. Should add WARN for unexpected failures, DEBUG for pipeline stage timing, per ES conventions.
- **`ActionListener.wrap()` patterns** — Several call sites use `ActionListener.wrap(onResponse, onFailure)` instead of the preferred `delegateFailureAndWrap()`. Some are intentional (dev pipeline converts failures to response fields), others should be modernized.
- **Merge eval/dev endpoints** — Both REST handlers dispatch to `TransportPiescriptAction` with a `dev` flag. Future: single `/_piescript/eval?dev` endpoint, eliminating `RestPiescriptDevAction`.
- **Duplicate `parseProgram()`** — `RestPiescriptAction` and `RestPiescriptDevAction` have identical methods. Extract to shared utility. Goes away when endpoints merge.
- **Empty mapping diagnostics** — When [[field-caps-resolution.data]] returns no usable fields, the elaborator silently produces `List { }`. Downstream type errors ("missing fields in `{ }`") are confusing when the real issue is a missing or unmapped index.

**Depends on**: [[transport-pipeline.infrastructure]], [[es-plugin.infrastructure]]
**Enables**: (none — production hardening)
**Connections**:
- part-of: [[phase-1.roadmap]] — carried forward as Phase 1 tech debt
- related: [[transport-versioning.infrastructure]] — another ES conventions gap
- related: [[dev-endpoint.tooling]] — the endpoints that need merging
- related: [[field-caps-resolution.data]] — empty mapping produces confusing errors
