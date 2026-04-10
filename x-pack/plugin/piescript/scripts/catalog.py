#!/usr/bin/env python3
"""Design space catalog — prints a structured summary of all zettels.

Usage:
    python3 scripts/catalog.py                 # full catalog (Rich)
    python3 scripts/catalog.py types           # filter by keyword
    python3 scripts/catalog.py --compact       # one-line-per-zettel summary
    python3 scripts/catalog.py --markdown      # plain markdown output
"""

import signal
import sys

from rich.console import Console
from rich.table import Table
from rich.text import Text

from lib.zettel import load_all_zettels

signal.signal(signal.SIGPIPE, signal.SIG_DFL)


def main() -> None:
    args = sys.argv[1:]
    compact = "--compact" in args
    markdown = "--markdown" in args
    filters = [a for a in args if not a.startswith("--")]

    console = Console(force_terminal=not markdown)
    zettels = load_all_zettels()

    if filters:

        def matches(z):
            searchable = z["title"].lower() + " " + z["file"].lower()
            for vals in z["frontmatter"].values():
                if isinstance(vals, list):
                    searchable += " " + " ".join(str(v) for v in vals)
                else:
                    searchable += " " + str(vals)
            return any(f.lower() in searchable for f in filters)

        zettels = [z for z in zettels if matches(z)]

    if markdown:
        _print_markdown(zettels, compact)
    elif compact:
        _print_compact_rich(zettels, console)
    else:
        _print_full_rich(zettels, console)


def _print_markdown(zettels, compact):
    print(f"# Design Space Catalog ({len(zettels)} items)\n")
    for z in zettels:
        if compact:
            tags = ", ".join(z["tags"])
            print(f"- **{z['title']}** `{z['file']}` [{tags}]")
        else:
            print(f"## {z['title']}")
            print(f"`{z['file']}`")
            for key, val in z["frontmatter"].items():
                if isinstance(val, list):
                    print(f"  {key}: {', '.join(str(v) for v in val)}")
                else:
                    print(f"  {key}: {val}")
            if z["desc"]:
                print(f"\n{z['desc']}")
            for line in z["connections"]:
                print(line)
            print()


def _print_compact_rich(zettels, console):
    table = Table(title=f"Design Space Catalog ({len(zettels)} items)")
    table.add_column("Title", style="bold")
    table.add_column("File", style="dim")
    table.add_column("Tags")
    for z in zettels:
        table.add_row(z["title"], z["file"], ", ".join(z["tags"]))
    console.print(table)


def _print_full_rich(zettels, console):
    console.print(
        f"[bold]# Design Space Catalog ({len(zettels)} items)[/bold]\n"
    )
    for z in zettels:
        title = Text(z["title"], style="bold cyan")
        console.print(title)
        console.print(f"  [dim]{z['file']}[/dim]")
        for key, val in z["frontmatter"].items():
            if isinstance(val, list):
                console.print(f"  {key}: {', '.join(str(v) for v in val)}")
            else:
                console.print(f"  {key}: {val}")
        if z["desc"]:
            console.print(f"\n  {z['desc']}")
        for line in z["connections"]:
            console.print(f"  {line}")
        console.print()


if __name__ == "__main__":
    main()
