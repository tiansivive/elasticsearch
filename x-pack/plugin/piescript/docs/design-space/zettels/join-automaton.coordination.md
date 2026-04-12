---
tags: [coordination, channels, search, theoretical, needs-design, later, concept]
refs:
  - doc:references.md
  - thread:distributed-coordination
---
# Join Automaton

An automaton for pattern matching over multi-value channel message stores. When [[multi-value-channels.coordination|multi-value channels]] arrive, `when` patterns need to match over accumulated messages -- not just wait for a single value per channel. A join automaton compiles these patterns into an efficient dispatch table.

The automaton structure:
- **States**: sets of partially-matched join patterns
- **Transitions**: message arrivals that advance partial matches
- **Accepting states**: all heads of a join pattern matched -- fire the reaction
- **Conflict resolution**: when multiple patterns match simultaneously, committed-choice semantics (first match wins, no backtracking)

This is the runtime counterpart to [[cham-patterns.coordination|CHAM patterns]] -- CHAM describes the semantics (chemical reactions on message stores), the join automaton provides the efficient implementation (compiled dispatch instead of brute-force store scanning).

The theoretical basis is Fruhwirth's CHR scheduling: [[fruhwirth-chr.paper|Fruhwirth (1998)]] analyzes how to efficiently detect when multi-headed rules become fireable as constraints arrive incrementally. The same analysis applies to join patterns over message stores.

**Depends on**: [[multi-value-channels.coordination]], [[when-synchronization.coordination]]
**Enables**: (none directly -- prerequisite for efficient multi-value when)
**Connections**:
- uses: [[multi-value-channels.coordination]] -- automaton operates over multi-value channel stores
- extends: [[when-synchronization.coordination]] -- generalizes single-value when to pattern matching over message stores
- uses: [[cham-patterns.coordination]] -- CHAM provides the semantic model; automaton provides the implementation
- informs: [[fruhwirth-chr.paper]] -- CHR scheduling theory is the basis for efficient join automaton compilation
- analogous-to: [[pattern-matching.hub]] -- value-level pattern matching compiles to decision trees; join patterns compile to join automata
- informs: [[backtracking.search]] -- join automaton uses committed-choice (no backtracking), but the compilation shares techniques with search
- part-of: [[future-coordination.roadmap]]
- uses: [[join-calculus.coordination]] -- join patterns are the Join Calculus primitive; the automaton implements them efficiently
