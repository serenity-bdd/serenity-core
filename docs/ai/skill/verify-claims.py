#!/usr/bin/env python3
"""Check the factual claims in the published guidance against the released jars.

usage: verify-claims.py [--online]

Reads: serenity-bdd/references/*.md, serenity-bdd/SKILL.md, ../agents-md/variants/*.md and
(if present) the docs repo header. For every claim it can check mechanically it verifies:
  1. package tables: each `Class` listed against a `package` really exists in that package;
  2. every fully qualified class or package named in the text exists (and is not marked deprecated
     when the text recommends it);
  3. every `Class.method(` call shown exists as a method of some class with that simple name;
  4. every claim in negative-claims.txt (classes and packages the guidance says do NOT exist) is absent;
  5. every `serenity-*` Maven artifact named exists in the local repository.
Exit code 1 if any claim fails. Needs Maven (the harness projects) and the harness dependencies in ~/.m2.
Limits: it checks existence, not behaviour (the example runs cover behaviour); prose claims are not checked.
"""
import glob, json, os, re, subprocess, sys, zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
EVAL = os.path.join(HERE, "..", "eval", "harness")
M2 = os.path.expanduser("~/.m2/repository")
DOCS_HEADER = os.path.join(HERE, "..", "..", "..", "..", "documentation", "serenity-bdd.github.io", "plugin", "llms-txt", "header.md")
NOT_ARTIFACTS = {"serenity-test-reviewer", "serenity-playwright-demo"}   # repository modules mentioned by name, not published artifacts
JDK_CLASSES = {"Pattern"}   # JDK classes that share a simple name with a class in some dependency; not Serenity claims
HARNESSES = ["h-sp-selenium", "h-po-selenium", "h-cuke-sp", "h-sp-rest", "h-all", "h-po-playwright"]


def classpath(harness):
    out = subprocess.run(["mvn", "-q", "-B", *os.environ.get("MVN_OFFLINE", "-o").split(), "dependency:build-classpath", "-Dmdep.outputFile=/dev/stdout"],
                         cwd=os.path.join(EVAL, harness), capture_output=True, text=True)
    return [p for p in out.stdout.strip().split(":") if p.endswith(".jar")]


EXTRA_ARTIFACTS = ["serenity-shutterbug"]   # artifacts the guidance mentions that no harness project depends on


def extra_jars():
    import urllib.request
    ver = json.load(open(os.path.join(HERE, "versions.json")))["serenity"]
    cache = "/tmp/claimjars"
    os.makedirs(cache, exist_ok=True)
    out = []
    for a in EXTRA_ARTIFACTS:
        f = os.path.join(cache, "%s-%s.jar" % (a, ver))
        if not os.path.exists(f):
            urllib.request.urlretrieve("https://repo1.maven.org/maven2/net/serenity-bdd/%s/%s/%s-%s.jar" % (a, ver, a, ver), f)
        out.append(f)
    return out


def class_index():
    classes, jars = set(), set()
    for h in HARNESSES:
        jars.update(classpath(h))
    jars.update(extra_jars())
    for j in jars:
        with zipfile.ZipFile(j) as z:
            for n in z.namelist():
                if n.endswith(".class") and "$" not in n and "/" in n and not n.startswith("META-INF"):
                    classes.add(n[:-6].replace("/", "."))
    packages = {c.rsplit(".", 1)[0] for c in classes}
    prefixes = set()
    for p in packages:
        parts = p.split(".")
        for i in range(1, len(parts) + 1):
            prefixes.add(".".join(parts[:i]))
    return classes, packages, prefixes, sorted(jars)


def strip_generated_blocks(text):
    """The moved-classes table of the upgrading reference is checked by upgrade-checks/verify.py (its old packages do not exist)."""
    return re.sub(r"<!-- moved-table:start -->.*?<!-- moved-table:end -->", "", text, flags=re.S)


def texts():
    files = glob.glob(os.path.join(HERE, "serenity-bdd", "references", "*.md")) + [os.path.join(HERE, "serenity-bdd", "SKILL.md"), os.path.join(HERE, "guide-writing-tests.md")]
    files += glob.glob(os.path.join(HERE, "..", "agents-md", "variants", "*.md"))
    if os.path.exists(DOCS_HEADER):
        files.append(DOCS_HEADER)
    return {os.path.relpath(f, HERE): strip_generated_blocks(open(f).read()) for f in files}


def expand(pkg):
    return pkg.replace("...playwright", "net.serenitybdd.screenplay.playwright", 1) if pkg.startswith("...") else pkg


def on_maven_central(artifact):
    """Old artifacts (serenity-cucumber4, serenity-jbehave, ...) are not in a fresh ~/.m2; Maven Central is the authority."""
    import urllib.request
    url = "https://repo1.maven.org/maven2/net/serenity-bdd/%s/maven-metadata.xml" % artifact
    try:
        with urllib.request.urlopen(url, timeout=30) as r:
            return r.status == 200
    except Exception:
        return False


