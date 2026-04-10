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
./scripts/catalog.py --markdown   # plain markdown output
```

### queue.py — pending work report

Shows open items from queue zettels and active thread priorities.

```bash
./scripts/queue.py                 # all pending items
./scripts/queue.py --markdown      # plain markdown
./scripts/queue.py --queues-only   # only queue zettels
./scripts/queue.py --threads-only  # only thread priorities
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

## Dependencies

- **PyYAML** — YAML frontmatter parsing
- **Rich** — colored terminal tables and formatted output

Declared in `pyproject.toml`. All scripts also support `--markdown` for
plain text output without Rich formatting.
