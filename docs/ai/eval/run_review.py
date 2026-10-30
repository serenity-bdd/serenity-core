#!/usr/bin/env python3
"""Score the living-documentation quality of generated tests (blind to the condition that produced them).

usage: run_review.py RESULTS_DIR [--model M] [--workers N] [--only A01,A02] [--force]

For each saved run in RESULTS_DIR (the .src directory or .raw.txt), asks a model with no tools to score the code
against the checks of skill/serenity-bdd/references/review-checklist.md that can be judged from source (A1-A3, B1-B3,
C1-C3), 0 (fail), 1 (weak) or 2 (pass), and writes RESULTS_DIR/review/<tag>.json plus RESULTS_DIR/review.json.
The judge does not see which guidance or skill the run had.
"""
import concurrent.futures as cf, glob, json, os, re, subprocess, sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import run_tests  # noqa: E402

PROMPTS = {}
for f in ("prompts-v2.json", "prompts-v3.json", "prompts-v4.json", "prompts-v5.json", "prompts-v6.json", "prompts-v7.json", "prompts.json"):
    if os.path.exists(os.path.join(HERE, f)):
        PROMPTS.update({p["id"]: p for p in json.load(open(os.path.join(HERE, f)))})

RUBRIC = """You are reviewing automated acceptance tests written with Serenity BDD. A Serenity report is living documentation: a business reader who has never seen the code reads the scenario titles, the steps and the requirements tree to learn what the application does. Serenity builds the report from these sources: the scenario title (@DisplayName on the method, or the method name turned into words, or the Cucumber scenario name), the step text (Task.where("..."), @Step("..."), or the Gherkin steps), and the grouping (package or folder structure, @Epic/@Feature/@Story annotations, one class or feature file per feature).

Judge the code only on these checks. Score each 0 (fails), 1 (weak) or 2 (good). Judge inside the style the code uses; never mark down the choice of Page Objects, Screenplay, Cucumber, Selenium, Playwright or REST.

A1 Intent: scenario titles state a business rule or outcome in plain words (good: "A reader sees an order number after paying"; bad: "testCheckout1", "loginTest", "shouldReturn201", a description of clicks).
A2 Focus: each scenario shows one behaviour and a reader can tell why it matters.
A3 Expectation: the final step or check states the business expectation in words, and the assertion checks the specific values the task gave.
B1 Business wording: the steps a report would show (task descriptions, @Step texts, Gherkin) read as a business story with the actor and the data, not as UI or HTTP mechanics.
B2 No technical leakage: no CSS selectors, ids, XPath, URLs, sleeps, status codes or driver calls in titles or in step text shown in the report (they may appear inside page objects, tasks, step definitions and assertions).
B3 Structure: business-level tasks/steps at the top; mechanics hidden one level down.
C1 Grouping: when the task has several areas, the tests are separated by function (packages or folders, or @Epic/@Feature/@Story, one class or feature file per area), not all in one class. If the task is a single scenario, score 2 when it sits in a sensibly named class/feature.
C2 Navigability: group, class, feature and package names are business phrases that make a table of contents of what the application does.
C3 Consistency: one way of grouping (not packages mixed with @Feature annotations), one style.

Reply with JSON only, no markdown fence: {"A1":n,"A2":n,"A3":n,"B1":n,"B2":n,"B3":n,"C1":n,"C2":n,"C3":n,"notes":"two or three sentences naming the main weakness"}"""


def collect(files):
    parts = []
    for p, c in sorted(files.items()):
        if p.endswith((".java", ".feature", ".md")) and "/target/" not in p:
            parts.append("=== %s ===\n%s" % (p, c))
    return "\n\n".join(parts)


def judge(rec_file, model):
    rec = json.load(open(rec_file))
    task = PROMPTS[rec["id"]]["prompt"].split("\n\nApplication under test")[0]
    files = run_tests.sources(os.path.dirname(rec_file), rec_file)
    if not files:
        return None
    prompt = RUBRIC + "\n\nTHE TASK THE AUTHOR WAS GIVEN:\n" + task + "\n\nTHE CODE:\n" + collect(files)[:60000]
    cmd = ["claude", "-p", "--tools", "", "--disable-slash-commands", "--setting-sources", "", "--strict-mcp-config", "--model", model, prompt]
    for _ in range(2):
        r = subprocess.run(cmd, capture_output=True, text=True)
        m = re.search(r"\{.*\}", r.stdout, re.S)
        if m:
            try:
                return json.loads(m.group(0))
            except ValueError:
                pass
    return None


def main():
    a = sys.argv[1:]
    rd = a[0]
    model = a[a.index("--model") + 1] if "--model" in a else "claude-sonnet-5-5"
    workers = int(a[a.index("--workers") + 1]) if "--workers" in a else 6
    only = set(a[a.index("--only") + 1].split(",")) if "--only" in a else None
    os.makedirs(os.path.join(rd, "review"), exist_ok=True)
    recs = []
    for f in sorted(glob.glob(os.path.join(rd, "**", "*.json"), recursive=True)):
        b = os.path.basename(f)
        if b in ("summary.json", "runtime.json", "app-runtime.json") or "/app-runtime/" in f or "/review/" in f:
            continue
        j = json.load(open(f))
        if "compiled" in j and j["id"] in PROMPTS and (not only or j["id"] in only):
            recs.append((f, j))

    def one(item):
        f, j = item
        tag = os.path.basename(f)[:-5]
        out = os.path.join(rd, "review", tag + ".json")
        if os.path.exists(out) and "--force" not in a:
            return tag, json.load(open(out))
        s = judge(f, model)
        res = dict(tag=tag, id=j["id"], model=j.get("model"), scores=s)
        json.dump(res, open(out, "w"), indent=1)
        return tag, res
    with cf.ThreadPoolExecutor(workers) as ex:
        results = dict(ex.map(one, recs))
    json.dump(results, open(os.path.join(rd, "review.json"), "w"), indent=1)
    print(len(results), "reviewed")


if __name__ == "__main__":
    main()
