#!/usr/bin/env python3
"""Print the living documentation of a Serenity run the way a reader sees it, and flag mechanical problems.

usage: outline.py <serenity report dir> [--json]
  (the directory is normally <project>/target/site/serenity; only the *.json outcome files are read)

Prints: the requirements path of each test, its title, and its steps. Then a list of FLAGS for things a reader
would trip on. The flags are objective checks; whether the wording is good is for the reviewer to judge.
"""
import glob, json, os, re, sys

SELECTOR = re.compile(r"(#[\w-]+|\.[a-z][\w-]+\b.*[>\[]|//\w|\[@|By\.\w+|css=|xpath|data-testid|getByRole|\bid=)", re.I)
URL = re.compile(r"https?://|localhost|:\d{4}\b")
HTTP = re.compile(r"\b(?:status(?: code)?|HTTP)\s*[:=]?\s*[1-5]\d\d\b|\b(?:200|201|204|400|401|403|404|422|500)\b")
PLUMBING = re.compile(r"\b(sleep|wait(?:s)? \d|findElement|executeScript|sendKeys|driver\.|click(?:s)? (?:on )?(?:the )?(?:element|button with|link with)|locator)\b", re.I)


def load(d):
    rows = []
    for f in sorted(glob.glob(os.path.join(d, "*.json"))):
        try:
            j = json.load(open(f))
        except ValueError:
            continue
        if not isinstance(j, dict) or "userStory" not in j:
            continue
        steps = []

        def walk(nodes, depth):
            for s in nodes or []:
                steps.append((depth, s.get("description", "")))
                walk(s.get("children"), depth + 1)
        walk(j.get("testSteps"), 0)
        us = j.get("userStory", {})
        rows.append(dict(path=us.get("path", ""), story=us.get("storyName", ""), title=j.get("title") or j.get("name", ""),
                         result=j.get("result", ""), steps=steps, description=j.get("description") or ""))
    return rows


def flags(rows):
    out = []
    if not rows:
        return ["no outcome files found: run the tests first"]
    paths = {r["path"] for r in rows}
    stories = {r["story"] for r in rows}
    if len(rows) >= 4 and len(stories) == 1:
        out.append("all %d scenarios are in one group (%s): split them by feature" % (len(rows), next(iter(stories))))
    depths = {len(re.split(r"[/.]", p)) for p in paths if p}
    if len(rows) >= 6 and max(depths or {0}) < 2:
        out.append("no functional hierarchy: use packages/folders or @Epic/@Feature/@Story to group the scenarios")
    seen = {}
    for r in rows:
        seen.setdefault(r["title"].lower(), []).append(r["path"])
    for t, ps in seen.items():
        if len(ps) > 1:
            out.append("duplicate scenario title '%s' (%d times)" % (t, len(ps)))
    for st in sorted(stories):
        if re.search(r"[a-z]Test$|Tests$|^Test[A-Z]", st or ""):
            out.append("feature '%s' shows with its class name (\"%s\"): put @DisplayName with a business phrase on the class" % (st, re.sub(r"(?<!^)(?=[A-Z])", " ", st).lower()))
    for r in rows:
        t = r["title"]
        where = "%s: " % t
        if re.search(r"\d$", t) or re.match(r"test\b", t, re.I) or t.endswith("()") or "_" in t or " " not in t.strip():
            out.append(where + "title looks like a method name")
        if len(t.split()) < 3:
            out.append(where + "title is too short to state a rule or outcome")
        if SELECTOR.search(t) or HTTP.search(t) or URL.search(t):
            out.append(where + "title contains technical detail (selector, URL or status code)")
        if not r["steps"]:
            out.append(where + "no steps recorded (nothing for a reader to follow)")
        for _, d in r["steps"]:
            if SELECTOR.search(d):
                out.append(where + "step shows a selector or element id: '%s'" % d[:80])
            elif URL.search(d):
                out.append(where + "step shows a URL: '%s'" % d[:80])
            elif PLUMBING.search(d):
                out.append(where + "step is plumbing, not business language: '%s'" % d[:80])
            elif HTTP.search(d) and not re.search(r"\border\b|\bORD-", d, re.I):
                out.append(where + "step mentions an HTTP status: '%s'" % d[:80])
            elif re.match(r"^(click|type|enter|select|open|navigate)\w* ", d, re.I) and not re.search(r"\{|\b(she|he|they|\w+ (?:clicks|types|enters|opens))\b", d, re.I):
                out.append(where + "step is a bare UI action without an actor: '%s'" % d[:80])
    return sorted(set(out))


def main():
    if len(sys.argv) < 2:
        sys.exit(__doc__)
    rows = load(sys.argv[1])
    fl = flags(rows)
    if "--json" in sys.argv:
        print(json.dumps(dict(scenarios=rows, flags=fl), indent=1))
        return
    tree = {}
    for r in sorted(rows, key=lambda r: (r["path"], r["title"])):
        tree.setdefault(r["path"], []).append(r)
    for path, rs in tree.items():
        print(path.replace("/", " > ") or "(no group)")
        for r in rs:
            print("  - %s  [%s]" % (r["title"], r["result"]))
            for depth, d in r["steps"]:
                print("      %s%s" % ("  " * depth, d))
    print("\nFLAGS (%d)" % len(fl))
    for f in fl:
        print("  ! " + f)
    sys.exit(1 if fl else 0)


if __name__ == "__main__":
    main()
