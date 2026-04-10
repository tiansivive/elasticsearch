#!/usr/bin/env python3
"""Thread-based roadmap dashboard — parallel work concerns with member status.

Finds zettels tagged `thread`, discovers members via `includes` edges and
`thread:` refs, and reports per-thread sequences with maturity and priority.

Usage:
    python3 scripts/roadmap_status.py                # Rich terminal output
    python3 scripts/roadmap_status.py --markdown     # plain markdown
    python3 scripts/roadmap_status.py --all          # include someday items
    python3 scripts/roadmap_status.py --thread NAME  # single thread by stem substring
    python3 scripts/roadmap_status.py --queue        # also show global queue
"""

import signal
import sys
from collections import defaultdict

from rich.console import Console
from rich.panel import Panel
from rich.table import Table

from lib.zettel import (
    load_all_zettels,
    refs_with_prefix,
    stem_index,
    zettels_by_tag,
)

signal.signal(signal.SIGPIPE, signal.SIG_DFL)

MATURITY_MAP = {
    "implemented": ("done", "[green]\u2713[/green]", "\u2713"),
    "designed": ("designed", "[yellow]\u25d0[/yellow]", "\u25d0"),
    "open": ("open", "[white]\u25cb[/white]", "\u25cb"),
}

PRIORITY_ORDER = ["now", "next", "ready", "later", "needs-design", "blocked", "someday"]


def _maturity(z: dict) -> str:
    for tag in ("implemented", "designed", "open"):
        if tag in z["tags"]:
            return tag
    return "open"


def _priority(z: dict) -> str:
    for p in PRIORITY_ORDER:
        if p in z["tags"]:
            return p
    return ""


def _priority_sort_key(z: dict) -> tuple[int, int]:
    is_implemented = 1 if _maturity(z) == "implemented" else 0
    p = _priority(z)
    priority_idx = PRIORITY_ORDER.index(p) if p in PRIORITY_ORDER else len(PRIORITY_ORDER)
    return (is_implemented, priority_idx)


def _sym_rich(maturity: str) -> str:
    return MATURITY_MAP.get(maturity, MATURITY_MAP["open"])[1]


def _sym_md(maturity: str) -> str:
    return MATURITY_MAP.get(maturity, MATURITY_MAP["open"])[2]


def _find_thread_members(
    thread_z: dict, all_zettels: list[dict], idx: dict[str, dict]
) -> list[dict]:
    """Find members of a thread via includes edges and thread: refs."""
    members_by_stem: dict[str, dict] = {}

    for edge in thread_z["edges"]:
        if edge["verb"] == "includes" and edge["target"] in idx:
            members_by_stem[edge["target"]] = idx[edge["target"]]

    thread_name = thread_z["stem"].replace(".thread", "")
    for z in all_zettels:
        thread_refs = refs_with_prefix(z, "thread:")
        if thread_name in thread_refs:
            members_by_stem[z["stem"]] = z

    members_by_stem.pop(thread_z["stem"], None)
    return list(members_by_stem.values())


def _thread_summary(members: list[dict]) -> dict[str, int]:
    counts: dict[str, int] = defaultdict(int)
    for z in members:
        counts[_maturity(z)] += 1
    return dict(counts)


def main() -> None:
    args = sys.argv[1:]
    markdown = "--markdown" in args
    show_all = "--all" in args
    show_queue = "--queue" in args
    thread_filter = None
    if "--thread" in args:
        i = args.index("--thread")
        if i + 1 < len(args):
            thread_filter = args[i + 1]

    all_zettels = load_all_zettels()
    idx = stem_index(all_zettels)
    threads = zettels_by_tag(all_zettels, "thread")

    if thread_filter:
        threads = [t for t in threads if thread_filter in t["stem"]]

    thread_data = []
    for t in sorted(threads, key=lambda z: z["title"].lower()):
        members = _find_thread_members(t, all_zettels, idx)
        if not show_all:
            members = [m for m in members if "someday" not in m["tags"]]
        members.sort(key=_priority_sort_key)
        thread_data.append((t, members))

    queue_z = None
    if show_queue:
        for z in all_zettels:
            if "queue" in z["tags"]:
                queue_z = z
                break

    if markdown:
        _print_markdown(thread_data, queue_z)
    else:
        _print_rich(thread_data, queue_z)


def _print_markdown(thread_data, queue_z):
    total_members = sum(len(m) for _, m in thread_data)
    print(f"# Roadmap Threads ({len(thread_data)} threads, {total_members} items)\n")

    for t, members in thread_data:
        summary = _thread_summary(members)
        parts = []
        for mat in ("implemented", "designed", "open"):
            if summary.get(mat, 0):
                parts.append(f"{summary[mat]} {mat}")
        summary_str = ", ".join(parts) if parts else "empty"

        print(f"## {t['title']} ({summary_str})\n")
        if t["desc"]:
            print(f"> {t['desc']}\n")

        if members:
            print("| Priority | Status | Item |")
            print("|----------|--------|------|")
            for m in members:
                mat = _maturity(m)
                sym = _sym_md(mat)
                pri = _priority(m)
                shared = ""
                thread_refs = refs_with_prefix(m, "thread:")
                t_name = t["stem"].replace(".thread", "")
                other_threads = [r for r in thread_refs if r != t_name]
                if other_threads:
                    shared = " *(shared: " + ", ".join(other_threads) + ")*"
                print(f"| {pri} | {sym} | {m['title']}{shared} |")
            print()

    if queue_z:
        print("## Global Queue\n")
        print(f"See `{queue_z['file']}`\n")


def _print_rich(thread_data, queue_z):
    console = Console()
    total_members = sum(len(m) for _, m in thread_data)
    console.print(
        f"\n[bold]Roadmap Threads[/bold] "
        f"({len(thread_data)} threads, {total_members} items)\n"
    )

    for t, members in thread_data:
        summary = _thread_summary(members)
        parts = []
        for mat in ("implemented", "designed", "open"):
            if summary.get(mat, 0):
                parts.append(f"{summary[mat]} {mat}")
        summary_str = ", ".join(parts) if parts else "empty"

        table = Table(
            title=f"{t['title']} ({summary_str})",
            show_lines=False,
            title_style="bold cyan",
        )
        table.add_column("Priority", width=14)
        table.add_column("Status", width=8)
        table.add_column("Item", style="bold")
        table.add_column("Shared", style="dim")

        for m in members:
            mat = _maturity(m)
            sym = _sym_rich(mat)
            pri = _priority(m)
            pri_style = {
                "now": "bold red",
                "next": "bold yellow",
                "ready": "green",
                "later": "dim",
                "needs-design": "magenta",
                "blocked": "red",
                "someday": "dim italic",
            }.get(pri, "")

            thread_refs = refs_with_prefix(m, "thread:")
            t_name = t["stem"].replace(".thread", "")
            other_threads = [r for r in thread_refs if r != t_name]
            shared = ", ".join(other_threads) if other_threads else ""

            table.add_row(
                f"[{pri_style}]{pri}[/{pri_style}]" if pri_style else pri,
                sym,
                m["title"],
                shared,
            )

        console.print(table)
        console.print()

    if queue_z:
        console.print(
            Panel(
                f"See [bold]{queue_z['file']}[/bold] for unthreaded pending items.",
                title="Global Queue",
            )
        )


if __name__ == "__main__":
    main()
