---
tags: [types, nbe, kinds, theoretical, concept, needs-design, someday]
refs:
  - adr:D-053
  - thread:type-foundations
---
# Type-Level Pattern Matching

`force` in `ElaborationState` already does type-level pattern matching — hardcoded cases
for `&`/`Pick`/`Omit` that reduce when arguments are concrete, stay stuck on unsolved metas.
User-defined type families would generalize this.

**Current state (hardcoded in `force`):**
```
force(App(App(TCon("&"), row1), row2))   → merge(row1, row2)    -- concrete: reduce
force(App(App(TCon("Pick"), row1), row2)) → intersect(...)       -- concrete: reduce
force(App(anything, meta))                → stuck                 -- neutral: return as-is
```

**Future (user-defined type families):**
```
type MapList : Row → Row where
| { f: a | r } → { f: List a | MapList r }
| {} → {}
```

`force` reduces `MapList` when its argument is concrete, stays stuck when it's a meta.
Same NbE pattern — evaluate into a semantic domain, get stuck on unknowns, read back.

**The NbE dual:** Value-level `CoreMatch` and type-level `force` cases follow the exact
same structure. Patterns match on constructors/shapes, bodies produce results, unknowns
cause stuckness. See [[nbe-dual-pattern.types]]. Designing the value-level `Pattern`
hierarchy well informs the type-level pattern representation.

**What this would enable:**
- `MapList : Row → Row` ([[maplist-operator.types]]) — user-defined, not hardcoded
- `Project : Label → Row → Type` ([[label-kind.types]]) — type-safe field projection
- General type-level computation over rows and kinds

**Depends on**: [[f-omega-lite.types]], [[pattern-matching.hub]]
**Enables**: [[maplist-operator.types]]
**Connections**:
- part-of: [[pattern-matching.hub]]
- analogous-to: [[nbe-dual-pattern.types]] — value-level CoreMatch and type-level force are the same NbE structure
- extends: [[f-omega-lite.types]] — user-defined type families generalize the hardcoded reducible builtins in force
- enables: [[maplist-operator.types]] — MapList would be a user-defined type family rather than a hardcoded force case
