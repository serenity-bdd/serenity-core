# Serenity Page Objects: imports and idioms cheat sheet

Verified against Serenity **5.4.1** (Playwright module: **5.3.11**, the latest published). The examples under `../examples/po-selenium` and `../examples/po-playwright` compile and their tests pass (headless Chrome and Playwright against the live todo app); run `check-examples.sh --run` to re-check.

Page Objects with step libraries work with Selenium and with Playwright, with JUnit 5. This is as valid as Screenplay. Use the style the user asked for or the project already uses, and don't mix Page Objects and Screenplay in one test.

## 1. Structure

```
Test (@Steps field)  ->  Step library (@Step methods)  ->  Page Object (locators and page actions)
```

Tests call step methods that read like business actions. Page Objects hide locators.

## 2. Where things live

| Class | Package |
|---|---|
| `@Step`, `@Steps`, `@Managed`, `@DefaultUrl`, `@WhenPageOpens`, `@WithTag`, `@Title`, `@Narrative`, `@Pending` | `net.serenitybdd.annotations` (**never** `net.thucydides.core.annotations`) |
| `SerenityJUnit5Extension` | `net.serenitybdd.junit5` |
| `PageObject`, `WebElementFacade` | `net.serenitybdd.core.pages` |
| `UIInteractionSteps` (base class for Selenium step libraries) | `net.serenitybdd.core.steps` |
| `Serenity` (static facade) | `net.serenitybdd.core.Serenity` |
| `ScenarioSteps` (older step library base class, still valid) | `net.thucydides.core.steps` |
| `Pages` | `net.thucydides.core.pages` |
| `@FindBy` | `org.openqa.selenium.support.FindBy` (Serenity also has `net.serenitybdd.core.annotations.findby.FindBy`) |

`net.thucydides.core.pages.WebElementFacade` and `net.thucydides.core.annotations.findby.FindBy` are deprecated aliases. Use the `net.serenitybdd` ones.

Maven: Selenium needs `serenity-core` and `serenity-junit5`. Playwright adds `serenity-playwright` and `com.microsoft.playwright:playwright`.

## 3. Selenium Page Objects

Files: `../examples/po-selenium/todo/{TodoPage,TodoSteps,AddTodoTest}.java`

- Page: `@DefaultUrl("https://...")`, `extends PageObject`, `@FindBy(css = "...") WebElementFacade field;`, or `$("css")`, `$$("css")` for lookups. **A String that starts with a tag name is not read as CSS.** `$("button")`, `$("th[data-sort='title']")` and `thenFind("button")` compile as XPath, so they look for the wrong thing and fail with `ElementShouldBeEnabledException` or `NoSuchElementException ... xpath`. Start the selector with `#` or `.` (`$("#go")`, `$(".sort")`), or use `$(By.cssSelector("button"))` (for a child element `row.findBy(By.cssSelector("button"))`; `thenFind` has no `By` version), or write a real XPath. The same rule applies to Screenplay `Target.the("name").locatedBy(..)`.
- **Don't write your own `open()`.** `PageObject.open()` already exists and opens the `@DefaultUrl`; it is `final`, so redeclaring it fails to compile. For another URL use `openAt("https://...")`, or a differently named method.
- `@FindBy(css = ...)` takes a String. `$(By.cssSelector(..))` takes a `By`. Don't mix them up.
- Step library: `extends UIInteractionSteps`, a field for each page object (injected automatically), and `@Step("Add the todo '{0}'")` on public methods. `{0}` is the first argument.
- Test: `@ExtendWith(SerenityJUnit5Extension.class)`, `@Managed(driver = "chrome") WebDriver driver;`, `@Steps TodoSteps todo;`. Assertions in the test with AssertJ or Hamcrest, or with `assertThat` inside a `@Step` method.
- Useful `WebElementFacade` methods: `type(..)`, `typeAndEnter(..)`, `click()`, `getText()`, `isVisible()`, `waitUntilVisible()`, `selectByVisibleText(..)`, `getValue()`.

## 4. Playwright Page Objects

