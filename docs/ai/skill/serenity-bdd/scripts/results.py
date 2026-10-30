#!/usr/bin/env python3
"""Summarise the results of a Serenity run from its JSON outcome files, small enough to read in one go.

usage: results.py <serenity report dir> [--json] [--all]
  (the directory is normally <project>/target/site/serenity; only the *.json outcome files are read)

Prints the totals, one line per test (result, title, requirement path, outcome file), and for each test that did not
pass: the failing step (or, when the failure is not attached to a step, the last step recorded), the exception type and message, the first stack frame in the project's own code, and the
outcome file to open for the full detail. --json prints the same as one JSON document. --all lists passing tests too
(by default a run with more than 40 tests lists only the ones that did not pass).
Exit code: 1 if any test failed, errored or was compromised, 0 otherwise (pending and skipped tests are listed, not counted as failures).
The outcome files are written while the tests run, so this works after a failed build, when the HTML report was not generated.
"""
import glob, json, os, sys

FAILED = {"FAILURE", "ERROR", "COMPROMISED"}
LIBRARY_PREFIXES = ("io.restassured", "org.junit", "org.apache", "org.opentest4j", "org.hamcrest", "org.assertj", "org.openqa",
                    "net.serenitybdd", "net.thucydides", "net.bytebuddy", "java.", "jdk.", "sun.", "io.cucumber", "com.microsoft.playwright",
                    "org.codehaus", "groovy", "org.mockito", "com.google")


def failing_step(steps):
    """The innermost step that failed: the first failed step with no failed child."""
    for s in steps or []:
        if s.get("result") in FAILED:
            return failing_step(s.get("children")) or s
    return None


def last_step(steps):
    """The last step recorded, at the deepest level."""
    last = None
    for s in steps or []:
        last = last_step(s.get("children")) or s
    return last


def own_frame(cause):
    for f in (cause or {}).get("stackTrace") or []:
        if not f.get("declaringClass", "").startswith(LIBRARY_PREFIXES):
            return "%s.%s (%s:%s)" % (f["declaringClass"], f["methodName"], f.get("fileName"), f.get("lineNumber"))
    return None


def load(d):
    rows = []
    for f in sorted(glob.glob(os.path.join(d, "*.json"))):
        try:
            j = json.load(open(f))
        except ValueError:
            continue
        if not isinstance(j, dict) or "userStory" not in j:
            continue
        row = dict(result=j.get("result", ""), title=j.get("title") or j.get("name", ""),
                   path=j.get("userStory", {}).get("path", ""), file=os.path.basename(f),
                   test=("%s.%s" % (j["testCaseName"], j["methodName"]) if j.get("methodName") else j.get("testCaseName", "")),
                   seconds=round((j.get("duration") or 0) / 1000.0, 1))
        if row["result"] in FAILED:
            cause = j.get("testFailureCause") or {}
            st = failing_step(j.get("testSteps"))
            # An assertion or wait that fails is not always recorded as a failed step: then the last step recorded is the context.
            ls = None if st else last_step(j.get("testSteps"))
            row.update(step=(st or ls or {}).get("description"), step_number=(st or ls or {}).get("number"), step_failed=bool(st),
                       error_type=j.get("testFailureClassname") or cause.get("errorType"),
                       message=(j.get("testFailureMessage") or cause.get("message") or "").strip(),
                       frame=own_frame(cause))
        rows.append(row)
    return rows


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if not args:
        sys.exit(__doc__)
    rows = load(args[0])
    if not rows:
        print("no outcome files found in %s: run the tests first" % args[0])
        sys.exit(2)
    counts = {}
    for r in rows:
        counts[r["result"]] = counts.get(r["result"], 0) + 1
    bad = [r for r in rows if r["result"] in FAILED]
    if "--json" in sys.argv:
        print(json.dumps(dict(total=len(rows), counts=counts, tests=rows), indent=1))
    else:
        print("%d tests: %s" % (len(rows), ", ".join("%d %s" % (n, k.lower()) for k, n in sorted(counts.items()))))
        listed = rows if ("--all" in sys.argv or len(rows) <= 40) else [r for r in rows if r["result"] != "SUCCESS"]
        for r in sorted(listed, key=lambda r: (r["result"] == "SUCCESS", r["path"], r["title"])):
            print("  %-10s %s  [%s]  %s" % (r["result"], r["title"], r["path"].replace("/", " > "), r["file"]))
        for r in bad:
            print("\nFAILED: %s  (%s)" % (r["title"], r["test"]))
            if r["step_number"] is None:
                print("  no steps recorded")
            else:
                print("  %s %s: %s" % ("failed step" if r["step_failed"] else "no step is marked failed; last step recorded is step", r["step_number"], r["step"]))
            print("  %s: %s" % (r["error_type"], " ".join(r["message"].split())[:400]))
            if r["frame"]:
                print("  at %s" % r["frame"])
            print("  detail: %s" % r["file"])
    sys.exit(1 if bad else 0)


if __name__ == "__main__":
    main()
