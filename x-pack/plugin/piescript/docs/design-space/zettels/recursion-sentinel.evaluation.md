---
tags: [language, evaluation, technique, concept, recursion]
refs: []
---
# Recursion Sentinel

Detecting access to uninitialized recursive bindings at runtime. The backpatch slot starts
with a sentinel value. If the evaluator reads it before initialization, it throws a clear
error: "binding accessed during own initialization."

OCaml does the same (`Undefined_recursive_module`). Necessary because piescript is strict —
unlike Haskell, accessing a recursive binding during its own definition isn't deferred by
laziness.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[recursion.hub]]
- implements: [[implicit-recursion.design]] — the safety net for strict evaluation
- uses: [[tying-the-knot.technique]] — the sentinel is the initial value of the backpatch slot
- contrasts-with: Haskell (laziness avoids this need entirely)
- deferred: currently not implemented — [[guarded-recursion.technique]] catches invalid self-references statically, making the runtime sentinel unnecessary for now. May be added later as a safety net if gaps in the static check are discovered.
