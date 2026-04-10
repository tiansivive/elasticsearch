#!/usr/bin/env python3
"""Vision coverage report — which vision.md sections are covered by zettels.

Usage:
    python3 scripts/vision_coverage.py               # Rich terminal output
    python3 scripts/vision_coverage.py --markdown     # plain markdown
    python3 scripts/vision_coverage.py --trim-only    # only trim candidates
    python3 scripts/vision_coverage.py --detail        # expand zettel descriptions
"""

import signal
import sys
from collections import defaultdict

from rich.console import Console
from rich.table import Table

from lib.zettel import load_all_zettels, refs_with_prefix

signal.signal(signal.SIGPIPE, signal.SIG_DFL)

MATURE_TAGS = {"implemented", "designed"}


def _maturity(z: dict) -> str:
    for tag in ("implemented", "designed", "open"):
        if tag in z["tags"]:
            return tag
    return "open"


def main() -> None:
    args = sys.argv[1:]
    markdown = "--markdown" in args
    trim_only = "--trim-only" in args
    detail = "--detail" in args

    all_zettels = load_all_zettels()

    section_zettels: dict[str, list[dict]] = defaultdict(list)
    for z in all_zettels:
        for ref in refs_with_prefix(z, "vision:"):
            section_zettels[ref].append(z)

    sections = sorted(section_zettels.keys())

    trim_candidates: list[str] = []
    unique_narrative: list[str] = []
    for section in sections:
        zettels = section_zettels[section]
        maturities = {_maturity(z) for z in zettels}
        all_mature = maturities <= MATURE_TAGS
        if all_mature or len(zettels) > 3:
            trim_candidates.append(section)
        else:
            unique_narrative.append(section)

    if markdown:
        _print_markdown(
            section_zettels, sections, trim_candidates, unique_narrative,
            trim_only, detail,
        )
    else:
        _print_rich(
            section_zettels, sections, trim_candidates, unique_narrative,
            trim_only, detail,
        )


def _maturity_summary(zettels: list[dict]) -> str:
    counts: dict[str, int] = defaultdict(int)
    for z in zettels:
        counts[_maturity(z)] += 1
    parts = []
    for mat in ("implemented", "designed", "open"):
        if counts[mat]:
            parts.append(f"{counts[mat]} {mat}")
    return ", ".join(parts)


def _print_markdown(
    section_zettels, sections, trim_candidates, unique_narrative,
    trim_only, detail,
):
    if not trim_only:
        total = len(sections)
        print(f"# Vision Coverage Report ({total} sections)\n")

    if trim_candidates:
        print("## Sections with zettel coverage (trim candidates)\n")
        print("| Vision section | Zettels | Maturity |")
        print("|---------------|---------|----------|")
        for section in trim_candidates:
            zs = section_zettels[section]
            print(
                f"| {section} | {len(zs)} | {_maturity_summary(zs)} |"
            )
        print()

    if not trim_only:
        uncovered = [s for s in sections if s not in trim_candidates]
        if uncovered:
            print("## Sections with light coverage (unique narrative)\n")
            for section in uncovered:
                zs = section_zettels[section]
                print(
                    f"- **{section}** — {len(zs)} zettels ({_maturity_summary(zs)})"
                )
            print()

    if detail:
        print("## Zettel detail per section\n")
        show = trim_candidates if trim_only else sections
        for section in show:
            print(f"### vision:{section}\n")
            for z in sorted(
                section_zettels[section], key=lambda x: x["title"].lower()
            ):
                mat = _maturity(z)
                desc = f" — {z['desc']}" if z["desc"] else ""
                print(f"- {z['file']} [{mat}]{desc}")
            print()


def _print_rich(
    section_zettels, sections, trim_candidates, unique_narrative,
    trim_only, detail,
):
    console = Console()

    if not trim_only:
        console.print(
            f"\n[bold]Vision Coverage Report ({len(sections)} sections)[/bold]\n"
        )

    if trim_candidates:
        table = Table(
            title="Sections with zettel coverage (trim candidates)",
            title_style="bold green",
        )
        table.add_column("Vision section", style="bold")
        table.add_column("Zettels", justify="right")
        table.add_column("Maturity")
        for section in trim_candidates:
            zs = section_zettels[section]
            table.add_row(section, str(len(zs)), _maturity_summary(zs))
        console.print(table)
        console.print()

    if not trim_only and unique_narrative:
        table = Table(
            title="Sections with light coverage (unique narrative)",
            title_style="dim",
        )
        table.add_column("Vision section", style="bold")
        table.add_column("Zettels", justify="right")
        table.add_column("Maturity")
        for section in unique_narrative:
            zs = section_zettels[section]
            table.add_row(section, str(len(zs)), _maturity_summary(zs))
        console.print(table)
        console.print()

    if detail:
        show = trim_candidates if trim_only else sections
        for section in show:
            console.print(f"[bold cyan]vision:{section}[/bold cyan]")
            for z in sorted(
                section_zettels[section], key=lambda x: x["title"].lower()
            ):
                mat = _maturity(z)
                style = "green" if mat == "implemented" else ("yellow" if mat == "designed" else "white")
                desc = f" — {z['desc'][:80]}" if z["desc"] else ""
                console.print(
                    f"  [{style}]{z['file']}[/{style}] [{mat}]{desc}"
                )
            console.print()


if __name__ == "__main__":
    main()
