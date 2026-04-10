---
tags: [language, tech-debt, task, ready, next]
refs:
  - roadmap:phase-1-tech-debt
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# String Concat

No way to concatenate Keyword values.

- Proposed: `<>` for string concat (Haskell Semigroup, Elixir convention).
- `++` for list concat.
- Both are future [[typeclasses.types|typeclass]] candidates (`Semigroup.<>`).

**Depends on**: [[keyword-string.types]]
**Enables**: (none directly)
**Connections**:
- prerequisite-for: [[typeclass-instances.types]] — Semigroup.<> candidate for string concat
- uses: [[keyword-string.types]] — operates on KeywordVal String representation
- blocks: some user programs that need to build strings dynamically
