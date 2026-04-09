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
- [x] Add remaining `Tracked in: [[zettel]]` links to `decisions.md` ADRs — resolved session:4e5e689a (55/55 done)
- [ ] Tech-debt extraction script — scan zettels tagged `tech-debt` + `task`, generate report
- [ ] Zettelkasten interaction skill — teach agents how to create/update/connect zettels properly (skill vs CLAUDE.md guidance?)
- [ ] Roadmap/ADR/vision generation scripts — extract from zettels, archive current files, generate as views
- [ ] Backfill thread.md — add retroactive session blocks for prior sessions (best-effort from transcripts)
