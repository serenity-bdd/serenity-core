#!/usr/bin/env python3
"""Runtime stage for the fresh prompt set: run generated tests against the local bookshop, healthy and faulty.

usage: run_app_tests.py RESULTS_DIR [--workers N] [--only A01,A02]

For every run in RESULTS_DIR (written by run_agentic.py with --prompts prompts-v2.json):
  1. start the bookshop on a private port and run the generated project against it (the port in the sources is replaced);
  2. if it passed, restart the bookshop with the defect named in the prompt and run the same project again.
Verdicts: COMPILE_FAIL; FAILS_HEALTHY (the test does not pass on the healthy application);
INSENSITIVE (passes on the healthy and on the faulty application, so it does not check the behaviour in question);
GOOD (passes on the healthy application and fails on the faulty one).
"""
import concurrent.futures as cf, glob, json, os, queue, re, subprocess, sys, time, urllib.request

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import run_tests  # noqa: E402

PROMPTS = {}
for _f in ("prompts-v2.json", "prompts-v3.json", "prompts-v4.json", "prompts-v5.json", "prompts-v6.json", "prompts-v7.json"):
    if os.path.exists(os.path.join(HERE, _f)):
        PROMPTS.update({p["id"]: p for p in json.load(open(os.path.join(HERE, _f)))})
SERVER = os.path.join(HERE, "testapp", "server.py")


def start_server(port, fault=""):
    cmd = [sys.executable, SERVER, "--port", str(port)] + (["--fault", fault] if fault else [])
    proc = subprocess.Popen(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    for _ in range(50):
        try:
            urllib.request.urlopen("http://localhost:%d/api/books" % port, timeout=1)
            return proc
        except Exception:
            time.sleep(0.1)
    proc.kill()
    raise RuntimeError("server did not start on %d" % port)


def stop(proc):
    proc.terminate()
    try:
        proc.wait(timeout=5)
    except Exception:
        proc.kill()


def retarget(files, port):
    return {p: (c.replace("8765", str(port)) if p.endswith((".java", ".feature", ".conf", ".properties", ".xml", ".json")) else c) for p, c in files.items()}


def judge(rec_file, port, timeout):
    rec = json.load(open(rec_file))
    prompt = PROMPTS[rec["id"]]
    files = retarget(run_tests.sources(os.path.dirname(rec_file), rec_file), port)
    if not files:
        return dict(verdict="COMPILE_FAIL", detail="no files")
    harness = rec["harness"]
    srv = start_server(port)
    try:
        healthy = run_tests.run_once(harness, files, timeout)
        if healthy["status"] in ("FAILED", "TIMEOUT", "NO_TESTS", "BUILD_ERROR"):
            again = run_tests.run_once(harness, files, timeout)       # one retry for a flaky first run
            if again["status"] == "PASSED":
                healthy = dict(again, firstAttempt=healthy["status"])
    finally:
        stop(srv)
    out = dict(healthy=healthy["status"], healthyFailure=healthy.get("firstFailure"), fault=prompt["fault"])
    if healthy["status"] == "COMPILE_FAIL":
        out["verdict"] = "COMPILE_FAIL"
    elif healthy["status"] != "PASSED":
        out["verdict"] = "FAILS_HEALTHY"
    else:
        srv = start_server(port, prompt["fault"])
        try:
            faulty = run_tests.run_once(harness, files, timeout)
        finally:
            stop(srv)
        out["faulty"] = faulty["status"]
        out["faultyFailure"] = faulty.get("firstFailure")
        out["verdict"] = "GOOD" if faulty["status"] == "FAILED" else "INSENSITIVE"
    return out


def main():
    a = sys.argv[1:]
    rd = a[0]
    workers = int(a[a.index("--workers") + 1]) if "--workers" in a else 3
    only = set(a[a.index("--only") + 1].split(",")) if "--only" in a else None
    recs = []
    for f in sorted(glob.glob(os.path.join(rd, "**", "*.json"), recursive=True)):
        if os.path.basename(f) in ("summary.json", "runtime.json", "app-runtime.json") or "/app-runtime/" in f:
            continue
        j = json.load(open(f))
        if "compiled" in j and j["id"] in PROMPTS and (not only or j["id"] in only):
            recs.append(f)
    ports = queue.Queue()
    for i in range(workers):
        ports.put(8800 + 10 * i)
    os.makedirs(os.path.join(rd, "app-runtime"), exist_ok=True)

    def work(f):
        port = ports.get()
        try:
            return f, judge(f, port, 420)
        except Exception as e:
            return f, dict(verdict="ERROR", detail=str(e)[:200])
        finally:
            ports.put(port)

    allres = []
    with cf.ThreadPoolExecutor(workers) as ex:
        for f, res in ex.map(work, recs):
            j = json.load(open(f))
            tag = os.path.basename(f)[:-5]
            full = dict(tag=tag, id=j["id"], model=j["model"], **res)
            json.dump(full, open(os.path.join(rd, "app-runtime", tag + ".json"), "w"), indent=1)
            allres.append(full)
            print("%s: %s" % (tag, res["verdict"]), flush=True)
    json.dump(allres, open(os.path.join(rd, "app-runtime.json"), "w"), indent=1)


if __name__ == "__main__":
    main()
