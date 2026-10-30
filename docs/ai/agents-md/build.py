#!/usr/bin/env python3
"""Generate the AGENTS.md variants (one per Serenity style) into ./variants/.

Each variant records the project's chosen style so that an AI coding assistant stays in it. The idiom
facts come from ../skill/serenity-bdd/references/ and are verified against Serenity 5.4.1 (Playwright
modules 5.3.11). Edit the data below, then run: python3 build.py
"""
import os

HERE = os.path.dirname(os.path.abspath(__file__))
DOCS = "https://serenity-bdd.github.io/docs/ai"

COMMON_ANNOTATIONS = ("`@Step`, `@Steps`, `@Managed`, `@DefaultUrl`, `@WithTag` come from `net.serenitybdd.annotations` "
                      "(never `net.thucydides.core.annotations`).")
JUNIT5 = ("JUnit 5: `@ExtendWith(SerenityJUnit5Extension.class)` from `net.serenitybdd.junit5`. Don't use `@RunWith(SerenityRunner.class)`. "
          "Serenity 5.4.1 brings JUnit 6.1.3 and Cucumber 8.0.4: don't pin older JUnit or Cucumber versions in the pom (mixed versions compile but fail at run time).")

STYLES = {
  "po-selenium": dict(
    name="Page Objects with a step library", driver="Selenium", runner="JUnit 5",
    deps="serenity-core, serenity-junit5",
    structure="Test (`@Steps` field) -> step library (`@Step` methods, extends `UIInteractionSteps`) -> page object (locators and page actions).",
    idioms=[
      COMMON_ANNOTATIONS, JUNIT5,
      "Page objects extend `net.serenitybdd.core.pages.PageObject`, use `WebElementFacade` (same package), `@DefaultUrl(\"...\")`, and `@FindBy(css = \"...\")` or `$(\"css\")`.",
      "`PageObject.open()` is final. Don't declare your own `open()`: use `@DefaultUrl` or `openAt(url)`.",
      "Step libraries extend `net.serenitybdd.core.steps.UIInteractionSteps`; page objects are injected as fields. Tests get step libraries with `@Steps` and the browser with `@Managed(driver = \"chrome\") WebDriver driver;`.",
      "`ScenarioSteps` (`net.thucydides.core.steps`) is correctly under `net.thucydides`; that prefix is not a sign of old code.",
      "A String selector that starts with a tag name is read as XPath: `$(\"button\")`, `$(\"th[data-x=y]\")` and `thenFind(\"button\")` find nothing useful. Start with `#` or `.`, or use `$(By.cssSelector(..))` / `findBy(By.cssSelector(..))`.",
    ],
    avoid="Screenplay classes (`Actor`, `Task`, `Question`), Playwright, `@RunWith`, `@Page`, and Cucumber glue code.",
    docs="page_object_idioms"),
  "po-playwright": dict(
    name="Page Objects with a step library", driver="Playwright", runner="JUnit 5",
    deps="serenity-core, serenity-junit5, serenity-playwright, com.microsoft.playwright:playwright",
    structure="Test (`@Steps` field) -> step library (`@Step` methods) -> page object (a plain class wrapping a Playwright `Page`).",
    idioms=[
      COMMON_ANNOTATIONS, JUNIT5,
      "Page objects are plain classes holding a `com.microsoft.playwright.Page`. They don't extend Serenity's `PageObject` and don't use `@FindBy` or `WebElementFacade`.",
      "The test starts `Playwright` and the `Browser` in `@BeforeAll`; in `@BeforeEach` it creates a page and calls `PlaywrightSerenity.registerPage(page)` (`net.serenitybdd.playwright`); in `@AfterEach` it calls `PlaywrightSerenity.unregisterPage(page)` and closes the page.",
      "Step libraries are plain classes with `@Step` methods. They get the page from `PlaywrightSerenity.getCurrentPage()`.",
    ],
    avoid="Selenium `WebDriver`, `@Managed`, `PageObject`/`WebElementFacade`, Screenplay classes, `@RunWith`, and Cucumber glue code.",
    docs="page_object_idioms"),
  "sp-selenium": dict(
    name="Screenplay", driver="Selenium", runner="JUnit 5",
    deps="serenity-core, serenity-junit5, serenity-screenplay, serenity-screenplay-webdriver, serenity-ensure",
    structure="Test -> actor (`OnStage.theActorCalled(..)`) performs tasks (`Task.where(..)`) built from interactions, and asks questions (`Question<T>`). Locators live in `Target` holder classes.",
    idioms=[
      COMMON_ANNOTATIONS, JUNIT5,
      "Core: `Actor`, `Task`, `Question`, `Performable` and `GivenWhenThen.seeThat` are in `net.serenitybdd.screenplay`; `OnStage` and `OnlineCast` are in `net.serenitybdd.screenplay.actors`.",
      "Selenium: `BrowseTheWeb` is in `net.serenitybdd.screenplay.abilities` (there is no `screenplay.webdriver` package). `Open`, `Click`, `Enter`, `Hit` are in `net.serenitybdd.screenplay.actions`; `Target` is in `net.serenitybdd.screenplay.targets`; `Text`, `Value`, `Visibility` are in `net.serenitybdd.screenplay.questions`.",
      "`Target.the(\"name\").locatedBy(\"..\")` takes a String that Serenity reads as XPath if it compiles as XPath, otherwise CSS: `.new-todo`, `#id` and `[data-test=x]` are CSS, but tag-prefixed selectors like `input.new-todo` or `button` are read as XPath and match nothing. For those use `Target.the(\"name\").located(By.cssSelector(\"input.new-todo\"))`.",
      "`Text.ofEach(target)` is a `Question<Collection<String>>`, not `List<String>`.",
      "Ask questions with `actor.asksFor(question)` or `seeThat(question, matcher)`. Don't use the old `.viewedBy(actor)`.",
      "Web `Ensure` is `net.serenitybdd.screenplay.ensure.Ensure` (artifact `serenity-ensure`).",
      "Set the stage in `@BeforeEach`: `OnStage.setTheStage(new OnlineCast())`, with `@Managed(driver = \"chrome\") WebDriver browser;` in the test.",
    ],
    avoid="Page objects and `@Steps` libraries, Playwright classes, `@RunWith`, and Cucumber glue code.",
    docs="screenplay_idioms"),
  "sp-playwright": dict(
    name="Screenplay", driver="Playwright", runner="JUnit 5",
    deps="serenity-core, serenity-junit5, serenity-screenplay, serenity-screenplay-playwright, serenity-playwright",
    structure="Test -> actor (`OnStage.theActorCalled(..)`) performs tasks (`Task.where(..)`) built from Playwright interactions, and asks questions. Locators are Playwright selector strings.",
    idioms=[
      COMMON_ANNOTATIONS, JUNIT5,
      "Everything Playwright-specific is under `net.serenitybdd.screenplay.playwright`: `abilities.BrowseTheWebWithPlaywright`, `actors.PlaywrightCast`, `interactions.*` (`Open`, `Click`, `Enter`, `Press`, `Check`, `SelectFromOptions`, `WaitFor`), `questions.*` (`Text`, `Value`, `SelectedOptionText`, `Checked`, `Count`), `assertions.Ensure`.",
      "Set the stage with `OnStage.setTheStage(new PlaywrightCast())`. Core classes: `Actor`, `Task`, `Question`, `Performable` and `GivenWhenThen.seeThat` are in `net.serenitybdd.screenplay`; `OnStage` is in `net.serenitybdd.screenplay.actors`.",
      "Locators are Playwright selector strings such as `\".new-todo\"` or `\"text='Delete'\"`. Don't import `net.serenitybdd.screenplay.targets.Target` or `net.serenitybdd.screenplay.ensure.Ensure`.",
      "Playwright's `Text.ofEach(..)` returns `List<String>`.",
    ],
    avoid="Selenium `WebDriver`, `@Managed`, Selenium Screenplay actions, page objects and `@Steps` libraries, `@RunWith`, and Cucumber glue code.",
    docs="screenplay_idioms"),
  "sp-rest": dict(
    name="Screenplay", driver="REST (no browser)", runner="JUnit 5",
    deps="serenity-core, serenity-junit5, serenity-screenplay, serenity-screenplay-rest, serenity-rest-assured, serenity-ensure",
    structure="Test -> actor with the `CallAnApi` ability performs tasks built from `Get`/`Post`/... interactions, and asks about `LastResponse`.",
    idioms=[
      COMMON_ANNOTATIONS, JUNIT5,
      "REST: `CallAnApi` is in `net.serenitybdd.screenplay.rest.abilities`; `Get`, `Post`, `Delete`, `Ensure` are in `net.serenitybdd.screenplay.rest.interactions`; `LastResponse`, `TheResponse` are in `net.serenitybdd.screenplay.rest.questions`. There is no `net.serenitybdd.rest.abilities` package.",
      "Create the actor with `Actor.named(\"Alice\").whoCan(CallAnApi.at(\"https://...\"))`.",
      "Core: `Actor`, `Task`, `Question`, `GivenWhenThen.seeThat` are in `net.serenitybdd.screenplay`.",
    ],
    avoid="Browser/UI classes, page objects, `@RunWith`.",
    docs="screenplay_idioms"),
  "cuke-sp": dict(
    name="Cucumber with Screenplay", driver="Selenium", runner="Cucumber on the JUnit Platform",
    deps="serenity-core, serenity-junit5, serenity-screenplay, serenity-screenplay-webdriver, serenity-cucumber, serenity-ensure, io.cucumber:cucumber-java, io.cucumber:cucumber-junit-platform-engine",
    structure="Feature files (`src/test/resources/features`) -> step definitions that put actors on stage and perform Screenplay tasks and questions. Locators live in `Target` holder classes.",
    idioms=[
      COMMON_ANNOTATIONS,
      "Runner: a `@Suite` class with `@IncludeEngines(\"cucumber\")`, `@SelectClasspathResource(\"features\")`, a `GLUE_PROPERTY_NAME` parameter for the step-definition package, and `PLUGIN_PROPERTY_NAME` = `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty`. No JUnit 4 `@RunWith(CucumberWithSerenity.class)`.",
      "Step definitions: a `@Before` hook calls `OnStage.setTheStage(new OnlineCast())`; a `@ParameterType` named `actor` returns `OnStage.theActorCalled(name)`.",
      "Screenplay classes: `Actor`, `Task`, `Question`, `GivenWhenThen.seeThat` are in `net.serenitybdd.screenplay`; `OnStage`, `OnlineCast` in `net.serenitybdd.screenplay.actors`; `BrowseTheWeb` in `net.serenitybdd.screenplay.abilities`; `Open`, `Enter`, `Click` in `net.serenitybdd.screenplay.actions`; `Target` in `net.serenitybdd.screenplay.targets` (`locatedBy(String)` reads tag-prefixed CSS like `input.new-todo` as XPath: use `located(By.cssSelector(..))` for those); `Text.ofEach(..)` is a `Question<Collection<String>>`.",
    ],
    avoid="Page objects and `@Steps` libraries, Playwright classes, `@RunWith`.",
    docs="screenplay_idioms"),
  "rest-steps": dict(
    name="REST step library (no Screenplay)", driver="REST (no browser; SerenityRest)", runner="JUnit 5",
    deps="serenity-core, serenity-junit5, serenity-rest-assured, serenity-ensure",
    structure="Test (JUnit 5) -> `@Steps` step library whose `@Step` methods make REST calls with `SerenityRest` and check the response. No actors or Screenplay tasks.",
    idioms=[
      COMMON_ANNOTATIONS, JUNIT5,
      "Make calls with `net.serenitybdd.rest.SerenityRest` (`given()`, `then()`, `lastResponse()`), not `io.restassured.RestAssured`, so requests and responses appear in the report.",
      "Declare the library with `@Steps` in the test (never `new`), and put the REST calls and the response checks in `@Step(\"business wording\")` methods.",
      "JSON numbers with a decimal point come back as `Float`: `equalTo(90.0f)`, or read `lastResponse().jsonPath().getDouble(..)`; `equalTo(90.0)` and `closeTo(..)` fail.",
    ],
    avoid="Screenplay actors and `CallAnApi`, browser classes, `@RunWith`, and `net.thucydides.core.annotations.*`.",
    docs="rest_step_library"),
  "cuke-pages": dict(
    name="Cucumber with page objects and step libraries", driver="Selenium", runner="Cucumber on the JUnit Platform",
    deps="serenity-core, serenity-junit5, serenity-cucumber, io.cucumber:cucumber-java, io.cucumber:cucumber-junit-platform-engine",
    structure="Feature files (`src/test/resources/features`) -> step definitions that call `@Steps` step libraries -> page objects (`PageObject` with `@FindBy`). No Screenplay.",
    idioms=[
      COMMON_ANNOTATIONS,
      "Runner: a `@Suite` class with `@IncludeEngines(\"cucumber\")`, `@SelectClasspathResource(\"features\")`, a `GLUE_PROPERTY_NAME` parameter for the step-definition package, and `PLUGIN_PROPERTY_NAME` = `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty`. No JUnit 4 `@RunWith(CucumberWithSerenity.class)`.",
      "In step definitions declare the library with `@Steps` (never `new`, or the page object fields stay null); page objects extend `net.serenitybdd.core.pages.PageObject`.",
      "In a Cucumber expression `book(s)` is optional text: it matches \"book\" and \"books\", not the literal characters `book(s)` in a feature file. An undefined step fails the build.",
      "A String selector that starts with a tag name is read as XPath: `$(\"button\")`, `$(\"th[data-x=y]\")` and `thenFind(\"button\")` find nothing useful. Start with `#` or `.`, or use `$(By.cssSelector(..))` / `findBy(By.cssSelector(..))`.",
    ],
    avoid="Screenplay actors and tasks, Playwright classes, `@RunWith`, and `net.thucydides.core.annotations.*`.",
    docs="cucumber"),
}

