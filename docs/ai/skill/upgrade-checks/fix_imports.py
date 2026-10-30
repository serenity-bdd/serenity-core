#!/usr/bin/env python3
"""Rewrite the imports of a Serenity 2.x/3.x project to the packages used by Serenity 4.x and 5.x.

usage: fix_imports.py PROJECT_DIR [--dry-run]

Changes only classes that moved to exactly one new place (derived from the jars by moves.py: 3.9.8 vs the
current release). Fully qualified names in code and wildcard imports of a package whose classes moved are
rewritten as well. Prints what it could not decide (ambiguous or removed classes) for a person to look at.
"""
import json, os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import moves  # noqa: E402


def mapping(old_version="3.9.8"):
    res = moves.classify(old_version)
    simple = {c: n for c, (st, n) in res.items() if st == "moved"}
    pkgs = {}
    for c, n in simple.items():
        pkgs.setdefault(c.rsplit(".", 1)[0], set()).add(n.rsplit(".", 1)[0])
    unchanged_pkgs = {c.rsplit(".", 1)[0] for c, (st, _) in res.items() if st == "unchanged"}
    unresolved = {c: (st, n) for c, (st, n) in res.items() if st in ("ambiguous", "gone")}
    return simple, pkgs, unchanged_pkgs, unresolved


def fix_file(path, simple, pkgs, unchanged_pkgs, unresolved, dry=False):
    text = open(path, encoding="utf-8", errors="ignore").read()
    orig = text
    changes = []
    for old in sorted(simple, key=len, reverse=True):
        new = simple[old]
        if re.search(r"\b" + re.escape(old) + r"\b", text):
            text = re.sub(r"\b" + re.escape(old) + r"\b", new, text)
            changes.append("%s -> %s" % (old.rsplit(".", 1)[1], new))
    # wildcard imports: import [static] old.pkg.*;
    for oldpkg, newpkgs in pkgs.items():
        pat = re.compile(r"^import\s+(static\s+)?" + re.escape(oldpkg) + r"\.\*;\s*$", re.M)
        def repl(m):
            lines = []
            if oldpkg in unchanged_pkgs:      # some classes stayed: keep the old wildcard too
                lines.append(m.group(0).strip())
            for np in sorted(newpkgs):
                lines.append("import %s%s.*;" % (m.group(1) or "", np))
            changes.append("wildcard %s.* -> %s" % (oldpkg, ", ".join(sorted(newpkgs))))
            return "\n".join(lines)
        text = pat.sub(repl, text)
    notes = [c for c in unresolved if re.search(r"\b" + re.escape(c) + r"\b", text) or re.search(r"import\s+" + re.escape(c.rsplit(".", 1)[0]) + r"\.\*;", text)]
    if text != orig and not dry:
        open(path, "w", encoding="utf-8").write(text)
    return changes, notes


def main():
    root = sys.argv[1]
    dry = "--dry-run" in sys.argv
    simple, pkgs, unchanged_pkgs, unresolved = mapping()
    total = 0
    for dirpath, _, files in os.walk(root):
        if "/target" in dirpath or "/.git" in dirpath:
            continue
        for f in files:
            if f.endswith((".java", ".groovy", ".kt")):
                p = os.path.join(dirpath, f)
                changes, notes = fix_file(p, simple, pkgs, unchanged_pkgs, unresolved, dry)
                if changes:
                    total += len(changes)
                    print("%s: %d change(s)" % (os.path.relpath(p, root), len(changes)))
                for n in notes:
                    print("  NEEDS A PERSON: %s (%s)" % (n, unresolved[n][0]))
    print("%d class references rewritten%s" % (total, " (dry run)" if dry else ""))


if __name__ == "__main__":
    main()
