#!/usr/bin/env python3
"""Tech debt report — prioritized list of tech-debt / task / known-issue zettels.

Usage:
    python3 scripts/tech_debt.py                       # Rich terminal output
    python3 scripts/tech_debt.py --markdown            # plain markdown
    python3 scripts/tech_debt.py --sort priority       # sort by dependency count (default)
    python3 scripts/tech_debt.py --sort alpha           # alphabetical
    python3 scripts/tech_debt.py --include-resolved    # include implemented items
"""

import signal
import sys
from collections import defaultdict

from rich.console import Console
from rich.table import Table
from rich.text import Text

from lib.zettel import (
    load_all_zettels,
    primary_concern,
    refs_with_prefix,
    reverse_dependency_graph,
    zettels_by_tag,
)

signal.signal(signal.SIGPIPE, signal.SIG_DFL)

DEBT_TAGS = {"tech-debt", "task", "known-issue"}


def main() -> None:
    args = sys.argv[1:]
    markdown = "--markdown" in args
    include_resolved = "--include-resolved" in args

    sort_mode = "priority"
    if "--sort" in args:
        idx = args.index("--sort")
        if idx + 1 < len(args):
            sort_mode = args[idx + 1]

    all_zettels = load_all_zettels()
    rev_deps = reverse_dependency_graph(all_zettels)

    debt = [z for z in all_zettels if DEBT_TAGS & set(z["tags"])]
    if not include_resolved:
        debt = [z for z in debt if "implemented" not in z["tags"]]

    dep_count: dict[str, int] = {}
    for z in debt:
        dep_count[z["stem"]] = len(rev_deps.get(z["stem"], []))

    grouped: dict[str, list[dict]] = defaultdict(list)
    for z in debt:
        grouped[primary_concern(z)].append(z)

    for items in grouped.values():
        if sort_mode == "alpha":
            items.sort(key=lambda z: z["title"].lower())
        else:
            items.sort(key=lambda z: dep_count[z["stem"]], reverse=True)

    total = sum(len(v) for v in grouped.values())

    if markdown:
        _print_markdown(grouped, dep_count, total)
    else:
        _print_rich(grouped, dep_count, total)


def _print_markdown(grouped, dep_count, total):
    print(f"# Tech Debt Report ({total} items)\n")
    for concern in sorted(grouped):
        items = grouped[concern]
        print(f"## {concern.title()} ({len(items)} items)\n")
        print("| Priority | Item | File | Tags | ADRs | Depended on by |")
        print("|----------|------|------|------|------|----------------|")
        for z in items:
            dc = dep_count[z["stem"]]
            tags = ", ".join(t for t in z["tags"] if t not in DEBT_TAGS)
            adrs = ", ".join(refs_with_prefix(z, "adr:")) or "\u2014"
            print(
                f"| {dc} | {z['title']} | `{z['file']}` | {tags} | {adrs} | {dc} items |"
            )
        print()


def _priority_style(count: int) -> str:
    if count >= 3:
        return "bold red"
    if count >= 1:
        return "yellow"
    return "dim"


def _print_rich(grouped, dep_count, total):
    console = Console()
    console.print(f"\n[bold]Tech Debt Report ({total} items)[/bold]\n")

    for concern in sorted(grouped):
        items = grouped[concern]
        table = Table(
            title=f"{concern.title()} ({len(items)} items)",
            title_style="bold cyan",
            show_lines=False,
        )
        table.add_column("Pri", justify="right", width=4)
        table.add_column("Item", style="bold")
        table.add_column("File", style="dim")
        table.add_column("Tags")
        table.add_column("ADRs")
        table.add_column("Deps", justify="right")

        for z in items:
            dc = dep_count[z["stem"]]
            tags = ", ".join(t for t in z["tags"] if t not in DEBT_TAGS)
            adrs = ", ".join(refs_with_prefix(z, "adr:")) or "\u2014"
            pri = Text(str(dc), style=_priority_style(dc))
            deps_text = Text(str(dc), style=_priority_style(dc))
            table.add_row(pri, z["title"], z["file"], tags, adrs, deps_text)

        console.print(table)
        console.print()


if __name__ == "__main__":
    main()
