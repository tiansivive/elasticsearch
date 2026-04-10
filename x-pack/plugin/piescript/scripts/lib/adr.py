"""Parser for decisions.md — extracts structured ADR records.

Splits on ``## D-NNN:`` heading boundaries and extracts metadata from each
entry despite format variations (bold vs heading sections, optional Phase
line, casing differences in Status, bullet-style D-053).
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from pathlib import Path

from .zettel import DOCS_DIR, resolve_links

DECISIONS_PATH = DOCS_DIR / "decisions.md"

HEADING_RE = re.compile(r"^## D-(\d+):\s*(.+)$")
PHASE_STATUS_RE = re.compile(
    r"\*\*Phase\*\*:\s*(.+?)\s*\|\s*\*\*Status\*\*:\s*(.+)", re.IGNORECASE
)
STATUS_ONLY_RE = re.compile(r"\*\*Status\*\*:\s*(.+)", re.IGNORECASE)
TRACKED_IN_RE = re.compile(r"\*\*Tracked in\*\*:\s*(.+)", re.IGNORECASE)
REF_RE = re.compile(r"\*\*Ref\*\*:\s*(.+)", re.IGNORECASE)
DATE_RE = re.compile(r"\*\*Date\*\*:\s*(.+)", re.IGNORECASE)


@dataclass
class ADR:
    number: int
    title: str
    phase: str | None = None
    status: str = "unknown"
    date: str | None = None
    tracked_in: list[str] = field(default_factory=list)
    refs: list[str] = field(default_factory=list)
    body: str = ""


def normalize_status(raw: str) -> str:
    """Lowercase and strip trailing parenthetical qualifiers for grouping."""
    s = raw.strip().rstrip(".")
    s = re.sub(r"\s*\(.*$", "", s)
    s = re.sub(r",\s*\*\*.*$", "", s)
    return s.lower()


def parse_decisions(path: Path | None = None) -> list[ADR]:
    """Parse all ADR entries from a decisions.md file."""
    p = path or DECISIONS_PATH
    text = p.read_text()
    lines = text.split("\n")

    adrs: list[ADR] = []
    current: ADR | None = None
    body_lines: list[str] = []

    def flush():
        nonlocal current, body_lines
        if current is not None:
            current.body = "\n".join(body_lines).strip()
            _extract_metadata(current)
            adrs.append(current)
        current = None
        body_lines = []

    for line in lines:
        m = HEADING_RE.match(line)
        if m:
            flush()
            current = ADR(number=int(m.group(1)), title=m.group(2).strip())
            continue
        if current is not None:
            body_lines.append(line)

    flush()
    return adrs


def _extract_metadata(adr: ADR) -> None:
    """Scan the body text to populate phase, status, tracked_in, refs, date."""
    for line in adr.body.split("\n"):
        stripped = line.strip().lstrip("- ")

        m = PHASE_STATUS_RE.search(stripped)
        if m:
            adr.phase = m.group(1).strip()
            adr.status = normalize_status(m.group(2))
            continue

        if adr.status == "unknown":
            m = STATUS_ONLY_RE.search(stripped)
            if m:
                adr.status = normalize_status(m.group(1))
                continue

        m = TRACKED_IN_RE.search(stripped)
        if m:
            adr.tracked_in = resolve_links(m.group(1))
            continue

        m = REF_RE.search(stripped)
        if m:
            adr.refs.extend(_parse_ref_values(m.group(1)))
            continue

        m = DATE_RE.search(stripped)
        if m:
            adr.date = m.group(1).strip()
            continue


def _parse_ref_values(text: str) -> list[str]:
    """Extract markdown-linked or plain session/plan references from a Ref line."""
    refs: list[str] = []
    for part in re.split(r"[,;]\s*", text):
        link = re.search(r"\[.*?\]\(([^)]+)\)", part)
        if link:
            refs.append(link.group(1))
        else:
            cleaned = part.strip().strip(".")
            if cleaned:
                refs.append(cleaned)
    return refs


def load_adrs(path: Path | None = None) -> list[ADR]:
    """Convenience alias for parse_decisions."""
    return parse_decisions(path)
