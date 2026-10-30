# AGENTS.md

This project's tests use Serenity BDD. Read this before writing or changing tests.

## Project style (fixed)

- Approach: **Screenplay**
- Driver: **REST (no browser)**
- Runner: **JUnit 5**
- Serenity dependencies: serenity-core, serenity-junit5, serenity-screenplay, serenity-screenplay-rest, serenity-rest-assured, serenity-ensure

Stay in this style. All Serenity styles are valid, but this project uses this one, so don't introduce another (and don't mix Page Objects and Screenplay in one test). If you think the style should change, ask first.

Avoid: Browser/UI classes, page objects, `@RunWith`.

## Structure

Test -> actor with the `CallAnApi` ability performs tasks built from `Get`/`Post`/... interactions, and asks about `LastResponse`.

Follow the existing package layout and naming. Look at an existing test before writing a new one, and reuse the tasks, questions, page objects or step libraries already there.

## Imports and idioms (verified against Serenity 5.4.1)

- `@Step`, `@Steps`, `@Managed`, `@DefaultUrl`, `@WithTag` come from `net.serenitybdd.annotations` (never `net.thucydides.core.annotations`).
- JUnit 5: `@ExtendWith(SerenityJUnit5Extension.class)` from `net.serenitybdd.junit5`. Don't use `@RunWith(SerenityRunner.class)`. Serenity 5.4.1 brings JUnit 6.1.3 and Cucumber 8.0.4: don't pin older JUnit or Cucumber versions in the pom (mixed versions compile but fail at run time).
- REST: `CallAnApi` is in `net.serenitybdd.screenplay.rest.abilities`; `Get`, `Post`, `Delete`, `Ensure` are in `net.serenitybdd.screenplay.rest.interactions`; `LastResponse`, `TheResponse` are in `net.serenitybdd.screenplay.rest.questions`. There is no `net.serenitybdd.rest.abilities` package.
- Create the actor with `Actor.named("Alice").whoCan(CallAnApi.at("https://..."))`.
- Core: `Actor`, `Task`, `Question`, `GivenWhenThen.seeThat` are in `net.serenitybdd.screenplay`.

If you need a class that isn't listed here or already used in the project, find its real package in the dependency jars (for example `unzip -l <jar> | grep ClassName`) instead of guessing.

## Commands

- Check that it compiles: `mvn -q test-compile`
- Run the tests and build the report: `mvn clean verify`
- Report: `target/site/serenity/index.html` (only built when the tests pass)
- Results, also after a failed build: read the JSON outcome files `target/site/serenity/*.json` (title, result, failure message, steps); see https://serenity-bdd.github.io/docs/ai/ai_test_results

## Rules

- Compile after every change, and fix compile errors before doing anything else.
- Write test steps in business language (`AddATodo.called("Buy milk")`, `todo.addTodo(..)`). Keep locators out of test classes.
- The Serenity report is living documentation that business readers use. Title every scenario with the rule or outcome in plain business English ("A reader sees an order number after paying", not `testCheckout1`), word steps as the business story with no selectors, ids, URLs or status codes, and end with the business expectation.
- Group tests by function (a package or feature folder per capability, a class or feature file per feature), the way the project already does, so the report reads as a table of contents. Don't mix package structure with `@Epic`/`@Feature`/`@Story` annotations in one project.
- After writing or changing tests, run them and review them: use the `serenity-test-reviewer` agent if the project has it, otherwise check the scenario titles, step wording, grouping and assertions yourself (`https://serenity-bdd.github.io/docs/ai/ai_living_documentation`). Fix what the review finds.
- Don't invent Serenity API. If unsure that a method exists, check the project, the docs or the jar.
- Say honestly what you ran: a test that compiles is not a test that passes.

## More detail

- Packages, common mistakes and compiling examples: https://serenity-bdd.github.io/docs/ai/ai_screenplay_idioms
- Dependencies and configuration: https://serenity-bdd.github.io/docs/ai/ai_project_setup
- Documentation index for AI tools: https://serenity-bdd.github.io/llms.txt
