#!/usr/bin/env python3
"""How-to evaluation: can a model do common Cucumber tasks in a Serenity project, with and without the skill?

usage: howto.py OUTDIR MODEL[,MODEL...] [TASK_ID,...] [--runs N] [--skill DIR]

Starts from the cuke-plain example (Maven, 5 scenarios) and gives the model one task. Success is judged by
running the project afterwards, not by reading the answer.
"""
import glob, json, os, re, shutil, subprocess, sys, tempfile, time

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402
import verify  # noqa: E402

TOOLS = "Bash(mvn:*) Bash(gradle:*) Read Write Edit Glob Grep Skill"
SKILL = None


def fresh_run(proj, extra=()):
    shutil.rmtree(os.path.join(proj, "target"), ignore_errors=True)
    rc, out = repro.run(proj, verify.BASE + list(extra))
    return verify.count_in(out), out   # Serenity's own count of the scenarios that ran (surefire also counts the skipped ones)


def check_smoke_default(proj):
    n, _ = fresh_run(proj)
    return n == 1


def check_rerun(proj):
    """After a run with a failing scenario, some file written by Cucumber's rerun plugin (any name) lists it."""
    verify.add_failing_feature(proj)
    fresh_run(proj)
    for f in glob.glob(os.path.join(proj, "target", "*")):
        if os.path.isfile(f) and f.endswith((".txt", ".rerun")):
            if "failing.feature" in open(f, errors="ignore").read():
                return True
    return False


def add_shared_feature(proj):
    repro.write(os.path.join(proj, "src/test/resources/features/customer.feature"),
                "Feature: Customer\n\n  Scenario: The customer name is remembered across steps\n"
                "    Given a customer called Alice\n    When the customer asks for the basket\n    Then the basket belongs to Alice\n")


def check_shared(proj):
    n, out = fresh_run(proj)
    return n == 6 and "Failures: 0, Errors: 0" in out


def check_parallel(proj):
    n, out = fresh_run(proj)
    blob = "".join(open(f, errors="ignore").read() for f in glob.glob(os.path.join(proj, "src/test/**/*.*"), recursive=True))
    blob += open(os.path.join(proj, "pom.xml")).read()
    # the property can be written as text (in a properties file, or as a key/value pair) or through the Constants class
    enabled = re.search(r"parallel\.enabled[\"']?[^a-z]{0,40}?(value\s*=\s*)?[\"']?true", blob) or re.search(r"PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME\s*,\s*value\s*=\s*\"true\"", blob)
    return n == 5 and "Failures: 0, Errors: 0" in out and enabled is not None


def check_gradle(proj):
    shutil.rmtree(os.path.join(proj, "target"), ignore_errors=True)
    r = subprocess.run(["gradle", "--no-daemon", "-q", "clean", "test", "-Dcucumber.filter.tags=@smoke"], cwd=proj,
                       capture_output=True, text=True, timeout=900)
    n = len(glob.glob(os.path.join(proj, "target/site/serenity/*.json")))
    return n == 1 and os.path.exists(os.path.join(proj, "target/site/serenity/index.html"))


TASKS = [
    dict(id="smoke-by-default", words="Make `mvn verify` run only the scenarios tagged @smoke by default, without passing any command-line options.",
         check=check_smoke_default),
    dict(id="rerun-failures", words="I want to be able to re-run only the scenarios that failed in the last run. Make whatever changes the project needs so that a run records its failed scenarios, and tell me the command to re-run them.",
         check=check_rerun),
    dict(id="share-customer", words="The feature file src/test/resources/features/customer.feature has no step definitions yet. Write them. Put the 'given' step in one Java class and the 'when' and 'then' steps in a different Java class, and make the customer name available to both.",
         setup=add_shared_feature, check=check_shared),
    dict(id="parallel", words="Run the scenarios in parallel, four at a time.", check=check_parallel),
    dict(id="gradle", words="Convert this project from Maven to Gradle (add build.gradle and settings.gradle). Keep the Serenity report, and make `gradle test -Dcucumber.filter.tags=@smoke` run only the @smoke scenarios and produce the report in target/site/serenity.",
         check=check_gradle),
]


