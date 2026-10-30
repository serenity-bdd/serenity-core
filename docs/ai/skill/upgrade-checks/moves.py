#!/usr/bin/env python3
"""Work out where public Serenity classes went between an old release and the current one.

usage: moves.py OLD_VERSION [--json]       # e.g. 3.9.8 or 4.2.34

Downloads the old jars from Maven Central (cached in /tmp/oldjars), indexes the classes of the old and the
current (5.4.1, from the harness classpaths) releases, and for every old class says: unchanged, moved
(a single class with that simple name exists elsewhere in the current release), ambiguous, or gone.
"""
import json, os, re, subprocess, sys, urllib.request, zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, ".."))
ARTIFACTS = ["serenity-core", "serenity-model", "serenity-junit", "serenity-screenplay", "serenity-screenplay-webdriver",
             "serenity-screenplay-rest", "serenity-rest-assured", "serenity-cucumber", "serenity-ensure"]
CACHE = "/tmp/oldjars"
WATCH = ("net.thucydides", "net.serenitybdd")


def fetch(version):
    os.makedirs(CACHE, exist_ok=True)
    jars = []
    for a in ARTIFACTS:
        f = os.path.join(CACHE, "%s-%s.jar" % (a, version))
        if not os.path.exists(f):
            try:
                urllib.request.urlretrieve("https://repo1.maven.org/maven2/net/serenity-bdd/%s/%s/%s-%s.jar" % (a, version, a, version), f)
            except Exception:
                continue
        jars.append(f)
    return jars


def index(jars):
    cls = set()
    for j in jars:
        with zipfile.ZipFile(j) as z:
            for n in z.namelist():
                if n.endswith(".class") and "$" not in n and "/" in n and not n.startswith("META-INF"):
                    c = n[:-6].replace("/", ".")
                    if c.startswith(WATCH) and not c.endswith(("package-info", "module-info")):
                        cls.add(c)
    return cls


def current_jars():
    m2 = os.path.expanduser("~/.m2/repository/net/serenity-bdd")
    v = json.load(open(os.path.join(HERE, "..", "versions.json")))["serenity"]
    jars = []
    for a in ARTIFACTS + ["serenity-playwright", "serenity-screenplay-playwright"]:
        for ver in (v, json.load(open(os.path.join(HERE, "..", "versions.json")))["serenityPlaywrightModules"]):
            f = os.path.join(m2, a, ver, "%s-%s.jar" % (a, ver))
            if os.path.exists(f):
                jars.append(f); break
    return jars


def classify(old_version):
    old, new = index(fetch(old_version)), index(current_jars())
    by_simple = {}
    for c in new:
        by_simple.setdefault(c.rsplit(".", 1)[1], []).append(c)
    res = {}
    for c in sorted(old):
        simple = c.rsplit(".", 1)[1]
        if c in new:
            res[c] = ("unchanged", None)
        else:
            cands = by_simple.get(simple, [])
            if len(cands) == 1:
                res[c] = ("moved", cands[0])
            elif cands:
                res[c] = ("ambiguous", cands)
            else:
                res[c] = ("gone", None)
    return res


if __name__ == "__main__":
    v = sys.argv[1]
    res = classify(v)
    if "--json" in sys.argv:
        print(json.dumps(res, indent=1)); sys.exit()
    import collections
    byp = collections.defaultdict(collections.Counter)
    for c, (st, _) in res.items():
        byp[c.rsplit(".", 1)[0]][st] += 1
    print("old classes: %d" % len(res), dict(collections.Counter(s for s, _ in res.values())))
    for p in sorted(byp, key=lambda p: -sum(byp[p].values()))[:30]:
        print("  %-60s %s" % (p, dict(byp[p])))
