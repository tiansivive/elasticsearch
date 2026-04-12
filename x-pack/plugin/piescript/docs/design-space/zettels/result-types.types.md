---
tags: [types, language, open, concept, needs-design, later]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
---
# Result Types

Sum types for error handling:

- `Result`/`Either` for [[send.coordination|send]] delivery errors (D-047 future).
- `Option`/`Maybe` for nullable values (D-007 fix).
- Requires [[adts.types|ADTs]] and [[pattern-matching.hub|pattern matching]].
- Would replace the current unsound [[null-as-bottom.types|null-as-bottom]].

**Depends on**: [[adts.types]], [[pattern-matching.hub]]
**Enables**: [[fire-and-forget.coordination]]
**Connections**:
- motivated-by: [[send.coordination]] — `send` returning `Result<Null, SendError>` is the concrete motivation
- solves: [[null-as-bottom.types]] — Result/Option would replace the unsound null-as-bottom
- related: [[type-narrowing.types]] — exhaustive matching on Result requires type narrowing
