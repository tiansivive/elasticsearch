#!/usr/bin/env python3
"""Design space catalog — prints a structured summary of all zettels.

No external dependencies. Parses YAML frontmatter generically (no hardcoded keys).

Usage:
    python3 docs/design-space/catalog.py              # full catalog
    python3 docs/design-space/catalog.py types         # filter: any zettel whose frontmatter contains "types"
    python3 docs/design-space/catalog.py --compact     # one-line-per-zettel summary
"""

import re, sys
from pathlib import Path

ZETTELS_DIR = Path(__file__).parent / "zettels"


def parse_frontmatter(raw):
    """Parse simple YAML frontmatter into a dict. Handles scalars, [inline lists], and - item lists."""
    result = {}
    current_key = None
    for line in raw.strip().splitlines():
        # key: value or key: [a, b, c]
        m = re.match(r"^(\w[\w-]*):\s*(.*)", line)
        if m:
            key, val = m.group(1), m.group(2).strip()
            bracket = re.match(r"\[(.+)\]", val)
            if bracket:
                result[key] = [v.strip() for v in bracket.group(1).split(",")]
            elif val:
                result[key] = val
            else:
                result[key] = []
            current_key = key
        elif line.strip().startswith("- ") and current_key:
            if not isinstance(result.get(current_key), list):
                result[current_key] = []
            result[current_key].append(line.strip()[2:].strip())
    return result


def parse_zettel(path):
    text = path.read_text()
    parts = text.split("---", 2)
    if len(parts) < 3:
        return None
    fm = parse_frontmatter(parts[1])
    body = parts[2].strip()

    # Title: first # heading
    m = re.search(r"^#\s+(.+)$", body, re.MULTILINE)
    title = m.group(1) if m else path.stem

    # Description: text between title and first **Bold** line or blank line after content
    lines = body.split("\n")
    desc_lines = []
    past_title = False
    for line in lines:
        if line.startswith("# "):
            past_title = True
            continue
        if past_title:
            if line.strip() == "" and desc_lines:
                break
            if line.startswith("**"):
                break
            desc_lines.append(line.strip())
    desc = " ".join(desc_lines).strip()

    # Everything from **Depends on** onward (connection lines)
    conn_lines = []
    in_conn = False
    for line in lines:
        if line.startswith("**Depends") or line.startswith("**Enables") or line.startswith("**Connections"):
            in_conn = True
        if in_conn:
            conn_lines.append(line)

    return {"file": path.name, "title": title, "frontmatter": fm, "desc": desc, "connections": conn_lines}


def main():
    args = sys.argv[1:]
    compact = "--compact" in args
    filters = [a for a in args if not a.startswith("--")]

    zettels = []
    for path in sorted(ZETTELS_DIR.glob("*.md")):
        z = parse_zettel(path)
        if z:
            zettels.append(z)

    # Filter: match against any frontmatter value (tags, refs, etc.) or title
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

    print(f"# Design Space Catalog ({len(zettels)} items)\n")

    for z in zettels:
        if compact:
            tags = ", ".join(z["frontmatter"].get("tags", []))
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


if __name__ == "__main__":
    import signal
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)
    main()