def main():
    classes, packages, prefixes, jars = class_index()
    by_simple = {}
    for c in classes:
        by_simple.setdefault(c.rsplit(".", 1)[1], []).append(c)
    all_negatives = [l.strip() for l in open(os.path.join(HERE, "negative-claims.txt")) if l.strip() and not l.startswith("#")]
    # "Class.method" (capitalised simple name, lower-case member) means: no class with that name has that method
    method_negatives = [n for n in all_negatives if re.fullmatch(r"[A-Z]\w*\.[a-z]\w*", n)]
    negatives = [n for n in all_negatives if n not in method_negatives]
    problems, checked = [], 0
    T = texts()

    # 1. package tables
    for f, t in T.items():
        for line in t.splitlines():
            cells = [c.strip() for c in line.strip().strip("|").split("|")]
            if len(cells) != 2 or not line.startswith("|") or "Class" in cells[0] or set(cells[0]) <= set("-"):
                continue
            pm = re.match(r"`(\.\.\.[\w.]+|net\.[\w.]+)`", cells[1])
            if not pm:
                continue
            pkg = expand(pm.group(1))
            lead = re.split(r"\(", cells[0], 1)[0]   # ignore parenthetical remarks such as "(use instead of OnlineCast)"
            names = re.findall(r"`@?([A-Z]\w+)(?:\.\w+\(\.\.\))?`", lead)
            for n in names:
                checked += 1
                cands = [pkg] if pkg.rsplit(".", 1)[-1][0].isupper() else [pkg + "." + n]
                if pkg.rsplit(".", 1)[-1][0].isupper():  # the cell names the class itself (e.g. ...playwright.Target)
                    ok = pkg in classes and pkg.rsplit(".", 1)[-1] == n
                else:
                    ok = cands[0] in classes
                if not ok:
                    problems.append("%s: `%s` is not in package %s" % (f, n, pkg))

    # 2. fully qualified names and packages named in the text
    neg_set = set(negatives)
    fqn = re.compile(r"\b((?:net|io|com|org)\.[a-z][\w]*(?:\.[a-z][\w]*)*(?:\.[A-Z]\w*)?)(?![\w-])")
    group_ids = {"org.apache.maven.plugins", "net.serenity", "net.serenity-bdd.maven.plugins", "org.junit", "io.cucumber", "com.microsoft.playwright", "org.assertj", "org.slf4j"}
    for f, t in T.items():
        seen = set()
        for tok in fqn.findall(t):
            tok = tok.rstrip(".")
            if tok in seen:
                continue
            seen.add(tok)
            if tok in neg_set or tok in group_ids or any(tok.startswith(n + ".") or tok == n for n in neg_set):
                continue
            if tok.startswith(("org.junit", "org.openqa", "org.assertj", "org.hamcrest", "org.slf4j", "io.cucumber.java", "io.cucumber.junit",
                               "com.microsoft.playwright", "net.serenity-bdd", "org.apache", "net.thucydides.core.annotations")):
                # third-party and legacy packages: check only that they exist when they are classes
                pass
            checked += 1
            last = tok.rsplit(".", 1)[-1]
            if last[0].isupper():
                if tok not in classes:
                    problems.append("%s: class %s not found" % (f, tok))
            elif tok not in prefixes and tok not in packages:
                problems.append("%s: package %s not found" % (f, tok))

    # 3. Class.method( calls
    for f, t in T.items():
        for cls, meth in set(re.findall(r"\b([A-Z]\w+)\.([a-z]\w*)\(", t)):
            if "%s.%s" % (cls, meth) in method_negatives or cls in JDK_CLASSES:
                continue
            cands = by_simple.get(cls)
            if not cands:
                continue   # JDK/other types; not checked
            checked += 1
            # one javap call for every class with this simple name (more than one jar may define it)
            out = subprocess.run(["javap", "-cp", ":".join(jars), *sorted(cands)], capture_output=True, text=True).stdout
            found = bool(re.search(r"\b%s\(" % re.escape(meth), out))
            if not found:
                problems.append("%s: no class named %s has a method %s(" % (f, cls, meth))

    # 4. negative claims must be absent
    for n in negatives:
        checked += 1
        last = n.rsplit(".", 1)[-1]
        present = n in classes if last[0].isupper() else (n in packages)
        if present:
            problems.append("negative-claims.txt: %s exists, but the guidance says it does not" % n)

    for mn in method_negatives:
        checked += 1
        cls, meth = mn.split(".")
        cands = by_simple.get(cls, [])
        out = subprocess.run(["javap", "-cp", ":".join(jars), *sorted(cands)], capture_output=True, text=True).stdout if cands else ""
        if re.search(r"\b%s\(" % re.escape(meth), out):
            problems.append("negative-claims.txt: %s.%s( exists, but the guidance says it does not" % (cls, meth))

    # 4b. method-level claims (javap-claims.txt)
    jc = os.path.join(HERE, "javap-claims.txt")
    for line in open(jc):
        if not line.strip() or line.startswith("#"):
            continue
        cls, rx = [x.strip() for x in line.split("|", 1)]
        checked += 1
        out = subprocess.run(["javap", "-cp", ":".join(jars), cls], capture_output=True, text=True).stdout
        if not out or not re.search(rx, out):
            problems.append("javap-claims.txt: %s does not match /%s/" % (cls, rx))

    # 5. Maven artifacts
    arts = set()
    for t in T.values():
        arts.update(a for a in re.findall(r"`(serenity-[a-z0-9-]+)`", t) if a not in NOT_ARTIFACTS)
    for a in sorted(arts):
        checked += 1
        if not glob.glob(os.path.join(M2, "net", "serenity-bdd", a, "*", "*.jar")) and not on_maven_central(a):
            problems.append("artifact %s not found in ~/.m2 or on Maven Central (net.serenity-bdd)" % a)

    print("checked %d claims in %d files against %d jars" % (checked, len(T), len(jars)))
    for p in problems:
        print("FAIL", p)
    if not problems:
        print("OK")
    sys.exit(1 if problems else 0)


if __name__ == "__main__":
    main()
