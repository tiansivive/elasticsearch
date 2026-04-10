# Piescript Design Space Scripts

Analysis and report generation over the piescript design-space zettelkasten.

## Setup

From the piescript root:

```bash
python3 -m venv scripts/.venv
source scripts/.venv/bin/activate
pip install scripts/
```

Or without a venv:

```bash
pip install --user pyyaml rich
```

## Scripts

All scripts are executable and run from the piescript root directory.

### catalog.py — design space catalog

```bash
./scripts/catalog.py              # full catalog
./scripts/catalog.py --compact    # one line per zettel
./scripts/catalog.py types        # filter by keyword
./scripts/catalog.py --es-code-gaps  # ES-internals without code: refs
```

### tech_debt.py — tech debt report

Scans zettels tagged `tech-debt`, `task`, or `known-issue` and produces a
prioritized report grouped by concern area.

```bash
./scripts/tech_debt.py            # Rich terminal output
./scripts/tech_debt.py --markdown # plain markdown (pipeable)
./scripts/tech_debt.py --sort priority  # sort by dependency count (default)
./scripts/tech_debt.py --sort alpha     # sort alphabetically
./scripts/tech_debt.py --include-resolved  # include implemented items
```

### roadmap_status.py — thread-based roadmap dashboard

Reports on thread-based work concerns (zettels tagged `thread`). Finds
members via `includes` edges and `thread:` refs, and shows per-thread
sequences with maturity and priority.

```bash
./scripts/roadmap_status.py                    # Rich terminal output
./scripts/roadmap_status.py --markdown         # plain markdown
./scripts/roadmap_status.py --all              # include someday items
./scripts/roadmap_status.py --thread NAME      # single thread by stem substring
./scripts/roadmap_status.py --queue            # also show global queue
```

### adr_index.py — ADR cross-reference

Cross-references `decisions.md` ADRs with zettel `adr:` refs. Generates an
index table and consistency report.

```bash
./scripts/adr_index.py                    # full index + consistency
./scripts/adr_index.py --markdown          # plain markdown
./scripts/adr_index.py --consistency-only  # just problems
./scripts/adr_index.py --status accepted   # filter by status
```

### vision_coverage.py — vision section coverage

Reports which `vision.md` sections are covered by zettels (candidates for
trimming).

```bash
./scripts/vision_coverage.py              # full report
./scripts/vision_coverage.py --markdown   # plain markdown
./scripts/vision_coverage.py --trim-only  # only trim candidates
./scripts/vision_coverage.py --detail     # expand zettel descriptions
```

## Dependencies

- **PyYAML** — YAML frontmatter parsing
- **Rich** — colored terminal tables and formatted output

Declared in `pyproject.toml`. All scripts also support `--markdown` for
plain text output without Rich formatting.
