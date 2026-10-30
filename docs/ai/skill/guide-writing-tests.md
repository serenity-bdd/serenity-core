---
id: ai_writing_tests
title: Writing Serenity BDD Tests with an AI Assistant
sidebar_position: 1.5
description: A practical workflow for asking an AI coding assistant for Serenity BDD tests, checking what it wrote, and reviewing the report, with one real example from prompt to result.
---

# Writing Serenity BDD Tests with an AI Assistant

This page is a workflow for getting good Serenity tests from an AI coding assistant, and for checking them. It works with any assistant. Where it mentions the agent skill, that is the Claude Code skill described in [Using Serenity BDD with AI Coding Assistants](ai_using_assistants).

The short version: **say which style you want, make the assistant compile and run what it writes, and read the report yourself.** An assistant that has not been told the style will pick one, and a test that compiles and passes can still check the wrong thing.

## 1. Set up once

Do whichever of these you can; each helps on its own.

- Put an `AGENTS.md` for your style in the project root (and a copy named `CLAUDE.md` for Claude Code). It records the style, driver and runner, so every later request stays in them. See [Installing](ai_using_assistants#installing).
- Or install the agent skill, or point the assistant at [llms.txt](https://serenity-bdd.github.io/llms.txt) or at the page for your style under [Idioms and Imports by Style](ai_idioms_overview).
- Make sure the project already builds and has a working Serenity setup ([Project Setup](ai_project_setup)). An assistant extends a working project far better than it creates one from nothing.

## 2. Write the request

A good request names five things. If one is missing, a well-configured assistant asks you; a poorly configured one guesses.

| Say | Why | Example |
|---|---|---|
| The style | Serenity supports several, all current; the assistant must not mix them | "Serenity Screenplay", "Page Objects with a step library", "Cucumber with page objects" |
| The driver | Selenium and Playwright use different packages and assertions | "with Selenium", "with Playwright", "REST only" |
| The runner | JUnit 5 and Cucumber are set up differently | "JUnit 5", "Cucumber on the JUnit Platform" |
| The behaviour, in business terms | The scenario title and step wording become the report | "a reader signs in with a wrong password and sees 'Invalid email or password'" |
| The expected values, exactly | The assertion should check the specific value, not that something is shown | "the total is $80.75", "an order number of the form ORD- followed by digits" |

Also useful: how the application is found (base URL, how to start it), the accounts or test data to use, and anything it must not touch.

If you are not sure which style to choose, say so. The skill and the `AGENTS.md` files are written to make the assistant list the options and ask, not to choose for you.

## 3. Make it compile and run

Ask the assistant to run the build itself and fix what fails:

```
mvn test-compile        # catches wrong package names and methods quickly
mvn verify              # runs the tests and builds the report
```

Add `-Dheadless.mode=true` to run a browser test without opening a window. Compile errors from wrong imports are the most common fault in generated Serenity code, and the second pass fixes them. An assistant that can run Maven usually gets there; one that cannot (a chat window) will often need you to paste back the error.

If the build fails, there is no HTML report, but the result of every test is still in `target/site/serenity/*.json`; ask the assistant to read it (see [Reading Test Results Without the HTML Report](ai_test_results)). Do not stop at "it compiles". A test that passes on the application as it is has proved little until you know it can fail.

## 4. Check that the test can fail

Break the application on purpose, or point the test at a state where the behaviour is wrong, and run it again. If it still passes, it is not checking what you asked for. This is the check the evaluation used: a generated test counted as good only if it passed on the healthy application and failed on the application with the matching defect.

Common causes of a test that cannot fail:

- the assertion is on a page title or heading that is present either way;
- it checks that an element exists, not what it says;
- it asserts the right value against the wrong element;
- it waits with a sleep and checks nothing afterwards.

## 5. Read the report

Open `target/site/serenity/index.html` after `mvn verify`. Read it as someone who has not seen the code:

- Do the scenario titles say what the business behaviour is?
- Do the step names read as a story ("Reader adds 'Clean Code' to the basket"), with no selectors, ids, URLs or status codes?
- Are the tests grouped by function (packages, or `@Narrative` and readme files), so the requirements view has a sensible shape?
- Does each test end with the expectation you asked for?

The [review checklist](ai_review_checklist) has the full list, and [Living Documentation](ai_living_documentation) explains how titles and structure become the report. In Claude Code, the test-review agent applies the same checklist to the new tests and the report.

## 6. Ask for changes, one at a time

When something is wrong, say what you saw in the report and what you want instead ("the step says `Click.on(#add)`; it should say what the reader does"). Keep to one style and one driver as you iterate.

## A worked example

This is a real run, not a mock-up. Claude Opus was asked, through Claude Code with the skill installed, to write a test against a small online bookshop that the authors keep for evaluation. The application has a catalog, a basket and a checkout. The prompt (shortened here) named the style, the driver and the runner, the behaviour, and the exact expected value:

> Using Serenity BDD Screenplay with Selenium and JUnit 5, write a test where a reader signs in, adds The Pragmatic Programmer, checks out as Ada Lovelace at '1 Main St' in Germany with the terms accepted, and checks that the confirmation shows an order number of the form ORD- followed by digits.

It was also given a description of the pages (element ids and labels), as you would give for your own application.

The assistant wrote the test and its tasks, ran `mvn verify -Dheadless.mode=true`, and reported one test, no failures. The test:

```java
@ExtendWith(SerenityJUnit5Extension.class)
class PlaceOrderTest {

    @Managed(driver = "chrome")
    WebDriver browser;

    @BeforeEach
    void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Test
    void readerCanOrderABookAndSeeTheOrderNumber() {
        Actor reader = OnStage.theActorCalled("Reader");

        reader.attemptsTo(
                SignIn.withCredentials("reader@example.com", "correct-horse"),
                AddToBasket.theBook("The Pragmatic Programmer"),
                Checkout.as("Ada Lovelace", "1 Main St", "Germany"),
                WaitUntil.the(CheckoutPage.CONFIRMATION, isVisible()));

        reader.should(seeThat("the order confirmation",
                Text.of(CheckoutPage.CONFIRMATION), matchesPattern(".*\\bORD-\\d+\\b.*")));
    }
}
```

and one of the tasks it created, with the business wording in the step title:

```java
public class AddToBasket {
    public static Performable theBook(String title) {
        return Task.where("{0} adds '" + title + "' to the basket",
                Click.on(CatalogPage.ADD_TO_BASKET_FOR.of(title)));
    }
}
```

**Step 4, can it fail?** The evaluation then ran the same test against a copy of the bookshop in which the confirmation no longer contains an order number. The test failed, with the message `Expected: an element containing 'ORD-' but: ... [Thank you! Order]`. So the assertion does check the order number, and the test was counted as good.

**Step 5, a review.** Things a reader would still want to change: the sign-in task opens a hard-coded `http://localhost:8765/login`, which belongs in `webdriver.base.url` in `serenity.conf` (see [Configuration](ai_configuration)); and the scenario title is a method name, `readerCanOrderABookAndSeeTheOrderNumber`, which the report turns into a readable title, but you may prefer a more specific one. These are the kind of follow-ups to ask for in step 6. Passing the evaluation did not mean there was nothing to improve.

## What to expect

In the evaluation (Claude models, a small test application; full figures on [Using Serenity BDD with AI Coding Assistants](ai_using_assistants#what-was-measured)):

- Written in one go, without the reference pages, a large share of tests failed: usually an import that does not exist, or a method from a different style. With the pages or the skill, most compiled and passed.
- With tools, so the assistant can run Maven and fix its own errors, nearly every test ended up good with or without the materials. What the materials changed was the number of Maven runs, about half.
- Passing is not the same as good: the checks above (can it fail, does the report read well) are still yours to do.

## Common mistakes to watch for

| In the generated code | What it means |
|---|---|
| Imports from `net.serenitybdd.screenplay.*` in a Page Objects project, or `@Steps` in a Screenplay project | Two styles mixed; ask for one |
| `Thread.sleep(...)` | A fixed wait; Serenity waits for elements itself, or use `WaitUntil` (Screenplay) |
| Selectors, ids or URLs in step titles | The report will read as a script, not a story |
| A selector like `$("button")` or `Target.the("x").locatedBy("input.x")` | A string that starts with a tag name is read as XPath, not CSS, so the element is not found at run time. Use `#id`, `.class` or `By.cssSelector(...)`; see [Page Objects](ai_page_object_idioms) and [Screenplay](ai_screenplay_idioms) |
| A numeric REST assertion like `equalTo(90.0)` that fails | REST-assured returns JSON decimals as `Float`; see [REST](ai_rest_step_library) |
| `@Managed WebDriver` in a step library or other helper class | It is filled in only in the test class, so the field is null elsewhere; extend `UIInteractionSteps` and use `getDriver()`, or use a page object. See [Troubleshooting](ai_troubleshooting) |
| A Cucumber step that is undefined or shown as pending | The step definition does not match the step text, and an undefined step fails the build; check the Cucumber expression. See [Cucumber](ai_cucumber) |
| An import such as `net.thucydides.core.annotations.Step` that does not compile | The annotations moved to `net.serenitybdd.annotations`; see [Upgrading](ai_upgrading). Many other `net.thucydides` packages are still current core packages, so judge each import against the pages |

The [troubleshooting page](ai_troubleshooting) lists build, report and browser errors by the message you see, with a fix that was reproduced.
