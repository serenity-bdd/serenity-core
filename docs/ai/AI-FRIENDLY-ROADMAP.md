# Making Serenity BDD AI-Coding Friendly: Roadmap

Status: In progress. WS8 (evaluation), WS2 (skill and `AGENTS.md` variants) and the first version of WS1 (`llms.txt` and idioms pages) are built and measured; see section 1a. WS1's docs changes are on an unpushed docs-repo branch. WS3 has a spike (findings and a proposed format, nothing implemented). WS4 to WS7 are not started.
Audience: Serenity maintainers
Target users: people writing tests with Serenity (not framework contributors)

## 1. Problem statement

AI coding assistants are now a primary way people write Serenity tests. They fail in predictable ways:

1. **Stale knowledge.** Models generate JUnit 4 `@RunWith(SerenityRunner.class)`, deprecated APIs and old configuration styles. Note that `net.thucydides.*` is **not** itself a sign of stale code: many core packages still live there and are current. Staleness has to be identified class by class, from observed failures, not by package prefix.
   - The project's own public samples are a likely source: the docs `docs-starter-tutorial` does not compile (see WS6).
2. **Style mixing.** Serenity supports several equally valid ways of working (see section 2.1), and agents blend them in a single project.
3. **Blind feedback loop.** After running tests, agents must scrape HTML reports or raw console output to learn what failed.
4. **Opaque errors.** Many exceptions describe the symptom, not the fix.
5. **No machine-readable view of a project.** Agents cannot cheaply discover existing tasks, questions, or requirements coverage, so they reinvent them.

## 1a. What the baseline measured

Detail and method are in `baseline-results.md`; the evaluation harness is in `eval/`. All results are from Claude 5.5 models, compile checks only (no browser tests were run).

- **Raw knowledge is uneven.** Without guidance, generated code compiled 53 to 80% of the time one-shot, depending on the style. Weakest: Screenplay with Selenium (Sonnet and Haiku mostly failed; Opus did well), Cucumber with Screenplay, and Page Objects with Selenium. Page Objects with Playwright was already near 100%.
- **The errors are narrow and repeated:** classes imported from packages that don't exist (`screenplay.webdriver.BrowseTheWeb`), annotations from `net.thucydides.core.annotations` instead of `net.serenitybdd.annotations`, `Collection` vs `List` on `Text.ofEach`, the old `.viewedBy(actor)` question API, JUnit 4 runners, and a page object redeclaring the final `open()`. They are mostly wrong-package and stale-API mistakes, not design mistakes. This also confirms that package prefix is not a staleness signal: `ScenarioSteps` and `Pages` correctly live in `net.thucydides`.
- **A compile loop hides most of it.** In agentic mode with Maven, every run ended with compiling code. The cost shows up as extra iterations (3.2 `mvn` runs per task on average).
- **Short, verified guidance fixes most of it.** A single reference sheet per style (exact packages, a mistakes table, one compiling example) took one-shot compile rates to 93% on held-out Screenplay prompts (from 53%) and 100% on held-out Page Object prompts (from 80%).
- **The skill cuts iterations.** With the skill installed, agentic runs took 1.9 `mvn` runs per task instead of 3.2, and 26% less time, at about the same cost. Models already followed an existing project's style without it.
- **A recorded style is followed.** With an `AGENTS.md` variant in an otherwise empty project, models wrote code in that style in 60 of 60 runs, against their own preference for Screenplay with Selenium. It doesn't reduce compile iterations (2.7 `mvn` runs per task, as without it).
- **Compile-only checks hide run-time errors.** Running the guidance for the first time found two mistakes that compiled but fail at run time: the Serenity Cucumber plugin class is `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel` (not `io.cucumber...`), and Serenity 5.4.1 depends on JUnit 6.1.3 and Cucumber 8.0.4, so older pinned versions fail with "conflicting versions". Both are fixed. All six reference example sets now run and pass (headless Chrome, Playwright, the live sample sites). The corrected harness didn't change the earlier compile results.
- **The runtime stage confirms and sharpens the results.** The tests models generated were run (`eval/run_tests.py`: headless Chrome and Playwright, live sample sites, one retry). Outside Cucumber, code that compiled almost always passed (28/28, 48/49, 35/36, 45/45), so compile rate was a good proxy there. Pass rates: Screenplay unguided 52% vs guided 89%; Page Objects 78% vs 100%; agentic with the skill 98% vs 83% without; agentic with an `AGENTS.md` variant 59/60. The skill's benefit is larger at runtime than at compile time.
- **Cucumber needs run-time checks and exact names.** Models (even Opus) guess the wrong Serenity Cucumber plugin class (0/27 pass unguided in one-shot, 0/6 agentic); an earlier version of the guidance repeated the wrong name and the models copied it. With the corrected reference 24/27 pass one-shot and 6/6 agentic.
- **A Serenity gotcha only run-time checks find.** `Target.the(..).locatedBy("input.new-todo")` reads the string as XPath, because it compiles as an XPath name test, and finds nothing; `.new-todo`, `#id` and `[x=y]` are CSS. It caused 5 of the 11 unguided agentic failures. The fix is `located(By.cssSelector(..))`; it is now in the guidance. Whether Serenity should document or change this behaviour is a question for the maintainers.
- **Style choice must be the user's.** Left to guess, models drifted to Screenplay with Selenium (8 of 12 unnamed runs). The skill now asks when the style is unclear and doesn't default; in 12 of 12 test runs it asked.