Files: `../examples/po-playwright/todo/{TodoPage,TodoSteps,AddTodoTest}.java`

- The Page Object is a plain class that holds a `com.microsoft.playwright.Page`. It does **not** extend `PageObject`, and doesn't use `@FindBy`, `WebElementFacade` or `@DefaultUrl`. Locators are Playwright `page.locator("css")`, `getByRole(..)`.
- A locator used with `click()`, `fill()`, `textContent()` or `waitFor()` must match exactly one element (strict mode); for lists use `.first()`, `.nth(i)`, `.count()` or `allTextContents()`. **Don't build a Playwright text pattern with `Pattern.quote(..)`.** Playwright sends the pattern to the browser's JavaScript engine, which doesn't understand Java's `\Q..\E`, so `setHasText(Pattern.compile("^\\s*" + Pattern.quote(title) + "\\s*$"))` matches nothing and the step times out after 30 seconds (two Opus runs). Use the plain string (`setHasText(title)` matches by substring, ignoring case; `text-is('..')` / `has-text('..')` in a selector string match exactly or by substring), or write the pattern by hand (`Pattern.compile("^\\s*Clean Code\\s*$")`). Run in `../../playwright-checks/verify.py`.
- Playwright Java API details models get wrong (run in `../../playwright-checks/verify.py`): `page.getByRole(role, new Page.GetByRoleOptions()...)` but `locator.getByRole(role, new Locator.GetByRoleOptions()...)`: passing the `Page` options to a locator is a compile error; and as a selector string a role needs the prefix, `"role=heading[name=\"Sign in\"]"`: `"heading[name=...]"` throws `InvalidSelectorError`.
- The test owns the browser: start `Playwright` and the `Browser` in `@BeforeAll`, and in `@BeforeEach` create a page and call `PlaywrightSerenity.registerPage(page)`. In `@AfterEach` call `PlaywrightSerenity.unregisterPage(page)` and close the page.
- Step library: a plain class with `@Step` methods that get the page from `PlaywrightSerenity.getCurrentPage()` (or take a `Page` parameter) and wrap it in the Page Object. No `UIInteractionSteps`.
- `PlaywrightSerenity` is in `net.serenitybdd.playwright`. A `@SerenityPlaywright` meta-annotation (`net.serenitybdd.playwright.junit5`) combines the extensions. See `serenity-playwright-demo` for it.

## 5. Mistakes models make (from the baseline)

| Wrong | Right |
|---|---|
| `import net.thucydides.core.annotations.{Step,Steps,Managed,DefaultUrl}` (most Page Object failures) | `net.serenitybdd.annotations.*` |
| Declaring `public void open()` in a page object | Delete it. Use the inherited `open()` with `@DefaultUrl`, or `openAt(url)` |
| `@FindBy("...")` or passing a String where a `By` is needed (and the reverse) | `@FindBy(css = "...")`; `$("css")` for Strings; `$(By.id("x"))` for a `By` |
| `$("button")`, `$("th[data-sort='title']")`, `row.thenFind("button")` (read as XPath) | `$("#go")`, `$(By.cssSelector("button"))`, `row.findBy(By.cssSelector("button"))` |
| JUnit 4 `@RunWith(SerenityRunner.class)` | `@ExtendWith(SerenityJUnit5Extension.class)` |
| `@Page` annotation | Doesn't exist. Use a page object field in the step library, or `@Steps` for step libraries in the test |
| `ScenarioSteps` from `net.serenitybdd...` | `net.thucydides.core.steps.ScenarioSteps` (this one *is* under `net.thucydides`) |
| Extending `PageObject` for a Playwright page, or `Page` from Selenium | Playwright pages are plain classes using `com.microsoft.playwright.Page` |

## 6. Not yet verified
- Runtime behaviour beyond the examples: they ran once against the live todo app.
- `@WhenPageOpens`, `waitFor`/`waitUntil*` helpers and `Pages`-based injection are not covered by examples.
- The Playwright sheet is based on 5.3.11 and the `serenity-playwright-demo` module.
