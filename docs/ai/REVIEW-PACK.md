# Review pack: AI guidance for Serenity BDD

For a Serenity maintainer. It lists what is proposed, what needs a decision from you, which behaviours of Serenity came up that you may want to treat as bugs or documentation gaps, every factual claim with the evidence behind it, and what has not been verified. Nothing has been pushed or published.

Estimated reading time: 30 minutes for sections 1 to 3 (the decisions and the possible bugs), 2 to 3 hours to check the claims in section 4 against your own knowledge.

## 1. What is proposed

Guidance that helps AI coding assistants (and people) write correct Serenity tests, built from code that was compiled and run against Serenity **5.4.1** (Playwright modules **5.3.11**, the latest published).

| Piece | Where | What it is |
|---|---|---|
| Agent skill | `docs/ai/skill/serenity-bdd/` (`SKILL.md` + 7 references + 8 compiling example sets) | Chooses the style (asks if unclear), then loads the matching reference: Screenplay, Page Objects, project setup, Cucumber, configuration, troubleshooting, upgrading. About 820 lines of reference text. |
| `AGENTS.md` variants | `docs/ai/agents-md/variants/` | One short file per style a project can copy, recording the style, the verified imports and the common mistakes. |
| Docs pages | docs repo, branch `add-llms-txt`: `docs/ai/` (8 pages) | The same references as a "Current Idioms and Imports" section. Generated from the skill (`skill/publish-docs.py`), so the two cannot drift. |
| `llms.txt`, `llms-full.txt` | docs repo, `plugin/llms-txt/` | A Docusaurus plugin that writes both at build time: a hand-written header for AI assistants plus a generated index of 59 pages. |
| Checks | `docs/ai/skill/` (`verify-claims.py`, `check-versions.py`, `release-check.sh`, `*-checks/`, `troubleshooting/repro.py`) and `.github/workflows/ai-guidance.yml` | Run on every change and weekly; fail when a claim stops being true or a new Serenity release appears. |
| Evidence and tools | `docs/ai/eval/`, `docs/ai/baseline-results.md`, `docs/ai/research/` | The evaluation harness and every result; research on what users ask. |

Size to know about: the saved evaluation outputs (`eval/results`, about 130 MB on disk) are no longer tracked (they are in `.gitignore`), but the earlier local commits still contain them in git history, so the history must be rewritten before anything is pushed. See section 8.

## 2. Decisions for you

