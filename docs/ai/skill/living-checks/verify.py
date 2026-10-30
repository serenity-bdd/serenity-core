#!/usr/bin/env python3
"""Verify the claims in serenity-bdd/references/living-documentation.md by running them.

usage: verify.py [CASE_ID ...]

Builds the `living-docs` example (browser-free JUnit 5 test classes in packages shop.ordering and shop.browsing) and
`living-docs-annotated` (a class with @Epic/@Feature/@Story), changes it as each case says, runs `mvn clean verify`, and reads the
requirements tree and the outcome JSON files that Serenity writes.
"""
import glob, json, os, re, shutil, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

SRC = os.path.join(repro.EX, "living-docs", "shop")
ANN = os.path.join(repro.EX, "living-docs-annotated", "shop", "annotated")
J = "src/test/java/shop/"


def project(drop=(), extra=None):
    work = tempfile.mkdtemp(prefix="live-")
    proj = os.path.join(work, "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-sp-selenium"), proj, ignore=shutil.ignore_patterns("target"))
    shutil.copytree(SRC, os.path.join(proj, J))
    shutil.copytree(ANN, os.path.join(proj, J, "annotated"))
    for d in drop:
        shutil.rmtree(os.path.join(proj, J, d))
    for path, text in (extra or {}).items():
        full = os.path.join(proj, path)
        os.makedirs(os.path.dirname(full), exist_ok=True)
        repro.write(full, text)
    return proj


def outcomes(proj):
    out = {}
    for f in glob.glob(os.path.join(proj, "target/site/serenity/*.json")):
        j = json.load(open(f))
        if "testSteps" in j or "userStory" in j:
            out[j.get("methodName") or j.get("name")] = j
    return out


def tree(proj):
    """The requirements tree from the report, as indented 'name (type)' lines.

    Every requirement page embeds the subtree for its own level, so use the largest one: the overview page."""
    best = []
    for f in glob.glob(os.path.join(proj, "target/site/serenity/*.html")):
        s = open(f).read()
        m = re.search(r"tsTree = (\[.*?\]);\s", s, re.S)
        if m and '"capability"' in m.group(1):
            lines = []
            def walk(nodes, d):
                for n in nodes:
                    lines.append("  " * d + "%s (%s)" % (n["text"], n["type"]))
                    walk(n.get("nodes", []), d + 1)
            walk(json.loads(m.group(1)), 0)
            if len(lines) > len(best):
                best = lines
    return best


def report_has(proj, text):
    return any(text in open(f).read() for f in glob.glob(os.path.join(proj, "target/site/serenity/*.html")))


def build(proj):
    return repro.run(proj, ["clean", "verify", "-Dheadless.mode=true"])


def case_packages():
    p = project(drop=["annotated"])
    build(p)
    return tree(p), lambda t: "Shop (capability)" in t[0] and any("Ordering (feature)" in l for l in t)


def case_annotations():
    p = project(drop=["ordering", "browsing"])
    build(p)
    return tree(p), lambda t: t == ["Buying books (capability)", "  Paying for an order (feature)", "    Pay by card (story)"]


def case_mixed():
    p = project()
    build(p)
    return tree(p), lambda t: any("Buying books (capability)" in l for l in t) and any("Shop (capability)" in l for l in t) and any("Ordering (feature)" in l for l in t)


def case_readme():
    p = project(drop=["annotated"], extra={"src/test/resources/stories/shop/ordering/readme.md": "## Ordering\n\nREADME-NARRATIVE-TEXT\n"})
    build(p)
    return report_has(p, "README-NARRATIVE-TEXT"), lambda v: v is True


def case_readme_elsewhere():
    p = project(drop=["annotated"], extra={"src/test/resources/shop/ordering/readme.md": "## Ordering\n\nREADME-ELSEWHERE\n"})
    build(p)
    return report_has(p, "README-ELSEWHERE"), lambda v: v is False


def case_narrative_annotation():
    p = project(drop=["annotated"])
    build(p)
    return report_has(p, "collect books in a basket"), lambda v: v is False


def case_titles():
    p = project(drop=["annotated"])
    build(p)
    o = outcomes(p)
    titles = sorted(j["title"] for j in o.values())
    return titles, lambda t: "A reader can add a book to an empty basket" in t and "Should keep every book added to the basket" in t


def case_steps():
    p = project(drop=["annotated"])
    build(p)
    o = outcomes(p)
    steps = [s["description"] for j in o.values() for s in j.get("testSteps", [])]
    return steps, lambda s: "Ada adds 'Refactoring' to the basket" in s and "Search the catalog for 'Refactoring'" in s and "The catalog lists 1 book(s)" in s


def case_class_displayname():
    p = project(drop=["annotated"])
    build(p)
    return tree(p), lambda t: any("Building a basket (feature)" in l for l in t) and any("Searching the catalog (feature)" in l for l in t)


def case_class_suffix():
    p = project(drop=["annotated"])
    repro.sub(os.path.join(p, J, "ordering/WhenAReaderBuildsABasketTest.java"), '@DisplayName("Building a basket")\n', "")
    build(p)
    return tree(p), lambda t: any("When a reader builds a basket test (feature)" in l for l in t)


CASES = dict(class_displayname=case_class_displayname, class_suffix=case_class_suffix, packages=case_packages, annotations=case_annotations, mixed=case_mixed, readme=case_readme,
             readme_elsewhere=case_readme_elsewhere, narrative_annotation=case_narrative_annotation, titles=case_titles, steps=case_steps)


def cuke_project(layout):
    work = tempfile.mkdtemp(prefix="livec-")
    proj = os.path.join(work, "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-cuke-sp"), proj, ignore=shutil.ignore_patterns("target"))
    shutil.copytree(os.path.join(repro.EX, "cuke-plain"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
    feats = os.path.join(proj, "src/test/resources/features")
    os.makedirs(feats, exist_ok=True)
    for f in glob.glob(os.path.join(proj, "src/test/java/features/*")):
        shutil.move(f, feats)
    if layout:
        layout(feats)
    return proj


def regroup(feats):
    os.makedirs(os.path.join(feats, "shopping"))
    for f in ("basket.feature", "maths.feature"):
        shutil.move(os.path.join(feats, f), os.path.join(feats, "shopping", f))
    repro.write(os.path.join(feats, "shopping", "readme.md"), "## Shopping\n\nCUKE-README-TEXT\n")
    repro.sub(os.path.join(feats, "shopping", "basket.feature"), "Feature: Shopping basket\n",
              "Feature: Shopping basket\n\n  FEATURE-DESCRIPTION-TEXT: a reader keeps books in a basket.\n")


def case_cucumber_folders():
    p = cuke_project(regroup)
    build(p)
    t = tree(p)
    return [t, report_has(p, "CUKE-README-TEXT"), report_has(p, "FEATURE-DESCRIPTION-TEXT")], lambda v: any("Shopping" in l for l in v[0]) and v[1] and v[2]


CASES.update(cucumber_folders=case_cucumber_folders)

if __name__ == "__main__":
    want = sys.argv[1:] or list(CASES)
    bad = 0
    for name in want:
        value, ok = CASES[name]()
        res = ok(value)
        bad += not res
        print("%-22s %s   %s" % (name, "ok" if res else "FAIL", json.dumps(value)[:200]))
    sys.exit(1 if bad else 0)
