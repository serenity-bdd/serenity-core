# Serenity Screenplay: imports and idioms cheat sheet

Verified against Serenity **5.4.1** (Playwright modules: **5.3.11**, the latest published). Every example under `../examples/` compiles and its tests pass (headless Chrome, Playwright and the live sample sites); run `check-examples.sh --run` to re-check.

Screenplay works with Selenium, Playwright and REST, with JUnit 5 or Cucumber. All of these are valid. Pick the one the user asked for or the project already uses, and don't mix them in one project.

## 1. Where things live

The packages are not guessable. The most common mistakes in the baseline come from guessing them.

### Core (all Screenplay projects), artifact `serenity-screenplay`
| Class | Package |
|---|---|
| `Actor`, `Task`, `Performable`, `Question`, `Interaction`, `Ability`, `Consequence`, `Tasks`, `RememberThat` | `net.serenitybdd.screenplay` |
| `GivenWhenThen` (`seeThat`) | `net.serenitybdd.screenplay` (use as a static import) |
| `OnStage`, `OnlineCast`, `Cast`, `Stage` | `net.serenitybdd.screenplay.actors` |
| `CastMember` | `net.serenitybdd.screenplay.annotations` |
| `Check` (conditional tasks) | `net.serenitybdd.screenplay.conditions` |
| `Wait`, `WaitUntil` | `net.serenitybdd.screenplay.waits` |

### Selenium, artifact `serenity-screenplay-webdriver`
| Class | Package |
|---|---|
| `BrowseTheWeb` | `net.serenitybdd.screenplay.abilities` |
| `Open`, `Click`, `Enter`, `Hit`, `Clear`, `Hover`, `Scroll`, `SelectFromOptions` | `net.serenitybdd.screenplay.actions` |
| `Target` | `net.serenitybdd.screenplay.targets`. `Target.the("name").locatedBy("..")` takes a **String**. Serenity treats the string as XPath if it compiles as an XPath expression, otherwise as CSS. So `.new-todo`, `#id`, `[data-test=x]` and `li label` are CSS, but a tag-prefixed selector such as `input.new-todo`, `button` or `h1` is read as XPath and finds nothing. For those use `Target.the("name").located(By.cssSelector("input.new-todo"))` (or a leading `.`/`#`, or an explicit `//input[...]`). |
| `Text`, `TextContent`, `Value`, `Attribute`, `Visibility`, `Presence`, `Enabled`, `Disabled`, `SelectedValue`, `CurrentVisibility`, `WebElementQuestion` | `net.serenitybdd.screenplay.questions` |

`Ensure` for web is in artifact `serenity-ensure`: **`net.serenitybdd.screenplay.ensure.Ensure`**.

### Playwright, artifact `serenity-screenplay-playwright`
Everything lives under `net.serenitybdd.screenplay.playwright`:

| Class | Package |
|---|---|
| `BrowseTheWebWithPlaywright` | `...playwright.abilities` |
| `PlaywrightCast` (use instead of `OnlineCast`) | `...playwright.actors` |
| `Open`, `Click`, `Enter`, `Press`, `Navigate`, `Check`, `Hover`, `WaitFor` ... | `...playwright.interactions` |
| `Text`, `Visibility`, `Count`, `Attribute` ... | `...playwright.questions` |
| `Ensure` | `...playwright.assertions` |
| `Target` | `...playwright.Target` |

Playwright locators are plain Playwright selector strings (`".new-todo"`, `"text='Delete'"`) or a Playwright `Target`.

Playwright classes that exist (all take a selector string or a `Target`):

| Kind | Classes and entry points |
|---|---|
| Interactions (`...playwright.interactions`) | `Open.url(..)`, `Navigate`, `Click.on(..)`, `DoubleClick`, `RightClick`, `Hover.over(..)`, `Enter.theValue(..).into(..)`, `Press.keys("Enter")`, `Clear`, `Focus`, `Check.the(..)`, `Uncheck.checkbox(..)`, `SelectFromOptions.byVisibleText(..)` / `.byValue(..)` / `.byIndex(..)` then `.from(selector)`, `Upload`, `Drag`, `ScrollIntoView`, `WaitFor.selector(..)` |
| Questions (`...playwright.questions`) | `Text.of(..)`, `Text.ofEach(..)` (`List<String>`), `Value.of(..)`, `SelectedOptionText.of(..)`, `Checked.of(..)`, `Visibility.of(..)`, `Presence`, `Absence`, `Count.of(..)`, `Attribute`, `Enabled`, `Disabled`, `AllOptionTexts` |
| Assertions (`...playwright.assertions.Ensure`) | `Ensure.that(selector).isVisible() / isHidden() / hasText(..) / containsText(..) / hasValue(..) / isChecked() / hasCount(n) / hasAttribute(n, v)` |

