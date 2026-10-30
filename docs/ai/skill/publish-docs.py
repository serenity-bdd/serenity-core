#!/usr/bin/env python3
"""Publish the skill's references as Docusaurus pages in the docs repo.

usage: publish-docs.py PATH/TO/serenity-bdd.github.io

The skill's references and examples are the single source. This writes docs/ai/*.md into the docs
repo, replacing links to example files with the example code inline. Re-run after changing a reference.
"""
import os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
REF = os.path.join(HERE, "serenity-bdd", "references")
EX = os.path.join(HERE, "serenity-bdd", "examples")

PAGES = [
    # file, id, title, position, example sets (descriptions are in DESCRIPTIONS)
    ("screenplay.md", "ai_screenplay_idioms", "Screenplay: Imports and Idioms", 2,
     ["sp-selenium", "sp-playwright", "sp-rest", "cuke-sp"]),
    ("page-objects.md", "ai_page_object_idioms", "Page Objects: Imports and Idioms", 3, ["po-selenium", "po-playwright"]),
    ("rest-assured.md", "ai_rest_step_library", "REST with SerenityRest and a Step Library", 3, ["rest-steps"]),
    ("project-setup.md", "ai_project_setup", "Project Setup: Dependencies and Configuration", 4, []),
    ("troubleshooting.md", "ai_troubleshooting", "Troubleshooting by Symptom", 5, []),
    ("configuration.md", "ai_configuration", "Configuration: serenity.conf, Properties and Environments", 4, ["config"]),
    ("upgrading.md", "ai_upgrading", "Upgrading to Serenity 5 and Recognising Old Code", 6, []),
    ("living-documentation.md", "ai_living_documentation", "Living Documentation: Intent, Wording and Structure", 2, ["living-docs", "living-docs-annotated"]),
    ("review-checklist.md", "ai_review_checklist", "Reviewing Tests and Living Documentation", 7, []),
    ("cucumber.md", "ai_cucumber", "Cucumber: Runner, Tags, Hooks, Gradle and Parallel", 3, ["cuke-plain", "cuke-pages"]),
]

DESCRIPTIONS = {
    "ai_screenplay_idioms": "Exact packages, common mistakes and compiling examples for Screenplay with Selenium, Playwright and REST, and for Cucumber with Screenplay.",
    "ai_page_object_idioms": "Exact packages, common mistakes and compiling examples for Page Objects with step libraries, using Selenium or Playwright.",
    "ai_rest_step_library": "Packages, the step-library pattern, and the number-matching and report pitfalls for REST API tests that use SerenityRest and @Steps; every claim was run.",
    "ai_project_setup": "Maven dependencies for each style, the report plugin, and a minimal serenity.conf.",
    "ai_configuration": "Where Serenity reads configuration from, which source wins, environments, reading values in code and the common keys; the behaviour was run.",
    "ai_upgrading": "What changed between Serenity 2.x, 3.x, 4.x and 5.x: Java and library versions, the 511 classes that moved, artifact changes, an upgrade recipe tested on real projects, and old code to recognise.",
    "ai_cucumber": "Running Serenity with Cucumber on the JUnit Platform: the runner, tags and filters under Maven and Gradle, hooks, shared state, re-running failures and parallel runs; every command was run.",
    "ai_living_documentation": "How scenario titles, step wording, packages, annotations and readme files become the Serenity report, and how to write tests that read as living documentation; the behaviour was run.",
    "ai_review_checklist": "A checklist for judging Serenity tests and the report they produce: intent, business wording, organisation and test quality.",
    "ai_troubleshooting": "Build, report and browser errors: the message, the cause and a fix that was reproduced and run.",
}

