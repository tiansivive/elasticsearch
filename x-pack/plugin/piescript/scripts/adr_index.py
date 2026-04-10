#!/usr/bin/env python3
"""ADR cross-reference — index decisions.md and check zettel consistency.

Usage:
    python3 scripts/adr_index.py                      # full index + consistency
    python3 scripts/adr_index.py --markdown            # plain markdown
    python3 scripts/adr_index.py --consistency-only    # just problems
    python3 scripts/adr_index.py --status accepted     # filter by status
"""

import signal
import sys
from collections import Counter, defaultdict

from rich.console import Console
from rich.panel import Panel
from rich.table import Table

from lib.adr import load_adrs
from lib.zettel import ZETTELS_DIR, load_all_zettels, refs_with_prefix

signal.signal(signal.SIGPIPE, signal.SIG_DFL)


def main() -> None:
    args = sys.argv[1:]
    markdown = "--markdown" in args
    consistency_only = "--consistency-only" in args
    status_filter = None
    if "--status" in args:
        idx = args.index("--status")
        if idx + 1 < len(args):
            status_filter = args[idx + 1].lower()

    adrs = load_adrs()
    all_zettels = load_all_zettels()

    adr_by_number: dict[int, object] = {a.number: a for a in adrs}

    zettel_to_adrs: dict[str, list[str]] = defaultdict(list)
    adr_to_zettels: dict[str, list[str]] = defaultdict(list)
    for z in all_zettels:
        for ref in refs_with_prefix(z, "adr:"):
            zettel_to_adrs[z["stem"]].append(ref)
            adr_to_zettels[ref].append(z["stem"])

    tracked_file_exists: dict[str, bool] = {}
    for adr in adrs:
        for slug in adr.tracked_in:
            path = ZETTELS_DIR / f"{slug}.md"
            tracked_file_exists[slug] = path.exists()

    if status_filter:
        adrs = [a for a in adrs if a.status.startswith(status_filter)]

    status_counts = Counter(a.status for a in load_adrs())

    orphaned_refs: list[tuple[str, str]] = []
    for stem, adr_refs in zettel_to_adrs.items():
        for ref in adr_refs:
            num_str = ref.replace("D-", "").replace("d-", "")
            try:
                num = int(num_str)
            except ValueError:
                continue
            if num not in adr_by_number:
                orphaned_refs.append((stem, ref))

    missing_tracked: list[tuple[int, str]] = []
    for adr in load_adrs():
        for slug in adr.tracked_in:
            if not tracked_file_exists.get(slug, False):
                missing_tracked.append((adr.number, slug))

    superseded_refs: list[tuple[str, str, str]] = []
    for adr in load_adrs():
        if "superseded" in adr.status or "subsumed" in adr.status:
            ref_key = f"D-{adr.number:03d}"
            for stem in adr_to_zettels.get(ref_key, []):
                superseded_refs.append((stem, ref_key, adr.status))

    if markdown:
        _print_markdown(
            adrs, adr_to_zettels, status_counts,
            orphaned_refs, missing_tracked, superseded_refs,
            consistency_only,
        )
    else:
        _print_rich(
            adrs, adr_to_zettels, status_counts,
            orphaned_refs, missing_tracked, superseded_refs,
            consistency_only,
        )


