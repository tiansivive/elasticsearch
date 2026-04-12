---
tags: [computation, theoretical, exploration, someday, graph-rewriting]
refs:
  - doc:references.md
  - resource:https://en.wikipedia.org/wiki/Interaction_nets
---
# Interaction Nets

Lafont's model. Computation as local graph rewriting -- two connected principal ports interact, pair is rewritten by rule. Inherently parallel (non-overlapping interactions fire concurrently). Surface analogy to Join Calculus (ports to channels, interaction rules to reaction rules).

HONEST UNCERTAINTY: The analogy may be shallow. Interaction nets target local parallelism (GPU, multicore). Piescript needs distributed code shipping. Whether they can unify both -- local parallel evaluation AND cross-node coordination -- is a research question. Could serve as a lowering IR handling closure conversion, monomorphization, parallel evaluation. But the JVM is not the natural target for interaction nets (no GPU, limited multicore exploitation for graph rewriting).

The interesting question: can interaction nets subsume Join Calculus, treating distributed shipping as just another interaction where one port happens to be remote? If so, local parallelism and distributed coordination become the same mechanism at different scales.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- analogous-to: [[join-calculus.coordination]] -- ports/channels, rules/reactions; depth unknown
- contrasts-with: [[cek-machine.evaluation]] -- graph rewriting vs stack/environment machine; fundamentally different computation models
- uses: [[closure-conversion.compilation]] -- lowering to interaction nets needs closure conversion
- uses: [[monomorphization.compilation]] -- nets need concrete types for port wiring
- extends: [[lowering-pass.performance]] -- interaction nets as a lowering target
- tension-with: [[execution-model.question]] -- very different path from interpreter or bytecode compilation
- tension-with: [[distributed-continuations.obstacle]] -- interactions across node boundaries face the distributed spanning problem
- implemented-by: [[hvm.comparable]] — the only real implementation of interaction nets for functional languages
