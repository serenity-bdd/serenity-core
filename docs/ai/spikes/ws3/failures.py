#!/usr/bin/env python3
"""WS3 spike: build a compact failure summary from a Serenity output directory (target/site/serenity).

usage: failures.py OUTPUT_DIR [--format json|markdown] [--max-message N] [--all]

Reads only the per-test outcome JSON files that Serenity already writes. Prototype for the proposed
`failures.json` / `serenity failures` contract (see failure-summary-spec.md). Not production code.
"""
import json, os, sys

SCHEMA_VERSION = "0.1"
BAD = ("FAILURE", "ERROR", "COMPROMISED")
COUNTED = ("SUCCESS", "FAILURE", "ERROR", "COMPROMISED", "PENDING", "SKIPPED", "IGNORED", "UNDEFINED")


def load_outcomes(out_dir):
    for name in sorted(os.listdir(out_dir)):
        if not name.endswith(".json"):
            continue
        try:
            with open(os.path.join(out_dir, name)) as f:
                j = json.load(f)
        except (ValueError, OSError):
            continue
        # Outcomes are the files with a result and a scenario id. Some (e.g. a failure before any step ran)
        # have no testSteps, userStory or testCaseName, so every other field is optional.
        if isinstance(j, dict) and "result" in j and "scenarioId" in j:
            yield name, j


def find_failing_step(steps, path=()):
    """Deepest step that carries the exception. Returns (path of descriptions, step) or None."""
    for s in steps:
        here = path + (s.get("description", ""),)
        found = find_failing_step(s.get("children", []), here)
        if found:
            return found
        if s.get("result") in BAD and "exception" in s:
            return here, s
    return None


def last_screenshot(steps, upto):
    """Last screenshot at or before the failing step, in execution order; `upto` is the failing step object."""
    last = None

    def walk(ss):
        nonlocal last
        for s in ss:
            for shot in s.get("screenshots", []) or []:
                last = shot.get("screenshot")
            if s is upto:
                return True
            if walk(s.get("children", [])):
                return True
        return False

    walk(steps)
    return last


def trim(text, limit):
    text = (text or "").strip()
    return text if len(text) <= limit else text[: limit - 1] + "…"


def summarise(out_dir, max_message=500, include_all=False):
    counts = {k: 0 for k in COUNTED}
    failures = []
    for fname, j in load_outcomes(out_dir):
        result = j["result"]
        counts[result] = counts.get(result, 0) + 1
        if result not in BAD and not include_all:
            continue
        story = j.get("userStory", {})
        cause = j.get("testFailureCause") or {}
        frames = cause.get("stackTrace") or []
        frame = frames[0] if frames else None
        found = find_failing_step(j.get("testSteps", []))
        shot = last_screenshot(j.get("testSteps", []), found[1]) if found else None
        html = fname[:-5] + ".html"
        failures.append({
            "scenarioId": j.get("scenarioId"),
            "title": j.get("title") or j.get("name"),
            "result": result,
            "requirement": story.get("path"),
            "test": {"class": j.get("testCaseName"), "method": j.get("methodName"), "source": j.get("testSource")},
            "location": ({"file": frame.get("fileName"), "line": frame.get("lineNumber"),
                          "class": frame.get("declaringClass"), "method": frame.get("methodName")} if frame else None),
            "failingStep": ({"path": list(found[0]), "number": found[1].get("number")} if found else None),
            "error": {"type": j.get("testFailureClassname") or cause.get("errorType"),
                      "message": trim(j.get("testFailureMessage") or cause.get("message"), max_message)},
            "screenshot": shot,
            "outcomeFile": fname,
            "htmlReport": html if os.path.exists(os.path.join(out_dir, html)) else None,
            "durationMs": j.get("duration"),
        })
    failures.sort(key=lambda f: (f["requirement"] or "", f["title"] or ""))
    total = sum(counts.values())
    return {"schemaVersion": SCHEMA_VERSION, "outputDir": out_dir,
            "summary": {"total": total, **{k.lower(): v for k, v in counts.items() if v}},
            "failures": failures}


def to_markdown(s):
    sm = s["summary"]
    parts = ", ".join("%d %s" % (v, k) for k, v in sm.items() if k != "total")
    out = ["# Serenity results: %d tests (%s)" % (sm["total"], parts), ""]
    if not s["failures"]:
        out.append("No failures.")
    for f in s["failures"]:
        out.append("## %s: %s" % (f["result"], f["title"]))
        t = f["test"]
        out.append("- Test: `%s`" % (("%s.%s" % (t["class"], t["method"])) if t["class"] else f["scenarioId"]))
        if f["location"]:
            out.append("- Location: `%s:%s`" % (f["location"]["file"], f["location"]["line"]))
        if f["failingStep"]:
            out.append("- Failing step: %s" % " > ".join(f["failingStep"]["path"]))
        out.append("- Error: `%s`" % f["error"]["type"])
        msg = f["error"]["message"].strip().replace("\n", " ")
        out.append("- Message: %s" % msg)
        if f["screenshot"]:
            out.append("- Screenshot: %s" % f["screenshot"])
        out.append("")
    return "\n".join(out)


if __name__ == "__main__":
    args = sys.argv[1:]
    fmt = args[args.index("--format") + 1] if "--format" in args else "json"
    maxm = int(args[args.index("--max-message") + 1]) if "--max-message" in args else 500
    summary = summarise(args[0], maxm, "--all" in args)
    print(to_markdown(summary) if fmt == "markdown" else json.dumps(summary, indent=2))