## 2. Supported styles

### 2.1 All of these are valid

Serenity has two fundamentally different ways to write tests, plus a driver choice:

| Style | Driver | Runner | Reference code in this repo |
|---|---|---|---|
| Page Objects + `@Steps` | Selenium | JUnit 5 | docs starter; `serenity-smoketests/.../pageobjects` |
| Page Objects + `@Steps` | Playwright | JUnit 5 | `serenity-playwright-demo` |
| Screenplay | Selenium | JUnit 5 or Cucumber | `serenity-smoketests/.../screenplay` |
| Screenplay | Playwright | JUnit 5 or Cucumber | `serenity-screenplay-playwright` |
| Screenplay (REST) | none | JUnit 5 or Cucumber | `serenity-tutorials` |

None of these is deprecated or second-class. Guidance must not promote one over the others.

### 2.2 The requirement for AI guidance

The goal is **consistency within a project**, not uniformity across projects. An agent must:
1. Work out which style a project uses (from dependencies, existing tests, or by asking), and
2. Stay in that style, using the idioms current for it.

When the style isn't named and the project doesn't show one, the agent must ask. It must not default, and must not recommend one style over another.

## 3. Goals and non-goals

**Goals**
- An agent given a blank project and a feature request writes idiomatic, current Serenity code on the first attempt.
- An agent can run tests, find out what failed and why, and fix it, without parsing HTML.
- The guidance is versioned, generated where possible, and tested.

**Non-goals**
- Removing or breaking existing styles (Page Objects, `@Step`) or deprecated APIs.
- Building an AI feature into the reports (e.g. LLM-generated summaries).
- Supporting every agent product individually. We target open conventions (`llms.txt`, `AGENTS.md`, Agent Skills, MCP).

## 4. Workstreams

### WS1: Current-knowledge docs (`llms.txt`, idioms, "don't do this")

**Deliverables**
- `/llms.txt`: short index with links to the key pages, plus the Serenity version it describes.
- `/llms-full.txt`: concatenated core docs for single-fetch ingestion.
- "Current idioms" pages, **one per supported style** (section 2.1): Page Objects + `@Steps` (Selenium and Playwright), Screenplay (Selenium, Playwright, REST) with JUnit 5 and with Cucumber, and `serenity.conf`. Each page is built from code that compiles and runs.
- A short "common mistakes" list. It contains only mistakes **observed in the WS8 baseline** (for example JUnit 4 runners, or an annotation imported from the wrong package), not a generated deprecation inventory. A scan of `@Deprecated` was tried and judged a dead end: it mostly finds internal and configuration-enum entries that models rarely generate, and it cannot see the real problems.
- Per-version variants (e.g. `/5.x/llms.txt`) once there is a second supported line.

