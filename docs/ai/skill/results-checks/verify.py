#!/usr/bin/env python3
"""Verify the claims in serenity-bdd/references/test-results.md by running them.

usage: verify.py [CASE_ID ...]

Copies the `rest-steps` example into the h-sp-rest harness (it starts its own HTTP server, so no network is needed),
runs it passing and failing, and reads the files Serenity writes. The json-summary case needs Maven Central
(set MVN_OFFLINE= to allow downloads, as CI does) and is skipped offline.
"""
import glob, json, os, re, subprocess, sys, tempfile, shutil

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

SCRIPT = os.path.join(HERE, "..", "serenity-bdd", "scripts", "results.py")
API = "src/test/java/api/"
SITE = "target/site/serenity"
OFFLINE = os.environ.get("MVN_OFFLINE", "-o").strip() != ""


def project(broken):
    work = tempfile.mkdtemp(prefix="results-")
    proj = os.path.join(work, "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-sp-rest"), proj, ignore=shutil.ignore_patterns("target"))
    shutil.copytree(os.path.join(repro.EX, "rest-steps", "api"), os.path.join(proj, API))
    if broken:
        repro.sub(proj + "/" + API + "BookshopApiTest.java", 'responseShouldShowTitle("Clean Code")', 'responseShouldShowTitle("Clean Coder")')
    return proj


def outcomes(proj):
    return {os.path.basename(f): json.load(open(f)) for f in glob.glob(os.path.join(proj, SITE, "*.json"))}


def results(proj, *flags):
    r = subprocess.run([sys.executable, SCRIPT, os.path.join(proj, SITE), *flags], capture_output=True, text=True)
    return r.returncode, r.stdout


def case_failed_run_files():
    """After a failed build the JSON outcomes and JUnit XML exist; the HTML report and summary.txt do not."""
    p = project(True)
    code, _ = repro.run(p, ["clean", "verify"])
    site = os.path.join(p, SITE)
    return [code != 0, len(outcomes(p)), len(glob.glob(site + "/SERENITY-JUNIT-*.xml")), os.path.exists(site + "/index.html"), os.path.exists(site + "/summary.txt")], \
        lambda v: v == [True, 3, 3, False, False]


def case_passing_run_files():
    p = project(False)
    code, _ = repro.run(p, ["clean", "verify"])
    site = os.path.join(p, SITE)
    return [code, os.path.exists(site + "/index.html"), os.path.exists(site + "/summary.txt")], lambda v: v == [0, True, True]


def case_file_names_are_hashes():
    p = project(False)
    repro.run(p, ["clean", "verify"])
    names = list(outcomes(p))
    return names, lambda n: len(n) == 3 and all(re.fullmatch(r"[0-9a-f]{64}\.json", x) for x in n)


def case_outcome_fields():
    p = project(True)
    repro.run(p, ["clean", "verify"])
    o = outcomes(p)
    bad = [j for j in o.values() if j["result"] == "FAILURE"][0]
    top = {"title", "result", "methodName", "testCaseName", "userStory", "testSteps", "duration", "startTime", "tags", "testSource"}
    cause = bad.get("testFailureCause", {})
    step = [s for s in bad["testSteps"] if s["result"] == "FAILURE"]
    rest = [c for s in bad["testSteps"] for c in s.get("children", []) if "restQuery" in c]
    have = [top <= set(bad), {"errorType", "message", "stackTrace"} <= set(cause), "testFailureMessage" in bad, "testFailureClassname" in bad,
            "testFailureSummary" in bad, bool(step) and "exception" in step[0], bool(rest) and {"method", "path", "statusCode", "responseBody"} <= set(rest[0]["restQuery"]),
            bad["userStory"].get("path") == "api.BookshopApiTest", sorted({j["result"] for j in o.values()}) == ["FAILURE", "SUCCESS"]]
    return have, lambda v: all(v)


def case_results_script():
    p = project(True)
    repro.run(p, ["clean", "verify"])
    code, out = results(p)
    jcode, jout = results(p, "--json")
    j = json.loads(jout)
    bad = [t for t in j["tests"] if t["result"] == "FAILURE"][0]
    return [code, jcode, "3 tests: 1 failure, 2 success" in out, "failed step 7: The response should show the title Clean Coder" in out,
            "Expected: Clean Coder" in " ".join(out.split()), "BookshopApi.java:" in out, j["total"] == 3, bad["step_number"] == 7, bad["file"] in out], \
        lambda v: v[0] == 1 and v[1] == 1 and all(v[2:])


def case_results_script_passing():
    p = project(False)
    repro.run(p, ["clean", "verify"])
    code, out = results(p)
    return [code, "3 tests: 3 success" in out, "FAILED" in out], lambda v: v == [0, True, False]


def case_no_outcomes():
    d = tempfile.mkdtemp()
    r = subprocess.run([sys.executable, SCRIPT, d], capture_output=True, text=True)
    return [r.returncode, "no outcome files" in r.stdout], lambda v: v == [2, True]


def case_json_summary():
    """The optional summary module, enabled as the reference says: a dependency of serenity-maven-plugin and -Dserenity.reports=json-summary."""
    if OFFLINE:
        return "skipped offline", lambda v: True
    p = project(True)
    repro.run(p, ["clean", "verify"])
    pom = repro.read(repro.pom(p))
    ver = re.search(r"<serenity.version>([^<]+)<", pom).group(1)
    repro.write(repro.pom(p), pom.replace("<artifactId>serenity-maven-plugin</artifactId>",
        "<artifactId>serenity-maven-plugin</artifactId>\n        <dependencies><dependency><groupId>net.serenity-bdd</groupId><artifactId>serenity-json-summary-report</artifactId><version>%s</version></dependency></dependencies>" % ver, 1))
    code, out = repro.run(p, ["serenity:reports", "-Dserenity.reports=json-summary"])
    f = os.path.join(p, SITE, "serenity-summary.json")
    if code != 0 or not os.path.exists(f):
        return [code, "no serenity-summary.json"], lambda v: False
    j = json.load(open(f))
    sc = j["resultsByFeature"][0]["scenarios"]
    failed = [s for s in sc if s["result"] == "failure"]
    return [sorted(j)[:3], j["results"]["counts"]["total"], j["results"]["counts"]["failure"], len(failed), "Expected: Clean Coder" in failed[0]["outcomes"][0]["errorMessage"]], \
        lambda v: v[1:] == [3, 1, 1, True] and {"report", "results", "resultsByFeature"} <= set(json.load(open(f)))


CASES = {k[5:]: v for k, v in dict(globals()).items() if k.startswith("case_")}

if __name__ == "__main__":
    want = sys.argv[1:] or list(CASES)
    bad = 0
    for name in want:
        value, ok = CASES[name]()
        res = ok(value)
        bad += not res
        print("%-26s %s   %s" % (name, "ok" if res else "FAIL", json.dumps(value)[:240]))
    sys.exit(1 if bad else 0)
