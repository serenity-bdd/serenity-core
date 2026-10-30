# Tests as living documentation

Verified against Serenity **5.4.1**: `../../living-checks/verify.py` builds the example in `../examples/living-docs` and `../examples/living-docs-annotated` (and the Cucumber example) and reads the report Serenity produces. This page applies to every style (Page Objects, Screenplay, Cucumber).

## The idea

A Serenity report is read by people who will never open the code: product owners, analysts, new team members, auditors. Its Requirements tab is a description of what the application does, built from the tests. So every scenario has two jobs: check the application, and tell a reader **what the business expects and why it matters**. A test that only passes is half done.

Write for that reader:

- The **scenario title** says the business rule or outcome, as a sentence a user would recognise.
- The **steps** read as a story in business language: who does what, in what situation, and what they should see.
- The **tests are grouped by what the application does** (ordering, searching, signing in), so the report is a navigable table of contents.

## 1. Scenario titles: the rule, not the mechanics

| Poor | Good |
|---|---|
| `testCheckout1` | `A reader sees an order number after paying` |
| `loginTest` | `Signing in with a wrong password is refused with a clear message` |
| `shouldReturn201` | `Placing an order confirms it with an order number` |
| `searchTest` | `Searching by title finds only matching books` |

- **JUnit 5:** use `@DisplayName("...")` on the method; the report title is exactly that (verified). Without it, Serenity turns the method name into words (`shouldKeepEveryBookAddedToTheBasket` becomes "Should keep every book added to the basket"), so a method name written as a sentence is also fine. A `test` prefix or a number is not.
- **Cucumber:** the `Scenario:` name is the title; write it as a sentence. Say the rule being shown, not the clicks.
- Name what the user gets or what the rule is. Avoid HTTP codes, element ids, class names and the word "test" in titles.

## 2. Steps: business language, one level above the mechanics

The report lists the steps of each scenario. Make them read like the scenario narrated.

- **Screenplay:** give every `Task` a sentence with `{0}` for the actor, and put the important data in it (verified):

  ```java
  Task.where("{0} adds '" + title + "' to the basket", ...)
  // report: Ada adds 'Refactoring' to the basket
  ```

  Tasks are business actions (`SignIn.withCredentials`, `Checkout.as`). Interactions (`Click`, `Enter`) stay inside tasks, so the report shows the business level first. Questions and `seeThat` checks read as expectations: `seeThat("the order confirmation", TheConfirmation.text(), containsString("ORD-"))`.
- **Page Objects:** annotate step-library methods with `@Step("Search the catalog for '{0}'")` (verified; `{0}` is the first parameter). Put the business verb in the step method (`shopper.searchesFor(term)`), not in the test (`driver.findElement(..)`).
- **Cucumber:** the Gherkin is the documentation. Use Given/When/Then for context, action, outcome. Keep each step about the business ("When Ada places the order"), not the UI ("When I click the button with id place-order"). Use a `Background` for context shared by all scenarios, and a `Scenario Outline` with an `Examples` table for the same rule over several values, with a meaningful column per input.
- Do not put CSS selectors, XPath, URLs, element ids, sleeps or HTTP status codes in step text. They belong inside the page object, task or step definition.
- Serenity writes the lines under a task itself: `Open.url(..)` shows as "opens the browser at http://..." and `Enter`/`Click` show the element names. You can't remove them, so keep URLs in one place and give `Target`s business names (`Target.the("email field")`). The task line above them is what the reader reads first.
- The last step of a scenario states the business expectation ("the confirmation shows an order number"), not a technical detail.

## 3. Organise by function

The report's Requirements tab is a tree. Serenity builds it from how you organise the tests:

| Style | How the tree is built | Verified result |
|---|---|---|
| JUnit 5 (any style), no annotations | the **package** path of the test class | `shop.ordering.WhenAReaderBuildsABasketTest` gives `Shop (capability)` > `Ordering (feature)` > `When a reader builds a basket test (feature)` |
| JUnit 5, annotations (recent releases; see the "Annotation-Based Requirements" docs page) | `@Epic` > `@Feature` > `@Story` on the test class | `@Epic("Buying books") @Feature("Paying for an order") @Story("Pay by card")` gives `Buying books (capability)` > `Paying for an order (feature)` > `Pay by card (story)` |
| Cucumber | the **folders** under `src/test/resources/features`, and the feature file | `features/shopping/basket.feature` gives `Shopping (capability)` > `Shopping basket (feature)` |

Rules that follow:

1. **Group by capability, then feature.** For example `ordering`, `browsing`, `accounts` as packages (or folders), with one test class (or feature file) per feature inside, each holding the scenarios of that feature. Not one class called `ShopTest` with everything in it, and not one package per technical layer.
2. **Prefer one way for the whole project.** If package-based and annotated classes are mixed, the Requirements page shows both as separate top-level trees side by side (verified), and each class is found in only one of them, so the project reads as two organisations. Follow what is already there.
3. **Class names end up in the tree.** With packages, the class name becomes the feature name, `Test` suffix included (`WhenSearchingTheCatalogTest` is shown as "When searching the catalog test", seen in a real report). Put `@DisplayName("Searching the catalog")` on the class: the feature then shows that text (verified). With annotations, the `@Feature` and `@Story` text is what the reader sees. Write these as business phrases.
4. **Cucumber:** one feature file per feature, in a folder per capability; the text under `Feature:` (a paragraph of business context before the first scenario) is shown on the feature page (verified).

## 4. Descriptions of the areas

- **Cucumber:** a `readme.md` in a features folder is the description of that capability. Its first line should be the name (`## Shopping`). Verified: it appears in the report.
- **JUnit 5:** put `readme.md` files under `src/test/resources/stories/<package path>/` (for the example `src/test/resources/stories/shop/ordering/readme.md`); the text appears in the report. The same file in `src/test/resources/shop/ordering/` was **ignored** (verified).
- **`@Narrative(text = {...})` did not appear in the JUnit 5 report** in 5.4.1 (verified, both on the class and in `package-info.java`). Don't rely on it; use the `readme.md` files.

Keep a description to a few sentences: what the area is for, and the rules that matter.

## 5. What a good scenario looks like

The shape only: `SignIn`, `Checkout`, `shopper` and the rest are the project's own classes.

Page Objects + step library:

```java
@Test
@DisplayName("Searching by title finds only matching books")
void searchingByTitle() {
    shopper.searchesFor("Refactoring");
    shopper.shouldSeeOnlyBooksCalled("Refactoring");
}
```

Screenplay:

```java
@Test
@DisplayName("A reader sees an order number after paying")
void orderNumber() {
    ada.attemptsTo(
        SignIn.withCredentials(reader),
        AddToBasket.theBook("The Pragmatic Programmer"),
        Checkout.as("Ada Lovelace").inCountry("Germany"));
    ada.should(seeThat("the order confirmation", TheConfirmation.text(), matchesPattern("Thank you! Order ORD-\\d+")));
}
```

Cucumber:

```gherkin
Feature: Placing an order

  Readers pay for the books in their basket and get an order number back.

  Scenario: A reader sees an order number after paying
    Given Ada has "The Pragmatic Programmer" in her basket
    When she checks out and accepts the terms
    Then she sees an order number like ORD-1001
```

## 6. Checking it

After the tests run, the report is what the reader sees. Run `scripts/outline.py <project>/target/site/serenity` (from the skill directory): it prints the requirements tree, the scenario titles and the steps of each scenario, and flags the mechanical problems (titles that look like method names, selectors or ids in step text, ungrouped tests). Read its output as the reader would: could someone who has never seen the code say what the application does? The `serenity-test-reviewer` agent does this check and the judgement part (`review-checklist.md`).

## 7. Not covered

Pending and manual scenarios, tags and `@WithTag`, Serenity's custom requirement types (`serenity.requirement.types`), the Jira requirements provider, `@Title`, and the exact look of the HTML report.
