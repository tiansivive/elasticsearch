---
tags: [infrastructure, esql, tech-debt, task, documentation, later]
refs:
  - code:IndexResolutionPrePass.java
  - thread:data-completeness
---
# ESQL Body Parser

The component that parses ESQL body strings to extract index patterns.
- The current implementation uses an opaque ESQL_BODY token with Java string parsing to find FROM clauses for index resolution
- This creates a double-parse: `IndexResolutionPrePass` extracts index names via string manipulation, then `Queries.query` parses the full ESQL body via the ESQL engine
- This tech debt goes away when the [[antlr-grammar.language]] structurally captures the FROM clause, making index extraction a tree walk rather than string parsing

**Depends on**: [[antlr-grammar.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[antlr-grammar.language]] — the grammar evolution that will eliminate this tech debt
- tension-with: [[compilation-boundary.esql]] — the boundary between piescript and ESQL compilation requires knowing index names before ESQL parsing
