#!/usr/bin/env python3
"""Pending work report — open items from queues and active thread priorities.

Usage:
    python3 scripts/queue.py                 # all pending items (Rich)
    python3 scripts/queue.py --markdown      # plain markdown
    python3 scripts/queue.py --queues-only   # only queue zettels, skip threads
    python3 scripts/queue.py --threads-only  # only thread priorities, skip queues
"""

import re
import signal
import sys
from pathlib import Path

from rich.console import Console
from rich.panel import Panel
from rich.table import Table

from lib.zettel import ZETTELS_DIR, load_all_zettels

signal.signal(signal.SIGPIPE, signal.SIG_DFL)

CHECKBOX_RE = re.compile(r"^- \[( |x|~)\] (.+)$")


def parse_queue_items(path: Path) -> list[dict]:
    """Parse [ ]/[x]/[~] checkbox items from a queue zettel."""
    items = []
    text = path.read_text()
    for line in text.splitlines():
        m = CHECKBOX_RE.match(line.strip())
        if m:
            status = {"x": "resolved", "~": "dropped", " ": "open"}[m.group(1)]
            items.append({"status": status, "text": m.group(2).strip()})
    return items


def get_thread_members(zettels: list[dict]) -> dict[str, list[dict]]:
    """Find thread hubs and their member zettels with priority tags."""
    threads = [z for z in zettels if "thread" in z["tags"]]
    zettel_by_stem = {z["file"].replace(".md", ""): z for z in zettels}

    priority_tags = {"now", "next", "later", "someday", "ready", "blocked", "needs-design"}
    result = {}

    for thread in threads:
        name = thread["title"]
        members = []
        # Find members via includes: edges in connections
        for line in thread.get("connections", []):
            if "includes:" in line:
                links = re.findall(r"\[\[([^\]]+)\]\]", line)
                for link in links:
                    # Handle display|target syntax
                    target = link.split("|")[-1] if "|" in link else link
                    member = zettel_by_stem.get(target)
                    if member:
                        tags = set(member["tags"])
                        prio = tags & priority_tags
                        maturity = tags & {"implemented", "open", "designed", "tech-debt", "deferred", "blocked"}
                        if maturity & {"implemented"}:
                            continue  # skip done items
                        members.append({
                            "title": member["title"],
                            "file": member["file"],
                            "priority": sorted(prio)[0] if prio else "",
                            "maturity": sorted(maturity)[0] if maturity else "open",
                        })
        if members:
            result[name] = members
    return result


def main() -> None:
    args = sys.argv[1:]
    markdown = "--markdown" in args
    queues_only = "--queues-only" in args
    threads_only = "--threads-only" in args

    console = Console(force_terminal=not markdown)
    zettels = load_all_zettels()

    # --- Queues ---
    if not threads_only:
        queue_zettels = [z for z in zettels if "queue" in z["tags"]]
        for qz in queue_zettels:
            path = ZETTELS_DIR / qz["file"]
            items = parse_queue_items(path)
            open_items = [i for i in items if i["status"] == "open"]

            if markdown:
                print(f"## {qz['title']} ({len(open_items)} open)\n")
                for item in open_items:
                    print(f"- [ ] {item['text']}")
                print()
            else:
                table = Table(title=f"{qz['title']} ({len(open_items)} open)")
                table.add_column("Item")
                for item in open_items:
                    table.add_row(item["text"])
                console.print(table)
                console.print()

    # --- Thread priorities ---
    if not queues_only:
        thread_members = get_thread_members(zettels)

        prio_order = {"now": 0, "next": 1, "ready": 2, "blocked": 3, "needs-design": 4, "later": 5, "someday": 6, "": 7}

        for thread_name, members in sorted(thread_members.items()):
            members.sort(key=lambda m: prio_order.get(m["priority"], 7))

            if markdown:
                print(f"## {thread_name} ({len(members)} pending)\n")
                for m in members:
                    prio = f"[{m['priority']}]" if m["priority"] else ""
                    print(f"- {prio} {m['title']} ({m['maturity']})")
                print()
            else:
                table = Table(title=f"{thread_name} ({len(members)} pending)")
                table.add_column("Priority", style="bold")
                table.add_column("Item")
                table.add_column("Status", style="dim")
                for m in members:
                    table.add_row(m["priority"] or "—", m["title"], m["maturity"])
                console.print(table)
                console.print()


if __name__ == "__main__":
    main()
