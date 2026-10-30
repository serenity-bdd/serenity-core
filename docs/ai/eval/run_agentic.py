#!/usr/bin/env python3
"""Baseline mode B: the model works in a prepared harness project with file tools and mvn, and can fix its own errors.

usage: run_agentic.py OUTDIR MODEL[,MODEL...] [PROMPT_ID,...] [--runs N]
"""
import json, os, re, shutil, subprocess, sys, tempfile, time
HERE = os.path.dirname(os.path.abspath(__file__))

AGENTS_MD = None  # set from --agents-md FILE (copied to AGENTS.md and CLAUDE.md in the project)
TASK = ("\n\nA Maven project with the Serenity BDD dependencies is already set up in the current directory. "
        "Write the code under src/test/java (and src/test/resources if needed). Do not change the pom.xml dependencies. "
        "No browser is available, so do not run the tests; check your work by running `mvn -o -q test-compile` "
        "and fix any errors until it compiles. When it compiles, stop. ")
TASK_APP = ("\n\nA Maven project with the Serenity BDD dependencies is already set up in the current directory. "
            "Write the code under src/test/java (and src/test/resources if needed). Do not change the pom.xml dependencies. "
            "The application described above is already running. Chrome is installed (use headless mode) and the Playwright browsers are available. "
            "Run your tests with `mvn -o verify -Dheadless.mode=true` and keep going until they pass. "
            "Nobody can answer questions: if something is unclear, state your assumption in one line and carry on. Stop when the tests pass.")
TOOLS = "Bash(mvn:*) Read Write Edit Glob Grep Skill"
SKILL = None  # set from --skill DIR
AGENTS = None  # set from --agents DIR (subagent definitions copied to .claude/agents)
ASSUME = ("Nobody can answer questions: if something is unspecified, "
          "state your assumption in one line and carry on.")
ASK = "If you need information from the user to proceed, ask your question and stop."

def run_one(model, prompt, harness, work, timeout=1200):
    proj = os.path.join(work, harness)
    shutil.copytree(os.path.join(HERE, "harness", harness), proj, ignore=shutil.ignore_patterns("target"))
    if AGENTS_MD:
        shutil.copy(AGENTS_MD, os.path.join(proj, "AGENTS.md")); shutil.copy(AGENTS_MD, os.path.join(proj, "CLAUDE.md"))
    if SKILL:
        shutil.copytree(SKILL, os.path.join(proj, ".claude", "skills", "serenity-bdd"), ignore=shutil.ignore_patterns("check-examples.sh"))
    tools = TOOLS
    user_install = "--user-install" in sys.argv   # skill and agent are installed under ~/.claude; the project has none
    if user_install:
        tools += " Agent Bash(python3:*)"
    if AGENTS:
        shutil.copytree(AGENTS, os.path.join(proj, ".claude", "agents"))
        tools += " Agent Bash(python3:*)"
    seeded = set()
    for root, _, fs in os.walk(os.path.join(proj, "src")):
        for f in fs: seeded.add(os.path.join(root, f))
    cmd = ["claude", "-p", "--output-format", "stream-json", "--verbose", "--model", model,
           "--setting-sources", "user" if user_install else "project", "--strict-mcp-config",
           "--permission-mode", "acceptEdits", "--allowedTools", *tools.split(), "--", prompt + (TASK_APP if "--app" in sys.argv else TASK + (ASK if "--ask-ok" in sys.argv else ASSUME))]
    t = time.time()
    try:
        r = subprocess.run(cmd, cwd=proj, capture_output=True, text=True, timeout=timeout)
        out, err = r.stdout, r.stderr
    except subprocess.TimeoutExpired as e:
        out, err = (e.stdout or b"").decode() if isinstance(e.stdout, bytes) else (e.stdout or ""), "TIMEOUT"
    secs = time.time() - t
    mvn_calls, tool_calls, turns, cost, touched_repo, skill_used, final_text = 0, 0, None, None, False, False, ""
    for line in out.splitlines():
        try: ev = json.loads(line)
        except Exception: continue
        if ev.get("type") == "assistant":
            for b in ev.get("message", {}).get("content", []):
                if b.get("type") == "tool_use":
                    tool_calls += 1
                    if b.get('name') == 'Skill': skill_used = True
                    if 'Projects/Serenity' in json.dumps(b.get('input', {})): touched_repo = True
                    if b.get("name") == "Bash" and "mvn" in json.dumps(b.get("input", {})):
                        mvn_calls += 1
        if ev.get("type") == "result":
            turns, cost = ev.get("num_turns"), ev.get("total_cost_usd"); final_text = ev.get("result") or ""
    return proj, out, err, secs, mvn_calls, tool_calls, turns, cost, touched_repo, seeded, skill_used, final_text

