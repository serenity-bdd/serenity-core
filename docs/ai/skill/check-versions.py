#!/usr/bin/env python3
"""Check that the versions the guidance describes are current and consistent.

usage: check-versions.py [--offline]

1. versions.json against Maven Central: newer Serenity (core) or Playwright-module releases fail the check,
   because the guidance has to be re-verified (run release-check.sh) and the version strings bumped.
2. Version strings in the published guidance (references, SKILL.md, AGENTS.md variants, llms.txt header):
   every Serenity-looking version (5.x.y) must be one of the two versions in versions.json.
3. The claim "Serenity <v> depends on JUnit <j> and Cucumber <c>": checked against the dependency tree of the
   Cucumber harness project (needs Maven; the harness dependencies resolve online or from ~/.m2).
Exit code 1 on any failure. With --offline, step 1 is skipped.
"""
import glob, json, os, re, subprocess, sys, urllib.request

HERE = os.path.dirname(os.path.abspath(__file__))
V = json.load(open(os.path.join(HERE, "versions.json")))
DOCS_HEADER = os.path.join(HERE, "..", "..", "..", "..", "documentation", "serenity-bdd.github.io", "plugin", "llms-txt", "header.md")


def latest(artifact):
    url = "https://repo1.maven.org/maven2/net/serenity-bdd/%s/maven-metadata.xml" % artifact
    with urllib.request.urlopen(url, timeout=30) as r:
        xml = r.read().decode()
    m = re.search(r"<release>([^<]+)</release>", xml) or re.search(r"<latest>([^<]+)</latest>", xml)
    return m.group(1)


def vkey(v):
    return tuple(int(x) for x in re.findall(r"\d+", v))


def main():
    offline = "--offline" in sys.argv
    problems = []

    if not offline:
        for artifact, key in (("serenity-core", "serenity"), ("serenity-playwright", "serenityPlaywrightModules")):
            try:
                cur = latest(artifact)
            except Exception as e:  # network trouble is a failure in CI, not something to hide
                problems.append("could not read Maven Central metadata for %s: %s" % (artifact, e))
                continue
            if vkey(cur) > vkey(V[key]):
                problems.append("newer release of %s: %s (guidance describes %s): re-verify and bump versions.json" % (artifact, cur, V[key]))
            elif vkey(cur) < vkey(V[key]):
                problems.append("versions.json says %s for %s but Maven Central's latest is %s" % (V[key], artifact, cur))
            else:
                print("ok: %s %s is the latest release" % (artifact, cur))

    files = glob.glob(os.path.join(HERE, "serenity-bdd", "references", "*.md")) + [os.path.join(HERE, "serenity-bdd", "SKILL.md")]
    files += glob.glob(os.path.join(HERE, "..", "agents-md", "variants", "*.md"))
    if os.path.exists(DOCS_HEADER):
        files.append(DOCS_HEADER)
    allowed = {V["serenity"], V["serenityPlaywrightModules"]}
    for f in files:
        for v in sorted(set(re.findall(r"(?<![\d.])5\.\d+\.\d+(?![\d])", open(f).read()))):
            if v not in allowed:
                problems.append("%s mentions Serenity %s, not one of %s" % (os.path.relpath(f, HERE), v, sorted(allowed)))
    print("checked version strings in %d files" % len(files))

    out = subprocess.run(["mvn", "-B", *os.environ.get("MVN_OFFLINE", "-o").split(), "dependency:tree",
                          "-Dincludes=org.junit.jupiter:junit-jupiter-api,io.cucumber:cucumber-core"],
                         cwd=os.path.join(HERE, "..", "eval", "harness", "h-cuke-sp"), capture_output=True, text=True).stdout
    junit = re.search(r"junit-jupiter-api:jar:([\d.]+)", out)
    cuke = re.search(r"cucumber-core:jar:([\d.]+)", out)
    for name, found, want in (("JUnit", junit, V["junit"]), ("Cucumber", cuke, V["cucumber"])):
        if not found:
            problems.append("could not read the %s version from the dependency tree" % name)
        elif found.group(1) != want:
            problems.append("Serenity %s brings %s %s but versions.json says %s" % (V["serenity"], name, found.group(1), want))
        else:
            print("ok: Serenity %s brings %s %s" % (V["serenity"], name, want))

    for p in problems:
        print("FAIL", p)
    print("OK" if not problems else "FAILED")
    sys.exit(1 if problems else 0)


if __name__ == "__main__":
    main()
