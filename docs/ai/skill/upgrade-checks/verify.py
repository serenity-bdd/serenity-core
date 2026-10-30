#!/usr/bin/env python3
"""Verify the claims in serenity-bdd/references/upgrading.md.

usage: verify.py

1. The moved-classes table in the reference equals the one computed from the jars; the counts in the text match.
2. 4.2.34 -> 5.4.1: every class keeps its name and package.
3. Java baselines: class-file versions of the 3.9.8, 4.2.34 and current serenity-core jars.
4. Artifacts on Maven Central: serenity-cucumber4/5/6 stopped in 2.x, serenity-jbehave in 1.x, serenity-junit is current.
5. fix_imports.py: an old-style class is rewritten and then compiles against the current release.
6. JUnit 4 and the JUnit 4 Cucumber runner run on the current release.
The five-project trial (section 4 of the reference) used projects outside this repository and is not re-run here.
"""
import collections, json, os, re, shutil, struct, subprocess, sys, tempfile, urllib.request, zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import moves, moved_table, repro  # noqa: E402

V = json.load(open(os.path.join(HERE, "..", "versions.json")))
REF = os.path.join(HERE, "..", "serenity-bdd", "references", "upgrading.md")
results = []


def check(name, ok, detail=""):
    results.append(ok)
    print("%-92s %s %s" % (name, "ok" if ok else "<-- MISMATCH", detail))


def class_major(jar):
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and "module-info" not in n and not n.startswith("META-INF"):
                return struct.unpack(">H", z.read(n)[6:8])[0]


def latest(artifact):
    xml = urllib.request.urlopen("https://repo1.maven.org/maven2/net/serenity-bdd/%s/maven-metadata.xml" % artifact, timeout=30).read().decode()
    return re.search(r"<release>([^<]+)</release>", xml).group(1)


def main():
    text = open(REF).read()

    # 1 + 2: tables and counts
    table, res = moved_table.table("3.9.8")
    m = re.search(r"<!-- moved-table:start -->\n(.*?)\n<!-- moved-table:end -->", text, re.S)
    check("moved-classes table in the reference equals the one computed from the jars", bool(m) and m.group(1).strip() == table.strip())
    c = collections.Counter(st for st, _ in res.values())
    expect = {"total": len(res), "unchanged": c["unchanged"], "moved": c["moved"], "ambiguous": c["ambiguous"], "gone": c["gone"]}
    nums = ("{:,}".format(expect["total"]), "{:,}".format(expect["unchanged"]), str(expect["moved"]), str(expect["ambiguous"]), str(expect["gone"]))
    check("3.9.8 counts in the text (%s)" % ", ".join(nums), all(n in text for n in nums), str(expect))
    res4 = moves.classify("4.2.34")
    n4 = collections.Counter(st for st, _ in res4.values())
    check("4.2.34 -> current: every class unchanged", set(n4) == {"unchanged"} and "{:,}".format(len(res4)) in text, "%d classes" % len(res4))

    # 3: Java baselines
    cur = os.path.expanduser("~/.m2/repository/net/serenity-bdd/serenity-core/%s/serenity-core-%s.jar" % (V["serenity"], V["serenity"]))
    check("class-file versions: 3.9.8 = Java 8, 4.2.34 = Java 11, current = Java 17",
          (class_major("/tmp/oldjars/serenity-core-3.9.8.jar"), class_major("/tmp/oldjars/serenity-core-4.2.34.jar"), class_major(cur)) == (52, 55, 61))

    # 4: artifacts
    stops = {"serenity-cucumber4": "2.1.2", "serenity-cucumber5": "2.2.6", "serenity-cucumber6": "2.6.0", "serenity-jbehave": "1.46.0"}
    got = {a: latest(a) for a in stops}
    check("last releases of serenity-cucumber4/5/6 and serenity-jbehave are the old ones", got == stops, str(got))
    check("serenity-cucumber and serenity-junit are current", latest("serenity-cucumber") == V["serenity"] and latest("serenity-junit") == V["serenity"])

    # 5: fix_imports + compile
    work = tempfile.mkdtemp(prefix="upgv-")
    try:
        proj = os.path.join(work, "p")
        shutil.copytree(os.path.join(repro.EVAL, "h-po-selenium"), proj, ignore=shutil.ignore_patterns("target"))
        d = os.path.join(proj, "src/test/java/demo"); os.makedirs(d)
        shutil.copy(os.path.join(HERE, "old-sample", "OldStyleSteps.java"), d)
        rc, out = repro.run(proj, ["-q", "clean", "test-compile"])
        before_fails = rc != 0 and "package net.thucydides.core.annotations does not exist" in out or "cannot find symbol" in out
        subprocess.run([sys.executable, os.path.join(HERE, "fix_imports.py"), proj], capture_output=True, text=True)
        rc2, out2 = repro.run(proj, ["-q", "clean", "test-compile"])
        check("an old-style class fails to compile, and compiles after fix_imports.py", before_fails and rc2 == 0, "" if rc2 == 0 else out2[-300:])
    finally:
        shutil.rmtree(work, ignore_errors=True)

    # 6: JUnit 4 compat
    work = tempfile.mkdtemp(prefix="upgj4-")
    try:
        proj = os.path.join(work, "p")
        shutil.copytree(os.path.join(HERE, "compat", "junit4"), proj)
        rc, out = repro.run(proj, ["clean", "verify"])
        check("JUnit 4 SerenityRunner and CucumberWithSerenity run on the current release (4 tests)",
              rc == 0 and "Results: 4 tests | 4 passed" in out, "" if rc == 0 else out[-300:])
    finally:
        shutil.rmtree(work, ignore_errors=True)
    sys.exit(0 if all(results) else 1)


if __name__ == "__main__":
    main()
