#!/usr/bin/env python3
"""Verify the claims about Cucumber with page objects and step libraries (no Screenplay) in references/cucumber.md.

usage: verify_pages.py [CASE_ID ...]

Copies the `cuke-pages` example (a page object, a @Steps step library and step definitions, driving a data: URL page in
headless Chrome) into the h-cuke-sp harness, changes it as each case says, runs `mvn clean verify`, and reads the result.
"""
import glob, json, os, shutil, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

SRC = os.path.join(repro.EX, "cuke-pages")
JAVA = "src/test/java/shop/"
FEATURE = "src/test/resources/features/sign_in.feature"


def project():
    proj = os.path.join(tempfile.mkdtemp(prefix="cukepages-"), "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-cuke-sp"), proj, ignore=shutil.ignore_patterns("target"))
    for d in ("demo", "todo"):
        shutil.rmtree(os.path.join(proj, "src/test/java", d), ignore_errors=True)
    shutil.rmtree(os.path.join(proj, "src/test/resources/features"), ignore_errors=True)
    shutil.copytree(os.path.join(SRC, "shop"), os.path.join(proj, JAVA))
    shutil.copytree(os.path.join(SRC, "features"), os.path.join(proj, "src/test/resources/features"))
    return proj


def build(p):
    return repro.run(p, ["clean", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"])


def steps(p):
    out = []
    for f in glob.glob(os.path.join(p, "target/site/serenity/*.json")):
        j = json.load(open(f))
        for s in j.get("testSteps", []):
            out.append(s["description"])
            out += [c["description"] for c in s.get("children", [])]
    return out


def results(p):
    return {j.get("title"): j.get("result") for j in (json.load(open(f)) for f in glob.glob(os.path.join(p, "target/site/serenity/*.json"))) if "testSteps" in j}


def case_baseline():
    p = project()
    code, out = build(p)
    r = results(p)
    return [len(r), sorted(set(r.values())), repro.has_report(p)], lambda v: v == [3, ["SUCCESS"], True]


def case_report_wording():
    p = project()
    build(p)
    s = steps(p)
    return s, lambda s: "Open the sign-in page" in s and any(x.startswith("Sign in as reader@example.com") for x in s) and "The message Invalid email or password should be shown" in s


def case_optional_text_literal():
    """A feature that says `book(s)` literally does not match a step written `book(s)`."""
    p = project()
    repro.sub(os.path.join(p, FEATURE), "<count> books", "<count> book(s)")
    code, out = build(p)
    return [sorted(set(results(p).values())), repro.line_matching(out, r"is undefined")], lambda v: "PENDING" in v[0] and v[1] is not None


def case_undefined_step_fails_build():
    p = project()
    repro.sub(os.path.join(p, FEATURE), "<count> books", "<count> book(s)")
    code, out = repro.run(p, ["clean", "verify", "-Dheadless.mode=true"])
    return code, lambda c: c != 0


def case_escaped_literal():
    p = project()
    repro.sub(os.path.join(p, FEATURE), "<count> books", "<count> book(s)")
    repro.sub(os.path.join(p, JAVA, "SignInStepDefinitions.java"), '{int} book(s)")', '{int} book\\\\(s)")')
    repro.sub(os.path.join(p, JAVA, "SignInStepDefinitions.java"), 'count + " books"', 'count + " books"')
    build(p)
    return sorted(set(results(p).values())), lambda r: r == ["SUCCESS"]


def case_new_instead_of_steps():
    """`new ReaderSteps()` instead of @Steps: the page object field is never filled in."""
    p = project()
    repro.sub(os.path.join(p, JAVA, "SignInStepDefinitions.java"), "    @Steps\n    ReaderSteps reader;", "    ReaderSteps reader = new ReaderSteps();")
    code, out = build(p)
    r = results(p)
    return [sorted(set(r.values())), repro.line_matching(out, r"NullPointerException")], lambda v: v[0] != ["SUCCESS"]


def click_with(code_line, nested=False):
    p = project()
    if nested:   # put the button inside a div, so it is not a direct child of body
        repro.sub(os.path.join(p, JAVA, "SignInPage.java"), "+ \"<button id='go'", "+ \"<div><button id='go'")
        repro.sub(os.path.join(p, JAVA, "SignInPage.java"), ">Sign in</button>\"", ">Sign in</button></div>\"")
    repro.sub(os.path.join(p, JAVA, "SignInPage.java"), "signInButton.click();", code_line)
    out = build(p)[1]
    return sorted(set(results(p).values())), repro.line_matching(out, r"ElementShouldBeEnabled|NoSuchElement|Unable to locate")


def case_tag_selector_is_xpath():
    return click_with('$("button").click();'), lambda v: v[0] == ["FAILURE"] and v[1] is not None


def case_tag_attribute_selector_is_xpath():
    return click_with("$(\"button[id='go']\").click();"), lambda v: v[0] == ["FAILURE"] and v[1] is not None


def case_thenfind_tag_is_xpath():
    return click_with('$(org.openqa.selenium.By.tagName("body")).thenFind("button").click();', nested=True), lambda v: v[0] == ["ERROR"] and v[1] is not None and "xpath" in v[1]


def case_by_css_selector_works():
    return click_with('$(org.openqa.selenium.By.cssSelector("button")).click();'), lambda v: v[0] == ["SUCCESS"]


def case_hash_selector_works():
    return click_with('$("#go").click();'), lambda v: v[0] == ["SUCCESS"]


def case_findby_by_works():
    return click_with('$(org.openqa.selenium.By.tagName("body")).findBy(org.openqa.selenium.By.cssSelector("button")).click();', nested=True), lambda v: v[0] == ["SUCCESS"]


CASES = {k[5:]: v for k, v in dict(globals()).items() if k.startswith("case_")}

if __name__ == "__main__":
    want = sys.argv[1:] or list(CASES)
    bad = 0
    for name in want:
        value, ok = CASES[name]()
        res = ok(value)
        bad += not res
        print("%-26s %s   %s" % (name, "ok" if res else "FAIL", json.dumps(value)[:260]))
    sys.exit(1 if bad else 0)