**Approach**
- Lives in `serenity-bdd.github.io` (Docusaurus). Generate `llms.txt` and `llms-full.txt` at build time from the docs tree (a small Docusaurus plugin or a build script), so they cannot drift.
- Idioms are lifted from the reference projects in WS6, never written freehand.
- Add a CI check that the sample projects compile against the current release.

**Status**: first version built, on the docs repo branch `add-llms-txt` (unpushed). A Docusaurus plugin (`plugin/llms-txt/`) writes `llms.txt` (a hand-written header for AI assistants plus a generated link index of 59 pages) and `llms-full.txt` (about 840 KB) at build time, default locale only, release notes excluded. A new docs section "Current Idioms and Imports" (overview, Screenplay, Page Objects, project setup) is generated from the skill's references and compiling examples by `skill/publish-docs.py`, so the docs and the skill share one source. Not done: pushing and review, per-version variants, and measuring whether `llms.txt` alone improves generated code. The header hard-codes the release (5.4.1) and must be updated per release. Several existing docs pages still pin older versions (for example 5.3.7 in `maven.md`). Cucumber without Screenplay needs more coverage.

**Done when**: a model with only `llms.txt` available produces code that compiles and follows the idioms for its chosen style, across the WS8 prompts.

### WS2: Official agent skill and `AGENTS.md` template

**Deliverables**
- A Serenity Agent Skill (`SKILL.md` + per-style reference files) with a **style-selection step first**: detect whether the project is Page Objects or Screenplay, Selenium or Playwright, JUnit 5 or Cucumber (from dependencies and existing tests), and ask the user when neither the request nor the project settles it (no default). Then load only the guidance for that style: project layout, Maven/Gradle setup, conventions (for Screenplay: actors, tasks, interactions, questions; for Page Objects: page objects, `@Steps` libraries, `WebElementFacade`), `serenity.conf` keys, how to run tests and read results, and pitfalls.
- A drop-in `AGENTS.md` template (also usable as `CLAUDE.md`) for user projects. It records the chosen style so the agent stays in it.
- Each starter project (WS6) ships both files, pre-filled for its style.

**Approach**
- Single source of truth in the docs repo (`docs/ai/`), published at a stable URL and also bundled in the starter projects.
- Skill content is derived from the same idioms page as WS1, so both are written once.
- The skill tells the agent to use the WS3 CLI for results, and the WS4 catalogue for discovery, once they exist.

**Status**: the skill is built (`skill/serenity-bdd/`, with a README) and measured against the WS8 baseline (section 1a). Style selection works: it follows an existing project's style and asks otherwise. The `AGENTS.md` variants are built (`agents-md/`: one per style, generated from the same references) and tested. Not done: distribution, shipping the variants in the starter projects (WS6), and testing with models other than Claude 5.5 or with agents other than Claude Code (the test read the file as `CLAUDE.md`). Page Object and Screenplay references are for Maven and JUnit 5 only.

**Living documentation**: the skill and the `AGENTS.md` variants now require business-English scenario titles and steps, grouping by function and a post-write review (`references/living-documentation.md`, `review-checklist.md`, the `serenity-test-reviewer` agent in `skill/agents/`). Blind-scored quality rose from 68% to 87% (single scenarios) and from 69%/72% to 91% (suite prompts) with all tests still passing and catching the planted faults.

**Done when**: the skill passes the WS8 eval with a measurable improvement over baseline (met: 41% fewer compile iterations, and 98% vs 83% of agentic runs passing at runtime).

### WS3: Agent-readable test results

