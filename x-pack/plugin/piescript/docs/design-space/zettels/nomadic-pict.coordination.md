---
tags: [coordination, mobility, theoretical, comparable, prior-art, decision]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Nomadic Pict

Nomadic Pict: mobile agents with type-tracked locality. An extension of the pi-calculus where agents (processes with code and state) can migrate between named sites, and the type system tracks which site an agent is located at. This provides a theoretical foundation for typed [[code-mobility.coordination]] -- knowing at compile time where code will execute.

- Extends [[pict.comparable]] with locations and mobile agents
- Theoretical basis for [[local-kind.types]] located types
- Informs [[data-locality.distributed]] typed location tracking

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- informs: [[code-mobility.coordination]] — typed location tracking
- informs: [[local-kind.types]] — Nomadic Pict's located types are the theoretical basis
- extends: [[pict.comparable]] — Nomadic Pict extends Pict with locations and mobile agents
- informs: [[data-locality.distributed]] — typed location tracking directly relates to data locality guarantees
