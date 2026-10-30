#!/usr/bin/env python3
"""Runtime stage: run the tests that models generated.

usage: run_tests.py RESULTS_DIR [--workers N] [--only ID,ID] [--timeout SECONDS] [--limit N]

For every run in RESULTS_DIR (written by run.py or run_agentic.py) the generated files are put into a
fresh copy of the harness project and `mvn verify` runs them (headless browsers, the live sample sites).
Runs that do not compile are recorded as COMPILE_FAIL. A failed run is repeated once, so one-off
network or site hiccups do not count. Writes RESULTS_DIR/runtime/<tag>.json and RESULTS_DIR/runtime.json.

Statuses: PASSED (>=1 test ran, none failed), FAILED (tests ran, some failed), NO_TESTS (compiled but no
test ran), COMPILE_FAIL, BUILD_ERROR (neither compile errors nor test results, e.g. a classpath problem),
TIMEOUT.
"""
import concurrent.futures as cf, glob, json, os, re, shutil, subprocess, sys, tempfile, time
import xml.etree.ElementTree as ET

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from run import extract  # noqa: E402


def sources(results_dir, rec_file):
    tag = os.path.basename(rec_file)[:-5]
    base = os.path.join(os.path.dirname(rec_file), tag)
    if os.path.exists(base + ".raw.txt"):
        return extract(open(base + ".raw.txt").read())
    if os.path.isdir(base + ".src"):
        files = {}
        for root, _, fs in os.walk(base + ".src"):
            for f in fs:
                rel = os.path.join("src", os.path.relpath(os.path.join(root, f), base + ".src"))
                files[rel] = open(os.path.join(root, f), errors="ignore").read()
        return files
    return {}


def surefire(proj):
    tot = dict(tests=0, failures=0, errors=0, skipped=0)
    first = None
    for x in glob.glob(os.path.join(proj, "target", "surefire-reports", "*.xml")):
        try:
            root = ET.parse(x).getroot()
        except ET.ParseError:
            continue
        for suite in root.iter("testsuite"):
            for k in tot:
                tot[k] += int(suite.get(k, 0) or 0)
        if first is None:
            for tag in ("failure", "error"):
                for el in root.iter(tag):
                    first = ((el.get("type") or "") + ": " + (el.get("message") or "")).strip()[:300]
                    break
                if first:
                    break
    return tot, first


def overlay_seed(harness, files):
    """Seeded projects: the saved sources include the seed as it was when the run was generated.
    Two seed defects were fixed after some runs were generated: FormTest pointed at a made-up URL, and
    the Cucumber runner named the wrong plugin class. Repair exactly those in seed-owned files, and
    leave everything else (including the model's own edits to seed files) alone. Returns (files, repaired)."""
    if not harness.startswith("seed-"):
        return files, 0
    seed = os.path.join(HERE, "harness", harness)
    out, repaired = dict(files), 0
    for root, _, fs in os.walk(os.path.join(seed, "src")):
        for f in fs:
            full = os.path.join(root, f)
            rel = os.path.relpath(full, seed)
            if rel not in out:
                out[rel] = open(full, errors="ignore").read()   # the model deleted a seed file: restore it
                continue
            text = out[rel]
            if "example.com/form" in text:
                out[rel] = open(full, errors="ignore").read(); repaired += 1
            elif "io.cucumber.core.plugin.SerenityReporterParallel" in text:
                out[rel] = text.replace("io.cucumber.core.plugin.SerenityReporterParallel",
                                        "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel"); repaired += 1
    return out, repaired


def run_once(harness, files, timeout):
    work = tempfile.mkdtemp(prefix="rt-")
    proj = os.path.join(work, harness)
    try:
        shutil.copytree(os.path.join(HERE, "harness", harness), proj, ignore=shutil.ignore_patterns("target"))
        for p, c in files.items():
            full = os.path.join(proj, p)
            os.makedirs(os.path.dirname(full), exist_ok=True)
            open(full, "w").write(c)
        t = time.time()
        try:
            r = subprocess.run(["mvn", "-B", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"],
                               cwd=proj, capture_output=True, text=True, timeout=timeout)
            out = r.stdout + r.stderr
        except subprocess.TimeoutExpired:
            return dict(status="TIMEOUT", secs=round(time.time() - t))
        secs = round(time.time() - t)
        if "COMPILATION ERROR" in out:
            return dict(status="COMPILE_FAIL", secs=secs)
        tot, first = surefire(proj)
        if tot["tests"] == 0:
            status = "BUILD_ERROR" if "BUILD FAILURE" in out else "NO_TESTS"
            return dict(status=status, secs=secs, detail=_build_error(out) if status == "BUILD_ERROR" else None)
        bad = tot["failures"] + tot["errors"]
        return dict(status="FAILED" if bad else "PASSED", secs=secs, counts=tot, firstFailure=first)
    finally:
        shutil.rmtree(work, ignore_errors=True)


def _build_error(out):
    for line in out.splitlines():
        if line.startswith("[ERROR]") and "Failed to execute goal" in line:
            return line[:300]
    return None


def run_record(rec_file, timeout):
    rec = json.load(open(rec_file))
    files = sources(os.path.dirname(rec_file), rec_file)
    harness = rec["harness"]
    if not files:
        return rec_file, dict(status="COMPILE_FAIL", detail="no files")
    files, reseeded = overlay_seed(harness, files)
    result = run_once(harness, files, timeout)
    result["repairedSeedFiles"] = reseeded
    attempts = 1
    if result["status"] in ("FAILED", "TIMEOUT", "NO_TESTS", "BUILD_ERROR"):
        again = run_once(harness, files, timeout)
        attempts = 2
        if again["status"] == "PASSED" or result["status"] in ("TIMEOUT",):
            result = dict(again, firstAttempt=result["status"], repairedSeedFiles=reseeded)
    result["attempts"] = attempts
    return rec_file, result


def main():
    args = sys.argv[1:]
    rd = args[0]
    workers = int(args[args.index("--workers") + 1]) if "--workers" in args else 3
    timeout = int(args[args.index("--timeout") + 1]) if "--timeout" in args else 300
    only = set(args[args.index("--only") + 1].split(",")) if "--only" in args else None
    limit = int(args[args.index("--limit") + 1]) if "--limit" in args else None
    recs = []
    for f in sorted(glob.glob(os.path.join(rd, "**", "*.json"), recursive=True)):
        b = os.path.basename(f)
        if b in ("summary.json", "runtime.json") or "/runtime/" in f:
            continue
        j = json.load(open(f))
        if "compiled" not in j or (only and j["id"] not in only):
            continue
        recs.append(f)
    if limit:
        recs = recs[:limit]
    os.makedirs(os.path.join(rd, "runtime"), exist_ok=True)
    allres = []
    with cf.ThreadPoolExecutor(workers) as ex:
        for f, res in ex.map(lambda f: run_record(f, timeout), recs):
            j = json.load(open(f))
            tag = os.path.basename(f)[:-5]
            full = dict(tag=tag, id=j["id"], model=j["model"], harness=j["harness"], compiledBefore=j["compiled"], **res)
            json.dump(full, open(os.path.join(rd, "runtime", tag + ".json"), "w"), indent=1)
            allres.append(full)
            print(f"{tag}: {res['status']}" + (f" ({res.get('firstFailure','')[:80]})" if res['status'] == 'FAILED' else ""), flush=True)
    json.dump(allres, open(os.path.join(rd, "runtime.json"), "w"), indent=1)


if __name__ == "__main__":
    main()