**Deliverables**
- A compact failure summary file, written next to the existing reports (e.g. `target/site/serenity/failures.json`), with per scenario: name, requirement path, status, failing step (with its path through nested steps), trimmed cause and stack, screenshot path, and the source location of the failing test where known.
- CLI commands in `serenity-cli`: `serenity failures`, `serenity summary`, `serenity outcome <test>`, each with `--format json|markdown`.
- A versioned, documented JSON schema. This is a contract, not an internal format.

**Approach**
- Build on the existing per-test JSON outcomes and `serenity-json-summary-report`. Do not add a second source of truth.
- Stack traces are trimmed to frames in user code plus the first framework frame. Cap output size by default, with a flag to expand.
- Output must be deterministic and stable-ordered so it diffs well.

**Spike findings** (`spikes/ws3/`, with a prototype and a proposed `failures.json` schema): the failing step, error, user-code location and last screenshot can be derived from the per-test outcome JSON that Serenity already writes (20 ms for 245 outcomes). A failed `mvn verify` leaves no HTML report (aggregation never runs), so the summary must come from the JSON or be written at test end. Gaps: failures that occur outside any step (all 5 in the Playwright module's output); Cucumber outcomes lack the test class and method, a stack frame, and the feature file path and line; one outcome has no steps or story; assertion messages can repeat. Not covered: failing browser tests with screenshots, JUnit 4, retries, parallel runs.

**Open questions**
- Cucumber location data (feature file path and scenario line) must come from `serenity-cucumber`; where from the pickle?
- Which outcome wins when a scenario has several (retries, parallel runs)?
- The writer: the spike's evidence favours a listener at test end (resolved in direction, to be confirmed in a prototype).

**Done when**: an agent can go from "tests failed" to "here is the failing step, cause and screenshot" in one CLI call.

### WS4: Reusable-code catalogue and requirements coverage

**Deliverables**
- A build-time `catalogue.json` listing the reusable building blocks in the project and its dependencies, with their factory methods, parameters, and Javadoc summaries. For Screenplay projects: tasks, interactions and questions. For Page Object projects: page objects and `@Steps` libraries with their methods.
- Requirements coverage output: requirement tree with scenario counts by status, and untested or pending requirements.

**Approach**
- Catalogue generation via annotation processing or a Maven/Gradle plugin goal. Choose after a spike; the plugin goal is probably simpler.
- Requirements coverage reuses the existing requirements model; the work is exposing it in the same JSON/CLI shape as WS3.

**Done when**: an agent asked to add a scenario can list existing tasks and reuse them.

### WS5: MCP server

**Deliverables** (all thin wrappers over WS3 and WS4)
- `run_scenario`, `run_tests` (by tag, class or feature)
- `get_failures`, `get_outcome`
- `list_catalogue` (tasks/questions or page objects/steps, per project style)
- `get_requirements_coverage`

**Approach**
- Deliberately sequenced after WS3 and WS4. If the CLI and JSON are good, the server is mostly wiring.
- Ship as a runnable artifact (e.g. `serenity-mcp`) usable via stdio, and document configuration for common clients.
- Read-only tools by default. Running tests is explicit and scoped to the project directory.

**Done when**: an MCP client can run a scenario, see the failure, and list reusable tasks without shell access.

### WS6: Clean reference projects, one per style

**Deliverables**
- Fix the existing public samples first. **Audit finding:** `sample-code/docs-starter-tutorial` (Page Objects + `@Steps`, Selenium, JUnit 5, pinned to 5.0.2) does not compile: it imports `Step` and `Managed` from `net.thucydides.core.annotations`, but they exist only in `net.serenitybdd.annotations`. Changing those imports makes it compile at 5.0.2 and 5.3.10. That fix, and the same one in the REST tutorial and four docs pages, is committed on the docs repo `master` (not pushed). The starter also uses the old `chrome.switches` option and an old version pin.
- One minimal, clean reference project per style in section 2.1. Each has: a minimal `serenity.conf` (not the 100-line cloud-provider file), `AGENTS.md`, the skill, and a CI file.
- Each project contains **one** style only. The current `serenity-smoketests` mixes Page Objects, Screenplay and Cucumber in one module, which is fine as a test bed but a poor example to learn from.
- The compiling examples under `skill/serenity-bdd/examples/` (one per style: Page Objects with Selenium and Playwright; Screenplay with Selenium, Playwright and REST; Cucumber with Screenplay) are a starting point for these projects.
- Tidy inconsistencies within a style, such as the unused JUnit 4 `RunWith` import in a JUnit 5 Screenplay sample.
- Docs structure: all styles presented as equals, each with its own getting-started path. No style is labelled legacy.

**Approach**
- No API removals. This is about the quality and consistency of the examples that people and models copy from.
- Compile every sample in CI against the current release.

### WS7: Actionable error messages

**Deliverables**
- Audit and rewrite of the most common failure messages, with a "To fix:" hint. First candidates: missing ability (`CannotPerformTask`-style failures, no `BrowseTheWeb`), driver and capability misconfiguration, missing `serenity.conf` / property typos, actor not found on stage, unsupported Java or Selenium version mismatches.
- A convention for hints so new errors follow it.

**Approach**
- Source candidates from GitHub issues, Stack Overflow and Slack/mailing-list questions, ranked by frequency.
- Ship incrementally, around 10 messages per release. Each is a small, low-risk PR.

### WS8: Evaluation harness (cross-cutting)

**Status**: built and used. `eval/` has 6 per-style harness projects, an all-styles project, four seeded projects, 26 prompts (original, held-out, unnamed and seeded), a one-shot runner and an agentic runner, and an installable-skill condition. `baseline-results.md` holds every run. Conditions run so far: baseline, a reference sheet in the prompt, the skill (agentic), the skill's ask behaviour, and `AGENTS.md` variants. The runtime stage (`run_tests.py`) runs the generated tests. Not yet: `llms.txt` only, skill + MCP, a local test app instead of live sites, or the unguided Page Objects with Playwright runs at runtime.

**Deliverables**
- A set of 15 to 25 prompts that **covers every style in section 2.1**, such as "write a login test using Page Objects and JUnit 5", "write it with Screenplay and Playwright", "add a REST check", and "fix this failing scenario". Some prompts name the style; some don't, to check the agent asks or detects it.
- An automated scorer: does it compile, does it avoid deprecated or wrong-package symbols, does it stay in one style, do the tests pass.
- Baseline findings feed WS1's "common mistakes" list.
- Runs across several models with these conditions: baseline, `llms.txt` only, skill, skill + MCP.

**Approach**
- Lives in its own repo or directory so it can be run on each release.
- Results published internally; use them to prioritise doc fixes.

## 5. Sequencing

| Phase | Scope | Why this order |
|---|---|---|
| 0 | **Done.** WS8 prompts, runners and baseline; the broken docs starter fixed (WS6) | Gives a measurable starting point; stops the project's own samples teaching old idioms |
| 1 | **Mostly done.** The skill and `AGENTS.md` variants (WS2), `llms.txt` and the idioms pages (WS1, unpushed) and compiling examples exist. Next: review and publish the docs changes, then the reference projects (WS6) | Guidance is lifted from code that compiles |
| 2 | WS3, then WS7 (ongoing) | Closes the feedback loop; errors improve it further |
| 3 | WS4 | Needed for discovery and for MCP |
| 4 | WS5 | Thin layer once WS3 and WS4 are stable |
| 5 | Refresh WS6 projects | Bundle the CLI, catalogue and MCP configuration into each starter |

WS7 and WS8 run continuously alongside the other phases.

## 6. Risks

- **Guidance drift**: docs, skill and `llms.txt` diverge. Mitigation: single source, generated outputs, CI checks.
- **Schema churn**: agents and the MCP server depend on WS3 output. Mitigation: version the schema, treat changes as breaking.
- **Oversized output** overwhelms agent context. Mitigation: trimmed defaults, explicit expansion flags.
- **Scope creep into per-vendor integrations.** Mitigation: stick to open conventions.
- **Eval overfitting**: tuning docs to the eval prompts. Mitigation: keep a held-out set. In practice: the first held-out Screenplay run (53% to 93%) was clean; the re-run after the sheet was extended is not, because the new rules came from those failures. Write a third prompt set before the next claim.
- **Compile-only measurement**: passing `mvn test-compile` doesn't show that a test passes, and it already hid two run-time errors in the guidance (section 1a). Mitigation, now in place: the reference examples run (`check-examples.sh --run`) and `eval/run_tests.py` runs generated tests. Remaining risk: the runtime stage uses live sample sites (saucedemo, the-internet, the todo app, jsonplaceholder) which can change or be slow; a local test app would remove that.
- **Model coverage**: everything so far is Claude 5.5. Other models may fail differently.

## 7. Success metrics

- Eval pass rate (compiles, no deprecated APIs, tests pass) versus baseline, per model. Current: compile and run-time pass rates in section 1a.
- Compile iterations per task in agentic mode (3.2 baseline, 1.9 with the skill).
- Number of deprecated symbols appearing in generated code.
- Agent steps from failing run to correct fix on a set of seeded failures.
- Adoption: downloads or fetches of `llms.txt`, skill, and MCP server.

## 8. Open questions

1. Where does the Agent Skill get distributed? Options: the docs site, the starter repo, a plugin marketplace. It currently lives in `docs/ai/skill/` in this repo and is installed by copying the directory.
2. MCP server packaging: standalone JAR, Maven plugin goal, or both?
3. ~~Is the failure summary produced by a listener at test end or by report aggregation?~~ The WS3 spike favours a listener (aggregation doesn't run after failed tests); to be confirmed.
4. How far should the catalogue (WS4) go for projects with custom abstractions, beyond tasks and questions?
5. Do we support Gradle at parity with Maven for the new CLI and plugin goals from day one?

## 9. Immediate next steps

Done: the docs starter and REST tutorial imports (docs repo, unpushed), the WS8 baseline, held-out, Cucumber and runtime runs, the Screenplay and Page Object reference sheets, the skill and its README and evaluations, `llms.txt` and `llms-full.txt` generation, the "Current Idioms and Imports" docs pages, the `AGENTS.md` variants and their evaluation, the WS3 failure-summary spike, running the reference examples, and the corrections they led to (Cucumber plugin name, JUnit and Cucumber versions, the `locatedBy` XPath-vs-CSS gotcha).

Next, in order:
1. **Publish what exists.** Push the docs-repo changes (the `add-llms-txt` branch and the earlier `master` commits) and open a PR; push serenity-core `main`; decide where the skill and `AGENTS.md` variants are distributed (open question 1). Raise the `locatedBy` behaviour with the maintainers.
2. **More runtime coverage (WS8):** the original prompt set and the unguided Page Objects with Playwright at runtime, a third held-out prompt set, and a local test app in place of the live sites.
3. **Continue the WS3 spike:** a failing browser test with screenshots, JUnit 4, retries and parallel runs; then decide the writer and the Cucumber location data, and build it with a schema test.
4. **Audit the other samples** (`serenity-smoketests`, `serenity-tutorials`, `serenity-playwright-demo`, mobile examples) for compile and run status and mixed idioms, then choose and build the reference projects, each shipping its `AGENTS.md` variant (WS6). Include a check that no pom pins JUnit or Cucumber versions older than the Serenity release uses.
5. **Measure `llms.txt` alone** (a model with web access and only `llms.txt`) against the skill (WS1, WS8).
6. **Test the skill and variants with other models and agents, and with Cucumber without Screenplay, Gradle, and JUnit 4 projects.**