def one(task, model, skill, out_dir, run_no, timeout=1200):
    work = tempfile.mkdtemp(prefix="howto-")
    proj = os.path.join(work, "p")
    try:
        shutil.copytree(os.path.join(repro.EVAL, "h-cuke-sp"), proj, ignore=shutil.ignore_patterns("target"))
        shutil.copytree(os.path.join(repro.EX, "cuke-plain"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
        shutil.move(os.path.join(proj, "src/test/java/features"), os.path.join(proj, "src/test/resources/features"))
        if task.get("setup"):
            task["setup"](proj)
        prompt = ("A Maven project with Serenity BDD and Cucumber tests is in the current directory. " + task["words"] +
                  " Do not change what the existing scenarios check. Nobody can answer questions: if something is unclear, state your assumption "
                  "in one line and carry on. Check your work by running the build.")
        if skill:
            shutil.copytree(skill, os.path.join(proj, ".claude", "skills", "serenity-bdd"), ignore=shutil.ignore_patterns("check-examples.sh"))
        cmd = ["claude", "-p", "--output-format", "stream-json", "--verbose", "--model", model, "--setting-sources", "project",
               "--strict-mcp-config", "--permission-mode", "acceptEdits", "--allowedTools", *TOOLS.split(), "--", prompt]
        t = time.time()
        try:
            r = subprocess.run(cmd, cwd=proj, capture_output=True, text=True, timeout=timeout)
            stream = r.stdout
        except subprocess.TimeoutExpired as e:
            stream = (e.stdout or b"").decode() if isinstance(e.stdout, bytes) else (e.stdout or "")
        secs = round(time.time() - t)
        tools = turns = 0; cost = None; skill_used = False
        for line in stream.splitlines():
            try: ev = json.loads(line)
            except Exception: continue
            if ev.get("type") == "assistant":
                for b in ev.get("message", {}).get("content", []):
                    if b.get("type") == "tool_use":
                        tools += 1
                        if b.get("name") == "Skill": skill_used = True
            if ev.get("type") == "result":
                turns, cost = ev.get("num_turns"), ev.get("total_cost_usd")
        ok = bool(task["check"](proj))
        keep = os.path.join(out_dir, "%s_%s_%s.project" % (task["id"], model, run_no))
        for f in ("pom.xml", "build.gradle", "settings.gradle"):
            if os.path.exists(os.path.join(proj, f)):
                os.makedirs(keep, exist_ok=True); shutil.copy(os.path.join(proj, f), keep)
        if os.path.isdir(os.path.join(proj, "src")):
            shutil.copytree(os.path.join(proj, "src"), os.path.join(keep, "src"), dirs_exist_ok=True)
        return dict(task=task["id"], model=model, ok=ok, secs=secs, tool_calls=tools, turns=turns, cost_usd=cost, skill_used=skill_used)
    finally:
        if os.environ.get("HOWTO_KEEP"):
            print("kept", work)
        else:
            shutil.rmtree(work, ignore_errors=True)


def main():
    global SKILL
    a = sys.argv[1:]
    if "--skill" in a: SKILL = os.path.abspath(a[a.index("--skill") + 1])
    out, models = a[0], a[1].split(",")
    only = a[2].split(",") if len(a) > 2 and not a[2].startswith("--") else [t["id"] for t in TASKS]
    runs = int(a[a.index("--runs") + 1]) if "--runs" in a else 1
    os.makedirs(out, exist_ok=True)
    res = []
    for m in models:
        for t in TASKS:
            if t["id"] not in only: continue
            for n in range(1, runs + 1):
                r = one(t, m, SKILL, out, n); r["run"] = n; res.append(r)
                json.dump(r, open(os.path.join(out, "%s_%s_%d.json" % (t["id"], m, n)), "w"), indent=1)
                print("%s %s %d: %s %ss turns=%s" % (t["id"], m, n, "OK" if r["ok"] else "FAILED", r["secs"], r["turns"]), flush=True)
    json.dump(res, open(os.path.join(out, "summary.json"), "w"), indent=1)


if __name__ == "__main__":
    main()
