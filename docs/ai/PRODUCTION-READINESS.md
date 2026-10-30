# Production readiness: AI guidance for Serenity BDD

Goal: a release of the guidance (skill, `AGENTS.md` variants, `llms.txt` and the idioms pages) that the Serenity project can stand behind, not an experiment. This page defines "ready", tracks the gaps, and orders the work. Results are in `baseline-results.md`; the plan for the whole effort is `AI-FRIENDLY-ROADMAP.md`.

## What "ready" means

| Gate | Requirement | Status |
|---|---|---|
| A. Correct | Every checkable claim in the guidance is verified by a script against the released jars, and every example compiles and runs. Both run on every change and on every Serenity release. | Done in the repo, unproven on a GitHub runner: `skill/release-check.sh` runs the claim verifier, the version check (Maven Central, version strings, the JUnit and Cucumber versions Serenity brings) and the examples with their tests; `.github/workflows/ai-guidance.yml` runs it on changes to `docs/ai`, weekly and on demand. Prose claims about behaviour still need individual tests. |
| B. Complete for the supported scope | Each style the project supports is covered end to end: project setup, a first test, configuration, running, reports. Anything not covered is stated in the guidance, with a pointer to the docs. | Maven, JUnit 5, Page Objects (Selenium, Playwright), Screenplay (Selenium, Playwright, REST), REST with SerenityRest and a `@Steps` step library, Cucumber with Screenplay, Cucumber with page objects and step libraries, `serenity.conf` and environments, Gradle for Cucumber, parallel Cucumber runs, upgrading and JUnit 4: covered (JUnit 4 and Gradle only as far as the references say). Not covered: Gradle beyond Cucumber, mobile, Spring/Guice object factories, cloud and grid capabilities. |
| C. Evidence that holds up | Pass rates are measured on prompt sets that were not used to write the guidance, with enough runs per cell, on repeatable targets, across model tiers. The limits (models, agents) are stated. | One clean held-out result per style; later sets were partly used to tune. 2 to 3 runs per cell. Live third-party sites. Claude 5.5 models and Claude Code only. |
| D. Reviewed and owned | A maintainer has reviewed every statement of fact and behaviour; there is an owner, a release checklist, and a place where users get the files. | Not reviewed. No owner or checklist. Distribution undecided. Nothing pushed. |
| E. Behaviour in real use | The things that depend on a live session are tested: the skill installed at user level, the follow-up after the skill asks, `AGENTS.md` read natively by other tools. | Not tested. |

## Known gaps, by gate

**A. Correct**
1. ~~Put `release-check.sh` in CI.~~ Written (`.github/workflows/ai-guidance.yml`); it has not yet run on a GitHub runner, so the Chrome and Playwright setup there is untested. The weekly run fails when Maven Central has a newer Serenity or Playwright-module release, which is the trigger to re-verify.
2. Claims the script cannot check: prose about behaviour (the `locatedBy` XPath rule, `open()` being final, soft-assertion behaviour). Each needs a test or a source reference; the verifier only checks that names exist.
3. ~~Stale version strings.~~ `skill/versions.json` is the single source; `check-versions.py` fails on any other Serenity version string in the guidance. The docs repo still needs the same check in its own CI (its header and pages are generated from this repo).

**B. Complete**
4. ~~Research what users actually need.~~ Done (`research/user-needs.md`): the guidance so far covers the 4 to 6% of questions that are about writing Screenplay tasks and page objects; the demand is driver and browser failures, reports, version and dependency errors, Cucumber mechanics, and configuration. Priorities below follow it.
5. ~~Troubleshooting by symptom.~~ First version done: 10 reproduced entries (`references/troubleshooting.md`, re-run by `troubleshooting/repro.py`); fix-it evaluation 48/48 with the skill vs 44/48 without (the difference is the silent "0 tests" case). Still to add, where they can be reproduced: more driver start-up failures, report problems with parallel runs, upgrade (3.x to 5.x) errors, dependency conflicts.
6. ~~Cucumber with JUnit 5.~~ First version done: `references/cucumber.md`, with every command run by `cucumber-checks/verify.py` (16 checks: runner, tags and name filters, single feature, rerun, hook order, shared state, parallel, Gradle, stale outcomes). Measured with models on 5 how-to tasks: 30/30 with the skill and 29/30 without, so no real gain on tasks models already know, at about 30% more cost per task. Done since: Cucumber with page objects and `@Steps` (section 10 of the reference, `examples/cuke-pages`, `cucumber-checks/verify_pages.py` with 12 checks). Not yet: Spring/Guice object factories.
7. ~~`serenity.conf` and environments.~~ First version done: `references/configuration.md`, 21 checks run by `config-checks/verify.py` (file locations and precedence, environments, command-line overrides, reading values in code, 14 keys checked against the enum, screenshot settings). Still to do: how-to evaluation with models; cloud/grid capabilities, proxies and other keys not covered. Done: upgrading and old code (`references/upgrading.md`, `upgrade-checks/`): moved-class table derived from the jars (511 of 1,574 classes since 3.9.8; none since 4.2.34), import-rewriting script, recipe applied to five real projects (2.1 to 4.2), JUnit 4 and the old Cucumber runner verified on 5.4.1. Not yet: measured with models (upgrade tasks), JBehave migration, Appium/Spring/JIRA/cloud modules. Done since: REST with SerenityRest and a step library (`references/rest-assured.md`, `examples/rest-steps`, `rest-checks/verify.py` with 10 checks). Remaining: integrations.
8. Decide the position on JUnit 4 and mobile: either cover them or state clearly that the guidance does not.

