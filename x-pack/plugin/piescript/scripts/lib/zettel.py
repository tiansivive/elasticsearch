"""Shared zettel parsing library for design-space scripts.

Parses YAML frontmatter via PyYAML and extracts body structure (title,
description, dependency/connection edges) via regex.
"""

from __future__ import annotations

import re
from collections import defaultdict
from pathlib import Path
from typing import Any

import yaml

DOCS_DIR = Path(__file__).parents[2] / "docs"
ZETTELS_DIR = DOCS_DIR / "design-space" / "zettels"

LINK_RE = re.compile(r"\[\[([^\]]+)\]\]")

CONCERN_TAGS = [
    "infrastructure",
    "types",
    "language",
    "data",
    "esql",
    "runtime",
    "performance",
    "coordination",
    "lifecycle",
    "external",
    "tooling",
    "security",
    "meta",
]


def parse_frontmatter(raw: str) -> dict[str, Any]:
    """Parse YAML frontmatter text into a dict."""
    result = yaml.safe_load(raw)
    if not isinstance(result, dict):
        return {}
    return result


def parse_zettel(path: Path) -> dict[str, Any] | None:
    """Parse a single zettel file into a structured dict.

    Returns None if the file has no valid frontmatter delimiters.
    """
    text = path.read_text()
    parts = text.split("---", 2)
    if len(parts) < 3:
        return None

    fm = parse_frontmatter(parts[1])
    body = parts[2].strip()

    m = re.search(r"^#\s+(.+)$", body, re.MULTILINE)
    title = m.group(1) if m else path.stem

    lines = body.split("\n")
    desc_lines: list[str] = []
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

    conn_lines: list[str] = []
    in_conn = False
    for line in lines:
        if (
            line.startswith("**Depends")
            or line.startswith("**Enables")
            or line.startswith("**Connections")
        ):
            in_conn = True
        if in_conn:
            conn_lines.append(line)

    depends_on = _extract_section_links(lines, "**Depends on**")
    enables = _extract_section_links(lines, "**Enables**")
    connection_edges = _extract_connection_edges(lines)

    return {
        "file": path.name,
        "stem": path.stem,
        "title": title,
        "frontmatter": fm,
        "tags": fm.get("tags", []),
        "refs": fm.get("refs", []),
        "desc": desc,
        "connections": conn_lines,
        "depends_on": depends_on,
        "enables": enables,
        "edges": connection_edges,
    }


def _extract_section_links(lines: list[str], header: str) -> list[str]:
    """Extract [[slug]] links from a single bold-label line (e.g. **Depends on**)."""
    for line in lines:
        if line.startswith(header):
            return resolve_links(line)
    return []


def _extract_connection_edges(lines: list[str]) -> list[dict[str, str]]:
    """Parse the **Connections** bullet list into structured edges.

    Each bullet looks like: ``- verb: [[target]] — optional note``
    """
    edges: list[dict[str, str]] = []
    in_section = False
    for line in lines:
        if line.startswith("**Connections**"):
            in_section = True
            continue
        if in_section:
            if line.startswith("**") or (line.strip() == "" and edges):
                break
            m = re.match(r"^-\s+(\S+):\s+\[\[([^\]]+)\]\](.*)$", line.strip())
            if m:
                note = m.group(3).lstrip(" —\u2014").strip()
                edges.append(
                    {"verb": m.group(1), "target": m.group(2), "note": note}
                )
    return edges


def resolve_links(text: str) -> list[str]:
    """Extract all [[slug]] references from a string."""
    return LINK_RE.findall(text)


def load_all_zettels(zettels_dir: Path | None = None) -> list[dict[str, Any]]:
    """Load and parse all .md files in the zettels directory."""
    d = zettels_dir or ZETTELS_DIR
    zettels: list[dict[str, Any]] = []
    for path in sorted(d.glob("*.md")):
        z = parse_zettel(path)
        if z:
            zettels.append(z)
    return zettels


def zettels_by_tag(
    zettels: list[dict[str, Any]], *tags: str
) -> list[dict[str, Any]]:
    """Filter zettels whose tags intersect the given set."""
    tag_set = set(tags)
    return [z for z in zettels if tag_set & set(z["tags"])]


def zettels_by_ref_prefix(
    zettels: list[dict[str, Any]], prefix: str
) -> list[dict[str, Any]]:
    """Filter zettels that have at least one ref starting with *prefix*."""
    return [
        z
        for z in zettels
        if any(isinstance(r, str) and r.startswith(prefix) for r in z["refs"])
    ]


def refs_with_prefix(z: dict[str, Any], prefix: str) -> list[str]:
    """Return the values (after the colon) for refs matching a prefix."""
    out: list[str] = []
    for r in z["refs"]:
        if isinstance(r, str) and r.startswith(prefix):
            out.append(r[len(prefix) :])
    return out


def dependency_graph(
    zettels: list[dict[str, Any]],
) -> dict[str, list[str]]:
    """Build a forward adjacency dict from Depends on / Enables edges.

    Keys are zettel stems. ``graph[a]`` lists stems that ``a`` depends on.
    """
    graph: dict[str, list[str]] = defaultdict(list)
    for z in zettels:
        for dep in z["depends_on"]:
            graph[z["stem"]].append(dep)
    return dict(graph)


def reverse_dependency_graph(
    zettels: list[dict[str, Any]],
) -> dict[str, list[str]]:
    """Build a reverse adjacency dict: for each stem, who depends on it."""
    rev: dict[str, list[str]] = defaultdict(list)
    for z in zettels:
        for dep in z["depends_on"]:
            rev[dep].append(z["stem"])
    return dict(rev)


def primary_concern(z: dict[str, Any]) -> str:
    """Return the first concern-level tag on a zettel, or 'other'."""
    for tag in z["tags"]:
        if tag in CONCERN_TAGS:
            return tag
    return "other"


def stem_index(zettels: list[dict[str, Any]]) -> dict[str, dict[str, Any]]:
    """Build a stem -> zettel lookup dict."""
    return {z["stem"]: z for z in zettels}
