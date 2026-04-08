# Queue

Pending work items. FIFO — oldest first. Each item references a zettel.
Resolve items top-down. `[ ]` open, `[x]` resolved, `[~]` dropped.

- [ ] [[recursion.language]] — design recursion mechanism (Y-combinator vs `fix` vs `rec`)
- [ ] [[pattern-matching.language]] — Phase 1e, deferred twice; needed for ADTs, error handling, control flow
- [ ] [[recursive-types.types]] — iso-recursive vs equi-recursive, mu-types
- [ ] [[runtime-dispatch.types]] — runtime polymorphism design (typeclass dictionaries? monomorphization?)
- [ ] [[type-narrowing.types]] — TypeScript-style if-check refinement
- [ ] [[keyword-string.types]] — Keyword vs String unification
- [ ] [[datetime.types]] — DateTime/IP/GeoPoint handling (currently Unsupported or lossy)
- [ ] [[numeric-precision.types]] — primitive type review, is `Double` as only numeric still right?
- [ ] [[transport-channels.infrastructure]] — ES transport layer as native channel mechanism
- [ ] [[string-concat.language]] — list concat `++` and string concat `<>` operators
- [ ] Revise `roadmap.md` — remove redundancy with design space zettels
- [ ] Revise `vision.md` — trim speculative sections that are now zettels
- [ ] Add remaining `Tracked in: [[zettel]]` links to `decisions.md` ADRs (48/55 done)
