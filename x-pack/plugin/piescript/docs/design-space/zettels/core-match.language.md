---
tags: [language, ir, implementation, implemented]
refs:
  - thread:language-expressiveness
---
# CoreMatch

New Core IR node for pattern matching. `IfExpr` in the grammar elaborates to `CoreMatch`
with Boolean literal patterns rather than a separate `CoreIf` node.

**IR structure:**
```
CoreMatch(CoreExpr scrutinee, List<MatchArm> arms, MonoType type)
MatchArm(Pattern pattern, CoreExpr body)
```

**Pattern hierarchy (sealed interface):**
```
LitPat(LitVal value)                              -- literal match
VarPat(@Nullable String debugName, MonoType type)  -- binds one variable
WildcardPat()                                       -- matches anything, binds nothing
RecordPat(Map<String, Pattern> fields)              -- record destructure (open-row)
ListPat(List<Pattern> elements)                     -- exact-length list match
ConsListPat(Pattern head, Pattern tail)             -- [h | t] decomposition
```

Future: `ConstructorPat(String name, List<Pattern> args)` added with [[adts.types]].

**Evaluation:** The evaluator tries arms top-to-bottom. For each arm, attempt to match the
scrutinee value against the pattern. On match, bind pattern variables into the de Bruijn
environment and evaluate the body. On no match, try the next arm. If no arm matches,
throw `EvaluationException`.

**Serialization:** `CoreMatch` and `Pattern` need wire format support in
`CoreExprSerialization` — stable byte tags for each pattern variant, recursive serialization
for nested patterns (future).

**Depends on**: [[core-ir.language]], [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- extends: [[core-ir.language]] — new CoreExpr variant
- uses: [[evaluator.language]] — pattern matching logic in the evaluator
- uses: [[serialization.infrastructure]] — wire format for CoreMatch + Pattern
