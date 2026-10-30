# Baseline Evaluation: Prompts and Scoring (DRAFT)

Roadmap reference: WS8. Purpose: find out what models actually get wrong when writing Serenity code, before writing any guidance.

Status: draft for review. Nothing here has been run yet.

## 1. Design

### Questions the baseline answers
1. Does generated code compile against a current Serenity release?
2. Does it stay inside one style (no Page Object / Screenplay blending)?
3. Which specific mistakes recur? (These become the WS1 "common mistakes" list.)
4. Does the agent detect or ask for the style when the prompt doesn't say?
5. Which styles are weakest?

### Parameters to fix before running
| Parameter | Decision |
|---|---|
| Serenity version under test | TBD. Local cache has 5.3.10 and 5.4.x snapshots; ideally the latest release |
| Models | TBD (at least two, ideally different families/sizes) |
| Runs per prompt per model | 3, because output varies |
| Conditions | Baseline only for now: no `llms.txt`, no skill, no repo access |

### Two modes
- **A. One-shot.** The model receives only the prompt and returns files. A harness drops them into a prepared project and compiles. Measures raw knowledge.
- **B. Agentic.** The model works in an empty directory with file and shell tools, and can run `mvn test-compile` and `mvn verify`. Measures whether it can recover from errors, and how many iterations that takes.

Run A first (cheap, deterministic). Run B on a subset to see whether tooling hides the problems.

### Harness projects
One prepared Maven project per style, with correct dependencies and a minimal config, so that compile failures reflect the generated *code*, not the model's dependency choices. A separate set of "from scratch" prompts (P11) asks the model to write the `pom.xml` itself and tests that.

| Harness | Dependencies |
|---|---|
| `h-po-selenium` | serenity-core, serenity-junit5 (JUnit 5), AssertJ |
| `h-po-playwright` | the above plus serenity-playwright **(verify artifact name)** |
| `h-sp-selenium` | serenity-core, serenity-screenplay, serenity-screenplay-webdriver, serenity-junit5, serenity-ensure |
| `h-sp-playwright` | serenity-screenplay-playwright **(verify)** and serenity-junit5 |
| `h-sp-rest` | serenity-screenplay, serenity-screenplay-rest, serenity-junit5 |
| `h-cuke-sp` | the Screenplay web harness plus serenity-cucumber and the Cucumber JUnit Platform engine |

Targets (public, stable, already used in repo samples): `https://todo-app.serenity-js.org` for web, `https://jsonplaceholder.typicode.com` for REST.

## 2. Prompts

Each prompt is given verbatim, with no extra context. "Style named" prompts specify it; "style unnamed" prompts test detection.

| ID | Style | Prompt |
|---|---|---|
| P01 | PO + Selenium, named | Using Serenity BDD with Page Objects and JUnit 5, write a test that opens https://todo-app.serenity-js.org, adds the todo "Buy milk", and checks it appears in the list. |
| P02 | PO + Selenium, named | Using Serenity BDD with Page Objects and JUnit 5, write a test for the todo app at https://todo-app.serenity-js.org that adds three items, completes one, and checks the "items left" count. Use a step library. |
| P03 | PO + Playwright, named | Using Serenity BDD with Playwright and Page Objects, write a JUnit 5 test that opens https://todo-app.serenity-js.org and adds the todo "Buy milk". |
| P04 | Screenplay + Selenium, named | Using Serenity BDD Screenplay with Selenium and JUnit 5, write a test where an actor called Toby adds "Buy milk" to the todo list at https://todo-app.serenity-js.org and sees it in the list. Create a reusable task and a question. |
| P05 | Screenplay + Playwright, named | Using Serenity BDD Screenplay with Playwright and JUnit 5, write a test where an actor adds "Buy milk" to the todo list at https://todo-app.serenity-js.org and checks it is shown. |
| P06 | Screenplay + REST, named | Using Serenity BDD Screenplay and JUnit 5, write a test that GETs /users/1 from https://jsonplaceholder.typicode.com and checks the name, then creates a post with a custom task. |
| P07 | Cucumber + Screenplay, named | Using Serenity BDD with Cucumber and Screenplay, write a feature file, step definitions and test runner for adding an item to the todo list at https://todo-app.serenity-js.org. |
| P08 | Unnamed | Write a Serenity BDD test that checks you can add a todo item at https://todo-app.serenity-js.org. |
| P09 | Unnamed | I want to start testing our web app with Serenity BDD. Set up a project and write a first test for logging in. *(Expect: agent asks about, or states, its style and driver choice.)* |
| P10 | Fix, in-style | *(Given a provided PO or Screenplay test containing a seeded failure: wrong locator.)* This Serenity test fails. Find and fix the problem. *(Agentic mode only.)* |
| P11 | From scratch | Write a complete `pom.xml` for a Serenity BDD project using JUnit 5 and Screenplay with Selenium, ready to run `mvn verify` and produce the Serenity report. |
| P12 | Config | Write a `serenity.conf` that runs Chrome headless with a 1280x800 window, takes screenshots for failures only, and sets the base URL to https://todo-app.serenity-js.org. |

