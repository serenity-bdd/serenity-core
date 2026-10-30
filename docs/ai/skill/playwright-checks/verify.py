#!/usr/bin/env python3
"""Verify the Playwright Screenplay claims in serenity-bdd/references/screenplay.md by running them.

usage: verify.py [CASE_ID ...]     (needs Chrome-free Playwright browsers installed; uses the h-sp-playwright harness)

Cases build the sp-playwright example (ListTest uses a data: page, so no site is needed) and change it:
  strict-violation      WaitFor.selector("#rows .row") on a 3-row list fails with 'strict mode violation'
  nth-works             the example as written (">> nth=0", hasCount, Text.ofEach) passes
  url-ensure-compile    Ensure.thatTheCurrentUrl().contains(..) does not compile ('inaccessible class')
  title-ensure-compile  Ensure.thatThePageTitle().isEqualTo(..) does not compile
  page-url              the current URL read from BrowseTheWebWithPlaywright.as(actor).getCurrentPage().url() works
"""
import os, re, shutil, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

LIST = "src/test/java/todo/ListTest.java"


def project(edit=None):
    work = tempfile.mkdtemp(prefix="pwv-")
    proj = os.path.join(work, "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-sp-playwright"), proj, ignore=shutil.ignore_patterns("target"))
    os.makedirs(os.path.join(proj, "src/test/java/todo"))
    shutil.copy(os.path.join(repro.EX, "sp-playwright", "todo", "ListTest.java"), os.path.join(proj, LIST))
    if edit:
        edit(os.path.join(proj, LIST))
    return proj


def run(proj):
    rc, out = repro.run(proj, ["clean", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"])
    return out


def case_strict():
    p = project(lambda f: repro.sub(f, 'WaitFor.selector("#rows .row >> nth=0")', 'WaitFor.selector("#rows .row")'))
    o = run(p)
    return "strict mode violation" in o, o


def case_nth():
    p = project()
    o = run(p)
    return re.search(r"Tests run: 1, Failures: 0, Errors: 0", o) is not None, o


def case_url():
    p = project(lambda f: repro.sub(f, 'Ensure.that("#rows .row").hasCount(3),', 'Ensure.thatTheCurrentUrl().contains("data:"), Ensure.that("#rows .row").hasCount(3),'))
    o = run(p)
    return "inaccessible class or interface" in o and "UrlEnsure" in o, o


def case_title():
    p = project(lambda f: repro.sub(f, 'Ensure.that("#rows .row").hasCount(3),', 'Ensure.thatThePageTitle().isEqualTo("x"), Ensure.that("#rows .row").hasCount(3),'))
    o = run(p)
    return "inaccessible class or interface" in o and "PageTitleEnsure" in o, o


def case_page_url():
    def edit(f):
        repro.sub(f, "import java.util.List;", "import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;\nimport static org.assertj.core.api.Assertions.assertThat;")
        repro.sub(f, "        ana.should(seeThat(Text.ofEach", '        assertThat(BrowseTheWebWithPlaywright.as(ana).getCurrentPage().url()).startsWith("data:text/html");\n        ana.should(seeThat(Text.ofEach')
    p = project(edit)
    o = run(p)
    return re.search(r"Tests run: 1, Failures: 0, Errors: 0", o) is not None, o


REGEX_TEST = """package todo;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class RegexTest {
    @Test
    void textPatterns() {
        try (Playwright pw = Playwright.create(); Browser b = pw.chromium().launch(); Page page = b.newPage()) {
            page.setDefaultTimeout(3000);
            page.setContent("<table><tr class='row'><td class='title'>Clean Code</td></tr><tr class='row'><td class='title'>Refactoring</td></tr></table>");
            String title = "Clean Code";
            // 1. a plain string matches by substring, case-insensitively
            assertThat(page.locator("tr.row").filter(new Locator.FilterOptions().setHasText(title)).count()).isEqualTo(1);
            // 2. a Java-regex pattern built by hand matches
            assertThat(page.locator("tr.row").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*Clean Code\\s*$"))).count()).isEqualTo(1);
            // 3. Pattern.quote(..) produces \\Q..\\E, which JavaScript does not understand: nothing matches
            assertThat(page.locator("tr.row").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*" + Pattern.quote(title) + "\\s*$"))).count()).isEqualTo(QUOTE_COUNT);
        }
    }
}
"""


def case_regex_quote():
    def edit(f):
        shutil.copy(f, f + ".bak")
    p = project()
    t = os.path.join(p, "src/test/java/todo/RegexTest.java")
    open(t, "w").write(REGEX_TEST.replace("QUOTE_COUNT", "0"))
    rc, o = repro.run(p, ["clean", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"])
    return re.search(r"Tests run: 1, Failures: 0, Errors: 0", o) is not None and "RegexTest" in o, o


ROLE_TEST = """package todo;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {
    @Test
    void roles() {
        try (Playwright pw = Playwright.create(); Browser b = pw.chromium().launch(); Page page = b.newPage()) {
            page.setDefaultTimeout(3000);
            page.setContent("<h1>Sign in</h1><div id='box'><button>Go</button></div>");
            // page.getByRole takes Page.GetByRoleOptions; locator.getByRole takes Locator.GetByRoleOptions
            assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Sign in")).count()).isEqualTo(1);
            assertThat(page.locator("#box").getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Go")).count()).isEqualTo(1);
            // as a selector string a role needs the role= prefix
            assertThat(page.locator("role=heading[name=\\"Sign in\\"]").count()).isEqualTo(1);
            assertThatThrownBy(() -> page.locator("heading[name=\\"Sign in\\"]").count()).hasMessageContaining("InvalidSelectorError");
        }
    }
}
"""


def case_role():
    p = project()
    open(os.path.join(p, "src/test/java/todo/RoleTest.java"), "w").write(ROLE_TEST)
    rc, o = repro.run(p, ["clean", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"])
    return re.search(r"Tests run: 1, Failures: 0, Errors: 0", o) is not None and "RoleTest" in o, o


def case_role_options_mismatch():
    p = project()
    t = ROLE_TEST.replace("page.locator(\"#box\").getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions()", "page.locator(\"#box\").getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()")
    open(os.path.join(p, "src/test/java/todo/RoleTest.java"), "w").write(t)
    rc, o = repro.run(p, ["clean", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"])
    return "Page.GetByRoleOptions cannot be converted to com.microsoft.playwright.Locator.GetByRoleOptions" in o, o


CASES = {"role": case_role, "role-options-mismatch": case_role_options_mismatch, "regex-quote": case_regex_quote, "strict-violation": case_strict, "nth-works": case_nth, "url-ensure-compile": case_url,
         "title-ensure-compile": case_title, "page-url": case_page_url}

if __name__ == "__main__":
    bad = 0
    for name in sys.argv[1:] or list(CASES):
        ok, out = CASES[name]()
        bad += not ok
        print("%-22s %s" % (name, "ok" if ok else "FAIL"))
        if not ok:
            print("\n".join(l for l in out.splitlines() if "ERROR" in l or "Tests run" in l)[:900])
    sys.exit(1 if bad else 0)
