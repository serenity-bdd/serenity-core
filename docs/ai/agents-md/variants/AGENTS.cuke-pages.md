# AGENTS.md

This project's tests use Serenity BDD. Read this before writing or changing tests.

## Project style (fixed)

- Approach: **Cucumber with page objects and step libraries**
- Driver: **Selenium**
- Runner: **Cucumber on the JUnit Platform**
- Serenity dependencies: serenity-core, serenity-junit5, serenity-cucumber, io.cucumber:cucumber-java, io.cucumber:cucumber-junit-platform-engine

Stay in this style. All Serenity styles are valid, but this project uses this one, so don't introduce another (and don't mix Page Objects and Screenplay in one test). If you think the style should change, ask first.

Avoid: Screenplay actors and tasks, Playwright classes, `@RunWith`, and `net.thucydides.core.annotations.*`.

## Structure

Feature files (`src/test/resources/features`) -> step definitions that call `@Steps` step libraries -> page objects (`PageObject` with `@FindBy`). No Screenplay.

Follow the existing package layout and naming. Look at an existing test before writing a new one, and reuse the tasks, questions, page objects or step libraries already there.

## Imports and idioms (verified against Serenity 5.4.1)

- `@Step`, `@Steps`, `@Managed`, `@DefaultUrl`, `@WithTag` come from `net.serenitybdd.annotations` (never `net.thucydides.core.annotations`).
- Runner: a `@Suite` class with `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features")`, a `GLUE_PROPERTY_NAME` parameter for the step-definition package, and `PLUGIN_PROPERTY_NAME` = `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty`. No JUnit 4 `@RunWith(CucumberWithSerenity.class)`.
- In step definitions declare the library with `@Steps` (never `new`, or the page object fields stay null); page objects extend `net.serenitybdd.core.pages.PageObject`.
- In a Cucumber expression `book(s)` is optional text: it matches "book" and "books", not the literal characters `book(s)` in a feature file. An undefined step fails the build.
- A String selector that starts with a tag name is read as XPath: `$("button")`, `$("th[data-x=y]")` and `thenFind("button")` find nothing useful. Start with `#` or `.`, or use `$(By.cssSelector(..))` / `findBy(By.cssSelector(..))`.

If you need a class that isn't listed here or already used in the project, find its real package in the dependency jars (for example `unzip -l <jar> | grep ClassName`) instead of guessing.

## Commands

- Check that it compiles: `mvn -q test-compile`
- Run the tests and build the report: `mvn clean verify`
- Report: `target/site/serenity/index.html`

## Rules

- Compile after every change, and fix compile errors before doing anything else.
- Write test steps in business language (`AddATodo.called("Buy milk")`, `todo.addTodo(..)`). Keep locators out of test classes.
- The Serenity report is living documentation that business readers use. Title every scenario with the rule or outcome in plain business English ("A reader sees an order number after paying", not `testCheckout1`), word steps as the business story with no selectors, ids, URLs or status codes, and end with the business expectation.
- Group tests by function (a package or feature folder per capability, a class or feature file per feature), the way the project already does, so the report reads as a table of contents. Don't mix package structure with `@Epic`/`@Feature`/`@Story` annotations in one project.
- After writing or changing tests, run them and review them: use the `serenity-test-reviewer` agent if the project has it, otherwise check the scenario titles, step wording, grouping and assertions yourself (`https://serenity-bdd.github.io/docs/ai/ai_living_documentation`). Fix what the review finds.
- Don't invent Serenity API. If unsure that a method exists, check the project, the docs or the jar.
- Say honestly what you ran: a test that compiles is not a test that passes.

## More detail

- Packages, common mistakes and compiling examples: https://serenity-bdd.github.io/docs/ai/ai_cucumber
- Dependencies and configuration: https://serenity-bdd.github.io/docs/ai/ai_project_setup
- Documentation index for AI tools: https://serenity-bdd.github.io/llms.txt