**Don't use `Ensure.thatTheCurrentUrl()` or `Ensure.thatThePageTitle()`: they return package-private classes, and any call on them fails to compile** ("UrlEnsure.contains(java.lang.String) is defined in an inaccessible class or interface"; run in `../../playwright-checks/verify.py`). To check the URL, assert on the page: `assertThat(BrowseTheWebWithPlaywright.as(actor).getCurrentPage().url()).contains("/catalog")` (after `attemptsTo(..)`, in the test body), or better, check something the user sees on the new page (`Ensure.that("h1").hasText("Catalog")`).

**A selector must match exactly one element** for `WaitFor.selector`, `Click.on`, `Enter`, `Ensure.that(..).hasText(..)` and `Text.of(..)` (Playwright's strict mode). A selector that matches a list (`"#books .book"` with five rows) fails at run time with `strict mode violation: ... resolved to 5 elements`. For a list: wait on the first row (`"#books .book >> nth=0"`), count with `Ensure.that("#books .book").hasCount(5)`, read all texts with `Text.ofEach(..)`, pick one with `">> nth=0"` or `">> nth=-1"` (last), or narrow with `has-text`/`text-is` (`"tr.book:has(td.title:text-is('Refactoring'))"`). `../examples/sp-playwright/todo/ListTest.java` runs these.

**Don't build a Playwright text pattern with `Pattern.quote(..)`.** Playwright sends the pattern to the browser's JavaScript engine, which doesn't understand Java's `\Q..\E`, so `setHasText(Pattern.compile("^\\s*" + Pattern.quote(title) + "\\s*$"))` matches nothing and the step times out after 30 seconds (two Opus runs). Use the plain string (`setHasText(title)` matches by substring, ignoring case; `text-is('..')` / `has-text('..')` in a selector string match exactly or by substring), or write the pattern by hand (`Pattern.compile("^\\s*Clean Code\\s*$")`). Run in `../../playwright-checks/verify.py`.

In a Playwright selector string a role needs the prefix: `"role=heading[name=\"Sign in\"]"`; without `role=` it throws `InvalidSelectorError` (run in `../../playwright-checks/verify.py`).

Playwright has no `net.serenitybdd.screenplay.ensure.Ensure` and no `Target` from `net.serenitybdd.screenplay.targets`: use the Playwright ones. See `../examples/sp-playwright/todo/FormTest.java`.

### REST, artifact `serenity-screenplay-rest`
| Class | Package |
|---|---|
| `CallAnApi` | `net.serenitybdd.screenplay.rest.abilities` |
| `Get`, `Post`, `Delete`, `Ensure` ... | `net.serenitybdd.screenplay.rest.interactions` |
| `LastResponse`, `TheResponse` | `net.serenitybdd.screenplay.rest.questions` |

### Shared across all styles
| Class | Package |
|---|---|
| `@Managed`, `@Step`, `@Steps`, `@DefaultUrl`, `@WithTag` ... | `net.serenitybdd.annotations` (never `net.thucydides.core.annotations`) |
| `SerenityJUnit5Extension` | `net.serenitybdd.junit5` |
| `ScenarioSteps` (Page Objects) | `net.thucydides.core.steps` (**this one is correct** under `net.thucydides`) |

## 2. Mistakes models make (from the baseline)

| Wrong | Right | Seen in |
|---|---|---|
| `import net.serenitybdd.rest.abilities.*` or `net.serenitybdd.rest.questions.*` for Screenplay REST (those packages don't exist) | `net.serenitybdd.screenplay.rest.abilities.CallAnApi`, `...rest.questions.LastResponse` | 3 held-out runs |
| `Target.the("x").locatedBy(By.id("x"))` | `locatedBy("#x")`, a String; for a `By` use `located(By.id("x"))` | 1 |
| `locatedBy("input.new-todo")` (tag-prefixed CSS; read as XPath, element not found at run time) | `located(By.cssSelector("input.new-todo"))` or `locatedBy(".new-todo")` | run-time failure, 5 of 66 unguided agentic runs |
| Two `public` classes in one file, or a class name different from its file name | One public class per file, and the file name equals the class name | 1 |
| `import net.serenitybdd.screenplay.webdriver.BrowseTheWeb` (package doesn't exist) | `net.serenitybdd.screenplay.abilities.BrowseTheWeb` | 8 of 31 failed runs |
| `Question<List<String>>` returned from `Text.ofEach(target)` (Selenium) | `Question<Collection<String>>`. Playwright's `Text.ofEach` returns `List<String>` | 8 |
| `.viewedBy(actor)` on `Text`/`Value`/`Visibility`... | `actor.asksFor(Text.of(target))` or `Text.of(target).answeredBy(actor)`. `.viewedBy` is the old API | 5 |
| `net.thucydides.core.annotations.DefaultUrl` (and `Step`, `Managed`) | `net.serenitybdd.annotations.*` | 4 |
| JUnit 4 `@RunWith(SerenityRunner.class)` in a JUnit 5 project | `@ExtendWith(SerenityJUnit5Extension.class)` | 3 |
| Moving `ScenarioSteps` to `net.serenitybdd...` | `net.thucydides.core.steps.ScenarioSteps` | 2 |
| `OnlineCast` in the wrong package | `net.serenitybdd.screenplay.actors.OnlineCast` | 2 |
| `@Page` annotation | Doesn't exist. In Page Object tests use `@Steps` for step libraries and extend `PageObject` for pages | 2 |
| `Instrumented` as a class | `Tasks.instrumented(MyTask.class, args)` (static method), or prefer `Task.where(..)` | 1 |
| `Ensure.thatTheCurrentUrl().contains(..)` or `Ensure.thatThePageTitle()` (Playwright) | Doesn't compile (package-private return types). Assert on `BrowseTheWebWithPlaywright.as(actor).getCurrentPage().url()` or on visible page content | 6 of the 8 guided Playwright compile failures inspected |
| `WaitFor.selector("#books .book")` or `Ensure.that("#books .book").hasText(..)` where the selector matches several rows | `"#books .book >> nth=0"`, or `hasCount(n)`, or `Text.ofEach(..)` | strict mode violation at run time, 2 guided runs |
| `Navigate` from a Selenium package | Selenium: `Open.url(..)`. `Navigate` exists only in Playwright | 1 |

## 3. Minimal working patterns

The source files are under `../examples/` and are compile-checked. Names are the same across styles so you can compare them.

| Style | Files | Key points |
|---|---|---|
| Screenplay + Selenium + JUnit 5 | `../examples/sp-selenium/todo/*` | `@ExtendWith(SerenityJUnit5Extension.class)`, `@Managed(driver="chrome") WebDriver`, `OnStage.setTheStage(new OnlineCast())`, `OnStage.theActorCalled("Toby")`, `Task.where(..)`, `Target.the(..).locatedBy(..)`, `seeThat(question, matcher)` or `Ensure.that(..)` |
| Screenplay + Playwright + JUnit 5 | `../examples/sp-playwright/todo/*` (todo list, form with select and checkbox) | `new PlaywrightCast()`, string selectors, `Press.keys("Enter")`, `Ensure.that(selector).hasText(..)` |
| Screenplay + REST + JUnit 5 | `../examples/sp-rest/api/*` | `Actor.named(..).whoCan(CallAnApi.at(baseUrl))`, `Get.resource(..)`, `Post.to(..).with(request -> ...)`, `LastResponse.received()` |
| Cucumber + Screenplay | `../examples/cuke-sp/*` | `@Suite` + `@IncludeEngines("cucumber")` + `@SelectClasspathResource("features")`, plugin `net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel`, a `{actor}` `@ParameterType`, `@Before` that sets the stage |

### Rules of thumb
- Use `Task.where("{0} does something", step1, step2)` for tasks. Use a class with `performAs` only when the task needs real logic.
- `actor.attemptsTo(..)` for actions. `actor.should(seeThat(..))` or `Ensure` for checks.
- Keep locators in a `Target` holder class (Selenium) or constants (Playwright).
- Build tasks and questions with a static factory (`AddATodo.called("Buy milk")`) so the test reads like a sentence.

## 4. Not yet verified
- `Ensure` for web has several overloads. Only `that(Target).hasText(..)` is compile-tested.
- Page Objects (Selenium and Playwright) are covered in `page-objects.md`, not here.
- The examples were run once against the live sample sites with Serenity 5.4.1 (Playwright modules 5.3.11). Other sites and larger suites may behave differently.
- `serenity-screenplay-playwright` is documented at 5.3.11, not 5.4.x.
