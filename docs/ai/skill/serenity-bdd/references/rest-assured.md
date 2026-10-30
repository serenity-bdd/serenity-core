# REST testing with SerenityRest and a step library (no Screenplay)

Verified against Serenity **5.4.1**. The example `../examples/rest-steps/api/*` compiles and its tests pass (it starts its own HTTP server, so it needs no network); `../../rest-checks/verify.py` runs every claim below.

This is one of several valid ways to test a REST API with Serenity. The other is Screenplay with `CallAnApi` (see `screenplay.md` and `../examples/sp-rest/`). Use the one the user asked for or the project already uses; don't mix them.

## What you need

Artifact `serenity-rest-assured` (it brings REST-assured and Hamcrest), plus `serenity-core` and `serenity-junit5` as in `project-setup.md`. Do not pin REST-assured or Groovy versions (see `troubleshooting.md`).

## Where things live

| Class | Package |
|---|---|
| `SerenityRest` (`given()`, `then()`, `lastResponse()`, `reset()`) | `net.serenitybdd.rest` |
| `@Step` | `net.serenitybdd.annotations` |
| `@Steps` | `net.serenitybdd.annotations` |
| `SerenityJUnit5Extension` | `net.serenitybdd.junit5` |
| `ScenarioSteps` (optional base class, not needed) | `net.thucydides.core.steps` |

## The pattern

1. **A step library**: a plain class whose methods are annotated `@Step("business wording")`. The REST calls go inside the methods, using `SerenityRest.given()...when().get(..)`. Use `SerenityRest`, not `io.restassured.RestAssured`: it is the same API, but Serenity records every request and response in the report.
2. **The test** declares `@Steps BookshopApi bookshop;` (Serenity creates the library) and reads like a story: `bookshop.listTheBooks(); bookshop.responseShouldHaveStatus(200);`.
3. **Checks go in `@Step` methods too**, using `SerenityRest.then().statusCode(..)` and `.body("path", matcher)`, so the report shows "The response should have status 200".

`SerenityRest.then()` checks the last response of the current thread. The base URI is not remembered between calls unless you keep it in the step library (as the example does) or set `SerenityRest`/`RestAssured.baseURI`.

## Mistakes models make

| Wrong | Right |
|---|---|
| `import net.thucydides.core.annotations.Step` or `...annotations.Steps` (compile error: cannot find symbol) | `net.serenitybdd.annotations.Step`, `net.serenitybdd.annotations.Steps` |
| `import io.restassured.RestAssured.given` for calls that should appear in the report | `net.serenitybdd.rest.SerenityRest.given` |
| Creating the library with `new BookshopApi()` in the test | `@Steps BookshopApi bookshop;`. With `new`, the business steps vanish from the report and only raw `GET http://...` lines remain. |
| `@Step` missing on a method | The method still runs but the report shows only the raw `GET http://...` line instead of your wording. |
| `body("total", equalTo(90.0))` or `closeTo(90.0, 0.001)` | JSON numbers with a decimal point come back as `Float`, so a `Double` matcher fails (`Expected: <90.0>`, `Expected: a numeric value within <0.001> of <90.0>`). Use `equalTo(90.0f)`, or read the value as a double: `SerenityRest.lastResponse().jsonPath().getDouble("total")` and assert on that (AssertJ). |
| Extending `ScenarioSteps` because old guides did | Not needed. A plain class works. If you do extend it, the package is `net.thucydides.core.steps.ScenarioSteps`. |

## Wording for the living documentation

Name steps for what a person does or sees, not for the HTTP call: "Look up book number 2", "The response should show the title Clean Code", not "GET /api/books/2" or "Check JSON path title". Give each `@Test` a `@DisplayName` that states the business outcome, and the class a `@DisplayName` naming the feature (see `living-documentation.md`).

## Examples

| Style | Files |
|---|---|
| REST + step library + JUnit 5 | `../examples/rest-steps/api/*` |
