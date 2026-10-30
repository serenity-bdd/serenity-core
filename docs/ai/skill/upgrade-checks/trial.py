#!/usr/bin/env python3
"""Upgrade trial: copy an old project, move it to the current Serenity, and see what still breaks.

usage: trial.py PROJECT_DIR [--no-fix] [--run]
"""
import json, os, re, shutil, subprocess, sys, tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
V = json.load(open(os.path.join(HERE, "..", "versions.json")))


def sh(cmd, cwd, timeout=900):
    r = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True, timeout=timeout)
    return r.returncode, r.stdout + r.stderr


def main():
    src = sys.argv[1]
    work = tempfile.mkdtemp(prefix="upg-")
    proj = os.path.join(work, "p")
    shutil.copytree(src, proj, ignore=shutil.ignore_patterns("target", ".git", ".idea", "*.iml", "node_modules"))
    pom = os.path.join(proj, "pom.xml")
    s = open(pom).read()
    s = re.sub(r"(<serenity(?:\.core|\.cucumber|\.maven|\.junit)?\.version>)[^<]*(</)", r"\g<1>%s\2" % V["serenity"], s)
    # artifacts that were renamed or split: serenity-cucumber4/5/6 became serenity-cucumber
    s = re.sub(r"<artifactId>serenity-cucumber[456]</artifactId>", "<artifactId>serenity-cucumber</artifactId>", s)
    src_text = "".join(open(os.path.join(d, f), errors="ignore").read() for d, _, fs in os.walk(proj) for f in fs if f.endswith(".java"))
    if "org.junit.runner" in src_text or "import org.junit.Test" in src_text:
        if "<artifactId>serenity-junit</artifactId>" not in s:     # the JUnit 4 runner is its own artifact now
            dep = ("    <dependency><groupId>net.serenity-bdd</groupId><artifactId>serenity-junit</artifactId><version>%s</version><scope>test</scope></dependency>\n"
                   "    <dependency><groupId>junit</groupId><artifactId>junit</artifactId><version>4.13.2</version><scope>test</scope></dependency>\n") % V["serenity"]
            s = s.replace("<dependencies>", "<dependencies>\n" + dep, 1)
    open(pom, "w").write(s)
    if "--no-fix" not in sys.argv:
        rc, out = sh([sys.executable, os.path.join(HERE, "fix_imports.py"), proj], proj)
        print(out.strip().splitlines()[-1])
        for l in out.splitlines():
            if "NEEDS A PERSON" in l: print(l)
    rc, out = sh(["mvn", "-B", "-q", "test-compile"], proj)
    errs = [l for l in out.splitlines() if l.startswith("[ERROR]")]
    print("test-compile:", "OK" if rc == 0 else "FAILED (%d error lines)" % len(errs))
    for l in errs[:40]:
        print("  ", l[:230].replace(proj, "."))
    if rc == 0 and "--run" in sys.argv:
        rc, out = sh(["mvn", "-B", "verify", "-Dheadless.mode=true", "-Dmaven.test.failure.ignore=true"], proj, 1800)
        print([l for l in out.splitlines() if "Tests run:" in l or "Results:" in l][-3:])
    print("kept", proj)


if __name__ == "__main__":
    main()
