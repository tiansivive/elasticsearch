---
tags: [control-flow, continuation, theoretical, concept]
refs: []
---
# Call/CC

Undelimited continuations (Scheme's call/cc). Captures the ENTIRE continuation, not a delimited prefix. Simpler than shift/reset but less composable -- captured continuation includes the whole program context. Historically important (Scheme, SML/NJ) but largely superseded by delimited continuations. Has "continuation sandwich" composability problems.

The key distinction from delimited continuations: call/cc captures everything "above" the current point, all the way to the top of the program. This makes it non-composable -- two libraries both using call/cc can interfere with each other because their captured continuations overlap. Delimited continuations solve this by bounding the capture scope.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- superseded-by: [[shift-reset.control]] -- delimited is strictly more composable
- uses: [[stackful-continuations.control]] -- typically implemented via stack capture
- tension-with: [[distributed-continuations.obstacle]] -- full continuation capture is even harder to distribute
