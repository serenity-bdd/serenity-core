#!/usr/bin/env python3
"""Verify the how-to claims in serenity-bdd/references/cucumber.md by running them.

usage: verify.py [CASE_ID ...]

Each case builds the cuke-plain example (5 scenarios: 1 @smoke, 2 outline rows, 2 @basket of which 1 @wip),
optionally edits it, runs Maven with the given arguments, and checks how many scenarios ran (and any extra
condition). The commands in the reference are exactly the ones used here.
"""
import json, os, re, shutil, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

BASE = ["verify", "-Dmaven.test.failure.ignore=true"]
RUNNER = "src/test/java/demo/RunCucumberTests.java"
FEATURES = "src/test/resources/features"


def add_param(p, line):
    """Add a @ConfigurationParameter line to the runner."""
    repro.sub(os.path.join(p, RUNNER), "public class RunCucumberTests", line + "\npublic class RunCucumberTests")


def add_failing_feature(p):
    repro.write(os.path.join(p, FEATURES, "failing.feature"),
                "Feature: Failing\n\n  Scenario: This one fails\n    Given I have 1 and 1\n    Then the sum should be 3\n")


def write_props(p, text):
    repro.write(os.path.join(p, "src/test/resources/junit-platform.properties"), text)


def count_in(o): return int(re.search(r"Results: (\d+) tests", o).group(1)) if re.search(r"Results: (\d+) tests", o) else -1


def pico(p):
    repro.sub(repro.pom(p), "  </dependencies>", "    <dependency><groupId>io.cucumber</groupId><artifactId>cucumber-picocontainer</artifactId><version>${cucumber.version}</version><scope>test</scope></dependency>\n  </dependencies>")
    d = os.path.join(p, "src/test/java/demo")
    repro.write(os.path.join(d, "Shared.java"), "package demo;\n\npublic class Shared { public String greeting; }\n")
    repro.write(os.path.join(d, "StepsA.java"), "package demo;\n\nimport io.cucumber.java.en.Given;\n\npublic class StepsA {\n    private final Shared shared;\n    public StepsA(Shared shared) { this.shared = shared; }\n    @Given(\"the greeting is {word}\")\n    public void greeting(String g) { shared.greeting = g; }\n}\n")
    repro.write(os.path.join(d, "StepsB.java"), "package demo;\n\nimport io.cucumber.java.en.Then;\nimport static org.assertj.core.api.Assertions.assertThat;\n\npublic class StepsB {\n    private final Shared shared;\n    public StepsB(Shared shared) { this.shared = shared; }\n    @Then(\"the greeting should be {word}\")\n    public void check(String g) { assertThat(shared.greeting).isEqualTo(g); }\n}\n")
    repro.write(os.path.join(p, FEATURES, "shared.feature"), "Feature: Sharing state\n\n  Scenario: Two step classes share an object\n    Given the greeting is hello\n    Then the greeting should be hello\n")


def hook_order(p):
    repro.write(os.path.join(p, "src/test/java/demo/Order.java"), """package demo;
import io.cucumber.java.After; import io.cucumber.java.Before;
public class Order {
  @Before(order = 5) public void b5() { System.out.println("HOOKORDER before-5"); }
  @Before(order = 1) public void b1() { System.out.println("HOOKORDER before-1"); }
  @After(order = 5) public void a5() { System.out.println("HOOKORDER after-5"); }
  @After(order = 1) public void a1() { System.out.println("HOOKORDER after-1"); }
}""")


def hook_order_ok(p, out):
    return re.findall(r"HOOKORDER (\S+)", out) == ["before-1", "before-5", "after-5", "after-1"]


