#!/usr/bin/env python3
"""Share of JUnit 5 test classes that give the report a business name (class-level @DisplayName, or @Feature/@Story).
usage: class_names.py DIR [DIR ...]   Cucumber-only runs and runs without @Test classes are skipped."""
import glob, json, os, re, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import run_tests

for d in sys.argv[1:]:
    runs = classes = named = runs_all = 0
    for f in sorted(glob.glob(os.path.join(d, "**", "*.json"), recursive=True)):
        if any(x in f for x in ("/review/", "/app-runtime/", "summary", "review.json", "runtime.json")):
            continue
        try:
            j = json.load(open(f))
        except ValueError:
            continue
        if "compiled" not in j:
            continue
        n = k = 0
        for p, c in run_tests.sources(os.path.dirname(f), f).items():
            if p.endswith(".java") and re.search(r"@Test\b", c):
                n += 1
                m = re.search(r"((?:@\w+(?:\([^)]*\))?\s*)*)(?:public\s+)?(?:final\s+)?class\s+\w+", c)
                head = m.group(1) if m else ""
                k += bool(re.search(r"@DisplayName|@Feature|@Story", head))
        if n:
            runs += 1; classes += n; named += k
            runs_all += (k == n)
    print("%-34s runs with test classes %-3d classes %-3d with a business name %3.0f%%  runs where all classes named %3.0f%%" % (
        os.path.basename(d.rstrip("/")), runs, classes, 100 * named / max(classes, 1), 100 * runs_all / max(runs, 1)))