**B2. Living documentation (added at the maintainer's request)**
- ~~Make agents write tests that communicate intent in business English, grouped by function, and review them.~~ Done: `references/living-documentation.md` (claims run), `review-checklist.md`, `serenity-test-reviewer` agent, `outline.py`. Blind-scored 68% to 87% (14 prompts) and 69%/72% to 91% (6 suite prompts), correctness unchanged (120/120). The reviewer agent is used in 17 to 28% of runs and adds nothing measurable beyond the skill's own `outline.py` self-check (91% with it, 90% without, same pass rate). Open: technical detail in step text (B2 check) is still the weakest; scoring is by a same-family model from source, so a human read of the rendered reports is needed.

**C. Evidence**
9. ~~A fresh prompt set, written after the guidance is frozen, on new tasks and apps.~~ Done (14 prompts, `prompts-v2.json`): one-shot 62% to 81% GOOD with the references; agentic at the ceiling (99% and 100%). See `baseline-results.md`.
10. ~~A local test application.~~ Done (`eval/testapp`, healthy-versus-faulty judge). It had a defect (the A05 checkout script) that the run exposed; fixed and re-judged.
11. More runs per cell and prompts with harder tasks (forms, tables, waits, API chaining), not only add-an-item flows.
12. Other model tiers and, where tools are available, other agents; otherwise state the limit.
13. Runtime results for the remaining conditions (original prompts, unguided Playwright Page Objects).

**D. Reviewed and owned**
14. Maintainer review of the references, the `AGENTS.md` variants, the docs pages and the `llms.txt` header.
15. Decide distribution (docs site, starter projects, a plugin marketplace) and ownership; write the release checklist.
16. Raise the `locatedBy` behaviour with the maintainers (document or change).

**E. Real use**
17. ~~Install the skill at user level; test the ask-then-answer flow end to end.~~ Done: auto-load 18/18 (all compiled and GOOD); ask-then-answer 8/9 asked first and 9/9 followed the answer. Claude Code only.
18. Test `AGENTS.md` with tools that read it natively, if any are available.

## Order of work

1. Gate A items 1 to 3, because everything else depends on the guidance staying correct.
2. Research (item 4), then B items 5 to 8 by demand. Update the references, examples and verifier together.
3. A local test application and a fresh, harder prompt set (C 9 to 11); freeze the guidance first; then measure.
4. Review and release process (D), then real-use tests (E).
5. Re-run the whole evaluation on the frozen release and record it; publish.

## Release checklist (draft)

- [ ] `skill/release-check.sh` passes against the Serenity release being documented.
- [ ] Version strings updated (references, `AGENTS.md` variants, `llms.txt` header, docs pages) and regenerated (`agents-md/build.py`, `skill/publish-docs.py`).
- [ ] Evaluation re-run on the frozen files; results recorded; no regression against the previous release.
- [ ] A maintainer has reviewed the diff.
- [ ] Docs PR merged; skill and `AGENTS.md` variants published to the agreed place.

## Update: REST step libraries and Cucumber with page objects

Measured one-shot (no tools), 3 models, judged healthy versus faulty against the local bookshop:

| Prompt set | No guidance | With the references |
|---|---|---|
| prompts-v5 (R01-R04, C01-C04; the guidance was tuned on these) | 31/72 (43%) | 71/72 (99%) |
| prompts-v6 (R05-R08, C05-C08; held out, then one rule added from its failures) | 33/72 (46%) | 68/72 (94%), 34/36 on the Cucumber half after the rule |
| prompts-v7 (R09-R12, C09-C12; written last, nothing tuned on it) | 26/72 (36%) | 71/72 (99%) |

With tools (agentic), prompts-v6: 48/48 with and without the skill; the skill halves the Maven runs (1.85 against 3.98 per run) and saves about 25% of the time.

Findings that came out of this work are in the review pack (section 3.1), with the fixes on the branch `fix/ai-review-findings`; the guidance describes the released 5.4.1 and needs re-verifying once a release contains them.
