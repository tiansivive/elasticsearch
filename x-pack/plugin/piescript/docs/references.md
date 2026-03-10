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

- [Springer](https://link.springer.com/chapter/10.1007/3-540-45699-6_6)
- [Microsoft Research](https://www.microsoft.com/en-us/research/publication/join-calculus-language-distributed-mobile-programming/)

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

## Relevance to Piescript

| Reference | Piescript concept it informs |
|-----------|------------------------------|
| Milner (textbook) | Core π-calculus intuition: names, channels, mobility |
| Sangiorgi (agent-passing) | Traveling closures: code mobility reduces to name passing |
| Fournet & Gonthier (join calculus) | Which process primitives are distributedly implementable |
| JoCaml | How to embed process primitives in an ML-family language |
| Honda et al. (session types) | Future: typing channel protocols for safety |
| Wadler (propositions as sessions) | Future: deadlock-freedom from the type system |
| Stark & Fiore (free-algebra models) | Plan graph as free monad over Π effects |
| Wu & Schrijvers (fusion for free) | Plan graph optimization via handler fusion |
