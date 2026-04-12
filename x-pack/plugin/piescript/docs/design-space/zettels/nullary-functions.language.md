---
tags: [language, evaluation, open, known-issue]
refs:
  - adr:D-053
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Nullary Functions / Arity-0 Builtins

Piescript has no nullary functions. `BuiltinVal(name, arity, partialArgs)` only triggers `executeBuiltin` when it receives an argument via `applyBuiltin`. An arity-0 builtin never executes — it sits as a `BuiltinVal` value forever.

Concrete impact: `ESQL.count` was originally typed as `Double` (arity 0, representing `COUNT(*)`). Had to be changed to `ESQL.count : Keyword → Double` (arity 1) taking a dummy `"*"` argument as a workaround. This is a language-level gap, not just an ESQL issue — any constant or thunk needs the same workaround.

Options for the future: (a) support arity-0 builtins by executing immediately at `CoreFree` resolution, (b) introduce unit type `()` so "no argument" is explicit, (c) lazy evaluation / thunks where `ESQL.count` is a thunk forced at use site.

**Depends on**: [[currying.language]], [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- workaround-for: [[esql-aggregates.esql]] — ESQL.count takes dummy Keyword arg
- related: [[closure-val.language]] — BuiltinVal dispatch only fires on application
- related: [[recursion.hub]] — thunks/lazy evaluation would solve both nullary and recursive patterns
