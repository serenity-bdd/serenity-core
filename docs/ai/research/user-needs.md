# What do Serenity BDD users need help with?

Research for Gate B of `PRODUCTION-READINESS.md`: decide what the guidance must cover, from demand. Fetched 2026-10-26. Reproduce with `python3 research/analyse.py [--lists]`; the raw data (not committed) is fetched into `research/data/`.

## Sources and limits

| Source | What | Limits |
|---|---|---|
| Stack Overflow, tag `serenity-bdd` | All 632 questions (2014 to 2025), titles, tags, views, answered flag | Skews to 2016 to 2020 and to older Serenity (68 `jbehave`, 26 `thucydides` co-tags). Only 47% answered. Volume has fallen from 110 questions in 2019 to 7 in 2025 (a fall in Stack Overflow use in general is a likely part of the reason; where the questions went instead is unknown). |
| GitHub issues, `serenity-bdd/serenity-core` | The newest 1,500 issues (2019 to 2026), open and closed; 546 from 2023 on | Reports to the maintainers, so mostly bugs and upgrade problems, not how-to questions. Few labelled. |
| Docs repo issues | 7 issues | Too few to use. |

Method: each title (plus tags for Stack Overflow) is matched against keyword rules per theme; a question can fall in several themes. Titles only, so counts are rough and tend to under-count. These sources show what people asked in public forums. They do not show what people now ask AI assistants, which is the question that matters; they are the best proxy available here. Nobody has read the answers.

## Themes, by share of questions

| Theme | Stack Overflow, all (632) | Stack Overflow, 2023+ (57) | GitHub issues, 2023+ (546) |
|---|---|---|---|
| Cucumber (features, steps, runners, tags) | 43% | 60% | 9% |
| Drivers and browsers (Chrome, Firefox, Edge, headless, remote, grids) | 40% | 40% | 21% |
| Reports, screenshots, requirements | 18% | 30% | 36% |
| Maven setup, dependencies, versions | 18% | 16% | 15% |
| JUnit runners and extensions | 12% | 16% | 9% |
| Configuration (`serenity.conf`, properties, environments) | 7% | 14% | 9% |
| REST and API testing | 9% | 12% | 8% |
| Integrations (JIRA, Spring, CI, other tools) | 8% | 11% | 2% |
| Parallel execution | 3% | 4% | 5% |
| Gradle | 4% | 7% | 3% |
| Mobile and Appium | 4% | 2% | 4% |
| Screenplay | 6% | 4% | 3% |
| Page Objects, `@Steps`, `WebElementFacade` | 4% | 4% | 2% |
| Waits and flaky elements | 2% | 4% | 1% |

By views on Stack Overflow, the biggest themes are drivers and browsers (600,000 views), Cucumber (507,000), Maven and versions (272,000) and reports (181,000).

## What the questions are actually about

Reading the most-viewed and the newest titles:

1. **Driver and browser start-up failures.** "Could not instantiate class org.openqa.selenium.chrome.ChromeDriver", `DriverConfigurationError`, "session not created", failures after a Chrome update ("Chrome 116", "121"), Jenkins pipelines, Docker, headless file upload. The most-viewed question of all (160,000 views) is the "Chrome is being controlled by automated test software" banner. The most-commented GitHub issue (63 comments) is a Chrome 116 start-up failure; the next Chrome one has 21.
2. **Reports.** Reports not generated, empty or showing 0 tests ("JUnit 5 and Cucumber 7"), generated only from `main` or only locally, wrong counts or "failed tests marked as passed", taking 30 minutes to aggregate, broken when tests run in parallel, Gradle aggregate not finding files, summary customisation. About a third of 2023+ GitHub issues.
3. **Upgrades, versions and the build.** "Dependency convergence error" (52,000 views), "Maven could not resolve dependencies", `NoClassDefFoundError` for `net.thucydides.model.util.EnvironmentVariables` after moving from 3 to 4, Java 17 to 21 failures, "How to migrate CucumberWithSerenity to JUnit 5".
4. **Cucumber mechanics.** Tag filtering (also under Gradle), scenario outlines, hooks, sharing data between steps and features, re-running failed scenarios, running from IntelliJ, parallel runs.
5. **Configuration.** Reading environment-specific values from `serenity.conf` (also from a static context), system properties, per-environment drivers.
6. **Old code.** About one in seven Stack Overflow questions (94 of 632) carries a `jbehave` or `thucydides` tag. That is the older Serenity code that assistants will have seen most of.

Writing a Screenplay task or a page object, which most of the guidance built so far is about, is 4% to 6% of public questions.

## What the docs cover today

The docs have no troubleshooting content for the errors above: none of "Could not instantiate", `DriverConfigurationError`, "session not created", `NoClassDefFoundError` or `NoSuchMethodError` appears in the docs outside the release notes, and neither "Selenium Manager" nor Java 21 does. Driver configuration is covered (`guide/driver_config.md`, 414 lines), as are Maven (`guide/maven.md`), Gradle (15 files mention it) and the 3 to 4 migration. The material is organised by feature, not by symptom, and a person who has an error message cannot find it from the message.

## What this means for the guidance

Coverage so far fits the writing-tests question and misses most of the demand. Proposed additions, in priority order:

| Priority | Addition | Why | How it would be built |
|---|---|---|---|
| 1 | **Troubleshooting by symptom**: driver start-up, reports missing or wrong, dependency and version conflicts, tests not found/run, with the error text, cause and fix | The largest share of public questions and the most views; nothing in the docs | For each entry: reproduce the error in a harness project, confirm the cause and the fix by running it, then record the exact message. Reproducible ones first (version conflicts, missing driver, reports from a failed build, Cucumber tests not discovered). |
| 2 | **Cucumber with JUnit 5**, with and without Screenplay: runner, tags (Maven and Gradle), scenario outlines, hooks, sharing state, re-running failures, parallel | 43% to 60% of Stack Overflow questions | Compiling and running examples; claims checked by `verify-claims.py`. |
| 3 | **Configuration**: `serenity.conf`, environments, reading values, system properties, per-environment drivers | 14% of recent questions; where models guess keys | An example with a test that reads each key; every key checked against `ThucydidesSystemProperty`. |
| 4 | **Upgrading and old code**: 3.x to 4.x to 5.x package moves, JUnit 4 to 5, `CucumberWithSerenity` to the JUnit Platform, JBehave and `thucydides` code | Models write old code; users ask about upgrade errors | Before-and-after pairs that compile and run; a table of moved classes derived from the jars. |
| 5 | **Gradle** (setup, tags, reports), **parallel execution**, **REST**, **integrations** | Smaller shares | Same method. |
| 6 | **Mobile**, deeper Screenplay and Page Object topics | Smallest shares of demand | Stated as not covered, with pointers to the docs. |

The troubleshooting entries also serve the failure-summary work (WS3) and actionable errors (WS7): the messages people search for are the ones whose text should say what to do.

## Open questions

- Do assistants get these questions the same way people post them? A sample of real assistant conversations about Serenity, if the maintainers can get one, would test this research.
- Is the 2023+ Stack Overflow sample (57 questions) enough? It is small; GitHub corroborates drivers, reports, versions and configuration, but not Cucumber (9% there, 60% on Stack Overflow, plausibly because Cucumber users ask how-to questions on Stack Overflow and report fewer bugs).
- How much of the historical demand is already answered by the current release (for example, driver management has changed)? Each troubleshooting entry must be reproduced against the current release before it is published.