def classify(proj, seeded):
    """Style tags found in the java files the model added (not in the seeded project)."""
    tags = set()
    for root, _, fs in os.walk(os.path.join(proj, "src")):
        for f in fs:
            path = os.path.join(root, f)
            if path in seeded or not f.endswith((".java", ".feature")): continue
            t = open(path, errors="ignore").read()
            if "io.cucumber" in t or f.endswith(".feature"): tags.add("cucumber")
            if "net.serenitybdd.screenplay.playwright" in t: tags.add("screenplay-playwright")
            elif "net.serenitybdd.screenplay.rest" in t: tags.add("screenplay-rest")
            elif "net.serenitybdd.screenplay" in t: tags.add("screenplay-selenium")
            elif "com.microsoft.playwright" in t: tags.add("po-playwright")
            elif any(k in t for k in ("PageObject", "UIInteractionSteps", "ScenarioSteps", "@Steps")): tags.add("po-selenium")
    return sorted(tags)

def final_compile(proj):
    shutil.rmtree(os.path.join(proj, "target"), ignore_errors=True)
    r = subprocess.run(["mvn", "-q", "-B", "-o", "test-compile"], cwd=proj, capture_output=True, text=True)
    errs = [l.split("] ", 1)[-1] for l in (r.stdout + r.stderr).splitlines() if l.startswith("[ERROR]") and ".java" in l]
    return r.returncode == 0, errs

def files_written(proj):
    res = []
    for root, _, fs in os.walk(os.path.join(proj, "src")):
        res += [os.path.relpath(os.path.join(root, f), proj) for f in fs]
    return sorted(res)

def main():
    global SKILL, AGENTS_MD, AGENTS
    if "--agents" in sys.argv: AGENTS = os.path.abspath(sys.argv[sys.argv.index("--agents") + 1])
    if "--agents-md" in sys.argv: AGENTS_MD = os.path.abspath(sys.argv[sys.argv.index("--agents-md") + 1])
    if "--skill" in sys.argv: SKILL = os.path.abspath(sys.argv[sys.argv.index("--skill") + 1])
    out, models = sys.argv[1], sys.argv[2].split(",")
    only = sys.argv[3].split(",") if len(sys.argv) > 3 and not sys.argv[3].startswith("--") else None
    runs = int(sys.argv[sys.argv.index("--runs") + 1]) if "--runs" in sys.argv else 1
    prompts_file = sys.argv[sys.argv.index("--prompts") + 1] if "--prompts" in sys.argv else "prompts.json"
    prompts = [p for p in json.load(open(os.path.join(HERE, prompts_file))) if not only or p["id"] in only]
    os.makedirs(out, exist_ok=True)
    scratch = tempfile.mkdtemp(prefix="agentic-")
    results = []
    for model in models:
        for p in prompts:
            for n in range(1, runs + 1):
                tag = f"{p['id']}_{model}_{n}"
                harness = p["harness"] if p["harness"] != "auto" else "h-all"  # unnamed: every style's dependencies available
                work = os.path.join(scratch, tag); os.makedirs(work)
                proj, raw, err, secs, mvn_calls, tool_calls, turns, cost, touched_repo, seeded, skill_used, final_text = run_one(model, p["prompt"], harness, work)
                open(os.path.join(out, tag + ".stream.jsonl"), "w").write(raw)
                ok, errs = final_compile(proj)
                tags = classify(proj, seeded)
                files = files_written(proj)
                # save the produced sources
                dst = os.path.join(out, tag + ".src")
                if os.path.isdir(os.path.join(proj, "src")): shutil.copytree(os.path.join(proj, "src"), dst, dirs_exist_ok=True)
                rec = dict(id=p["id"], style=p["style"], model=model, run=n, harness=harness, compiled=ok,
                           mvn_calls=mvn_calls, tool_calls=tool_calls, turns=turns, cost_usd=cost, touched_repo=touched_repo, skill_used=skill_used, final_text=final_text[:600], style_found=tags,
                           secs=round(secs), files=files, errors=errs[:10], stderr=err[:300])
                results.append(rec)
                json.dump(rec, open(os.path.join(out, tag + ".json"), "w"), indent=1)
                print(f"{tag}: {'OK ' if ok else 'FAIL'} mvn={mvn_calls} tools={tool_calls} turns={turns} {rec['secs']}s", flush=True)
    json.dump(results, open(os.path.join(out, "summary.json"), "w"), indent=1)

if __name__ == "__main__":
    main()
