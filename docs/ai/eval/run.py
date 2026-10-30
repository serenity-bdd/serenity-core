#!/usr/bin/env python3
"""Baseline mode A runner: one-shot generation with no tools, then compile in a harness copy.

usage: run.py OUTDIR MODEL[,MODEL...] [PROMPT_ID,...] [--runs N]
"""
import json, os, re, shutil, subprocess, sys, tempfile, time

HERE = os.path.dirname(os.path.abspath(__file__))
SUFFIX = ("\n\nReturn every file as a fenced code block immediately preceded by a line "
          "`FILE: <path relative to the project root>`. Put Java under src/test/java and resources under "
          "src/test/resources. Do not include a pom.xml. Add no commentary.")

GUIDANCE = ""
if "--guidance" in sys.argv:
    GUIDANCE = ("Reference notes for Serenity BDD (verified against the current release):\n\n"
                + open(sys.argv[sys.argv.index("--guidance") + 1]).read() + "\n\n---\nTask:\n")


# --guidance-auto: give each prompt the references that match its style (used with prompts-v2.json)
REFS = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "skill", "serenity-bdd", "references")
BY_STYLE = {"Page Objects": ["page-objects.md"], "Screenplay": ["screenplay.md"], "Cucumber + Screenplay": ["screenplay.md", "cucumber.md"],
            "Cucumber + page objects": ["cucumber.md", "page-objects.md"], "REST + step library": ["rest-assured.md"]}


def auto_guidance(style):
    names = list(next((v for k, v in BY_STYLE.items() if style.startswith(k)), []))
    if names and "--guidance-living" in sys.argv:
        names.append("living-documentation.md")
    text = "\n\n---\n\n".join(open(os.path.join(REFS, n)).read() for n in names)
    return "Reference notes for Serenity BDD (verified against the current release):\n\n" + text + "\n\n---\nTask:\n" if names else ""


def ask(model, prompt, cwd, style=""):
    prompt = (auto_guidance(style) if "--guidance-auto" in sys.argv else GUIDANCE) + prompt
    cmd = ["claude", "-p", "--tools", "", "--disable-slash-commands", "--setting-sources", "",
           "--strict-mcp-config", "--model", model, prompt + SUFFIX]
    t = time.time()
    r = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True)
    return r.stdout, r.stderr, time.time() - t

def extract(text):
    files = {}
    for m in re.finditer(r"FILE:\s*`?([^\s`]+)`?\s*\n```[^\n]*\n(.*?)\n```", text, re.S):
        files[m.group(1).lstrip("/")] = m.group(2) + "\n"
    return files

def pick_harness(files):
    blob = "\n".join(files.values())
    if "net.serenitybdd.screenplay.playwright" in blob: return "h-sp-playwright"
    if "com.microsoft.playwright" in blob: return "h-po-playwright"
    if "io.cucumber" in blob: return "h-cuke-sp"
    if "screenplay.rest" in blob: return "h-sp-rest"
    if "net.serenitybdd.screenplay" in blob: return "h-sp-selenium"
    return "h-po-selenium"

def compile_in(harness, files, work):
    dst = os.path.join(work, harness)
    shutil.copytree(os.path.join(HERE, "harness", harness), dst,
                    ignore=shutil.ignore_patterns("target"))
    for p, c in files.items():
        full = os.path.join(dst, p)
        os.makedirs(os.path.dirname(full), exist_ok=True)
        open(full, "w").write(c)
    r = subprocess.run(["mvn", "-q", "-B", "-o", "test-compile"], cwd=dst, capture_output=True, text=True)
    errs = [l.split("] ", 1)[-1] for l in (r.stdout + r.stderr).splitlines() if l.startswith("[ERROR]") and ".java" in l]
    return r.returncode == 0, errs

def main():
    out, models = sys.argv[1], sys.argv[2].split(",")
    only = sys.argv[3].split(",") if len(sys.argv) > 3 and not sys.argv[3].startswith("--") else None
    runs = int(sys.argv[sys.argv.index("--runs") + 1]) if "--runs" in sys.argv else 1
    prompts_file = sys.argv[sys.argv.index("--prompts") + 1] if "--prompts" in sys.argv else "prompts.json"
    prompts = [p for p in json.load(open(os.path.join(HERE, prompts_file))) if not only or p["id"] in only]
    os.makedirs(out, exist_ok=True)
    scratch = tempfile.mkdtemp(prefix="eval-")  # empty cwd for the model: no repo files, no CLAUDE.md
    results = []
    for model in models:
        for p in prompts:
            for n in range(1, runs + 1):
                tag = f"{p['id']}_{model}_{n}"
                raw, err, secs = ask(model, p["prompt"], scratch, p["style"])
                open(os.path.join(out, tag + ".raw.txt"), "w").write(raw)
                files = extract(raw)
                harness = p["harness"] if p["harness"] != "auto" else pick_harness(files)
                if files:
                    ok, errs = compile_in(harness, files, os.path.join(scratch, tag))
                else:
                    ok, errs = False, ["NO FILES EXTRACTED"]
                rec = dict(id=p["id"], style=p["style"], model=model, run=n, harness=harness,
                           compiled=ok, errors=errs[:15], files=sorted(files), secs=round(secs), stderr=err[:300])
                results.append(rec)
                json.dump(rec, open(os.path.join(out, tag + ".json"), "w"), indent=1)
                print(f"{tag}: {'OK ' if ok else 'FAIL'} {len(files)} files, {len(errs)} errors, {rec['secs']}s", flush=True)
    json.dump(results, open(os.path.join(out, "summary.json"), "w"), indent=1)

if __name__ == "__main__":
    main()