def _print_markdown(
    adrs, adr_to_zettels, status_counts,
    orphaned_refs, missing_tracked, superseded_refs,
    consistency_only,
):
    total = len(load_adrs())
    if not consistency_only:
        print(f"# ADR Index ({total} decisions)\n")
        print("## Summary\n")
        print("| Status | Count |")
        print("|--------|-------|")
        for status, count in sorted(status_counts.items()):
            print(f"| {status} | {count} |")

        print(f"\n## Full Index\n")
        print("| ADR | Title | Status | Phase | Zettel refs |")
        print("|-----|-------|--------|-------|-------------|")
        for adr in adrs:
            ref_key = f"D-{adr.number:03d}"
            zettel_refs = adr_to_zettels.get(ref_key, [])
            tracked_str = ", ".join(adr.tracked_in)
            extra = [s for s in zettel_refs if s not in adr.tracked_in]
            refs_str = tracked_str
            if extra:
                refs_str += f" + {len(extra)} more"
            phase = adr.phase or "\u2014"
            print(
                f"| D-{adr.number:03d} | {adr.title} | {adr.status} | {phase} | {refs_str} |"
            )

    print("\n## Consistency\n")
    all_have_tracked = all(a.tracked_in for a in load_adrs())
    print(
        f"- All {total} ADRs have tracked-in links: "
        f"{'OK' if all_have_tracked else 'MISSING'}"
    )

    if orphaned_refs:
        print(f"- Orphaned zettel refs ({len(orphaned_refs)}):")
        for stem, ref in orphaned_refs:
            print(f"  - `{stem}` references {ref} (not found in decisions.md)")
    else:
        print("- Orphaned zettel refs: (none)")

    if missing_tracked:
        print(f"- Missing tracked-in zettel files ({len(missing_tracked)}):")
        for num, slug in missing_tracked:
            print(f"  - D-{num:03d} tracked-in `{slug}` — file not found")
    else:
        print("- Missing tracked-in zettel files: (none)")

    if superseded_refs:
        print(f"- Zettels referencing superseded ADRs ({len(superseded_refs)}):")
        for stem, ref, status in superseded_refs:
            print(f"  - `{stem}` references {ref} ({status})")
    else:
        print("- Zettels referencing superseded ADRs: (none)")


def _print_rich(
    adrs, adr_to_zettels, status_counts,
    orphaned_refs, missing_tracked, superseded_refs,
    consistency_only,
):
    console = Console()
    total = len(load_adrs())

    if not consistency_only:
        console.print(f"\n[bold]ADR Index ({total} decisions)[/bold]\n")

        summary = Table(title="Status Summary", show_lines=False)
        summary.add_column("Status")
        summary.add_column("Count", justify="right")
        for status, count in sorted(status_counts.items()):
            style = "dim" if "superseded" in status or "subsumed" in status else ""
            summary.add_row(status, str(count), style=style)
        console.print(summary)
        console.print()

        index_table = Table(title="Full Index", show_lines=False)
        index_table.add_column("ADR", width=7)
        index_table.add_column("Title", style="bold")
        index_table.add_column("Status")
        index_table.add_column("Phase")
        index_table.add_column("Zettel refs")

        for adr in adrs:
            ref_key = f"D-{adr.number:03d}"
            zettel_refs = adr_to_zettels.get(ref_key, [])
            tracked_str = ", ".join(adr.tracked_in)
            extra = [s for s in zettel_refs if s not in adr.tracked_in]
            refs_str = tracked_str
            if extra:
                refs_str += f" + {len(extra)} more"
            phase = adr.phase or "\u2014"
            style = (
                "dim"
                if "superseded" in adr.status or "subsumed" in adr.status
                else ""
            )
            index_table.add_row(
                f"D-{adr.number:03d}", adr.title, adr.status, phase, refs_str,
                style=style,
            )
        console.print(index_table)

    lines: list[str] = []
    all_have_tracked = all(a.tracked_in for a in load_adrs())
    lines.append(
        f"All {total} ADRs have tracked-in links: "
        + ("[green]OK[/green]" if all_have_tracked else "[red]MISSING[/red]")
    )

    if orphaned_refs:
        lines.append(f"[red]Orphaned zettel refs ({len(orphaned_refs)}):[/red]")
        for stem, ref in orphaned_refs:
            lines.append(f"  {stem} references {ref} (not found)")
    else:
        lines.append("Orphaned zettel refs: [green](none)[/green]")

    if missing_tracked:
        lines.append(
            f"[red]Missing tracked-in files ({len(missing_tracked)}):[/red]"
        )
        for num, slug in missing_tracked:
            lines.append(f"  D-{num:03d} tracked-in {slug} — file not found")
    else:
        lines.append("Missing tracked-in zettel files: [green](none)[/green]")

    if superseded_refs:
        lines.append(
            f"[yellow]Zettels referencing superseded ADRs ({len(superseded_refs)}):[/yellow]"
        )
        for stem, ref, status in superseded_refs:
            lines.append(f"  {stem} references {ref} ({status})")
    else:
        lines.append(
            "Zettels referencing superseded ADRs: [green](none)[/green]"
        )

    console.print()
    console.print(
        Panel("\n".join(lines), title="Consistency Report", border_style="cyan")
    )


if __name__ == "__main__":
    main()
