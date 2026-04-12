---
tags: [thread, roadmap, fault-tolerance, language, types]
refs: []
---
# Error Handling & Fault Tolerance

From source-location error messages to OTP-style supervision trees. This thread
covers the full spectrum of how piescript handles, reports, and recovers from
errors — spanning type-level safety (Result/Option), runtime diagnostics (error
provenance), resource cleanup (bracket patterns), and distributed fault tolerance
(sagas, supervision).

## Sequence

1. **Error provenance** [[error-provenance.language]] — ready
   Thread `Source` through evaluator so runtime errors point to call sites.

2. **Forall type** [[forall-type.types]] — ready
   D-038 tech debt. `@AwaitsFix` tests. Unblocks bidir checking.
   _Shared with: type-foundations_

3. **ADTs** [[adts.types]] — needs-design
   Sum types for Option/Result. Declaration syntax, closed vs open, constructors.
   _Shared with: language-expressiveness_

4. **Pattern matching** [[pattern-matching.hub]] — ready
   Match expressions (`match x | pat -> body`), `if/then/else` as sugar.
   Phase 1 (basic patterns) complete. ADT constructor patterns deferred.
   _Shared with: language-expressiveness_

5. **Result/Option types** [[result-types.types]] — after #3, #4
   `Result a e` / `Option a` replacing null-as-bottom.
   Depends on: [[adts.types]], [[pattern-matching.hub]]

6. **Null-as-bottom fix** [[null-as-bottom.types]] — after #5
   Remove unsound `Null` unification. Depends on Option type existing.

7. **Bracket patterns** [[bracket-patterns.language]] — needs-design (after #4)
   `bracket acquire release use` for Searcher/Writer/channel cleanup.

8. **Channel lifecycle** [[channel-lifecycle.infrastructure]] — needs-design
   Leak prevention for `spawn!` channels that never complete.

9. **Saga coordination** [[saga-coordination.coordination]] — exploration
   Multi-shard write coordination with compensating actions.

10. **OTP supervision** [[otp-supervision.coordination]] — exploration
    Supervisor trees, restart strategies. Depends on: [[actor-model.lifecycle]]

**Depends on**: (none — root thread)
**Enables**: (none directly)
**Connections**:
- includes: [[error-provenance.language]]
- includes: [[forall-type.types]]
- includes: [[adts.types]]
- includes: [[pattern-matching.hub]]
- includes: [[result-types.types]]
- includes: [[null-as-bottom.types]]
- includes: [[bracket-patterns.language]]
- includes: [[channel-lifecycle.infrastructure]]
- includes: [[saga-coordination.coordination]]
- includes: [[otp-supervision.coordination]]
- includes: [[create-vs-index.data]]
- includes: [[fire-and-forget.coordination]]
- related: [[language-expressiveness.thread]] — shares ADTs, pattern matching
- related: [[type-foundations.thread]] — shares Forall type
