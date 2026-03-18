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

## Bird-Meertens Formalism and Data Parallelism

The algebraic theory of why `map`, `filter`, and `reduce` can be safely parallelized and
distributed. Directly relevant to Block D (push-down compilation): the BMF tells us which
piescript combinators are safe to fuse into ESQL plans and which can be partitioned across
shards/threads. The core insight is that **list homomorphisms** — functions `h` satisfying
`h(xs ++ ys) = h(xs) ⊕ h(ys)` for some associative `⊕` — are automatically parallelizable.

### Bird — *An Introduction to the Theory of Lists* (1987)

Establishes the algebraic framework: lists as a free monoid, and computation over lists as
homomorphisms. `map f` is a homomorphism (preserves concatenation). `fold ⊕ e` is a homomorphism
when `⊕` is associative with identity `e`. `filter p` is a homomorphism (it distributes over
concatenation). These algebraic identities are what make scatter-gather execution correct.

- [PDF (Oxford)](https://www.cs.ox.ac.uk/files/3378/PRG56.pdf)

### Meertens — *Algorithmics: Towards Programming as a Mathematical Activity* (1986)

The companion paper establishing the "Bird-Meertens Formalism" (BMF). Develops a calculus of
program transformations where parallelism emerges from algebraic laws rather than explicit thread
management. The formalism treats parallelizability as a consequence of the algebraic structure
of the computation, not as an annotation.

- [Springer](https://link.springer.com/chapter/10.1007/3-540-16042-6_15)

### Gibbons — *The Third Homomorphism Theorem* (1996)

Proves that if a function over lists can be written as both a `foldl` and a `foldr`, it must be a
list homomorphism — and therefore parallelizable. Provides a mechanical method for discovering
parallelism in sequential code. Relevant to Block D: if a piescript `reduce` can be identified as
a homomorphism (which it is when the combining function is associative), the optimizer can safely
split it across partitions.

- [PDF](http://www.cs.ox.ac.uk/people/jeremy.gibbons/publications/thirdht.pdf)

### Meijer, Fokkinga, Paterson — *Functional Programming with Bananas, Lenses, Envelopes and Barbed Wire* (1991)

Generalizes folds from lists to arbitrary algebraic data types via **catamorphisms** (and their
duals: anamorphisms, hylomorphisms, paramorphisms). The parallelization story extends beyond flat
lists to any initial algebra. Relevant to piescript if it gains algebraic data types — recursive
types can be consumed by catamorphisms that admit the same parallel decomposition as list folds.

- [PDF (Citeseer)](https://maartenfokkinga.github.io/utwente/mmf91m.pdf)

### Blelloch — *Programming Parallel Algorithms* (1996)

Formalizes **nested data parallelism**: parallel operations nested inside other parallel operations,
with a compiler that flattens them into efficient flat parallelism. The NESL language demonstrates
this. Relevant to piescript's future: a `map` over a stream where each element itself triggers a
parallel computation (e.g., `map (fn row -> spawn (query ...)) rows`) is nested data parallelism.

- [PDF (CMU)](https://www.cs.cmu.edu/~guyb/papers/Ble96.pdf)

### Wadler — *Theorems for Free!* (1989)

Parametricity: polymorphic functions satisfy algebraic laws for free, derived from their types
alone. A function `f : ∀a. [a] → [a]` must commute with `map g` for any `g` — no proof needed,
it follows from the type. This is why swapping the underlying container from a local list to a
distributed dataset preserves correctness: the operations are defined only in terms of the
algebraic interface, so any lawful implementation works.

- [PDF](https://homepages.inf.ed.ac.uk/wadler/papers/free/free.ps)
- [ACM DL](https://dl.acm.org/doi/10.1145/99370.99404)

### Lämmel — *Google's MapReduce Programming Model — Revisited* (2008)

Formally analyzes MapReduce through the lens of BMF and list homomorphisms. Shows that the
map-reduce pattern is a specific instance of the Bird-Meertens algebraic framework: `map` is a
list homomorphism, `reduce` (with an associative combiner) is a fold over a commutative monoid,
and the shuffle/group-by phase is a natural transformation. Makes explicit the connection between
the industry practice (Hadoop/MapReduce) and the theory (BMF).

- [PDF (ScienceDirect)](https://www.sciencedirect.com/science/article/pii/S0167642307001281)

### Chambers et al. — *FlumeJava: Easy, Efficient Data-Parallel Pipelines* (ICFP, 2010)

Google's internal system for data-parallel pipelines. Explicitly models computation as deferred
functional combinators over `PCollection`s (parallel collections). The optimizer fuses combinator
chains, performs push-down, and selects execution strategies — the same optimization space as
piescript's Block D. The `PCollection` is essentially a distributed functor.

- [PDF (Google Research)](https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/35650.pdf)

### Yu et al. — *DryadLINQ: A System for General-Purpose Distributed Data-Parallel Computing Using a High-Level Language* (OSDI, 2008)

Takes LINQ — which is monadic comprehension syntax over `IEnumerable` — and executes it
distributedly. The closest existing system to the pattern piescript is building: same functor
interface (map/filter/reduce), different execution backend (local collection vs. distributed
DAG). Shows that the algebraic interface is sufficient to enable transparent distribution.

- [PDF (Microsoft Research)](https://www.microsoft.com/en-us/research/wp-content/uploads/2008/10/DryadLINQ.pdf)

### Relevance to Piescript

The BMF tells us precisely which piescript operations are safe to push down and parallelize:

- **`map f`** is always a homomorphism (distributes over concatenation). Safe to push down to
  shards unconditionally. Block D compiles `map` → ESQL `EVAL`.
- **`filter p`** is always a homomorphism. Safe to push down. Block D compiles `filter` →
  ESQL `WHERE`.
- **`reduce e f`** is a homomorphism when `f` is associative with identity `e` (i.e., `(e, f)`
  forms a monoid). For Block D, the optimizer needs to verify (or the user needs to assert)
  associativity to split a reduce across partitions.
- **User-defined combinators** over streams inherit parallelizability from their algebraic
  structure. If piescript gains typeclasses, a `Monoid` constraint on `reduce`'s combiner would
  make parallelizability a type-level guarantee.

The free monad perspective from [architecture.md](architecture.md) connects to BMF: the residual
of partial evaluation is a tree of coordination effects. Push-down compilation (Block D) fuses
homomorphic combinators into ESQL plans. Non-homomorphic operations (stateful folds, operations
with data dependencies between elements) remain at the piescript level.

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

## Chemical Abstract Machine, Functional-Logic Programming, and Constraint Handling

The theoretical foundations for generalized `when` clause matching: treating channel message stores
as multisets with functional-pattern-based reaction rules and maximal parallel firing.

### Berry & Boudol — *The Chemical Abstract Machine* (TCS, 1992)

The original CHAM paper. Models concurrent computation as chemical reactions over a multiset
("solution") of molecules. Reaction rules fire when matching molecules are present; multiple
non-overlapping reactions fire in parallel (maximal parallelism). The join calculus (Fournet &
Gonthier) restricts the CHAM to simple presence-based matching for distributed implementation
efficiency. Piescript's future direction relaxes this restriction back toward CHAM semantics:
functional patterns as reaction rules, maximal parallel firing over channel message stores.
The performance argument is that channels are coordination (control plane), not data movement
(data plane) — the Exchange handles high-throughput streaming, so control-plane message stores
are small enough for expressive matching.

- [ScienceDirect](https://www.sciencedirect.com/science/article/pii/030439759290185I)

### Antoy & Hanus — *Functional Logic Programming* (CACM, 2010)

Survey of functional-logic programming, covering narrowing (running functions "backwards" to
find inputs satisfying a pattern), residuation, and non-deterministic search. Curry is the
primary language discussed. The key mechanism for piescript: functional patterns — using a
function definition as a pattern, where the runtime narrows (searches) for values that satisfy
the function. Applied to channel message stores, this means the programmer writes a function
describing the *shape* of a match, and the runtime finds all satisfying subsets of accumulated
messages. Narrowing's natural multi-solution enumeration provides the set of all matches, which
can fire concurrently as parallel `when` body executions.

- [ACM DL](https://dl.acm.org/doi/10.1145/1721654.1721675)

### Antoy & Hanus — *Curry: A Truly Integrated Functional Logic Language*

The Curry language report. Defines functional patterns, narrowing strategies (needed narrowing,
parallel narrowing), and non-deterministic functions. Functional patterns allow any function
call on the left-hand side of a rule — the runtime inverts it via narrowing. This is the
specific mechanism envisioned for piescript's generalized `when` clauses: a function used as a
pattern over a channel's message store, with the runtime searching for satisfying assignments.

- [Curry homepage](https://curry.pages.ps.informatik.uni-kiel.de/curry-lang.org/)
- [Curry report (PDF)](https://curry.pages.ps.informatik.uni-kiel.de/curry-lang.org/documentation/report.html)

### Frühwirth — *Theory and Practice of Constraint Handling Rules* (JLP, 1998)

Constraint Handling Rules (CHR): multi-headed rules that fire when multiple constraints match,
with multiset semantics. CHR's simpagation rules (keep some constraints, remove others) are
analogous to channel pattern matching that consumes matched messages while leaving others.
The CHR operational semantics (refined semantics with committed choice) informs how to schedule
pattern matching over channel stores.

- [ScienceDirect](https://www.sciencedirect.com/science/article/pii/S0743106698100055)

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
| Bird (theory of lists) | Algebraic foundation: `map`/`filter`/`reduce` as list homomorphisms (Block D) |
| Gibbons (third homomorphism theorem) | Identifying parallelizable reductions mechanically (Block D) |
| Meijer et al. (bananas/catamorphisms) | Generalizing folds to algebraic data types (future ADTs) |
| Blelloch (nested data parallelism) | Nested parallel computation flattening (Block E) |
| Wadler (theorems for free) | Parametricity guarantees for swapping local → distributed containers |
| Lämmel (MapReduce revisited) | Formal link between scatter-gather execution and BMF |
| FlumeJava / DryadLINQ | Deferred combinator fusion, push-down optimization (Block D) |
| Stark & Fiore (free-algebra models) | Theoretical basis for algebraic effect interpretation (informational) |
| Wu & Schrijvers (fusion for free) | Future: optimization via handler fusion (Block D) |
| BEAM / Erlang | Distribution transparency, OTP supervision, scheduling fairness |
| Bernardy et al. (Linear Haskell) | Linearity on arrows, backward-compatible, practical (D-018) |
| Brady (Idris 2 / QTT) | Multiplicity framework {0, 1, ω} for channels and erasure |
| Orchard et al. (Granule) | Graded modal types for fine-grained resource tracking |
| Berry & Boudol (CHAM) | Future: multiset semantics + maximal parallel firing for generalized `when` patterns |
| Antoy & Hanus (functional-logic / Curry) | Future: narrowing-based functional patterns as `when` reaction rules |
| Frühwirth (CHR) | Future: multi-headed rule scheduling over channel message stores |
