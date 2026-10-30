#!/usr/bin/env python3
"""Classify Serenity BDD user questions and issues into themes (keyword rules) and count them.

usage: analyse.py            # prints the tables used in user-needs.md
Data (fetched 2026-10-26): data/stackoverflow-serenity-bdd.json (all 632 questions tagged serenity-bdd),
data/github-serenity-core-issues.json (the newest 1500 issues, open and closed), data/github-docs-issues.json.
A question can match several themes. The rules are crude on purpose: they count, they do not judge.
"""
import collections, datetime, html, json, os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
THEMES = [
    ("Cucumber (features, steps, runners, tags)", r"cucumber|gherkin|feature file|scenario outline|step def|glue|\.feature"),
    ("Screenplay", r"screenplay|actor|\bperformable\b|\btask\b.*question|serenity-screenplay"),
    ("Page Objects, @Steps, WebElementFacade", r"page ?object|@steps|webelementfacade|@findby|@managed|pageobject|step librar|@step\b|\bsteps?\b.*class"),
    ("JUnit (4 and 5), runners, extensions", r"junit|serenityrunner|runwith|serenityjunit5|surefire|failsafe|testng"),
    ("Maven setup, dependencies, versions", r"maven|pom\.xml|dependenc|artifact|classnotfound|nosuchmethod|noclassdef|version|java 1|jdk|incompatible|conflict"),
    ("Gradle", r"gradle"),
    ("Drivers, browsers, grids (Chrome, Firefox, Edge, headless, remote)", r"webdriver|chromedriver|geckodriver|chrome|firefox|edge|safari|headless|selenium|remote|grid|docker|driver\b|browser|selenoid|saucelabs|browserstack|lambdatest"),
    ("Playwright", r"playwright"),
    ("REST / API testing", r"rest[- ]?assured|\brest\b|\bapi\b|restassured|callanapi|http|json|response|request"),
    ("Reports, screenshots, requirements", r"report|screenshot|aggregate|html|requirement|narrative|living documentation|index\.html|single.page|json summary|tag"),
    ("Configuration (serenity.conf, properties, environments)", r"serenity\.conf|serenity\.properties|\bconfig|environment|properties|webdriver\.base|base\.url|hocon|capabilit|system propert"),
    ("Parallel execution", r"parallel|thread|concurren|fork"),
    ("Waits, timeouts, stale/flaky elements", r"wait|timeout|stale|flaky|intermittent|not interactable|no such element|click intercepted"),
    ("Mobile / Appium", r"appium|mobile|android|\bios\b"),
    ("Integrations (JIRA, Spring, CI, other tools)", r"jira|spring|jenkins|github action|azure|gitlab|ci/cd|allure|reportportal|xray|zephyr|testrail|kotlin|groovy|sonar"),
    ("Data-driven tests", r"data[- ]?driven|csv|examples table|datatable|parameteri[sz]ed|testdata"),
]

def themes_of(text):
    t = text.lower()
    return [n for n, rx in THEMES if re.search(rx, t)]

def year(ts):
    return datetime.datetime.fromtimestamp(ts).year if isinstance(ts, (int, float)) else int(ts[:4])

def table(counter, total, title):
    print("\n" + title)
    for n, c in counter.most_common():
        print("  %4d  %4.0f%%  %s" % (c, 100 * c / total, n))

so = json.load(open(os.path.join(HERE, "data", "stackoverflow-serenity-bdd.json")))
gh = json.load(open(os.path.join(HERE, "data", "github-serenity-core-issues.json")))

print("Stack Overflow questions tagged serenity-bdd:", len(so), "| views total", sum(q["view_count"] for q in so))
by_year = collections.Counter(year(q["creation_date"]) for q in so)
print("by year:", dict(sorted(by_year.items())))
for label, sel in (("all years", so), ("2023 and later", [q for q in so if year(q["creation_date"]) >= 2023])):
    c = collections.Counter(); v = collections.Counter()
    for q in sel:
        for th in themes_of(html.unescape(q["title"]) + " " + " ".join(q["tags"])):
            c[th] += 1; v[th] += q["view_count"]
    table(c, len(sel), "SO themes (%s, n=%d), by number of questions" % (label, len(sel)))
    print("  views: " + ", ".join("%s %d" % (n.split(" (")[0], x) for n, x in v.most_common(6)))
tagc = collections.Counter(t for q in so for t in q["tags"] if t != "serenity-bdd")
print("\nSO co-tags:", tagc.most_common(25))
print("answered: %.0f%%" % (100 * sum(1 for q in so if q["is_answered"]) / len(so)))

print("\nGitHub serenity-core issues (newest %d, %s to %s)" % (len(gh), min(i["createdAt"] for i in gh)[:10], max(i["createdAt"] for i in gh)[:10]))
gy = collections.Counter(int(i["createdAt"][:4]) for i in gh); print("by year:", dict(sorted(gy.items())))
rec = [i for i in gh if int(i["createdAt"][:4]) >= 2023]
c = collections.Counter()
for i in rec:
    for th in themes_of(i["title"]): c[th] += 1
table(c, len(rec), "GitHub themes (2023 and later, n=%d)" % len(rec))
lab = collections.Counter(l for i in rec for l in i["labels"]); print("labels:", lab.most_common(12))

if "--lists" in sys.argv:
    print("\nTop SO questions by views:")
    for q in sorted(so, key=lambda q: -q["view_count"])[:30]:
        print("  %6d  %d  %s" % (q["view_count"], year(q["creation_date"]), html.unescape(q["title"])[:110]))
    print("\nNewest SO questions:")
    for q in sorted(so, key=lambda q: -q["creation_date"])[:30]:
        print("  %d  %s" % (year(q["creation_date"]), html.unescape(q["title"])[:120]))
    print("\nMost-commented GitHub issues (2023+):")
    for i in sorted(rec, key=lambda i: -i["comments"])[:25]:
        print("  %3d  #%d  %s" % (i["comments"], i["number"], i["title"][:110]))