def rerun_case(c):
    """Two runs: the first fails and writes target/rerun.txt; the second runs only what is listed in it."""
    work = tempfile.mkdtemp(prefix="cuke-")
    proj = os.path.join(work, "p")
    try:
        shutil.copytree(os.path.join(repro.EVAL, "h-cuke-sp"), proj, ignore=shutil.ignore_patterns("target"))
        shutil.copytree(os.path.join(repro.EX, "cuke-plain"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
        shutil.move(os.path.join(proj, "src/test/java/features"), os.path.join(proj, "src/test/resources/features"))
        add_failing_feature(proj)
        repro.sub(os.path.join(proj, RUNNER), "SerenityReporterParallel,pretty", "SerenityReporterParallel,pretty,rerun:target/rerun.txt")
        rc, out = repro.run(proj, BASE)
        first = count_in(out)
        rerun_file = os.path.join(proj, "target", "rerun.txt")
        listed = open(rerun_file).read().strip() if os.path.exists(rerun_file) else ""
        for stale in ("site", "surefire-reports"):
            shutil.rmtree(os.path.join(proj, "target", stale), ignore_errors=True)
        rc2, out2 = repro.run(proj, BASE + ["-Dcucumber.features=@target/rerun.txt"])
        second = repro.surefire_tests(proj)
        ok = first == 6 and second == 1 and "failing.feature" in listed
        return dict(id=c["id"], expected=1, ran=second, ok=ok, detail="first run %d scenarios, rerun.txt: %s" % (first, listed))
    finally:
        shutil.rmtree(work, ignore_errors=True)


def gradle_case(c):
    """Gradle project built from the same sources; the build file is gradle/build.gradle."""
    import subprocess
    work = tempfile.mkdtemp(prefix="cukeg-")
    try:
        shutil.copytree(os.path.join(repro.EX, "cuke-plain"), os.path.join(work, "src/test/java"))
        shutil.move(os.path.join(work, "src/test/java/features"), os.path.join(work, "src/test/resources/features"))
        shutil.copy(os.path.join(HERE, "gradle", "build.gradle"), os.path.join(work, "build.gradle"))
        repro.write(os.path.join(work, "settings.gradle"), "rootProject.name = 'cuke-plain'\n")
        if c["id"] == "gradle-tags-not-forwarded":
            repro.sub(os.path.join(work, "build.gradle"), "    systemProperties System.properties.findAll { k, v -> k.toString().startsWith('cucumber.') }\n", "")
        r = subprocess.run(["gradle", "--no-daemon", "-q", "clean", "test", *c["gargs"]], cwd=work, capture_output=True, text=True, timeout=900)
        out = r.stdout + r.stderr
        reports = os.path.exists(os.path.join(work, "target/site/serenity/index.html"))
        n = len([f for f in __import__("glob").glob(os.path.join(work, "target/site/serenity/*.json"))])
        ok = n == c["expect"] and (reports or not c.get("must_have_report"))
        return dict(id=c["id"], expected=c["expect"], ran=n, ok=ok, detail="report " + ("yes" if reports else "no") + (" | " + out.strip().splitlines()[-1][:120] if r.returncode and out.strip() else ""))
    finally:
        shutil.rmtree(work, ignore_errors=True)


def stale_case(c):
    """Serenity counts the outcome files already in target/site/serenity, including ones from earlier runs."""
    work = tempfile.mkdtemp(prefix="cukes-")
    proj = os.path.join(work, "p")
    try:
        shutil.copytree(os.path.join(repro.EVAL, "h-cuke-sp"), proj, ignore=shutil.ignore_patterns("target"))
        shutil.copytree(os.path.join(repro.EX, "cuke-plain"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
        shutil.move(os.path.join(proj, "src/test/java/features"), os.path.join(proj, "src/test/resources/features"))
        repro.run(proj, BASE)                                                              # 5 scenarios
        rc, out = repro.run(proj, BASE + ["-Dcucumber.filter.tags=@smoke"])                 # 1 scenario, no clean
        stale = count_in(out)
        shutil.rmtree(os.path.join(proj, "target", "site"), ignore_errors=True)
        rc, out = repro.run(proj, BASE + ["-Dcucumber.filter.tags=@smoke"])
        clean = count_in(out)
        return dict(id=c["id"], expected=1, ran=clean, ok=stale == 5 and clean == 1, detail="without cleaning the report said %d tests for a run of 1" % stale)
    finally:
        shutil.rmtree(work, ignore_errors=True)


def run_case(c):
    if c.get("special") == "stale":
        return stale_case(c)
    if c.get("special") == "gradle":
        return gradle_case(c)
    if c.get("special") == "rerun":
        return rerun_case(c)
    work = tempfile.mkdtemp(prefix="cuke-")
    proj = os.path.join(work, "p")
    try:
        shutil.copytree(os.path.join(repro.EVAL, "h-cuke-sp"), proj, ignore=shutil.ignore_patterns("target"))
        shutil.copytree(os.path.join(repro.EX, "cuke-plain"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
        shutil.move(os.path.join(proj, "src/test/java/features"), os.path.join(proj, "src/test/resources/features"))
        if c.get("mutate"):
            c["mutate"](proj)
        rc, out = repro.run(proj, c["args"])
        n = count_in(out)
        ok = n == c["expect"] and (not c.get("must_have_report") or repro.has_report(proj))
        ok = ok and c.get("check", lambda p, o: True)(proj, out)
        if c["id"] == "all-scenarios":   # the hooks' report data is in the outcomes
            blob = "".join(open(f).read() for f in __import__("glob").glob(os.path.join(proj, "target/site/serenity/*.json")))
            ok = ok and "Scenario tags" in blob and blob.count("basket scenario") >= 1
        return dict(id=c["id"], expected=c["expect"], ran=n, ok=ok)
    finally:
        shutil.rmtree(work, ignore_errors=True)


CASES = [
    dict(id="all-scenarios", args=BASE, expect=5),
    dict(id="filter-by-tag-command-line", args=BASE + ["-Dcucumber.filter.tags=@smoke"], expect=1),
    dict(id="exclude-tag-command-line", args=BASE + ["-Dcucumber.filter.tags=not @wip"], expect=4),
    dict(id="tag-expression-and", args=BASE + ["-Dcucumber.filter.tags=@basket and not @wip"], expect=1),
    dict(id="filter-by-name", args=BASE + ["-Dcucumber.filter.name=Adding passes"], expect=1),
    dict(id="tags-in-runner", args=BASE, expect=4,
         mutate=lambda p: (repro.sub(os.path.join(p, RUNNER), "import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;",
                                     "import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;\nimport static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;"),
                           add_param(p, '@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")'))),
    dict(id="tags-in-junit-platform-properties", args=BASE, expect=4,
         mutate=lambda p: write_props(p, "cucumber.filter.tags=not @wip\n")),
    dict(id="single-feature-file", args=BASE + ["-Dcucumber.features=src/test/resources/features/basket.feature"], expect=2, note="features selected from the command line"),
    dict(id="rerun-failed-scenarios", special="rerun", expect=1),
    dict(id="share-state-picocontainer", args=BASE, expect=6, mutate=pico),
    dict(id="hook-order", args=BASE + ["-Dcucumber.filter.tags=@smoke"], expect=1, mutate=hook_order, check=hook_order_ok),
    dict(id="stale-outcomes-counted", special="stale", expect=1),
    dict(id="gradle-run", special="gradle", gargs=[], expect=4, must_have_report=True, note="outcome files: the outline counts once"),
    dict(id="gradle-tags-forwarded", special="gradle", gargs=["-Dcucumber.filter.tags=@smoke"], expect=1),
    dict(id="gradle-tags-not-forwarded", special="gradle", gargs=["-Dcucumber.filter.tags=@smoke"], expect=4),
    dict(id="parallel", args=BASE, expect=5, must_have_report=True,
         mutate=lambda p: write_props(p, "cucumber.execution.parallel.enabled=true\ncucumber.execution.parallel.config.strategy=fixed\n"
                                         "cucumber.execution.parallel.config.fixed.parallelism=4\n")),
]


def main():
    wanted = sys.argv[1:]
    res = []
    for c in CASES:
        if wanted and c["id"] not in wanted:
            continue
        r = run_case(c); res.append(r)
        print("%-36s expected %d ran %d %s %s" % (r["id"], r["expected"], r["ran"], "ok" if r["ok"] else "<-- MISMATCH", r.get("detail", "")))
    sys.exit(0 if all(r["ok"] for r in res) else 1)


if __name__ == "__main__":
    main()