INDEX = """---
id: ai_idioms_overview
title: Current Idioms and Imports
sidebar_position: 1
description: Exact packages, working examples and common mistakes for each way of writing tests with Serenity BDD.
---

# Current Idioms and Imports

These pages list the exact packages, dependencies and idioms for each supported way of writing tests with Serenity BDD, with compiling examples. They are written for people and for AI coding assistants, which most often go wrong by guessing a package name.

Serenity supports several styles. All of them are current and valid:

| Approach | Drivers | Page |
|---|---|---|
| Page Objects with a step library | Selenium, Playwright | [Page Objects: Imports and Idioms](ai_page_object_idioms) |
| Screenplay | Selenium, Playwright, REST | [Screenplay: Imports and Idioms](ai_screenplay_idioms) |
| Cucumber with Screenplay | Selenium | [Screenplay: Imports and Idioms](ai_screenplay_idioms) |
| Cucumber with page objects and a step library | Selenium | [Cucumber](ai_cucumber) |
| REST with a step library (no Screenplay) | REST-assured via SerenityRest | [REST with SerenityRest and a Step Library](ai_rest_step_library) |

Use JUnit 5 as the runner for all of them. Dependencies, the report plugin and `serenity.conf` are on [Project Setup](ai_project_setup).

## Choosing a style

Pick the style the project already uses, and stay in it: one approach, one driver and one runner per project, and don't mix Page Objects and Screenplay in one test. To see which a project uses, look at `pom.xml` (`serenity-screenplay*`, `serenity-playwright`, `serenity-cucumber`) and the existing tests. For a new project, choose by what suits the team. None of the styles is deprecated.

## If you use an AI coding assistant

Tell it which style, driver and runner you want, or point it at these pages (or at [llms.txt](https://serenity-bdd.github.io/llms.txt)). Ask it to compile its output (`mvn test-compile`) before running it. Many `net.thucydides` packages are current core packages, so that prefix on its own is not a sign of outdated code: judge each class against these pages.
"""

VERIFIED = {
    "screenplay.md": "Verified against Serenity **5.4.1** (Playwright modules: **5.3.11**, the latest published). The examples at the end of this page compile and their tests pass.",
    "page-objects.md": "Verified against Serenity **5.4.1** (Playwright module: **5.3.11**, the latest published). The examples at the end of this page compile and their tests pass.",
    "upgrading.md": "Verified against Serenity **5.4.1**. The class counts come from the released jars, and the recipe was applied to five real projects.",
    "configuration.md": "Verified against Serenity **5.4.1**. The behaviour described here was run against the example at the end of the page.",
    "cucumber.md": "Verified against Serenity **5.4.1** (Cucumber 8.0.4, JUnit 6.1.3). Every command on this page was run against the example at the end of the page.",
    "troubleshooting.md": "Verified against Serenity **5.4.1** (Playwright modules: **5.3.11**). Each entry was reproduced in a test project and its fix was run.",
    "living-documentation.md": "Verified against Serenity **5.4.1**. The behaviour described here was run against the example at the end of the page.",
    "review-checklist.md": "Written for Serenity **5.4.1**.",
    "rest-assured.md": "Verified against Serenity **5.4.1**. The example at the end of this page compiles and its tests pass; each claim was run.",
    "project-setup.md": "Verified against Serenity **5.4.1**. The dependency sets, JUnit BOM and report plugin were run end to end (`mvn clean verify` ran the examples and produced the report). The `serenity.conf` snippet follows the project's own samples but was not run.",
}

def fence(path):
    lang = "gherkin" if path.endswith(".feature") else "hocon" if path.endswith(".conf") else "python" if path.endswith(".py") else "java"
    return "```%s\n%s\n```\n" % (lang, open(path).read().rstrip())

EXTRA_FILES = {"living-documentation.md": [("scripts/outline.py", os.path.join(HERE, "serenity-bdd", "scripts", "outline.py"), "python")], "cucumber.md": [("build.gradle", os.path.join(HERE, "cucumber-checks", "gradle", "build.gradle"), "groovy")]}


