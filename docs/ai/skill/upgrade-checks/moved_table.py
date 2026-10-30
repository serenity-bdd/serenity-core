#!/usr/bin/env python3
"""Print the Markdown table of classes that moved between Serenity 3.x and the current release, grouped by package.

usage: moved_table.py [OLD_VERSION]      (default 3.9.8)
"""
import collections, sys, os
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import moves


def table(old_version="3.9.8"):
    res = moves.classify(old_version)
    groups = collections.defaultdict(list)
    for c, (st, n) in res.items():
        if st == "moved":
            groups[(c.rsplit(".", 1)[0], n.rsplit(".", 1)[0])].append(c.rsplit(".", 1)[1])
    rows = ["| Old package (3.x) | New package | Classes |", "|---|---|---|"]
    for (old, new), names in sorted(groups.items()):
        rows.append("| `%s` | `%s` | %s |" % (old, new, ", ".join("`%s`" % n for n in sorted(names))))
    return "\n".join(rows), res


if __name__ == "__main__":
    print(table(sys.argv[1] if len(sys.argv) > 1 else "3.9.8")[0])
