# References

> **Living doc** — add new references as they become relevant to design discussions.

## Foundational Textbooks

### Milner — *Communicating and Mobile Systems: the π-Calculus* (Cambridge, 1999)

The introductory textbook by the inventor of the π-calculus. 174 pages, accessible. Covers names,
interaction, behavioural equivalence, and type systems for interaction patterns. Best starting point
for building intuition about what names, channels, and mobility mean.

- [Cambridge University Press](https://www.cambridge.org/us/universitypress/subjects/computer-science/communications-information-theory-and-security/communicating-and-mobile-systems-pi-calculus)

### Sangiorgi & Walker — *The π-Calculus: A Theory of Mobile Processes* (Cambridge, 2001)

The graduate-level reference. 580 pages, comprehensive. Covers operational semantics, bisimulation
theory, type systems (sorting, I/O types, linear types), and higher-order π-calculus (process
passing). The chapter on higher-order π-calculus (code/process passing over channels) is directly
relevant to piescript's traveling-closure model. The type system chapters inform future channel
typing.

- [Cambridge University Press](https://www.cambridge.org/gb/universitypress/subjects/computer-science/programming-languages-and-applied-logic/pi-calculus-theory-mobile-processes)

## Original Papers

### Milner, Parrow, Walker — *A Calculus of Mobile Processes, Parts I and II* (Information and Computation, 1992)

The founding paper. Part I defines the calculus and strong bisimulation. Part II covers weak
bisimulation and equational theory. Introduces the π-calculus as an extension of CCS that
naturally expresses processes with changing communication structure through name passing.

- [Part I (LFCS report)](http://www.lfcs.inf.ed.ac.uk/reports/89/ECS-LFCS-89-85/)

### Sangiorgi — *π-Calculus, Internal Mobility, and Agent-Passing Calculi* (TCS, 1996)

Shows that higher-order π-calculus (where processes/code are sent over channels, not just names)
can be encoded in the first-order π-calculus. Theoretical basis for "code mobility is just name
passing." Directly relevant to piescript: traveling closures do not require a fundamentally new
calculus — they can be modeled within standard π.

- [Semantic Scholar](https://www.semanticscholar.org/paper/pi-Calculus%2C-Internal-Mobility%2C-and-Agent-Passing-Sangiorgi/80159843149f602792d36d6c3e65f72bc8b48822)

## Row Types and Record Systems

### Leijen — *Extensible records with scoped labels* (2005)

Defines a system of extensible records with row polymorphism using scoped labels. The key
insight is that rows are flat field sets with optional tail variables, and row unification
operates on field-set differences rather than recursive head/tail decomposition. This is the
basis for piescript's open-row unification (D-030), adapted to our flat `RowType` representation.

- [PDF (Microsoft Research)](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/02/fclabels.pdf)

## Bidirectional Type Checking

### Dunfield & Krishnaswami — *Complete and Easy Bidirectional Typechecking for Higher-Rank Polymorphism* (ICFP, 2013)

The foundational reference for practical bidirectional type checking. Describes how to combine
inference mode (synthesize types bottom-up) and checking mode (propagate expected types
top-down) in a single algorithm. The checking rule for universal types — "to check `e` against
`∀a. τ`, introduce a fresh skolem `a` and check `e` against `τ`" — directly informs piescript's
handling of type annotations (D-034). While piescript currently uses rank-1 polymorphism (not
higher-rank), the bidirectional structure is the same.

- [PDF (arXiv)](https://arxiv.org/pdf/1306.6032.pdf)

## Session Types (Typing Channels)

For the future: type-checking that processes communicate correctly on channels (if process A sends
an `Int` on channel `c`, process B expects an `Int` on `c`).

### Honda, Vasconcelos, Kubo — *Language Primitives and Type Discipline for Structured Communication-Based Programming* (ESOP, 1998)

The original session types paper. Introduces binary session types: types that describe the protocol
on a channel (send int, then receive string, then done).

- [PDF](https://filipendule.github.io/mgs/honda.vasconcelos.kubo.pdf)

### Honda, Yoshida, Carbone — *Multiparty Asynchronous Session Types* (POPL, 2008)

Extends session types from two-party to multi-party protocols. Relevant when `par` blocks involve
more than two communicating processes.

- [ACM DL](https://dl.acm.org/doi/10.1145/1328438.1328472)

### Wadler — *Propositions as Sessions* (ICFP, 2012)

Curry-Howard correspondence for concurrency: propositions in linear logic correspond to session
types, proofs correspond to processes. Provides deadlock-freedom and protocol compliance for free
from the type system. The most relevant paper for piescript's future type system — if channels are
typed with session types derived from linear logic, well-typed programs cannot deadlock.

- [PDF](https://www.pure.ed.ac.uk/ws/portalfiles/portal/18383989/Wadler_2012_Propositions_as_Sessions.pdf)

## The Join Calculus (π-calculus Designed for Distributed Implementation)

Arguably the most practically relevant variant for piescript. The join calculus was specifically
designed to be implementable in distributed systems, avoiding π-calculus constructs (like
input-guarded choice) that are notoriously hard to implement distributedly.

### Fournet & Gonthier — *The Join Calculus: A Language for Distributed Mobile Programming* (2000)

A variant of the π-calculus with explicit locality, strict local synchronization (messages travel
to a destination and interact only after arrival), and multi-way join patterns. Shows how to
restrict process primitives to ones that have efficient distributed implementations while
maintaining full π-calculus expressiveness.

This is the **primary theoretical foundation** for piescript's coordination model (D-040). The
`spawn`/`join`/channel primitives are directly derived from the join calculus's asynchronous
message-passing and reaction rules.

- [Springer](https://link.springer.com/chapter/10.1007/3-540-45699-6_6)
- [Microsoft Research](https://www.microsoft.com/en-us/research/publication/join-calculus-language-distributed-mobile-programming/)

### Fournet & Gonthier — *The Join Calculus: A Language for Distributed Mobile Programming* (Tutorial, 2000)

The tutorial version of the join calculus paper, providing a more accessible introduction with
examples of encoding functions, mutable state, and concurrent data structures using join
patterns. Covers the core calculus, surface code, and operational semantics. Directly informed
the piescript redesign from plan-graph architecture to join-calculus primitives.

- [Microsoft Research (PDF)](https://www.microsoft.com/en-us/research/wp-content/uploads/2017/01/join-tutorial.pdf)

## Implemented Languages (π-calculus in Practice)

### Pierce & Turner — *Pict: A Programming Language Based on the Pi-Calculus*

Statically-typed concurrent language compiling directly from π-calculus. Channels are first-class
values, processes compose, the type system works in practice. Closest language to the theoretical
foundations.

- [Pict homepage](https://www.cis.upenn.edu/~bcpierce/papers/pict/Html/Pict.html)

### Nomadic Pict

Extends Pict with locations and mobile agents (units of computation that migrate between machines).
The type system tracks which channels are local vs. remote. Directly relevant to piescript's
model of code traveling to data nodes.

- [Nomadic Pict homepage](https://www.cs.put.poznan.pl/pawelw/npict/)

### JoCaml — OCaml with Join Calculus Primitives

Adds three keywords (`def`, `reply`, `spawn`) to OCaml. Shows how to embed process calculus
primitives in a practical ML-family language with minimal surface syntax disruption. Very close
to what piescript is doing: embedding process primitives in a functional language.

- [JoCaml homepage](http://jocaml.inria.fr/)
- [Programming in JoCaml (extended)](https://hal.science/inria-00166125/)

## Free Monads, Algebraic Effects, and Process Calculi

### Stark & Fiore — *Free-Algebra Models for the π-Calculus* (TCS, 2008)

Shows π-calculus semantics can be characterized using enriched Lawvere theories and computational
monads. Theoretical basis for representing π-calculus effects as a free monad — directly relevant
to piescript's plan-graph-as-free-monad architecture.

- [ScienceDirect](https://www.sciencedirect.com/science/article/pii/S0304397507007086)

### Wu & Schrijvers — *Fusion for Free: Efficient Algebraic Effect Handlers* (MPC, 2015)

On optimizing free monad / effect handler chains by fusing handlers. Relevant to the plan graph
optimizer: if Π effects are represented as a free monad, handler fusion is how the plan is
optimized before execution.

- [PDF](https://people.cs.kuleuven.be/~tom.schrijvers/Research/papers/mpc2015.pdf)

### Plotkin & Pretnar — *Handlers of Algebraic Effects* (2009)

The foundational framework for algebraic effects and handlers. Effects (exceptions, state,
nondeterminism, I/O) are modeled through equational theories with primitive operations. Handlers
yield models of these theories. Relevant to the general approach of separating effect description
from effect interpretation.

- [PDF](https://homepages.inf.ed.ac.uk/gdp/publications/handling-algebraic-effects.pdf)

## BEAM / Erlang / Elixir — Lessons and Differentiation

Erlang/BEAM is the most successful production system for distributed computation with message
passing. Piescript and BEAM solve related problems from opposite starting points:

- **Erlang is process-centric** (you design process topology, messages find their way).
  **Piescript is data-centric** (you write transforms, the runtime places computation).
- **Erlang processes are stateful, long-lived actors.** Piescript computations are stateless
  and ephemeral — `spawn` forks a computation that completes once and writes to a channel.
- **Erlang is first-order π-calculus** (pids travel, processes stay put). **Piescript is
  higher-order π-calculus** (closures/code travel to data nodes).
- **Erlang effects are immediately executed** (spawn, send). **Piescript effects can be
  described as data** (the free monad residual from partial evaluation — see
  [architecture.md § Theoretical Model](architecture.md)), inspectable and optimizable before
  execution (Block D).

### What piescript can learn from BEAM

- **Distribution transparency**: `Pid ! Message` works identically for local and remote pids.
  Piescript's coordination model should provide similar transparency — the user writes transforms
  without caring whether they run locally or remotely.
- **Hot code loading**: BEAM upgrades running code without stopping processes. Relevant for a
  future module system — updating stored piescript definitions while queries are in flight.
- **OTP patterns**: supervisor trees, gen_server, gen_statem encode decades of reliability
  engineering. If piescript gets long-running processes (continuous queries, materialized views),
  OTP-style supervision informs the design.
- **Preemptive scheduling via reductions**: BEAM counts reductions (function calls, operations)
  to preempt processes fairly. If piescript runs multiple spawned computations concurrently
  on a node, a similar fairness mechanism may be needed.
- **Per-process GC**: BEAM garbage-collects each process independently, avoiding global pauses.
  Relevant if piescript's spawned computations have independent memory lifecycles.

### Where piescript is fundamentally different

- **The coordination IR is optimizable.** Erlang's runtime executes code as-is. Piescript's
  free monad residual (the output of partial evaluation — see architecture.md) is a data
  structure that the optimizer transforms before execution (dead-branch elimination, push-down
  into queries, combinator fusion). This is the free monad advantage (Block D).
- **Static types.** Erlang is dynamically typed (Dialyzer is opt-in, incomplete). Piescript has
  HM inference with row polymorphism. Future: session types for channel protocols, linear types
  for safe code mobility. Well-typed programs cannot send the wrong type on a channel.
- **Columnar vectorized execution.** BEAM is a bytecode register machine optimized for latency.
  Piescript's hot path runs ExpressionEvaluators on columnar Blocks — optimized for throughput
  on analytical workloads.

### Key references

- [The BEAM Book](https://happi.github.io/theBeamBook/) — deep dive into the Erlang runtime
- [BEAM (Wikipedia)](https://en.wikipedia.org/wiki/BEAM_(Erlang_virtual_machine)) — architecture
  overview
- Joe Armstrong's PhD thesis, *Making Reliable Distributed Systems in the Presence of Software
  Errors* (2003) — the design philosophy behind Erlang's fault tolerance model

## Linear Types, QTT, and Substructural Type Systems

For the future: safe resource management, ownership, and (speculatively) mutable shared state.

### Bernardy, Boespflug, Newton, Peyton Jones, Spiwack — *Linear Haskell: Practical Linearity in a Higher-Order Polymorphic Language* (POPL, 2018)

The most directly relevant paper for piescript's linearity story. Attaches linearity to function
arrows rather than bifurcating types into linear and non-linear. Backward-compatible: existing
code typechecks without modification. Implemented in GHC. Demonstrates safe mutable arrays with
pure interfaces and protocol enforcement in I/O.

Key insight for piescript: linearity on arrows (not types) means `Stream` is not inherently
linear — a function that consumes a stream linearly uses `Stream a -o b`, while a function that
shares a stream uses `Stream a -> b`. Most code is unaffected.

- [PDF](https://hal.science/hal-01673536/file/Linear%20Haskell%20practical%20linearity%20in%20a%20higher-order%20polymorphic%20language.pdf)
- [Microsoft Research](https://www.microsoft.com/en-us/research/publication/linear-haskell-practical-linearity-higher-order-polymorphic-language/)

### Brady — *Idris 2: Quantitative Type Theory in Practice* (ECOOP, 2021)

Full-scale implementation of QTT in a practical programming language. Multiplicities {0, 1, ω}
on every binding. 0 = erased (compile-time only), 1 = linear (exactly once), ω = unrestricted.
Demonstrates how QTT enables both erasure and linearity in the same framework.

The theoretical foundation piescript would build on if QTT is adopted — but without dependent
types (piescript's types are not intricate enough to need terms in types).

- [PDF](https://www.type-driven.org.uk/edwinb/papers/idris-qtt.pdf)
- [Dagstuhl](https://drops.dagstuhl.de/entities/document/10.4230/LIPIcs.ECOOP.2021.9)

### Orchard et al. — *Quantitative Program Reasoning with Graded Modal Types* (ICFP, 2019)

Granule: a linear functional language with graded modal types. Generalizes linear types by
quantifying non-linear use through indexed modalities. Addresses resource usage bounds, security
levels, effect tracking, and cost properties — all through the same framework.

More expressive than QTT's {0, 1, ω} semiring — supports arbitrary graded modalities. Relevant
if piescript ever needs finer-grained usage tracking (e.g., "used at most N times" or
"used with security level L").

- [PDF](https://www.cs.kent.ac.uk/people/staff/dao7/publ/granule-icfp19.pdf)
- [Granule Project](https://granule-project.github.io/)
- [GitHub](https://github.com/dorchard/granule)

## Relevance to Piescript

| Reference | Piescript concept it informs |
|-----------|------------------------------|
| Milner (textbook) | Core π-calculus intuition: names, channels, mobility |
| Sangiorgi (agent-passing) | Traveling closures: code mobility reduces to name passing |
| Fournet & Gonthier (join calculus) | **Primary model**: `spawn`/`join`/channels, reaction rules, local synchronization (D-040) |
| Fournet & Gonthier (join tutorial) | Accessible introduction to join patterns, encoding functions and state |
| JoCaml | How to embed process primitives in an ML-family language |
| Leijen (extensible records) | Open-row unification algorithm for row polymorphism (D-030) |
| Dunfield & Krishnaswami (bidirectional) | Checking rule for universal types, annotation elaboration (D-034) |
| Honda et al. (session types) | Future: typing channel protocols for safety |
| Wadler (propositions as sessions) | Future: deadlock-freedom from the type system |
| Stark & Fiore (free-algebra models) | Theoretical basis for algebraic effect interpretation (informational) |
| Wu & Schrijvers (fusion for free) | Future: optimization via handler fusion (Block D) |
| BEAM / Erlang | Distribution transparency, OTP supervision, scheduling fairness |
| Bernardy et al. (Linear Haskell) | Linearity on arrows, backward-compatible, practical (D-018) |
| Brady (Idris 2 / QTT) | Multiplicity framework {0, 1, ω} for channels and erasure |
| Orchard et al. (Granule) | Graded modal types for fine-grained resource tracking |