TEMPLATE = """# AGENTS.md

This project's tests use Serenity BDD. Read this before writing or changing tests.

## Project style (fixed)

- Approach: **{name}**
- Driver: **{driver}**
- Runner: **{runner}**
- Serenity dependencies: {deps}

Stay in this style. All Serenity styles are valid, but this project uses this one, so don't introduce another (and don't mix Page Objects and Screenplay in one test). If you think the style should change, ask first.

Avoid: {avoid}

## Structure

{structure}

Follow the existing package layout and naming. Look at an existing test before writing a new one, and reuse the tasks, questions, page objects or step libraries already there.

## Imports and idioms (verified against Serenity 5.4.1)

{idioms}

If you need a class that isn't listed here or already used in the project, find its real package in the dependency jars (for example `unzip -l <jar> | grep ClassName`) instead of guessing.

## Commands

- Check that it compiles: `mvn -q test-compile`
- Run the tests and build the report: `mvn clean verify`
- Report: `target/site/serenity/index.html` (only built when the tests pass)
- Results, also after a failed build: read the JSON outcome files `target/site/serenity/*.json` (title, result, failure message, steps); see {docs}/ai_test_results

## Rules

- Compile after every change, and fix compile errors before doing anything else.
- Write test steps in business language (`AddATodo.called("Buy milk")`, `todo.addTodo(..)`). Keep locators out of test classes.
- The Serenity report is living documentation that business readers use. Title every scenario with the rule or outcome in plain business English ("A reader sees an order number after paying", not `testCheckout1`), word steps as the business story with no selectors, ids, URLs or status codes, and end with the business expectation.
- Group tests by function (a package or feature folder per capability, a class or feature file per feature), the way the project already does, so the report reads as a table of contents. Don't mix package structure with `@Epic`/`@Feature`/`@Story` annotations in one project.
- After writing or changing tests, run them and review them: use the `serenity-test-reviewer` agent if the project has it, otherwise check the scenario titles, step wording, grouping and assertions yourself (`{docs}/ai_living_documentation`). Fix what the review finds.
- Don't invent Serenity API. If unsure that a method exists, check the project, the docs or the jar.
- Say honestly what you ran: a test that compiles is not a test that passes.

## More detail

- Packages, common mistakes and compiling examples: {docs}/ai_{docs_id}
- Dependencies and configuration: {docs}/ai_project_setup
- Documentation index for AI tools: https://serenity-bdd.github.io/llms.txt
"""

def main():
    out = os.path.join(HERE, "variants")
    os.makedirs(out, exist_ok=True)
    for key, s in STYLES.items():
        body = TEMPLATE.format(
            name=s["name"], driver=s["driver"], runner=s["runner"], deps=s["deps"], avoid=s["avoid"],
            structure=s["structure"], idioms="\n".join("- " + i for i in s["idioms"]),
            docs=DOCS, docs_id=s["docs"])
        with open(os.path.join(out, "AGENTS.%s.md" % key), "w") as f:
            f.write(body)
        print("wrote AGENTS.%s.md" % key)

if __name__ == "__main__":
    main()
