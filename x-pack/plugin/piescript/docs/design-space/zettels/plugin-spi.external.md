---
tags: [external, designed, concept, needs-design, someday]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:external-interaction
---
# Plugin SPI

Any ES plugin registers piescript builtins with [[type-scheme.types|type schemes]], arities, and Java implementations via `PiescriptExtension` interface. Users get type-safe builtins. Enables Kafka, HTTP, ML inference, custom connectors without changing piescript core.

- `typeSchemes()` returns a map of [[qualified-builtin-namespacing.language|qualified names]] (e.g. `"Kafka.produce"`) to `TypeScheme` objects.
- `arities()` returns the [[currying.language|curried]] argument count for each function (used by the [[evaluator.language|evaluator]] to know when a partial application is fully saturated).
- `execute(name, args, listener)` is the async Java implementation receiving an `ActionListener` for non-blocking completion.

Type schemes use the same `TypeScheme`/`MonoType`/`RowType` classes as the [[prelude.language|Prelude]] -- plugin authors define polymorphic types using [[rigid-variables.types|Rigids]] (type variables) and [[kind-system.types|kind]] annotations. The [[elaboration-architecture.types|elaborator]] validates calls against plugin type schemes identically to built-in functions: [[unification-algorithm.types|unification]], constraint solving, and arity checking all apply uniformly.

Plugins register via ES's standard `java.util.ServiceLoader` mechanism. Discovery happens at node startup; the Prelude is augmented with all discovered extensions before any script is elaborated.

**Depends on**: [[prelude.language]], [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- implements: Layer 2 of external interaction model — each integration is typed and sandboxed
- alternative-to: [[ffi-painless.external]] — both are external interaction layers; SPI for typed builtins, FFI for raw Java calls
- analogous-to: [[module-system.tooling]] — plugin builtins extend the namespace similar to module imports
