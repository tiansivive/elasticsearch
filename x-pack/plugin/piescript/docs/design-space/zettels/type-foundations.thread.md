---
tags: [thread, roadmap, types]
refs: []
---
# Type Foundations

From fixing the Forall type representation to QTT linearity and session types.
This thread covers the evolution of piescript's type system beyond the current
Hindley-Milner + F-omega-lite foundation — filling tech debt gaps, adding new
type-level features, and building toward the long-term vision of linear types
and session-typed channels.

## Sequence

1. **Forall type** [[forall-type.types]] — ready
   D-038: `MonoType` -> `Type` with `Forall` variant. `@AwaitsFix` tests.
   _Shared with: error-handling_

2. **Bidirectional checking fix** [[bidir-checking.types]] — ready (after #1)
   Complete check mode for polytype ascription. Depends on Forall type.

3. **resolve-deep removal** [[resolve-deep.types]] — ready
   Replace `resolveDeep` in CorePrinter with environment-based Rigid resolution.

4. **zonkOrKeep -> Optional** [[zonker.types]] — ready
   D-032: switch to `Optional`-returning `zonk` API.

5. **Environment-carrying instantiation** [[environment-carrying-instantiation.types]] — later
   D-037: carry environment through instantiation.

6. **Recursive types** [[recursive-types.types]] — needs-design
   Iso-recursive vs equi-recursive. Relaxing occurs check.
   Complements: [[recursion.hub]]

7. **Type narrowing** [[type-narrowing.types]] — needs-design
   TypeScript-style if-check refinement. Depends on ADTs + pattern matching.

8. **Runtime dispatch** [[runtime-dispatch.types]] — needs-design
   Typeclass dictionaries vs monomorphization.

9. **Lacks constraint** [[lacks-constraint.types]] — needs-design
   Row constraints for precise Pick/Omit/keep/drop encoding.

10. **Label kind** [[label-kind.types]] — needs-design
    Type-level string singletons for type-safe field projection.

11. **Typeclasses** [[typeclasses.types]] — needs-design
    Ad-hoc polymorphism: instance resolution, coherence, dictionary passing.
    _Shared with: language-expressiveness_

12. **Higher-rank polymorphism** [[higher-rank.types]] — someday
    Rank-N types. Depends on Forall variant existing.

13. **QTT linearity** [[qtt-linearity.types]] — someday
    Multiplicities {0, 1, omega} on bindings. Linear channels.

14. **Session types** [[session-types.types]] — someday
    Type-checked channel protocols. Deadlock freedom.
    Depends on: [[qtt-linearity.types]]

**Depends on**: (none — root thread)
**Enables**: (none directly)
**Connections**:
- includes: [[forall-type.types]]
- includes: [[bidir-checking.types]]
- includes: [[resolve-deep.types]]
- includes: [[zonker.types]]
- includes: [[environment-carrying-instantiation.types]]
- includes: [[recursive-types.types]]
- includes: [[type-narrowing.types]]
- includes: [[runtime-dispatch.types]]
- includes: [[lacks-constraint.types]]
- includes: [[label-kind.types]]
- includes: [[typeclasses.types]]
- includes: [[higher-rank.types]]
- includes: [[qtt-linearity.types]]
- includes: [[session-types.types]]
- related: [[error-handling.thread]] — shares Forall type
- related: [[language-expressiveness.thread]] — shares typeclasses
- related: [[ownership-resources.thread]] — QTT linearity is the shared prerequisite
