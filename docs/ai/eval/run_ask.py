#!/usr/bin/env python3
"""Ask-then-answer test with the skill installed at user level (see install_user_level.sh).

usage: run_ask.py OUT_DIR MODEL [--runs N]

Turn 1: an unclear-style prompt (no style named). The model should ask which style, and write no test files.
Turn 2: resumed with the user's answer. The model should then write tests in exactly that style.
Records per case: asked (no test files after turn 1), then files, compile result and detected style after turn 2.
"""
import json, os, shutil, subprocess, sys, tempfile
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import run_agentic as ra  # noqa: E402

CASES = [
    # id, harness, first prompt (style unclear), the answer, expected style tag
    ("U1", "h-all", "Write a Serenity BDD test that signs in to the bookshop at http://localhost:8765 with reader@example.com / correct-horse and checks that the Catalog page is shown. The app is running; do not start it.",
     "Screenplay with Selenium please.", "screenplay-selenium"),
    ("U2", "h-all", "I want to start testing our web app with Serenity BDD. Write a first test for the bookshop sign-in at http://localhost:8765 (valid account reader@example.com / correct-horse; the Catalog page should show). It is already running.",
     "Page Objects with a step library, using Playwright.", "po-playwright"),
    ("U3", "h-all", "Add a Serenity BDD test for the bookshop API at http://localhost:8765: GET /api/books returns the 5 books. The app is running; do not start it.",
     "Screenplay, REST.", "screenplay-rest"),
]
TOOLS = "Bash(mvn:*) Read Write Edit Glob Grep Skill Agent Bash(python3:*)"
SUFFIX = "\n\nRun `mvn -o verify -Dheadless.mode=true` until it passes."


def claude(model, prompt, cwd, resume=None):
    cmd = ["claude", "-p", "--output-format", "stream-json", "--verbose", "--model", model, "--setting-sources", "user",
           "--strict-mcp-config", "--permission-mode", "acceptEdits", "--allowedTools", *TOOLS.split()]
    if resume:
        cmd += ["--resume", resume]
    cmd += ["--", prompt]
    r = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True, timeout=1500)
    sid, text, skill, tools = None, "", False, 0
    for line in r.stdout.splitlines():
        try:
            ev = json.loads(line)
        except ValueError:
            continue
        sid = ev.get("session_id", sid)
        if ev.get("type") == "assistant":
            for b in ev.get("message", {}).get("content", []):
                if b.get("type") == "tool_use":
                    tools += 1
                    skill = skill or b.get("name") == "Skill"
        if ev.get("type") == "result":
            text = ev.get("result") or ""
    return sid, text, skill, tools, r.stdout


def new_files(proj, seeded):
    out = []
    for root, _, fs in os.walk(os.path.join(proj, "src")):
        out += [os.path.join(root, f) for f in fs if os.path.join(root, f) not in seeded]
    return out


def main():
    out, model = sys.argv[1], sys.argv[2]
    os.makedirs(out, exist_ok=True)
    for cid, harness, p1, answer, expect in CASES:
        work = tempfile.mkdtemp(prefix="ask-")
        proj = os.path.join(work, harness)
        shutil.copytree(os.path.join(HERE, "harness", harness), proj, ignore=shutil.ignore_patterns("target"))
        seeded = {os.path.join(r, f) for r, _, fs in os.walk(os.path.join(proj, "src")) for f in fs}
        sid, q, skill1, tools1, raw1 = claude(model, p1 + SUFFIX, proj)
        wrote_after_1 = bool(new_files(proj, seeded))
        sid2, final, skill2, tools2, raw2 = claude(model, answer, proj, resume=sid) if sid else (None, "", False, 0, "")
        files = new_files(proj, seeded)
        tags = ra.classify(proj, seeded)
        ok, errs = ra.final_compile(proj) if files else (False, ["no files"])
        rec = dict(case=cid, model=model, asked=not wrote_after_1, question=q[:600], skill_loaded=skill1, turn2_files=len(files),
                   style_expected=expect, style_found=tags, compiled=ok, final=final[:400])
        json.dump(rec, open(os.path.join(out, "%s_%s.json" % (cid, model)), "w"), indent=1)
        open(os.path.join(out, "%s_%s.stream1.jsonl" % (cid, model)), "w").write(raw1)
        open(os.path.join(out, "%s_%s.stream2.jsonl" % (cid, model)), "w").write(raw2)
        print(cid, model, "asked" if rec["asked"] else "WROTE-WITHOUT-ASKING", "skill" if skill1 else "no-skill", expect, tags, "compiled" if ok else "NOT-COMPILED", flush=True)


if __name__ == "__main__":
    main()