def examples(sets, fname=None):
    out = "\n## Examples\n\nThese files compile against Serenity 5.4.1 (Playwright examples: 5.3.11).\n"
    for title, path, lang in EXTRA_FILES.get(fname, []):
        out += "\n### `%s`\n\n```%s\n%s\n```\n" % (title, lang, open(path).read().rstrip())
    for name in sets:
        base = os.path.join(EX, name)
        for root, _, files in sorted(os.walk(base)):
            for f in sorted(files):
                full = os.path.join(root, f)
                rel = os.path.join(name, os.path.relpath(full, base))
                out += "\n### `%s`\n\n%s" % (rel, fence(full))
    return out

def transform(text, fname):
    text = re.sub(r"^# .*\n", "", text, count=1)
    text = re.sub(r"^Verified against .*\n", VERIFIED[fname] + "\n", text, flags=re.M)
    text = text.replace("`../examples/", "`")
    text = re.sub(r"<!-- moved-table:(?:start|end) -->\n?", "", text)   # comments are not valid in MDX
    text = text.replace("`../../upgrade-checks/fix_imports.py PROJECT_DIR`", "the `fix_imports.py` script in the serenity-core repository (`docs/ai/skill/upgrade-checks`)")
    text = text.replace("`cucumber-checks/gradle/build.gradle`", "the `build.gradle` at the end of this page")
    # paths into the verification scripts mean nothing on the website
    text = re.sub(r"`\.\./\.\./(?:cucumber-checks|config-checks|troubleshooting|upgrade-checks|living-checks|playwright-checks)/[\w./-]+`", "the verification script in the serenity-core repository (`docs/ai/skill`)", text)
    text = text.replace("Mistakes models make (from the baseline)", "Common mistakes")
    text = text.replace("the sheet", "this page").replace("this sheet", "this page")
    text = re.sub(r"\n\nReference poms: .*\n", "\n", text)
    text = text.replace("The most common mistakes in the baseline come from guessing them.", "Guessing them is the most common cause of compile errors.")
    text = text.replace("The Playwright sheet is based", "The Playwright section is based")
    text = text.replace("The sheet gives no runtime guarantees.", "Nothing here is a runtime guarantee.")
    # drop the "Seen in" column of the mistakes table
    lines = []
    for ln in text.split("\n"):
        if ln.startswith("|") and ln.count("|") == 4 and "Seen in" in ln:
            ln = "|".join(ln.split("|")[:3]) + "|"
        elif ln.startswith("|") and re.search(r"\|\s*(\d+( of \d+ failed runs)?|\d+ held-out runs)\s*\|$", ln) and ln.count("|") == 4:
            ln = "|".join(ln.split("|")[:3]) + "|"
        if lines and lines[-1].startswith("| Wrong | Right |") and ln.startswith("|---"):
            ln = "|---|---|"
        lines.append(ln)
    text = "\n".join(lines)
    return text

def main(repo):
    outdir = os.path.join(repo, "docs", "ai")
    os.makedirs(outdir, exist_ok=True)
    open(os.path.join(outdir, "_category_.json"), "w").write(
        '{\n  "label": "Current Idioms and Imports",\n  "position": 10.5,\n  "link": {"type": "doc", "id": "ai_idioms_overview"}\n}\n')
    open(os.path.join(outdir, "overview.md"), "w").write(INDEX)
    for fname, id_, title, pos, sets in PAGES:
        body = transform(open(os.path.join(REF, fname)).read(), fname)
        slug = {"screenplay.md": "screenplay-idioms", "page-objects.md": "page-object-idioms", "rest-assured.md": "rest-step-library", "project-setup.md": "project-setup", "troubleshooting.md": "troubleshooting", "cucumber.md": "cucumber", "configuration.md": "configuration", "upgrading.md": "upgrading", "living-documentation.md": "living-documentation", "review-checklist.md": "review-checklist"}[fname]
        fm = '---\nid: %s\ntitle: "%s"\nsidebar_position: %d\ndescription: "%s"\n---\n\n' % (id_, title, pos, DESCRIPTIONS[id_])
        open(os.path.join(outdir, slug + ".md"), "w").write(fm + "# " + title + "\n\n" + body.strip() + "\n" + examples(sets, fname))
    print("wrote", outdir)

if __name__ == "__main__":
    main(sys.argv[1])