| # | Question | What the pack currently does | My recommendation |
|---|---|---|---|
| D1 | **Neutrality between styles.** Page Objects, Screenplay and Cucumber, with Selenium, Playwright or REST, are all presented as equally valid. The skill and the `AGENTS.md` files never pick a default; when the style is unclear the skill asks. | As stated. | Keep. In tests, models left to guess drifted to Screenplay with Selenium (8 of 12 unnamed runs), so not choosing is a deliberate behaviour. **Decided (2026-10-30): Keep (standing rule).** |
| D2 | **JUnit 4 and the old Cucumber runner.** The upgrading page says JUnit 4 (`serenity-junit`) and `@RunWith(CucumberWithSerenity.class)` still work on 5.4.1 (both were run) and that a working JUnit 4 suite need not be rewritten. | As stated. | Decide whether you want to say this, or whether JUnit 4 is on its way out; if so I will reword. **Decided (2026-10-30): Keep as is: JUnit 4 and the old Cucumber runner still work; no advice to migrate.** |
| D3 | **JBehave.** States that `serenity-jbehave` has no 5.x release and stories must move to Cucumber first. | As stated. | Confirm. No migration steps are given. **Decided (2026-10-30): Confirmed.** |
| D4 | **Advice to use `-Dmaven.test.failure.ignore=true`** (exit code 0 even when tests fail) so the report is built after failures, or to run `serenity-maven-plugin:aggregate` by hand. | Both are given, with the warning about the exit code. | Decide if you would rather recommend something else (for example binding the report goal differently). **Decided (2026-10-30): Recommend running `serenity-maven-plugin:aggregate` after the failed build; the ignore flag is the second option, with its warning. Reworded.** |
| D5 | **Where does this live and who owns it?** Options: the docs site (the section exists), the starter repos (ship the matching `AGENTS.md`), the repository (the skill), a plugin marketplace. | Skill and checks are in `serenity-core/docs/ai`; docs pages are generated into the docs repo. | Docs pages in the docs repo, skill in `serenity-core`. Name an owner for the release step (section 7). **Decided (2026-10-30): As now: skill and checks in `serenity-core/docs/ai`, pages in the docs repo; the repository owner runs the release step.** |
| D6 | **Tools shipped as scripts.** `fix_imports.py` (rewrites a 2.x/3.x project's imports) and `moves.py` are Python scripts. | Scripts in the repository. | Decide whether they should become part of the Serenity CLI or stay as scripts. **Decided (2026-10-30): Stay as scripts.** |
| D7 | **Hard-coded versions.** The `llms.txt` header and the page headers name 5.4.1 and 5.3.11. `check-versions.py` fails the weekly build when Maven Central has something newer. | As stated. | A person must update `skill/versions.json`, regenerate, and re-run the release check on each release. Is that acceptable? **Decided (2026-10-30): Acceptable: manual version bump, the weekly CI failure is the reminder.** |
| D8 | **Wording about other products and sites.** The pages mention Chrome, Playwright, GitHub Actions, and public sample sites (todo app, saucedemo, the-internet, jsonplaceholder). | As stated. | Check that you are comfortable with the sample sites. **Decided (2026-10-30): Comfortable with the sample sites.** |
| D9 | **The URL structure.** Pages are at `/docs/ai/ai_screenplay_idioms` and so on (Docusaurus uses the id). | As stated. | Rename the section or add `slug`s if you want shorter URLs. **Decided (2026-10-30): Keep the URLs (already published and linked).** |

## 3. Behaviours found that you may want to treat as bugs or documentation gaps

These came up while verifying the guidance. Each is reproduced by a check (the check is named). The guidance documents them as they are. For each, "document", "change" or "ignore" is your call.

| # | Behaviour | Evidence | Why it matters |
|---|---|---|---|
| B1 | `Target.the(..).locatedBy("input.new-todo")` is read as **XPath**, not CSS: Serenity treats the string as XPath when it compiles as an XPath expression, and `input.new-todo` (a name test) does. The element is then not found. `.new-todo`, `#id`, `[x=y]` are CSS. | `troubleshooting/repro.py` case `css-read-as-xpath` | 5 of the 11 failures of unguided agentic runs. Hard to see from the error message. |
| B2 | `serenity.conf` is read **only from `src/test/resources`**. A copy in the project root is silently ignored. | `config-checks/verify.py` layouts | A common source of "my config is ignored". |
| B3 | A `serenity.properties` in the project root **beats** `serenity.conf` in `src/test/resources`; with both in resources, the conf wins. | `config-checks/verify.py` layouts | Surprising precedence. |
| B4 | Keys under `environments { }` are **invisible** to `environmentVariables.getProperty(..)` (null in every environment); only `EnvironmentSpecificConfiguration` returns them. | `config-checks/verify.py` environments | The Stack Overflow question "getting null value when trying to read environment specific data from serenity.conf" (6,298 views). |
| B5 | `-Dapp.url=...` does **not** override `environments { staging { app.url } }`; you need `-Denvironments.staging.app.url=...`. | same | |
| B6 | OS environment variables (`SERENITY_PROJECT_NAME`) do not set `serenity.project.name`. | `config-checks/verify.py` osenv | If this is intended, say so in the properties documentation. |
| B7 | `DriverConfigurationError: Unsupported browser type: chromee. Did you mean firefox?`: the suggestion for a misspelt `chrome` is `firefox`. | `repro.py` case `unknown-driver` | A wrong hint in an error message. |
| B8 | Outcome files from earlier runs in `target/site/serenity` are counted in the next report: a run of 1 scenario reported `Results: 5 tests`. | `cucumber-checks/verify.py` `stale-outcomes-counted` | "Wrong counts" reports. |
| B9 | A Cucumber `@Suite` class whose name Maven's test plugin ignores (for example `CucumberSuite`) runs nothing: BUILD SUCCESS and `Results: 0 tests`, with no warning. | `repro.py` case `runner-not-picked-up` | The model's fix rate was 2 of 6 without the reference and 6 of 6 with it: the symptom has no error to search for. A warning from the Serenity plugin when it sees zero outcomes could help. |
| B10 | When tests fail, `mvn verify` builds **no** HTML report (the report goal runs in `post-integration-test`, which is never reached). | `repro.py` case `no-report-after-failed-tests` | Users read this as "the report is broken". |
| B11 | A `@Managed WebDriver` field in a step library stays `null`; the first use is a `NullPointerException`. | `repro.py` case `managed-in-step-library` | Easy to do; the error does not mention `@Managed`. |
| B12 | `Text.ofEach(target)` returns `Question<Collection<String>>` in Screenplay with Selenium and `Question<List<String>>` in Playwright. | `javap-claims.txt` | 8 of the 31 compile failures of unguided one-shot runs. |
| B13 | `Ensure` exists in three places with different packages and APIs (`serenity-ensure`, the REST module, the Playwright module). | `verify-claims.py` | Models import the wrong one. |
| B14 | Gradle does not pass `-D` options to the test JVM (Gradle behaviour), so `gradle test -Dcucumber.filter.tags=@smoke` is ignored unless the build forwards it. | `cucumber-checks/verify.py` `gradle-tags-*` | Stack Overflow 2024: "Serenity BDD, Java and Gradle not enforcing Cucumber Tags". Document in the Gradle section of the docs. |
| B15 | The existing docs have no troubleshooting for the top error messages, and several pages still pin 5.3.7 (for example `guide/maven.md`). The Playwright modules are published only up to 5.3.11. | `research/user-needs.md`; `check-versions.py` | |
| B16 | `@Narrative(text=...)` on a JUnit 5 test class, or in `package-info.java`, does not appear in the 5.4.1 report (run: `living-checks/verify.py`, case `narrative_annotation`). Descriptions for JUnit 5 are read only from `src/test/resources/stories/<package path>/readme.md`. | `references/living-documentation.md` | |
| B17 | (Corrected.) Mixing package-based and `@Epic`/`@Feature`/`@Story`-annotated classes does **not** drop either: the Requirements overview shows both hierarchies as separate top-level trees (case `mixed`). An earlier version of this row said the package classes disappear; that came from a check that read a sub-page. Package-based class names still show their `Test` suffix ("When searching the catalog test") unless the class has `@DisplayName`. | `references/living-documentation.md` | Not changed: renaming would alter existing requirement paths and tags. |
| B18 | In `serenity-screenplay-playwright` 5.3.11, `Ensure.thatTheCurrentUrl()` and `Ensure.thatThePageTitle()` return package-private classes (`UrlEnsure`, `PageTitleEnsure`), so every call on them fails to compile from user code (run: `playwright-checks/verify.py`). Models reach for them often, because the method names suggest they are the intended API. | `references/screenplay.md` | |

### 3.1 What was done about B1-B18

Code fixes are on the branch `fix/ai-review-findings` of serenity-core (based on `origin/main`, version 5.4.2-SNAPSHOT; not pushed). Documentation fixes are on the branch `docs-ai-findings` of the documentation repository (based on `origin/master`; not pushed). The guidance in this pack is verified against the released 5.4.1, so it still describes the old behaviour of the fixed items until a release contains the fixes.

| Item | Outcome |
|---|---|
| B1 | Not fixable: Serenity cannot know whether a String is valid CSS. Documented (troubleshooting reference and AGENTS.md notes). |
| B2 | Code: a warning, once, when `serenity.conf` is in the project directory but not on the classpath. Docs: configuration table. |
| B3, B6 | Documentation only: the lookup table and the environments section of the properties reference. B6 (OS variables do not set properties) is not changed in code. |
| B5 | Code: `-Dapp.url=...` now overrides `environments { staging { app.url } }`, in both `getProperty` and `EnvironmentSpecificConfiguration`. Both resolve a value in one order: explicitly given (-D, or set in code), then the active environment(s), `all` and `default`, then the plain value from the files. `SystemEnvironmentVariables` records which keys were explicit; `EnvironmentVariables` gets a default `isExplicitlyDefined()` returning false, so other implementations (such as the mock used in tests) keep the old behaviour. Behaviour change to flag: a key defined both at top level in `serenity.conf` and inside `environments { }` now returns the environment value from `getProperty` (as `ThucydidesSystemProperty.from` already did); before it returned the top-level value. |
| B4 | Code: `getProperty()` falls back to values defined only inside `environments { }` (active environment(s), then `all`, then `default`). The precedence is described under B5. 14 unit tests cover B4 and B5; the model, reports, core, junit5, screenplay, cucumber, ensure, rest-assured and screenplay-rest suites pass, and the config example was run end to end. |
| B7 | Code: the closest-driver suggestion compared upper-case enum names with the typed text, so `chromee` suggested `firefox`. Now case-insensitive and includes synonyms; 4 new unit tests. |
| B8 | Documentation only: the Maven guide says old outcome files are counted and to use `clean`. |
| B9 | Code: the report logs a warning when it finds no results, naming the runner-class pattern as the usual cause. Checked end to end. |
| B10 | Documentation only: the Maven guide says why there is no report after failed tests and how to get one. |
| B11 | Code: a warning, once per field, when a step library has a `@Managed` field. Checked end to end. |
| B12, B13 | Not changed: altering public return types or merging the three `Ensure` classes would break callers. They stay in the guidance. |
| B14 | Documentation only: Gradle caution and snippet in the Cucumber page; runner naming caution added next to it. |
| B15 | Documentation: the stale 5.3.7 versions are replaced. |
| B16 | Code: a class-level `@Narrative` now supplies the story narrative for JUnit 5 test classes (when there is no `@Description`); unit tests added and the report checked. |
| B17 | The first half of the row was wrong (a check read a sub-page); both hierarchies appear. Corrected here, in the reference and in the checks. The `Test` suffix in class-based feature names is left alone, because renaming would change existing requirement paths and tags. |
| B18 | Code: `UrlEnsure` and `PageTitleEnsure` are now public classes in their own files; a test in another package proves they compile from user code. |

Tests run after the changes: serenity-model and serenity-reports in full (all pass), the step-related tests of serenity-core (315 run, 0 failures, 3 skipped), and the new tests for the Playwright module.

## 4. Claim inventory

Evidence codes: **J** the class, package, method or artifact was checked against the released jars; **R** reproduced by running it in a test project (the check is named); **S** read from the source or javap output, no run; **D** taken from the Serenity documentation, not independently checked; **O** observed in real projects or runs, not automated; **E** from the evaluation results (section 5).

How each check is run is in section 6. Re-running everything: `skill/release-check.sh`.

### 4.1 Screenplay (`references/screenplay.md`)

| Claim | Evidence |
|---|---|
| Every class named in the package tables (core, Selenium, Playwright, REST, shared annotations) is in the stated package and artifact | J `verify-claims.py` (package tables) |
| The Playwright interaction, question and `Ensure` method lists exist | J (`Class.method(` check) |
| `Text.ofEach` returns `Collection<String>` (Selenium) and `List<String>` (Playwright) | J `javap-claims.txt` |
| `Target.locatedBy(String)` reads XPath when the string compiles as XPath | R `css-read-as-xpath`; S |
| `Target.located(By)` is the way to give a `By` | J |
| The list of frequent mistakes, and how often they happened | E (counts from 31 failed one-shot runs); the "right" forms J/R |
| The four patterns (Selenium, Playwright, REST, Cucumber with Screenplay) work | R `check-examples.sh --run` |
| `.viewedBy(actor)` is the old question API | S (deprecated `UIStateReaderBuilder`); E |

### 4.2 Page Objects (`references/page-objects.md`)

| Claim | Evidence |
|---|---|
| Package table; `PageObject.open()` is `final`; `openAt` exists; `UIInteractionSteps` extends `PageObject` and has `getDriver()` | J, `javap-claims.txt` |
| `net.thucydides.core.pages.WebElementFacade` and the `thucydides` `FindBy` are deprecated aliases | S (javap `Deprecated` attribute) |
| The Playwright page object is a plain class using `PlaywrightSerenity.registerPage/unregisterPage/getCurrentPage` | R `check-examples.sh --run po-playwright` |
| The Selenium page object, step library and test pattern works | R `check-examples.sh --run po-selenium` |

### 4.3 Project setup (`references/project-setup.md`)

| Claim | Evidence |
|---|---|
| Dependency sets per style | R every harness project and example builds and runs |
| Serenity 5.4.1 depends on JUnit 6.1.3 and Cucumber 8.0.4 | J `check-versions.py` (dependency tree) |
| Importing the JUnit BOM keeps the JUnit artifacts aligned | R `mixed-junit-versions` (the fix) |
| The report plugin config produces `target/site/serenity/index.html` | R `mvn verify` in the example runs |
| The `serenity.conf` snippet | R `config-checks/verify.py` (values read back) |
| Playwright modules are published only up to 5.3.11 | J `check-versions.py` (Maven Central) |

### 4.4 Troubleshooting (`references/troubleshooting.md`)

Each entry: a symptom text quoted from the real output, a cause, a fix. All reproduced by `troubleshooting/repro.py`, which breaks a project, checks the symptom, applies the fix and checks it works:

`mixed-junit-versions`, `cucumber-version-mismatch`, `wrong-cucumber-plugin`, `runner-not-picked-up`, `no-report-after-failed-tests`, `restassured-groovy-pins`, `serenity-plugin-missing`, `managed-in-step-library`, `css-read-as-xpath`, `driver-path-wrong`, `unknown-driver`: **R**.

Entries covered by other checks: stale outcomes and Gradle `-D` (R `cucumber-checks/verify.py`); a `null` read from `serenity.conf` (R `config-checks/verify.py`).

Entries that are **O** only (seen while upgrading real projects, not reproduced in a test project): the `NoClassDefFoundError: io/cucumber/core/eventbus/EventBus` after raising the version with Cucumber 4 pinned; the bundled-driver `drivers { mac { ... } }` variant of the chromedriver error. The page labels these.

The "Not reproduced here" list (Docker/Jenkins browser start-up, dependency convergence, 3.x to 4.x class moves, slow or parallel reports) has no tested fixes and says so.

### 4.5 Cucumber (`references/cucumber.md`)

All run by `cucumber-checks/verify.py` against the `cuke-plain` example (5 scenarios): the runner; tag filters from the command line, from the runner and from `junit-platform.properties`; the name filter; one feature file; `rerun` and re-running from `rerun.txt`; hook order (`@Before` ascending, `@After` descending); `@Before("@tag")`; report data from a hook; data tables and a custom parameter type; session variables and PicoContainer for sharing state; parallel execution; the Gradle build and report; Gradle not forwarding `-D`; stale outcomes. **R**.

Not run: Spring or Guice object factories; Cucumber with page objects and `@Steps`.

### 4.6 Configuration (`references/configuration.md`)

All run by `config-checks/verify.py` (21 checks): where files are read from and which wins (8 layouts); environments (`default`, named, `all`, fallback); command-line overrides; reading values in code (static access, injection in a step library, `Serenity.environmentVariables()`); built-in keys inside `environments { }`; OS environment variables ignored; a deleted `serenity.conf` still read until `mvn clean`; `headless.mode`; what the screenshot settings do on a passing test. **R**.

Key existence: 14 keys checked against `ThucydidesSystemProperty` (**J**). The **meaning** of each key comes from the Serenity documentation (**D**) except where the page says "(run)".

### 4.8 Living documentation (`references/living-documentation.md`)

Run by `living-checks/verify.py` (9 cases): package tree, annotation tree, mixed tree, readme location (right and wrong), `@Narrative` not shown, `@DisplayName` and method-name titles, `Task.where` and `@Step` wording, Cucumber folders with readme and feature description. `review-checklist.md` and the `serenity-test-reviewer` agent are judgement guidance, not verified claims.

### 4.7 Upgrading (`references/upgrading.md`)

| Claim | Evidence |
|---|---|
| Java baselines (3.x = 8, 4.x = 11, 5.x = 17) | J class-file versions, `upgrade-checks/verify.py` |
| 3.9.8 to 5.4.1: 1,574 classes, 1,051 unchanged, 511 moved, 7 ambiguous, 5 gone | J `moves.py`; the table in the page equals the computed one (checked) |
| 4.2.34 to 5.4.1: all 1,587 classes unchanged | J |
| `serenity-cucumber4/5/6` stopped at 2.1.2, 2.2.6, 2.6.0; `serenity-jbehave` at 1.46.0 | J Maven Central |
| `fix_imports.py` rewrites an old-style class so that it compiles | R `upgrade-checks/verify.py` |
| JUnit 4 `SerenityRunner` and `CucumberWithSerenity` run on 5.4.1 | R `upgrade-checks/verify.py` |
| The five-project trial results (2.1.12, 2.4.51, 3.0.2, 3.3.0, 4.2.8) | O: the projects are outside this repository; not re-run |
| `InputField.called` and `PageElement.called` no longer exist | J `negative-claims.txt` |

## 5. Evidence from the evaluation

All with Claude 5.5 models (Opus, Sonnet, Haiku), run through the `claude` CLI. Details and caveats: `baseline-results.md`.

| Question | Result |
|---|---|
| Does a short reference improve one-shot code? (compile rate) | Screenplay held-out prompts 38/72 to 67/72, then 69/72 after an extension that was partly tuned on those prompts; Page Object held-out prompts 36/45 to 45/45 (clean: written before the prompts) |
| Does generated code run? | Compiled code almost always passed (28/28, 48/49, 35/36, 45/45). Screenplay 28/54 to 48/54; Page Objects 35/45 to 45/45 |
| Cucumber at run time | 0/27 unguided (models guess the wrong plugin class); 24/27 with the corrected reference. An earlier version of the reference had the wrong class name and the models copied it: the compile check could not see this |
| The skill in an agentic loop | `mvn` runs per task 3.2 to 1.9; tests passing at runtime 55/66 to 65/66 |
| Does a recorded style in `AGENTS.md` get followed? | 60/60, and 59/60 passed |
| Does the skill ask when the style is unclear? | 12/12 |
| Fresh prompts on a local app (healthy vs faulty), one-shot | 62% to 81% GOOD with the references (126 runs each); agentic 99% vs 100% (ceiling) |
| Living documentation (blind scored, 0 to 100%) | 68% / 68% (no skill / old skill) to 87% with the new skill on 14 prompts; 69% / 72% to 91% on 6 suite prompts; no loss of correctness (120/120 GOOD) |
| Fix a broken project | 44/48 without, 48/48 with the skill; the whole difference is the "0 tests" case (2/6 to 6/6) |
| Cucumber how-to tasks | 29/30 without, 30/30 with the skill: no real gain, about 30% more cost per task |

Limits to keep in mind: 2 to 3 runs per cell; live third-party sample sites; Claude 5.5 models and Claude Code only (the `AGENTS.md` test read the file as `CLAUDE.md`); several rules in the references were written from failures on the evaluation prompts, so a fresh prompt set is still needed before the pass rates are quoted as general.

## 6. Not verified, not covered

- Behaviour beyond the examples: the examples ran once on the live sample sites.
- Models other than Claude 5.5, agents other than Claude Code, and the skill installed at user level (`~/.claude/skills`).
- Whether a model follows the user's answer after the skill asks (the test stops at the question).
- Appium and mobile, Spring, JIRA, cloud and grid capabilities, proxies, JUnit 4 configuration details, Spock, Kotlin, Gradle versions other than 9.8.1.
- JBehave migration; Serenity 1.x.
- The "Not reproduced here" troubleshooting items.
- Prose claims about behaviour in the references that no check covers. If you spot one, it is a candidate for a new check.
- `.github/workflows/ai-guidance.yml` has not run on a GitHub runner.

## 7. How to re-run every check

From the repository root (Java 17, Maven, Python 3, bash 4+, headless Chrome, network on first run):

```
MVN_OFFLINE= bash docs/ai/skill/release-check.sh
```

This runs, in order: regenerate the `AGENTS.md` variants (must produce no diff); `verify-claims.py` (360 existence checks against the jars); `check-versions.py` (Maven Central and the dependency tree); the troubleshooting cases; the Cucumber, upgrade and configuration checks; and the examples with their tests. It takes about 15 minutes. `--no-run` compiles the examples only.

Release checklist (proposed): the check passes against the Serenity release being documented; versions in `skill/versions.json` bumped and everything regenerated (`agents-md/build.py`, `skill/publish-docs.py`); the evaluation re-run on the frozen files and compared with the last release; a maintainer has read the diff; the docs PR is merged; the skill and variants are published where D5 says.

## 8. Repository hygiene before pushing

**Update:** `eval/results` is untracked and ignored; the local history still holds it (the local `.git` pack is about 380 MB). Before any push, squash or rewrite the local commits so the result files never reach the remote. The figures are recorded in `baseline-results.md`; the outputs can be regenerated with the `eval/run*.py` scripts.

- `docs/ai/eval/results` is **37 MB, about 3,800 files** of raw model output. That is evidence, not product. Options: (a) a separate repository for the evaluation (harness plus results); (b) keep the harness and `baseline-results.md` here and publish the raw results as a release asset; (c) leave as is. I recommend (a) or (b) for a pull request to this repository.
- 35 local commits in `serenity-core` (branch `main`) and 10 in the docs repo (the branch `add-llms-txt` plus two on `master`). They show the working history, including corrections. Squashing them before pushing is possible.
- Python caches were being tracked and have been removed (`docs/ai/.gitignore`).
- The docs repo has an untracked `yarn.lock` that is not part of this work.

## 9. Sign-off

Please mark what you have reviewed.

- [x] Section 2: decisions D1 to D9 answered (2026-10-30).
- [ ] Section 3: each behaviour B1 to B18 classified as document, change or ignore.
- [ ] Section 4: pages read for factual accuracy: Screenplay [ ] Page Objects [ ] Project setup [ ] Troubleshooting [ ] Cucumber [ ] Configuration [ ] Upgrading [ ] the `llms.txt` header [ ] the `AGENTS.md` variants [ ]
- [ ] Section 8: repository layout decided.
- [ ] Owner named for the release checklist.