Coverage check: every style in the roadmap's section 2.1 has at least one named prompt (P01 to P07), and detection is tested (P08, P09). A held-out second set of prompts (different apps, different tasks) should be written to avoid tuning guidance to these.

## 3. Scoring

Score each run on every item. Items are binary unless noted. **Hard** items are the compile gate; the rest are diagnostic.

### 3.1 Compile and run (hard)
| # | Check |
|---|---|
| C1 | Compiles in the harness project with no edits |
| C2 | If it compiles, tests run and pass against the live target (mode A: optional, flaky targets are noted) |

If C1 fails, record every compile error (file, symbol, message), because these are the data we care about most.

### 3.2 Style consistency
| # | Check |
|---|---|
| S1 | Uses exactly one style (no PageObject mixed with Screenplay in the same test) |
| S2 | Uses the style the prompt named (named prompts); records which style it chose (unnamed prompts) |
| S3 | Uses the right driver (Selenium vs Playwright) |
| S4 | Uses the right runner (JUnit 5 extension vs JUnit 4 `@RunWith`; Cucumber via JUnit Platform Suite) |

### 3.3 Idiom correctness (diagnostic)
Record each as ok / wrong / n/a. The expected values come from the reference code in this repo (smoketests, tutorials, Playwright demo), which compiles.

| # | Check | Expected |
|---|---|---|
| I1 | Annotation imports (`Step`, `Steps`, `Managed`, `WithTag`) | `net.serenitybdd.annotations.*` |
| I2 | JUnit 5 integration | `@ExtendWith(SerenityJUnit5Extension.class)` |
| I3 | Screenplay actor setup | `Actor.named(..).can(BrowseTheWeb.with(driver))`, or `OnStage` / `OnlineCast` |
| I4 | Tasks | `Task.where(..)` or `instrumented(..)` with `performAs` |
| I5 | UI locators | `Target.the("..").locatedBy(..)` |
| I6 | Questions and assertions | `Question<T>` with `seeThat` / `Ensure`; names which `Ensure` class (it exists in several modules) |
| I7 | Page Objects | Extends `PageObject`, uses `@DefaultUrl`, `WebElementFacade` or `$()` |
| I8 | Cucumber runner | `@Suite` + `@IncludeEngines("cucumber")`, plugin `SerenityReporterParallel` |
| I9 | Config | `serenity.conf` HOCON using `serenity.*` and `webdriver {}` blocks, not `chrome.switches` or `thucydides.*` |
| I10 | Playwright integration | Uses the Serenity Playwright API correctly for page registration or the Screenplay ability |
| I11 | No invented API | Every Serenity class/method used exists. Count hallucinated symbols |

### 3.4 Process (mode B only)
| # | Check |
|---|---|
| X1 | Compile attempts needed before success (0 = first time) |
| X2 | Did it ask or state its style/driver choice when unnamed (P08, P09)? |
| X3 | Did it read the Serenity report or console output to diagnose a failure? |
| X4 | Number of tool calls to resolution (P10) |

## 4. Procedure

1. Prepare the harness projects (one per style) and confirm each builds with a trivial reference test.
2. Fix Serenity version and models. Record versions in the results file.
3. For each model, prompt, and repeat: run, save raw output, drop files into a clean copy of the harness, run `mvn test-compile`, and record compile errors.
4. Score S, I, and X items. Do the first pass by hand to learn the failure categories; automate afterwards where checks are mechanical (imports, runner, annotations can be grep-checked).
5. Aggregate:
   - compile rate per style and per model
   - top recurring compile errors and wrong imports (frequency table)
   - hallucinated symbols list
   - style-mixing rate
   - detection behaviour for unnamed prompts

## 5. Output

A results file (`docs/ai/baseline-results.md`) with:
- run parameters
- the table above, summarised per style and model
- a ranked list of recurring mistakes, each with an example and a count
- a short conclusion on which style and area needs guidance first

That ranked list is the direct input for WS1's "common mistakes" and for the skill's per-style files.

## 6. Open points

- Is Playwright (Page Objects) provided by a separate module or by the core? The harness dependencies marked **(verify)** need checking against the repo POMs.
- Which model set and Serenity version? (See section 1.)
- Live targets can be flaky or change. Consider whether C2 should use a local test app or a recorded stub, so run failures don't pollute the data.
- P10 needs a seeded-failure project; I'd build it from a reference sample with one deliberate error.
