---
tags: [language, design-pattern, feature, needs-design, later]
refs:
  - thread:language-expressiveness
---
# Pattern Reuse

The `Pattern` infrastructure must be reusable beyond `match` expressions. Patterns appear
in four binding sites, all desugared to `CoreMatch`:

| Site | Surface syntax | Desugaring |
|------|---------------|-----------|
| `match` | `match x \| { n } -> n` | Direct `CoreMatch` |
| Lambda param | `fn { name: n } -> n` | `fn $arg -> match $arg \| { name: n } -> n` |
| `when` binding | `when (ch { name: n }) -> n` | Wrap `when` body in `CoreMatch` per binding |
| `let` binding | `let { x, y } = point in ...` | `let $tmp = point in match $tmp \| { x, y } -> ...` |

Phase 1 implements `match` only. The other sites are syntactic sugar added incrementally —
they reuse the `Pattern` type, the pattern type inference, and the `CoreMatch` evaluation,
with no duplication.

**Key constraint:** The `Pattern` sealed hierarchy and the evaluator's pattern-matching logic
must be designed once for all four sites. The grammar extensions for lambda/when/let
destructuring are additive changes to the parser and elaborator — the pattern infrastructure
is shared.

**Depends on**: [[pattern-matching.hub]], [[core-match.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- enhances: [[when-synchronization.coordination]] — destructuring in channel bindings
- enhances: [[currying.language]] — destructuring in lambda params
- enhances: [[block-expressions.language]] — destructuring in let bindings
