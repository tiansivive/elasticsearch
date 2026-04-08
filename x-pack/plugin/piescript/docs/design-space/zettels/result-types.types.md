---
tags: [types, language, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Result Types

Sum types for error handling: Result/Either for send delivery errors (D-047 future), Option/Maybe for nullable values (D-007 fix). Requires ADTs and pattern matching. Would replace the current unsound null-as-bottom.

**Depends on**: [[adts.types]], [[pattern-matching.language]]
**Enables**: [[fire-and-forget.coordination]]
**Connections**:
- related: [[send.coordination]] — `send` returning `Result<Null, SendError>` is the concrete motivation
- related: [[null-as-bottom.types]] — Result/Option would replace the unsound null-as-bottom
- related: [[type-narrowing.types]] — exhaustive matching on Result requires type narrowing
