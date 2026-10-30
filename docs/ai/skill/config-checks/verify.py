#!/usr/bin/env python3
"""Verify the claims in serenity-bdd/references/configuration.md by running them.

usage: verify.py [CASE_ID ...]

Builds the `config` example (a ConfigurationTest that prints `CFG name=value` lines), places configuration files
and command-line options as each case says, runs `mvn clean test`, and compares the printed values.
"""
import glob, json, os, re, shutil, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
sys.path.insert(0, os.path.join(HERE, "..", "troubleshooting"))
import repro  # noqa: E402

CONF = 'serenity {\n  project.name = "FROM CONF"\n}\nwebdriver.base.url = "https://conf.example.com"\n'
PROPS = "serenity.project.name=FROM PROPERTIES\nwebdriver.base.url=https://props.example.com\n"
RES = "src/test/resources/"


def project(files, remove_default_conf=False):
    work = tempfile.mkdtemp(prefix="cfgv-")
    proj = os.path.join(work, "p")
    shutil.copytree(os.path.join(repro.EVAL, "h-po-selenium"), proj, ignore=shutil.ignore_patterns("target"))
    shutil.copytree(os.path.join(repro.EX, "config"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
    shutil.copytree(os.path.join(proj, "src/test/java/resources"), os.path.join(proj, "src/test/resources"), dirs_exist_ok=True)
    shutil.rmtree(os.path.join(proj, "src/test/java/resources"))
    if remove_default_conf:
        os.remove(os.path.join(proj, RES + "serenity.conf"))
    for f, c in files.items():
        os.makedirs(os.path.dirname(os.path.join(proj, f)), exist_ok=True)
        repro.write(os.path.join(proj, f), c)
    return work, proj


def run(proj, flags=()):
    rc, out = repro.run(proj, ["clean", "test", "-Dtest=ConfigurationTest", *flags])
    return dict(re.findall(r"^CFG ([^=]+)=(.*)$", out, flags=re.M)), out


# --- where the files are read from, and which wins ------------------------------------------------------------
LAYOUTS = [
    ("conf in src/test/resources", {RES + "serenity.conf": CONF}, ("FROM CONF", "https://conf.example.com")),
    ("conf in project root", {"serenity.conf": CONF}, (None, None)),
    ("properties in project root", {"serenity.properties": PROPS}, ("FROM PROPERTIES", "https://props.example.com")),
    ("properties in src/test/resources", {RES + "serenity.properties": PROPS}, ("FROM PROPERTIES", "https://props.example.com")),
    ("conf (resources) + properties (root): properties win", {RES + "serenity.conf": CONF, "serenity.properties": PROPS}, ("FROM PROPERTIES", "https://props.example.com")),
    ("conf (resources) + properties (resources): conf wins", {RES + "serenity.conf": CONF, RES + "serenity.properties": PROPS}, ("FROM CONF", "https://conf.example.com")),
    ("legacy thucydides.* key in conf", {RES + "serenity.conf": 'thucydides.project.name = "LEGACY CONF"\n'}, ("LEGACY CONF", None)),
    ("legacy thucydides.* key in properties", {"serenity.properties": "thucydides.project.name=LEGACY PROPERTIES\n"}, ("LEGACY PROPERTIES", None)),
]


def check_layouts():
    res = []
    for name, files, (pname, base) in LAYOUTS:
        work, proj = project(files, remove_default_conf=True)
        try:
            vals, _ = run(proj)
            got_name = vals.get("project.name"); got_base = vals.get("base.url")
            ok = (pname is None or got_name == pname) and (base is None or got_base == base)
            if pname is None:     # the file must have been ignored: the enum returns null/empty
                ok = got_name in ("null", "", None) and got_base in ("null", "", None)
            res.append(dict(id="layout: " + name, ok=ok, detail="project.name=%s base.url=%s" % (got_name, got_base)))
        finally:
            shutil.rmtree(work, ignore_errors=True)
    return res


# --- environments, command line, reading ---------------------------------------------------------------------
def env_cases():
    work, proj = project({})
    out = []
    try:
        def case(cid, flags, expect):
            vals, text = run(proj, flags)
            bad = {k: (vals.get(k), v) for k, v in expect.items() if vals.get(k) != v}
            out.append(dict(id=cid, ok=not bad, detail="mismatch %s" % bad if bad else "ok"))
        case("default environment", [], {"specific app.url": "https://dev.example.com", "specific db.name": "dev", "specific company": "Acme", "plain app.url": "null"})
        case("-Denvironment=staging: staging value, default fallback, all", ["-Denvironment=staging"],
             {"specific app.url": "https://staging.example.com", "specific db.name": "dev", "specific company": "Acme", "plain app.url": "null"})
        case("-Dwebdriver.base.url overrides the file", ["-Dwebdriver.base.url=https://cli.example.com"], {"base.url": "https://cli.example.com"})
        case("-Dapp.url does NOT override an environments value", ["-Denvironment=staging", "-Dapp.url=https://cli-app.example.com"],
             {"specific app.url": "https://staging.example.com"})
        case("-Denvironments.staging.app.url overrides it", ["-Denvironment=staging", "-Denvironments.staging.app.url=https://cli-staging.example.com"],
             {"specific app.url": "https://cli-staging.example.com"})
        case("own key outside environments { } is a plain property", [], {"my.setting": "plain value"})
        case("injection, static access and Serenity.environmentVariables()", [],
             {"injected": "true https://dev.example.com", "by-name": "https://base.example.com", "serenity-facade": "https://base.example.com"})
        return out
    finally:
        shutil.rmtree(work, ignore_errors=True)


def os_env_and_stale():
    out = []
    work, proj = project({})
    try:
        os.environ["SERENITY_PROJECT_NAME"] = "FROM OS ENV"
        try:
            vals, _ = run(proj)
        finally:
            del os.environ["SERENITY_PROJECT_NAME"]
        out.append(dict(id="OS environment variable SERENITY_PROJECT_NAME is not used", ok=vals.get("project.name") == "Config demo", detail=vals.get("project.name")))
        # stale copy: delete the file without cleaning
        os.remove(os.path.join(proj, RES + "serenity.conf"))
        rc, text = repro.run(proj, ["test", "-Dtest=ConfigurationTest"])
        stale = dict(re.findall(r"^CFG ([^=]+)=(.*)$", text, flags=re.M)).get("project.name")
        vals, _ = run(proj)   # run() cleans
        out.append(dict(id="a deleted serenity.conf is still read until mvn clean", ok=stale == "Config demo" and vals.get("project.name") in ("null", "", None),
                        detail="without clean: %s, with clean: %s" % (stale, vals.get("project.name"))))
    finally:
        shutil.rmtree(work, ignore_errors=True)
    return out


def builtin_per_environment():
    conf = ('webdriver.base.url = "https://base.example.com"\nserenity.project.name = "Top"\nenvironments {\n'
            '  default { webdriver.base.url = "https://default-env.example.com" }\n'
            '  staging { webdriver.base.url = "https://staging-env.example.com", serenity.project.name = "Staging project" }\n}\n')
    work, proj = project({RES + "serenity.conf": conf})
    out = []
    try:
        for cid, flags, expect in (
                ("built-in keys inside environments { default }: override the top-level value", [], {"base.url": "https://default-env.example.com", "project.name": "Top"}),
                ("built-in keys inside environments { staging } with -Denvironment=staging", ["-Denvironment=staging"],
                 {"base.url": "https://staging-env.example.com", "project.name": "Staging project"})):
            vals, _ = run(proj, flags)
            bad = {k: (vals.get(k), v) for k, v in expect.items() if vals.get(k) != v}
            out.append(dict(id=cid, ok=not bad, detail="ok" if not bad else "mismatch %s" % bad))
        return out
    finally:
        shutil.rmtree(work, ignore_errors=True)


def key_cases():
    """Every key in the reference's `| Key |` tables is a real Serenity property."""
    ref = os.path.join(HERE, "..", "serenity-bdd", "references", "configuration.md")
    keys = []
    if os.path.exists(ref):
        in_table = False
        for line in open(ref):
            if line.startswith("| Key"):
                in_table = True; continue
            if in_table and line.startswith("|---"):
                continue
            if in_table and line.startswith("|"):
                m = re.match(r"\|\s*`([^`]+)`", line)
                if m: keys.append(m.group(1))
            elif in_table:
                in_table = False
    work, proj = project({})
    try:
        repro.write(os.path.join(proj, "src/test/java/cfg/DumpKeys.java"),
                    "package cfg;\nimport net.thucydides.model.ThucydidesSystemProperty;\nimport org.junit.jupiter.api.Test;\n"
                    "class DumpKeys { @Test void dump() { for (ThucydidesSystemProperty p : ThucydidesSystemProperty.values()) System.out.println(\"KEY \" + p.getPropertyName()); } }\n")
        rc, out = repro.run(proj, ["clean", "test", "-Dtest=DumpKeys"])
        known = set(re.findall(r"^KEY (\S+)", out, flags=re.M))
    finally:
        shutil.rmtree(work, ignore_errors=True)
    missing = [k for k in keys if k not in known and k.replace("thucydides.", "serenity.") not in known]
    return [dict(id="keys: %d keys in the reference exist in ThucydidesSystemProperty (%d known)" % (len(keys), len(known)), ok=bool(keys) and not missing,
                 detail="missing %s" % missing if missing else "ok")]


# --- screenshots: the documented values do what they say -----------------------------------------------------
def screenshot_cases():
    out = []
    counts = {}
    headless_seen = False
    for value in ("DISABLED", "FOR_FAILURES", "FOR_EACH_ACTION", "BEFORE_AND_AFTER_EACH_STEP", "AFTER_EACH_STEP", "UNSET"):
        work = tempfile.mkdtemp(prefix="cfgs-")
        proj = os.path.join(work, "p")
        try:
            shutil.copytree(os.path.join(repro.EVAL, "h-po-selenium"), proj, ignore=shutil.ignore_patterns("target"))
            shutil.copytree(os.path.join(repro.EX, "po-selenium"), os.path.join(proj, "src/test/java"), dirs_exist_ok=True)
            _, text = repro.run(proj, ["clean", "test", "-Dheadless.mode=true"] + ([] if value == "UNSET" else ["-Dserenity.take.screenshots=" + value]))
            if value == "UNSET":
                headless_seen = "--headless" in text
            n = 0
            for f in glob.glob(os.path.join(proj, "target/site/serenity/*.json")):
                n += read_count(f)
            counts[value] = n
        finally:
            shutil.rmtree(work, ignore_errors=True)
    ok = counts.get("DISABLED") == 0 and counts.get("FOR_FAILURES") == 0 and counts.get("FOR_EACH_ACTION", 0) > 0 and counts.get("UNSET", 0) > 0 and all(counts.get(v, 0) > 0 for v in ("BEFORE_AND_AFTER_EACH_STEP", "AFTER_EACH_STEP"))
    out.append(dict(id="headless.mode=true puts --headless in Chrome's arguments", ok=headless_seen, detail="seen" if headless_seen else "not seen"))
    out.append(dict(id="screenshots: none for DISABLED and FOR_FAILURES on a passing test; the other values, and the default, take some", ok=ok, detail=str(counts)))
    return out


def read_count(path):
    j = json.load(open(path))
    def walk(steps):
        c = 0
        for s in steps:
            c += len(s.get("screenshots", []) or []) + walk(s.get("children", []))
        return c
    return walk(j.get("testSteps", []))


def main():
    wanted = sys.argv[1:]
    results = []
    for group, fn in (("layouts", check_layouts), ("environments", env_cases), ("builtin", builtin_per_environment), ("osenv", os_env_and_stale), ("keys", key_cases), ("screenshots", screenshot_cases)):
        if wanted and group not in wanted:
            continue
        for r in fn():
            results.append(r)
            print("%-82s %s %s" % (r["id"], "ok" if r["ok"] else "<-- MISMATCH", r["detail"]))
    sys.exit(0 if all(r["ok"] for r in results) else 1)


if __name__ == "__main__":
    main()
