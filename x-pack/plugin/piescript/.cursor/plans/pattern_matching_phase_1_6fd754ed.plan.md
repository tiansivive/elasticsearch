---
name: Pattern Matching Phase 1
overview: "Implement Phase 1 of pattern matching: `match` expressions as the primary control-flow primitive, `if/then/else` as sugar over Boolean match, with literal/variable/wildcard/record/list patterns. No ADTs, no exhaustiveness checking, no constructor patterns."
todos:
  - id: zettels
    content: "Create implementation zettel and queue zettel: pattern-matching-phase1.implementation.md and pattern-matching-phase1.queue.md"
    status: completed
  - id: pattern-ir
    content: Pattern sealed interface + CoreMatch + Alternative in piescript.core package
    status: completed
  - id: eval-match
    content: "EvalMatch.java: pattern matching evaluator (tryMatch per pattern variant, top-to-bottom arm dispatch)"
    status: completed
  - id: grammar
    content: "ANTLR grammar: MatchExpr rule, pattern hierarchy, recordPatField with shorthand"
    status: completed
  - id: elaborator
    content: "Matches.java elaborator helper: match elaboration, desugarIf, pattern type inference"
    status: completed
  - id: serialization
    content: "CoreExprSerialization: TAG_MATCH + Pattern serialization (stable byte tags, recursive)"
    status: completed
  - id: printer
    content: "CorePrinter: printExpr case for CoreMatch, printPattern helper"
    status: completed
  - id: unit-tests
    content: "Unit tests: PatternTests, CoreMatchTests, ElaboratorTests (match cases), EvaluatorTests (match cases)"
    status: completed
  - id: integration-tests
    content: "Integration tests in PiescriptIT: literal match, if/else, record destructuring, list decomposition"
    status: completed
  - id: docs
    content: Update current-state.md, thread.md, thread hubs, queue; mark pattern matching implemented
    status: completed
isProject: false
---

# Pattern Matching Phase 1

## Scope

Phase 1 implements `match` expressions (D-010) with the pattern types defined in
[[pattern-types.language]]: literal, variable, wildcard, record (open-row), exact-length
list, and cons-list (`[h | t]`). `if/then/else` becomes sugar for Boolean match.
No ADTs, no constructor patterns, no exhaustiveness checking, no pattern destructuring
in lambda/when/let (those are [[pattern-reuse.language]], future work).

The design is fully specified across six existing zettels:

- [[match-syntax.language]] -- ML-style `match expr | pat -> body`
- [[pattern-types.language]] -- `Pattern` sealed hierarchy
- [[match-type-checking.language]] -- elaboration algorithm (unification-based)
- [[core-match.language]] -- `CoreMatch` IR node + `Alternative`
- [[pattern-reuse.language]] -- why `Pattern` must be designed for reuse (lambda/when/let)
- [[type-level-matching.types]] -- NbE dual (theoretical, not implemented here)

## Design Decisions (pre-settled)

- **D-010**: `match` is the primitive; `if` is sugar
- **D-029**: open rows came first (done); pattern matching follows
- **No `with` keyword**: `match expr | pat -> body` (per [[match-syntax.language]])
- **No exhaustiveness**: runtime `EvaluationException` on no-match (per [[match-type-checking.language]])
- **De Bruijn binding order**: alphabetical by field name for record patterns (per [[match-type-checking.language]])
- **Record patterns are open-row**: `{ name: n }` matches any record with a `name` field

## File Changes

### 1. ANTLR Grammar

**[PiescriptLexer.g4](src/main/antlr/PiescriptLexer.g4)**: `MATCH` token already exists (line 20). No lexer changes needed.

**[PiescriptAntlrParser.g4](src/main/antlr/PiescriptAntlrParser.g4)**: Add `MatchExpr` rule and `pattern` hierarchy.

```
// In the expr rule, add before pipeExpr:
| MATCH expr alternative+                              # MatchExpr

// New rules:
alternative
    : BAR pattern ARROW expr
    ;

pattern
    : UNDERSCORE                                      # WildcardPattern
    | INTEGER_LITERAL                                 # IntLitPattern
    | DECIMAL_LITERAL                                 # DecLitPattern
    | QUOTED_STRING                                   # StringLitPattern
    | TRUE                                            # TrueLitPattern
    | FALSE                                           # FalseLitPattern
    | NULL                                            # NullLitPattern
    | LOWER_IDENT                                     # VarPattern
    | LBRACE RBRACE                                   # EmptyRecordPattern
    | LBRACE recordPatField (COMMA recordPatField)* (BAR LOWER_IDENT)? RBRACE  # RecordPattern
    | LBRACKET RBRACKET                               # EmptyListPattern
    | LBRACKET pattern (COMMA pattern)* RBRACKET      # ExactListPattern
    | LBRACKET pattern BAR pattern RBRACKET           # ConsListPattern
    ;

recordPatField
    : ident COLON pattern                             // field with sub-pattern
    | LOWER_IDENT                                     // shorthand: `{ name }` = `{ name: name }`
    ;
```

Key grammar notes:

- `BAR` (`|`) already exists as a token, used by record update and row types -- no ambiguity since `alternative` is bracketed by `MATCH ... expr` context
- Record shorthand `{ name }` desugars to `{ name: name }` in the elaborator (same as JS destructuring)
- Nested patterns are recursive (`recordPatField` contains `pattern`)

### 2. Pattern Sealed Hierarchy

**New file: [Pattern.java](src/main/java/org/elasticsearch/xpack/piescript/core/Pattern.java)**

```java
public sealed interface Pattern permits
    Pattern.LitPat, Pattern.VarPat, Pattern.WildcardPat,
    Pattern.RecordPat, Pattern.ListPat, Pattern.ConsListPat {

    record LitPat(LitVal value) implements Pattern {}
    record VarPat(@Nullable String debugName, MonoType type) implements Pattern {}
    record WildcardPat() implements Pattern {}
    record RecordPat(Map<String, Pattern> fields, boolean hasTail,
                     @Nullable String tailName) implements Pattern {}
    record ListPat(List<Pattern> elements) implements Pattern {}
    record ConsListPat(Pattern head, Pattern tail) implements Pattern {}
}
```

Lives in `piescript.core` alongside `CoreExpr` -- patterns are IR-level constructs, not surface syntax.

### 3. CoreMatch IR Node

**New file: [CoreMatch.java](src/main/java/org/elasticsearch/xpack/piescript/core/CoreMatch.java)**

```java
public final class CoreMatch extends CoreExpr {
    private final List<Alternative> arms;  // each arm: Pattern + CoreExpr body
    // scrutinee is children.get(0)
    // arm bodies are children.get(1..n)
}
```

**New file: [Alternative.java](src/main/java/org/elasticsearch/xpack/piescript/core/Alternative.java)**

```java
public record Alternative(Pattern pattern, CoreExpr body) {}
```

**[CoreExpr.java](src/main/java/org/elasticsearch/xpack/piescript/core/CoreExpr.java)**: Add `CoreMatch` to the `permits` clause.

### 4. Elaborator -- New `Matches.java` Helper

**New file: [Matches.java](src/main/java/org/elasticsearch/xpack/piescript/elab/Matches.java)**

Following the existing delegation pattern (`Lambda.java`, `Records.java`, `Let.java`, `Sends.java`, etc.):

- `static CoreExpr match(MatchExprContext ctx, ElaborationContext ectx, Elaborator elab)` -- entry point
- `static CoreExpr desugarIf(IfExprContext ctx, ElaborationContext ectx, Elaborator elab)` -- `if c then a else b` -> `CoreMatch` with `LitPat(true)` / `LitPat(false)`
- `private static PatternResult inferPattern(PatternContext pctx, ElaborationState state, Elaborator elab, Elaborator.Src src)` -- returns `PatternResult(Pattern pat, MonoType patType, List<Binding> bindings)`
- Pattern type inference per [[match-type-checking.language]]:
  - Infer scrutinee type -> `τ_scrut`
  - Fresh meta for result type -> `?result`
  - For each arm `| pattern -> body`:
    - Infer pattern type -> `τ_pat` (generating metas for pattern variables)
    - Emit constraint: `τ_scrut ~ τ_pat` (unify scrutinee with pattern)
    - Extend environment with pattern variables (their metas are now constrained)
    - Infer body type in extended env -> `τ_body`
    - Emit constraint: `?result ~ τ_body` (all branches agree on result type)
  - Result type of the match expression is `?result`

**[Elaborator.java](src/main/java/org/elasticsearch/xpack/piescript/elab/Elaborator.java)**:

- In `elaborate()` switch: add `case MatchExprContext` -> `Matches.match(...)`
- Change `IfExprContext` from throwing to calling `Matches.desugarIf(...)`
- In `check()`: add `case MatchExprContext` -> check mode (propagate expected type to arm bodies)

### 5. Evaluator -- Pattern Matching Logic

**New file: [EvalMatch.java](src/main/java/org/elasticsearch/xpack/piescript/eval/EvalMatch.java)**

Following the existing delegation pattern (`EvalBuiltins.java`, `EvalCoordination.java`, etc.):

- `static void evaluateMatch(CoreMatch match, Value[] env, Evaluator eval, ActionListener<Value> listener)` -- evaluate scrutinee, try arms top-to-bottom
- `private static MatchResult tryMatch(Pattern pat, Value scrutinee)` -- returns `Optional<Map<Integer, Value>>` (de Bruijn position -> bound value) or empty on mismatch
- Individual pattern matchers:
  - `LitPat`: compare `LitVal` against runtime value (Double/Keyword/Boolean/Null equality)
  - `VarPat`: always matches, binds the value
  - `WildcardPat`: always matches, binds nothing
  - `RecordPat`: check all fields present in `RecordVal`, recursively match sub-patterns; if `hasTail`, bind remaining fields as a new `RecordVal`
  - `ListPat`: check `ListVal` length matches, recursively match elements
  - `ConsListPat`: check `ListVal` non-empty, match head against first element, match tail against `ListVal` of rest

**[Evaluator.java](src/main/java/org/elasticsearch/xpack/piescript/eval/Evaluator.java)**:

- Add `case CoreMatch` in `evaluate()` switch -> delegate to `EvalMatch.evaluateMatch(...)`

### 6. Serialization

**[CoreExprSerialization.java](src/main/java/org/elasticsearch/xpack/piescript/core/CoreExprSerialization.java)**:

- New tag: `TAG_MATCH = 18`
- Write: tag + scrutinee + arm count + (pattern + body) per arm
- Read: inverse

**New or extended: Pattern serialization** (in `CoreExprSerialization` or a new `PatternSerialization.java`):

- Stable byte tags per `Pattern` variant (0-5)
- Recursive for nested patterns (RecordPat fields, ListPat elements, ConsListPat head/tail)

### 7. CorePrinter

**[CorePrinter.java](src/main/java/org/elasticsearch/xpack/piescript/core/CorePrinter.java)**:

- Add `case CoreMatch` to `printExpr` / `printExprRaw` -- format as `match scrutinee | pat -> body | ...`
- Add `printPattern(Pattern pat)` helper

### 8. Builder DSL

**[Exprs.java](src/main/java/org/elasticsearch/xpack/piescript/core/Exprs.java)** (if it exists, or inline in tests):

- `match(CoreExpr scrutinee, Alternative... arms)` factory
- `arm(Pattern pat, CoreExpr body)` factory

### 9. Tests

**Unit tests:**

- `PatternTests.java` -- Pattern construction, equality, sealed hierarchy
- `CoreMatchTests.java` -- CoreMatch construction, children, replaceChildren
- `ElaboratorTests.java` -- add match test cases:
  - Basic literal match (Double, Keyword, Boolean)
  - Variable binding in match arm
  - Wildcard arm
  - Record pattern (open-row, with tail)
  - List pattern (exact, cons)
  - `if/then/else` desugaring
  - Type error: arms with inconsistent result types
  - Type error: scrutinee/pattern type mismatch
  - Nested patterns
- `EvaluatorTests.java` -- add match evaluation cases:
  - Literal matching (first arm wins)
  - Variable binding
  - Wildcard fallback
  - Record destructuring
  - List destructuring
  - No-match -> `EvaluationException`
  - Boolean literal matching (covers if/else runtime behavior)

**Integration tests:**

- `PiescriptIT.java` -- add REST-level match tests:
  - `match 42 | 42 -> "yes" | _ -> "no"` -> `"yes"`
  - `if true then 1 else 2` -> `1`
  - Record destructuring from ESQL results
  - List head/tail decomposition

### 10. Documentation Updates

- **[current-state.md](docs/current-state.md)**: Move pattern matching from "What Does Not Exist" to "What Works"
- **[thread.md](docs/design-space/thread.md)**: Append session block for this work
- **Thread hubs**: Update [[error-handling.thread]] and [[language-expressiveness.thread]] to mark pattern matching as implemented
- **Queue**: Mark pattern matching resolved in [[global-pending.queue]]

### 11. New Zettels

`**pattern-matching-phase1.implementation.md` -- implementation zettel connecting the plan to the design space:

- Tags: `[language, control-flow, implementation, now]`
- Refs: `plan:pattern_matching_phase1`, `thread:error-handling`, `thread:language-expressiveness`
- Connections: `implements: [[pattern-matching.hub]]`, `implements: [[core-match.language]]`, `implements: [[match-syntax.language]]`, `implements: [[pattern-types.language]]`, `implements: [[match-type-checking.language]]`
- Scope: what Phase 1 covers vs defers (no ADTs, no exhaustiveness, no lambda/when/let destructuring)

`**pattern-matching-phase1.queue.md` -- queue zettel tracking the implementation steps:

- Tags: `[queue, implementation]`
- Refs: `plan:pattern_matching_phase1`
- Contains the checklist of implementation steps.

`**if-as-match-sugar.language.md` -- atomic zettel documenting the `if`->`CoreMatch` desugaring:

- Tags: `[language, syntax, implementation, decision]`
- Refs: `adr:D-010`
- Documents: `if c then a else b` -> `CoreMatch(c, [Alternative(LitPat(true), a), Alternative(LitPat(false), b)])`
- Connections: `part-of: [[pattern-matching.hub]]`, `implements: D-010`

## Implementation Order

**CRITICAL: Stop and ask the user to review the work after completing each step.**

The tasks are ordered to build bottom-up with testability at each step:

1. **Zettels first** -- create the implementation and queue zettels to track progress
2. **IR layer** (Pattern + CoreMatch + Alternative) -- pure data, immediately testable
3. **Evaluator** (EvalMatch) -- can test with hand-built CoreMatch nodes before parser/elaborator
4. **Grammar** -- add ANTLR rules, regenerate
5. **Elaborator** (Matches.java + desugarIf) -- full pipeline testable
6. **Serialization** -- needed for cross-node match expressions
7. **CorePrinter** -- dev endpoint support
8. **Tests** -- unit tests alongside each step; integration tests at the end
9. **Docs and zettels** -- close the loop (note: the queue and implementation zettels should be updated continuously as steps are completed)

