#!/usr/bin/env python3
"""Fix-it evaluation: can a model repair a broken Serenity project, with and without the skill?

usage: fixtask.py OUTDIR MODEL[,MODEL...] [CASE_ID,...] [--runs N] [--skill DIR]

For each case from repro.py: build the broken project, run Maven once to capture what a user would paste,
give the model the symptom and the project (tools: Maven, file tools, optionally the skill), then check
whether the project now works (the same check repro.py uses for the documented fix).
"""
import json, os, re, shutil, subprocess, sys, tempfile, time

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import repro  # noqa: E402

FILE_CASES = ["mixed-junit-versions", "cucumber-version-mismatch", "wrong-cucumber-plugin", "runner-not-picked-up",
              "no-report-after-failed-tests", "serenity-plugin-missing", "managed-in-step-library", "css-read-as-xpath"]
USER_WORDS = {
    "runner-not-picked-up": "`mvn verify` succeeds, but the Serenity report says 0 tests and none of the Cucumber scenarios run. Make the scenarios run.",
    "no-report-after-failed-tests": "A test fails (that is expected for now), but after `mvn verify` there is no Serenity HTML report in target/site/serenity. I need the report to be produced. Do not change the feature or the step code.",
}
TOOLS = "Bash(mvn:*) Read Write Edit Glob Grep Skill"
SKILL = None


def tail_for_user(out):
    keep = [l for l in out.splitlines() if re.search(r"ERROR|Exception|Error:|Results:|Tests run:|FAIL|No tests|conflicting|Caused by", l)]
    seen, res = set(), []
    for l in keep:
        l = l.strip()[:300]
        if l not in seen:
            seen.add(l); res.append(l)
    return "\n".join(res[:25])


def one(case, model, ver, skill, timeout=900):
    work = tempfile.mkdtemp(prefix="fix-")
    proj = os.path.join(work, "p")
    try:
        shutil.copytree(os.path.join(repro.EVAL, case["harness"]), proj, ignore=shutil.ignore_patterns("target"))
        shutil.copytree(os.path.join(repro.EX, case["base"]), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
        feats = os.path.join(proj, "src/test/java/features")
        if os.path.isdir(feats):
            shutil.move(feats, os.path.join(proj, "src/test/resources/features"))
        if case.get("default_surefire"):
            s = repro.read(repro.pom(proj))
            repro.write(repro.pom(proj), re.sub(r"\s*<configuration>\s*<!-- generated code.*?</configuration>", "", s, flags=re.S))
        case["break_"](proj)
        rc, out = repro.run(proj, case["args"])
        shutil.rmtree(os.path.join(proj, "target"), ignore_errors=True) if case["id"] != "no-report-after-failed-tests" else None
        words = USER_WORDS.get(case["id"]) or "Running `mvn verify` gives these errors:\n\n" + tail_for_user(out) + "\n\nFind the cause and fix the project."
        prompt = ("A Maven project with Serenity BDD tests is in the current directory. " + words +
                  " Do not change what the tests check. There is no browser, but headless Chrome is installed. Nobody can answer questions: "
                  "if something is unclear, state your assumption in one line and carry on. Stop when `mvn verify` behaves correctly.")
        if skill:
            shutil.copytree(skill, os.path.join(proj, ".claude", "skills", "serenity-bdd"),
                            ignore=shutil.ignore_patterns("check-examples.sh"))
        cmd = ["claude", "-p", "--output-format", "stream-json", "--verbose", "--model", model, "--setting-sources", "project",
               "--strict-mcp-config", "--permission-mode", "acceptEdits", "--allowedTools", *TOOLS.split(), "--", prompt]
        t = time.time()
        try:
            r = subprocess.run(cmd, cwd=proj, capture_output=True, text=True, timeout=timeout)
            stream = r.stdout
        except subprocess.TimeoutExpired as e:
            stream = (e.stdout or b"").decode() if isinstance(e.stdout, bytes) else (e.stdout or "")
        secs = round(time.time() - t)
        mvn_calls = tool_calls = 0; turns = cost = None; skill_used = False; read_ref = False
        for line in stream.splitlines():
            try: ev = json.loads(line)
            except Exception: continue
            if ev.get("type") == "assistant":
                for b in ev.get("message", {}).get("content", []):
                    if b.get("type") == "tool_use":
                        tool_calls += 1
                        if b.get("name") == "Skill": skill_used = True
                        if b.get("name") == "Bash" and "mvn" in json.dumps(b.get("input", {})): mvn_calls += 1
                        if "troubleshooting.md" in json.dumps(b.get("input", {})): read_ref = True
            if ev.get("type") == "result":
                turns, cost = ev.get("num_turns"), ev.get("total_cost_usd")
        # judge: the same check repro.py uses for the documented fix, on a fresh build
        if case["id"] == "no-report-after-failed-tests":
            ok = repro.has_report(proj)
            if not ok:   # the model may have fixed the cause instead of producing the report from the failed build
                rc2, out2 = repro.run(proj, ["verify"]); ok = repro.has_report(proj)
        else:
            shutil.rmtree(os.path.join(proj, "target"), ignore_errors=True)
            rc2, out2 = repro.run(proj, case["args"])
            ok = bool(case["works"](proj, out2))
        return dict(case=case["id"], model=model, fixed=ok, secs=secs, mvn_calls=mvn_calls, tool_calls=tool_calls, turns=turns,
                    cost_usd=cost, skill_used=skill_used, read_troubleshooting=read_ref)
    finally:
        shutil.rmtree(work, ignore_errors=True)


def main():
    global SKILL
    a = sys.argv[1:]
    if "--skill" in a: SKILL = os.path.abspath(a[a.index("--skill") + 1])
    out, models = a[0], a[1].split(",")
    only = a[2].split(",") if len(a) > 2 and not a[2].startswith("--") else FILE_CASES
    runs = int(a[a.index("--runs") + 1]) if "--runs" in a else 1
    ver = json.load(open(os.path.join(HERE, "..", "versions.json")))["serenity"]
    os.makedirs(out, exist_ok=True)
    allres = []
    for model in models:
        for cid in only:
            case = next(c for c in repro.CASES if c["id"] == cid)
            for n in range(1, runs + 1):
                res = one(case, model, ver, SKILL)
                res["run"] = n
                allres.append(res)
                json.dump(res, open(os.path.join(out, "%s_%s_%d.json" % (cid, model, n)), "w"), indent=1)
                print("%s %s %d: %s mvn=%s turns=%s %ss" % (cid, model, n, "FIXED" if res["fixed"] else "NOT FIXED", res["mvn_calls"], res["turns"], res["secs"]), flush=True)
    json.dump(allres, open(os.path.join(out, "summary.json"), "w"), indent=1)


if __name__ == "__main__":
    main()
