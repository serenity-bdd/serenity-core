---
name: serenity-bdd
description: Write, fix or set up Serenity BDD tests in Java (Page Objects with @Steps, Screenplay, Cucumber; Selenium, Playwright or REST). Use when the user mentions Serenity BDD, Serenity Screenplay, serenity-bdd dependencies, SerenityJUnit5Extension, @Steps, Actor/Task/Question, or asks for web or API tests with Serenity reports.
---

# Serenity BDD

Serenity BDD supports several styles. They are all valid, and you must pick the one the user wants rather than your own favourite. Don't mix Page Objects and Screenplay in one test.

The packages are not guessable and your memory of them is often stale (old `net.thucydides` imports, nonexistent `screenplay.webdriver` packages). Read the reference for the chosen style before writing code.

## Step 1: choose the style

Decide in this order and stop at the first that applies.

1. **The user named it** ("Screenplay with Playwright", "Page Objects", "Cucumber").
2. **The project already uses one.** Check `pom.xml`/`build.gradle` and existing tests:
   - `serenity-screenplay*` dependency, or imports of `net.serenitybdd.screenplay` -> Screenplay (driver: `serenity-screenplay-webdriver` = Selenium, `serenity-screenplay-playwright` = Playwright, `serenity-screenplay-rest` = REST).
   - `serenity-rest-assured` without `serenity-screenplay-rest`, with `SerenityRest` calls inside `@Step` methods -> REST step library.
   - `@Steps` / `PageObject` / `UIInteractionSteps` -> Page Objects. `serenity-playwright` without Screenplay -> Playwright Page Objects.
   - `serenity-cucumber` or `.feature` files -> Cucumber (with the Screenplay or step-library style the glue code uses).
   Follow the project's existing style and driver. Don't add a second one.
3. **Otherwise stop and ask the user. Do not pick a default and do not write code first.** Ask in one short message, offering the real choices:
   - Approach: Page Objects with a step library, Screenplay, or Cucumber (with Screenplay or with step libraries/page objects).
   - Driver: Selenium or Playwright (or REST for API tests).
   Say that all of them are supported, and don't recommend one over another. Wait for the answer. If you cannot ask because nobody can answer, say that the style is needed, list the options, and stop without writing code.

Always use JUnit 5 (`@ExtendWith(SerenityJUnit5Extension.class)`) unless the project is already on JUnit 4.

## Step 2: read the reference

| Style | Read |
|---|---|
| Screenplay (Selenium, Playwright, REST) and Cucumber with Screenplay | `references/screenplay.md` |
| Page Objects + step libraries (Selenium, Playwright) | `references/page-objects.md` |
| REST with `SerenityRest` and a `@Steps` step library (no Screenplay) | `references/rest-assured.md` |
| New project, dependencies, `serenity.conf`, running and reports | `references/project-setup.md` |
| Cucumber on the JUnit Platform: runner, tags (Maven and Gradle), outlines, hooks, shared state, re-running failures, parallel | `references/cucumber.md` |
| `serenity.conf`, `serenity.properties`, environments, system properties, reading configuration values, screenshots and driver settings | `references/configuration.md` |
| Upgrading a project from Serenity 2.x, 3.x or 4.x; recognising old code (old imports, `serenity-cucumber4/5/6`, JBehave, `drivers {}` blocks); JUnit 4 and the old Cucumber runner | `references/upgrading.md` |
| How titles, step wording and structure turn into the report (living documentation); grouping tests by function; readme files | `references/living-documentation.md` (read it for every task that writes tests) |
| Reviewing tests you or someone else wrote | `references/review-checklist.md` |
| A build, report or browser error, or tests that don't run | `references/troubleshooting.md` (find the message; each entry was reproduced and its fix run) |

Each reference lists exact packages, the common mistakes, and points to a compiling example in `examples/`. Copy the structure of the example for your style. Don't import a class you haven't seen in the reference or the project; if you need one that isn't listed, look in the project's dependency jars or sources instead of guessing a package.

## Step 3: write the code

The report is living documentation: people who never read the code read it to learn what the application does. Every scenario must communicate its **intent** as well as its steps, in plain business English, and the tests must be **grouped by function** so the report is easy to navigate. Read `references/living-documentation.md` before writing, and:

- **Title each scenario with the rule or outcome** ("A reader sees an order number after paying"), with `@DisplayName` (JUnit 5), a sentence-like method name, or the Cucumber scenario name. Never `testCheckout1`.
- **Write steps as the business story**: Screenplay tasks with `Task.where("{0} adds 'x' to the basket", ...)`, `@Step("Search the catalog for '{0}'")` in step libraries, business-level Gherkin. No selectors, ids, URLs, sleeps or status codes in titles or step text.
- **End with the business expectation**, and make the assertion prove it with the specific values from the task.
- **Group by function**: a package (or feature folder) per capability, a class or feature file per feature, using the project's existing way (packages, `@Epic/@Feature/@Story`, or Cucumber folders); don't mix the two JUnit ways. With JUnit 5 packages, put `@DisplayName("<business phrase>")` on each test class, or the report shows the class name ("When signing in test"). Add the short `readme.md` descriptions the reference describes when you create a new area.
- One consistent style, driver and runner per project.
- Keep locators in page objects (Page Objects), `Target` holders (Selenium Screenplay) or constants (Playwright Screenplay).
- Don't invent API. If unsure that a method exists, check the reference, the examples, or the jar.

## Step 4: compile and fix

1. Run `mvn -q test-compile` (or `./gradlew compileTestJava`) in the project.
2. For each error, check the "Mistakes models make" table in the reference first (wrong package, `List` vs `Collection`, `By` vs String, `open()` override). For run-time errors (browser start-up, report missing, 0 tests, version conflicts) read `references/troubleshooting.md`.
3. If the symbol isn't covered, find the real class (`unzip -l <jar> | grep ClassName` in `~/.m2`), fix the import, and recompile.
4. When it compiles, run the tests (`mvn clean verify`) if the user has a runnable environment, and report the result honestly. A test that compiles is not a test that passes.

## Step 5: review

When the tests compile and have run, have them reviewed before you report:

1. If a `serenity-test-reviewer` subagent is available, run it (give it the user's task and the paths of the new tests). Otherwise do the review yourself with `references/review-checklist.md`, including `python3 scripts/outline.py target/site/serenity` to see the report as a reader does.
2. Fix every FAIL it reports (and the WEAK items that are cheap), run the tests again, and review once more. Two rounds at most; report anything left.

## Reporting back

Say which style, driver and runner you used and why (one line), list the files you created, state whether it compiled and whether tests were run, and give the reviewer's verdict (with anything left unfixed).
