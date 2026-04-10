---
tags: [types, resources, theoretical, someday]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:ownership-resources
---
# Borrow Checking

Borrow checking vs [[qtt-linearity.types]] trade-off for resource safety.

- Rust's borrow checker enforces [[ownership.types]] and borrowing rules through lifetime analysis
- QTT (Quantitative Type Theory) achieves similar goals through usage quantities on bindings
- The trade-off: borrow checking is more familiar but requires lifetime annotations; QTT integrates more naturally into a dependent/polymorphic type system

**Depends on**: [[ownership.types]], [[qtt-linearity.types]]
**Enables**: (none)
**Connections**:
- part-of: [[phase-6.roadmap]]
- contrasts-with: [[qtt-linearity.types]] — Rust approach vs Idris/Linear Haskell approach
