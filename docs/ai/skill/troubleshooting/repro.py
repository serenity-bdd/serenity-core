#!/usr/bin/env python3
"""Reproduce the problems described in serenity-bdd/references/troubleshooting.md, and check each fix.

usage: repro.py [CASE_ID ...]        # default: all cases; writes observed.json

Each case builds a project from a harness and one of the compiling examples, breaks it in one specific way,
runs Maven and checks that the documented symptom appears, applies the documented fix, and checks that the
project then works. The messages in observed.json are the exact text to quote in the reference.
Needs Maven, headless Chrome and the network (some cases use the live todo app).
"""
import json, os, re, shutil, subprocess, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
EVAL = os.path.join(HERE, "..", "..", "eval", "harness")
EX = os.path.join(HERE, "..", "serenity-bdd", "examples")
MVN = ["mvn", "-B", *os.environ.get("MVN_OFFLINE", "").split()]


def read(p): return open(p).read()
def write(p, s): open(p, "w").write(s)
def sub(p, old, new, count=1):
    s = read(p)
    assert old in s, "%s: %r not found" % (p, old)
    write(p, s.replace(old, new, count))


def run(proj, args):
    r = subprocess.run(MVN + args, cwd=proj, capture_output=True, text=True, timeout=600)
    return r.returncode, r.stdout + r.stderr


def surefire_tests(proj):
    n = 0
    for f in os.listdir(os.path.join(proj, "target", "surefire-reports")) if os.path.isdir(os.path.join(proj, "target", "surefire-reports")) else []:
        if f.endswith(".xml"):
            n += sum(int(x) for x in re.findall(r'<testsuite [^>]*\btests="(\d+)"', read(os.path.join(proj, "target", "surefire-reports", f))))
    return n


def has_report(proj): return os.path.exists(os.path.join(proj, "target", "site", "serenity", "index.html"))


def line_matching(out, rx):
    for l in out.splitlines():
        if re.search(rx, l):
            return re.sub(r"^\[(ERROR|INFO|WARNING)\]\s*", "", l).strip()[:400]
    return None


PROBE = """package api;

import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;

class GroovyProbeTest {
    @Test
    void restAssuredStarts() {
        // building a request specification initialises REST-assured (and its Groovy)
        SerenityRest.given().baseUri("http://localhost");
    }
}
"""

# --- the cases -----------------------------------------------------------------------------------------------
def pom(p): return os.path.join(p, "pom.xml")
def runner(p): return os.path.join(p, "src/test/java/demo/RunCucumberTests.java")


def break_mixed_junit(p):
    s = read(pom(p))
    s = re.sub(r"\s*<dependencyManagement>.*?</dependencyManagement>", "", s, flags=re.S)
    s = s.replace("${junit-jupiter.version}", "5.12.2").replace("${junit-platform.version}", "1.12.2")
    write(pom(p), s)


