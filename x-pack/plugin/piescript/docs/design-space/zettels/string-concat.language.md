---
tags: [language, tech-debt]
refs:
  - roadmap:phase-1-tech-debt
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# String Concat

No way to concatenate Keyword values. Proposed: `<>` for string concat (Haskell Semigroup, Elixir convention). `++` for list concat. Both are future typeclass candidates (Semigroup.<>).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: blocks some user programs that need to build strings dynamically
