#!/usr/bin/env python3
"""Verify the claims in serenity-bdd/references/rest-assured.md by running them.

usage: verify.py [CASE_ID ...]

Copies the `rest-steps` example into the h-sp-rest harness, changes it as each case says, runs Maven and reads the
result. The example starts its own HTTP server, so no network is needed.
"""
import glob, json, os, re, shutil, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

SRC = os.path.join(repro.EX, "rest-steps", "api")
API = "src/test/java/api/"


def project():
    work = tempfile.mkdtemp(prefix="rest-")
    proj = os.path.join(work, "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-sp-rest"), proj, ignore=shutil.ignore_patterns("target"))
    shutil.copytree(SRC, os.path.join(proj, API))
    return proj


def verify(proj):
    return repro.run(proj, ["clean", "verify"])


def steps(proj):
    out = []
    for f in glob.glob(os.path.join(proj, "target/site/serenity/*.json")):
        j = json.load(open(f))
        for s in j.get("testSteps", []):
            out.append(s["description"])
    return out


def case_baseline():
    p = project()
    code, out = verify(p)
    return [code, repro.surefire_tests(p), repro.has_report(p)], lambda v: v == [0, 3, True]


def case_step_text():
    p = project()
    verify(p)
    s = steps(p)
    return s, lambda s: "List the books in the catalogue" in s and "Look up book number 2" in s and "The response should show the title Clean Code" in s


def case_double_fails():
    p = project()
    repro.sub(p + "/" + API + "BookshopApi.java", "equalTo((float) total)", "equalTo(total)")
    code, out = verify(p)
    return [code, repro.line_matching(out, r"Expected:.*90|Expected: is <90")], lambda v: v[0] != 0 and v[1] is not None


def case_closeto_double_fails():
    p = project()
    repro.sub(p + "/" + API + "BookshopApi.java", "equalTo((float) total)", "closeTo(total, 0.001)")
    repro.sub(p + "/" + API + "BookshopApi.java", "import static net.serenitybdd.rest.SerenityRest.given;", "import static net.serenitybdd.rest.SerenityRest.given;\nimport static org.hamcrest.Matchers.closeTo;")
    code, out = verify(p)
    return [code, repro.line_matching(out, r"numeric value within")], lambda v: v[0] != 0 and v[1] is not None


def case_jsonpath_double():
    p = project()
    repro.sub(p + "/" + API + "BookshopApi.java", "SerenityRest.then().body(\"total\", org.hamcrest.Matchers.equalTo((float) total));",
              "org.assertj.core.api.Assertions.assertThat(SerenityRest.lastResponse().jsonPath().getDouble(\"total\")).isEqualTo(total);")
    code, out = verify(p)
    return [code, repro.surefire_tests(p)], lambda v: v == [0, 3]


def case_old_import():
    p = project()
    repro.sub(p + "/" + API + "BookshopApi.java", "net.serenitybdd.annotations.Step", "net.thucydides.core.annotations.Step")
    code, out = verify(p)
    return [code, repro.line_matching(out, r"BookshopApi.*cannot find symbol|BookshopApiTest.*cannot find symbol")], lambda v: v[0] != 0 and v[1] is not None


def case_old_steps_import():
    p = project()
    repro.sub(p + "/" + API + "BookshopApiTest.java", "net.serenitybdd.annotations.Steps", "net.thucydides.core.annotations.Steps")
    code, out = verify(p)
    return [code, repro.line_matching(out, r"BookshopApi.*cannot find symbol|BookshopApiTest.*cannot find symbol")], lambda v: v[0] != 0 and v[1] is not None


def case_scenariosteps():
    p = project()
    f = p + "/" + API + "BookshopApi.java"
    repro.sub(f, "public class BookshopApi {", "public class BookshopApi extends net.thucydides.core.steps.ScenarioSteps {")
    code, out = verify(p)
    return [code, repro.surefire_tests(p)], lambda v: v == [0, 3]


def case_no_step_annotation():
    p = project()
    repro.sub(p + "/" + API + "BookshopApi.java", '    @Step("List the books in the catalogue")\n', "")
    verify(p)
    s = steps(p)
    return s, lambda s: "List the books in the catalogue" not in s


def case_new_in_test_class():
    """A step library created with `new` instead of @Steps: calls still work, but are not reported as steps."""
    p = project()
    repro.sub(p + "/" + API + "BookshopApiTest.java", "    @Steps\n    BookshopApi bookshop;", "    BookshopApi bookshop = new BookshopApi();")
    verify(p)
    s = steps(p)
    return s, lambda s: "List the books in the catalogue" not in s


CASES = {k[5:]: v for k, v in dict(globals()).items() if k.startswith("case_")}

if __name__ == "__main__":
    want = sys.argv[1:] or list(CASES)
    bad = 0
    for name in want:
        value, ok = CASES[name]()
        res = ok(value)
        bad += not res
        print("%-22s %s   %s" % (name, "ok" if res else "FAIL", json.dumps(value)[:240]))
    sys.exit(1 if bad else 0)