CASES = [
    dict(id="mixed-junit-versions", title="JUnit versions pinned in the pom are older than the one Serenity uses",
         base="cuke-plain", harness="h-cuke-sp", args=["verify"], keep_pom=True,
         break_=break_mixed_junit, symptom=r"conflicting versions|not being properly aligned",
         fix=lambda p: shutil.copy(os.path.join(EVAL, "h-cuke-sp", "pom.xml"), pom(p)), works=lambda p, o: surefire_tests(p) > 0 and "BUILD SUCCESS" in o),
    dict(id="cucumber-version-mismatch", title="Cucumber pinned to a version Serenity's Cucumber module was not built for",
         base="cuke-plain", harness="h-cuke-sp", args=["verify"],
         break_=lambda p: sub(pom(p), "<cucumber.version>8.0.4", "<cucumber.version>7.31.0"), symptom=r"AbstractMethodError",
         fix=lambda p: sub(pom(p), "<cucumber.version>7.31.0", "<cucumber.version>8.0.4"), works=lambda p, o: surefire_tests(p) > 0 and "BUILD SUCCESS" in o),
    dict(id="wrong-cucumber-plugin", title="Wrong class name for the Serenity Cucumber plugin",
         base="cuke-plain", harness="h-cuke-sp", args=["verify"],
         break_=lambda p: sub(runner(p), "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel", "io.cucumber.core.plugin.SerenityReporterParallel"),
         symptom=r"Failed to transform configuration parameter with key 'cucumber.plugin'",
         fix=lambda p: sub(runner(p), "io.cucumber.core.plugin.SerenityReporterParallel", "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"),
         works=lambda p, o: surefire_tests(p) > 0 and "BUILD SUCCESS" in o),
    dict(id="runner-not-picked-up", title="Cucumber runner class whose name Maven's test plugin ignores",
         base="cuke-plain", harness="h-cuke-sp", args=["verify"], default_surefire=True,
         break_=lambda p: (os.rename(runner(p), os.path.join(p, "src/test/java/demo/CucumberSuite.java")),
                           sub(os.path.join(p, "src/test/java/demo/CucumberSuite.java"), "class RunCucumberTests", "class CucumberSuite")),
         symptom=r"Results: 0 tests",
         fix=lambda p: (os.rename(os.path.join(p, "src/test/java/demo/CucumberSuite.java"), runner(p)),
                        sub(runner(p), "class CucumberSuite", "class RunCucumberTests")),
         works=lambda p, o: surefire_tests(p) > 0 and "BUILD SUCCESS" in o),
    dict(id="no-report-after-failed-tests", title="No Serenity report after a build with failing tests",
         base="cuke-plain", harness="h-cuke-sp", args=["verify"],
         break_=lambda p: sub(os.path.join(p, "src/test/resources/features/maths.feature"), "Then the sum should be 4", "Then the sum should be 5"),
         symptom=r"There are test failures|Tests run:.*Failures: [1-9]", expect_no_report=True,
         fix_args=["net.serenity-bdd.maven.plugins:serenity-maven-plugin:%VER%:aggregate"],
         works=lambda p, o: has_report(p)),
    dict(id="restassured-groovy-pins", title="REST-assured and Groovy versions pinned in the pom (left over from an older Serenity)",
         base="sp-rest", harness="h-sp-rest", args=["verify", "-Dmaven.test.failure.ignore=true", "-Dtest=GroovyProbeTest"], keep_pom=True,
         break_=lambda p: (sub(pom(p), "  </dependencies>", "    <dependency><groupId>io.rest-assured</groupId><artifactId>rest-assured</artifactId><version>4.5.1</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>io.rest-assured</groupId><artifactId>json-path</artifactId><version>4.5.1</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>io.rest-assured</groupId><artifactId>xml-path</artifactId><version>4.5.1</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>org.codehaus.groovy</groupId><artifactId>groovy</artifactId><version>3.0.9</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>org.codehaus.groovy</groupId><artifactId>groovy-xml</artifactId><version>3.0.9</version><scope>test</scope></dependency>\n  </dependencies>"),
                           write(os.path.join(p, "src/test/java/api/GroovyProbeTest.java"), PROBE)),
         symptom=r"Conflicting module versions|Could not initialize class io.restassured.RestAssured",
         fix=lambda p: (sub(pom(p), "    <dependency><groupId>io.rest-assured</groupId><artifactId>rest-assured</artifactId><version>4.5.1</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>io.rest-assured</groupId><artifactId>json-path</artifactId><version>4.5.1</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>io.rest-assured</groupId><artifactId>xml-path</artifactId><version>4.5.1</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>org.codehaus.groovy</groupId><artifactId>groovy</artifactId><version>3.0.9</version><scope>test</scope></dependency>\n"
                                "    <dependency><groupId>org.codehaus.groovy</groupId><artifactId>groovy-xml</artifactId><version>3.0.9</version><scope>test</scope></dependency>\n", "")),
         works=lambda p, o: surefire_tests(p) > 0 and "Failures: 0, Errors: 0" in o),
    dict(id="serenity-plugin-missing", title="Serenity API used in a Cucumber run without the Serenity plugin",
         base="cuke-plain", harness="h-cuke-sp", args=["verify", "-Dmaven.test.failure.ignore=true"],
         break_=lambda p: (sub(runner(p), "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty", "pretty"),
                           sub(os.path.join(p, "src/test/java/demo/MathSteps.java"), "sum = a + b; }", 'sum = a + b; net.serenitybdd.core.Serenity.recordReportData().withTitle("inputs").andContents(a + "," + b); }')),
         symptom=r"No BaseStepListener has been registered",
         fix=lambda p: sub(runner(p), '"pretty")', '"net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty")'),
         works=lambda p, o: surefire_tests(p) > 0 and "Failures: 0, Errors: 0" in o),
    dict(id="managed-in-step-library", title="@Managed WebDriver field in a step library",
         base="po-selenium", harness="h-po-selenium", args=["verify", "-Dmaven.test.failure.ignore=true", "-Dheadless.mode=true"],
         break_=lambda p: (sub(os.path.join(p, "src/test/java/todo/TodoSteps.java"), "    TodoPage todoPage;", "    TodoPage todoPage;\n\n    @net.serenitybdd.annotations.Managed\n    org.openqa.selenium.WebDriver driver;"),
                           sub(os.path.join(p, "src/test/java/todo/TodoSteps.java"), "        todoPage.open();", '        driver.get("https://todo-app.serenity-js.org");')),
         symptom=r'because "this.driver" is null',
         fix=lambda p: sub(os.path.join(p, "src/test/java/todo/TodoSteps.java"), 'driver.get("https://todo-app.serenity-js.org");', 'getDriver().get("https://todo-app.serenity-js.org");'),
         works=lambda p, o: surefire_tests(p) > 0 and "Failures: 0, Errors: 0" in o),
    dict(id="css-read-as-xpath", title="Tag-prefixed CSS locator read as XPath",
         base="sp-selenium", harness="h-sp-selenium", args=["verify", "-Dmaven.test.failure.ignore=true", "-Dheadless.mode=true"],
         break_=lambda p: sub(os.path.join(p, "src/test/java/todo/TodoList.java"), 'located(By.cssSelector("input.new-todo"))', 'locatedBy("input.new-todo")'),
         symptom=r'"method":"xpath","selector":"input.new-todo"',
         fix=lambda p: sub(os.path.join(p, "src/test/java/todo/TodoList.java"), 'locatedBy("input.new-todo")', 'located(By.cssSelector("input.new-todo"))'),
         works=lambda p, o: surefire_tests(p) > 0 and "Failures: 0, Errors: 0" in o),
    dict(id="driver-path-wrong", title="webdriver.chrome.driver points at a file that does not exist",
         base="po-selenium", harness="h-po-selenium", args=["verify", "-Dmaven.test.failure.ignore=true", "-Dheadless.mode=true", "-Dwebdriver.chrome.driver=/no/such/chromedriver"],
         break_=lambda p: None, symptom=r"/no/such/chromedriver|driver executable|Could not instantiate|The driver",
         fix_args_replace=("-Dwebdriver.chrome.driver=/no/such/chromedriver", "-Dwebdriver.driver=chrome"),
         works=lambda p, o: surefire_tests(p) > 0 and "Failures: 0, Errors: 0" in o),
    dict(id="unknown-driver", title="Unknown driver name in webdriver.driver",
         base="po-selenium", harness="h-po-selenium", args=["verify", "-Dmaven.test.failure.ignore=true", "-Dheadless.mode=true", "-Dwebdriver.driver=chromee"],
         break_=lambda p: None, symptom=r"chromee|Unsupported|UnsupportedDriver|Could not instantiate",
         fix_args_replace=("-Dwebdriver.driver=chromee", "-Dwebdriver.driver=chrome"),
         works=lambda p, o: surefire_tests(p) > 0 and "Failures: 0, Errors: 0" in o),
]


def execute(case, ver):
    work = tempfile.mkdtemp(prefix="repro-")
    proj = os.path.join(work, "p")
    try:
        shutil.copytree(os.path.join(EVAL, case["harness"]), proj, ignore=shutil.ignore_patterns("target"))
        shutil.copytree(os.path.join(EX, case["base"]), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
        feats = os.path.join(proj, "src/test/java/features")
        if os.path.isdir(feats):
            shutil.move(feats, os.path.join(proj, "src/test/resources/features"))
        if case.get("default_surefire"):
            s = read(pom(proj))
            write(pom(proj), re.sub(r"\s*<configuration>\s*<!-- generated code.*?</configuration>", "", s, flags=re.S))
        case["break_"](proj)
        rc, out = run(proj, case["args"])
        symptom = line_matching(out, case["symptom"])
        if not symptom and os.environ.get("REPRO_DEBUG"):
            print("---- output of the broken build (%s) ----\n%s" % (case["id"], "\n".join(out.splitlines()[-45:])))
        result = dict(id=case["id"], title=case["title"], symptomSeen=bool(symptom), message=symptom)
        if case.get("expect_no_report"):
            result["noReportAfterFailure"] = not has_report(proj)
        if "fix" in case:
            shutil.rmtree(os.path.join(proj, "target"), ignore_errors=True)
            case["fix"](proj)
            rc2, out2 = run(proj, case["args"])
        elif "fix_args" in case:
            rc2, out2 = run(proj, [a.replace("%VER%", ver) for a in case["fix_args"]])
        else:
            old, new = case["fix_args_replace"]
            rc2, out2 = run(proj, [new if a == old else a for a in case["args"]])
        result["fixWorks"] = bool(case["works"](proj, out2))
        result["fixOutputTail"] = out2.strip().splitlines()[-1][:200] if out2.strip() else ""
        return result
    finally:
        shutil.rmtree(work, ignore_errors=True)


def main():
    ver = json.load(open(os.path.join(HERE, "..", "versions.json")))["serenity"]
    wanted = sys.argv[1:]
    results = []
    for c in CASES:
        if wanted and c["id"] not in wanted:
            continue
        try:
            r = execute(c, ver)
        except Exception as e:
            r = dict(id=c["id"], title=c["title"], symptomSeen=False, fixWorks=False, error=str(e)[:300])
        ok = r.get("symptomSeen") and r.get("fixWorks") and r.get("noReportAfterFailure", True)
        print("%-30s symptom=%s fix=%s %s" % (c["id"], r.get("symptomSeen"), r.get("fixWorks"), "" if ok else "<-- CHECK " + str(r.get("error", ""))))
        if r.get("message"): print("      ", r["message"][:200])
        results.append(r)
    if not wanted:
        json.dump(results, open(os.path.join(HERE, "observed.json"), "w"), indent=1)
    sys.exit(0 if all(r.get("symptomSeen") and r.get("fixWorks") for r in results) else 1)


if __name__ == "__main__":
    main()
