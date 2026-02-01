# Serenity Screenplay with Playwright

This module provides a [Screenplay pattern](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals) implementation for browser automation using [Playwright](https://playwright.dev/java/).

The Screenplay pattern offers a more expressive, maintainable approach to test automation by modeling tests around actors, abilities, tasks, and questions.

## Quick Start

### Maven Dependencies

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-screenplay-playwright</artifactId>
    <version>${serenity.version}</version>
    <scope>test</scope>
</dependency>
```

For Cucumber integration, also add:

```xml
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-cucumber</artifactId>
    <version>${serenity.version}</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <scope>test</scope>
</dependency>
```

> **Tip:** Use the `cucumber-bom` for consistent Cucumber version management across all Cucumber dependencies.

### Install Playwright Browsers

Before running tests, install the Playwright browsers:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

---

## Using with JUnit 5

### Basic Test Structure

```java
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.playwright.actors.PlaywrightCast;
import net.serenitybdd.screenplay.playwright.interactions.*;
import net.serenitybdd.screenplay.ensure.Ensure;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
class SearchTest {

    @BeforeEach
    void setupStage() {
        OnStage.setTheStage(new PlaywrightCast());
    }

    @Test
    void shouldSearchForProducts() {
        Actor customer = OnStage.theActorCalled("Customer");

        customer.attemptsTo(
            Open.url("https://example.com"),
            Enter.theValue("Widget").into("#search"),
            Click.on("#search-button"),
            Ensure.that("#results").isVisible()
        );
    }
}
```

### Automatic Cleanup

Browser resources are automatically cleaned up through two mechanisms:

1. **TestLifecycleEvents**: The `BrowseTheWebWithPlaywright` ability subscribes to test lifecycle events and cleans up when tests finish.

2. **OnStage.drawTheCurtain()**: Optionally call this in `@AfterEach` to explicitly clean up all actors on stage:

```java
@AfterEach
void teardownStage() {
    OnStage.drawTheCurtain();
}
```

---

## Using with Cucumber

Serenity Playwright integrates seamlessly with Cucumber for BDD-style tests.

### 1. Create the Test Suite Runner

```java
package yourpackage;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("yourpackage")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
    value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
    value = "yourpackage,net.serenitybdd.cucumber.actors")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME,
    value = "src/test/resources/features")
public class CucumberTestSuite {
}
```

> **Important**: Include `net.serenitybdd.cucumber.actors` in your glue path. This registers Serenity's `StageDirector` which automatically calls `OnStage.drawTheCurtain()` after each scenario.

### 2. Create Playwright Hooks

```java
package yourpackage;

import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.playwright.actors.PlaywrightCast;

public class PlaywrightHooks {

    /**
     * Set up the Playwright stage before each scenario.
     * Order 0 ensures this runs first.
     */
    @Before(order = 0)
    public void setTheStage(Scenario scenario) {
        OnStage.setTheStage(new PlaywrightCast());
    }

    /**
     * Define the {actor} parameter type for Cucumber steps.
     * This allows steps like "Given Toby is on the application"
     * to automatically resolve "Toby" to an Actor instance.
     */
    @ParameterType(".*")
    public Actor actor(String actorName) {
        return OnStage.theActorCalled(actorName);
    }
}
```

### 3. Write Step Definitions

```java
package yourpackage;

import io.cucumber.java.en.*;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.serenitybdd.screenplay.playwright.interactions.*;

public class SearchStepDefinitions {

    @Given("{actor} is on the search page")
    public void actorIsOnTheSearchPage(Actor actor) {
        actor.attemptsTo(
            Open.url("https://example.com/search")
        );
    }

    @When("{actor} searches for {string}")
    public void actorSearchesFor(Actor actor, String searchTerm) {
        actor.attemptsTo(
            Enter.theValue(searchTerm).into("#search"),
            Click.on("#search-button")
        );
    }

    @Then("the search results should be displayed")
    public void theSearchResultsShouldBeDisplayed() {
        Actor actor = OnStage.theActorInTheSpotlight();
        actor.attemptsTo(
            Ensure.that("#results").isVisible()
        );
    }
}
```

### 4. Write Feature Files

```gherkin
Feature: Product Search
  As a customer
  I want to search for products
  So that I can find what I need

  Scenario: Searching for a product
    Given Alice is on the search page
    When Alice searches for "Widget"
    Then the search results should be displayed
```

---

## Key Concepts

### PlaywrightCast

`PlaywrightCast` is a Cast implementation that automatically provides actors with the `BrowseTheWebWithPlaywright` ability:

```java
// Basic usage
OnStage.setTheStage(new PlaywrightCast());

// With custom browser type
OnStage.setTheStage(
    new PlaywrightCast()
        .withBrowserType("firefox")
        .withHeadlessMode(false)
);

// With launch options
BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
    .setHeadless(false)
    .setSlowMo(100);

OnStage.setTheStage(
    new PlaywrightCast().withLaunchOptions(options)
);
```

### BrowseTheWebWithPlaywright

The core ability that wraps Playwright's browser automation:

```java
// Manual ability assignment (if not using PlaywrightCast)
Actor alice = Actor.named("Alice");
alice.can(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

// Access the Playwright page
Page page = BrowseTheWebWithPlaywright.as(alice).getCurrentPage();
```

### Target

Targets define UI elements to interact with:

```java
// CSS selector
Target SEARCH_FIELD = Target.the("search field").locatedBy("#search");

// XPath selector
Target SUBMIT_BUTTON = Target.the("submit button").locatedBy("//button[@type='submit']");

// Use in interactions
actor.attemptsTo(
    Enter.theValue("test").into(SEARCH_FIELD),
    Click.on(SUBMIT_BUTTON)
);
```

---

## Available Interactions

### Navigation
- `Open.url(String url)` - Navigate to a URL

### Input
- `Enter.theValue(String).into(Target/String)` - Type text into an input
- `Click.on(Target/String)` - Click an element
- `DoubleClick.on(Target/String)` - Double-click an element
- `Check.checkbox(Target/String)` - Check a checkbox
- `SelectFromOptions.byVisibleText(String).from(Target/String)` - Select from dropdown
- `Press.keys(String)` - Send keyboard keys
- `Upload.file(Path).to(Target/String)` - Upload a file

### Waiting
- `WaitFor.the(Target/String).toBeVisible()` - Wait for element visibility

---

## Available Questions

### Text & Attributes
- `Text.of(Target/String)` - Get element text
- `Attribute.of(Target/String).named(String)` - Get attribute value
- `SelectOptions.of(Target/String)` - Get select options

### Visibility
- `Visibility.of(Target/String)` - Check if element is visible

---

## Available Assertions (Ensure)

```java
actor.attemptsTo(
    // Visibility
    Ensure.that(ELEMENT).isVisible(),
    Ensure.that(ELEMENT).isNotVisible(),

    // Text content
    Ensure.that(ELEMENT).hasText("expected"),
    Ensure.that(ELEMENT).containsText("partial"),

    // Attributes
    Ensure.that(ELEMENT).hasAttribute("name", "value"),

    // Enabled state
    Ensure.that(ELEMENT).isEnabled(),
    Ensure.that(ELEMENT).isDisabled(),

    // Checkbox state
    Ensure.that(CHECKBOX).isChecked(),
    Ensure.that(CHECKBOX).isNotChecked()
);
```

---

## Configuration

### Browser Configuration

Configure via system properties or `serenity.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `playwright.browsertype` | Browser to use: `chromium`, `firefox`, `webkit` | `chromium` |
| `playwright.headless` | Run in headless mode | `true` |
| `playwright.slowmo` | Slow down operations (ms) | - |
| `playwright.tracing` | Enable Playwright tracing | `false` |
| `playwright.timeout` | Default timeout (ms) | - |

### Example serenity.properties

```properties
# Browser settings
playwright.browsertype=chromium
playwright.headless=true

# Debugging (disable for CI)
playwright.slowmo=0

# Tracing (for debugging)
playwright.tracing=false

# Screenshots
serenity.take.screenshots=FOR_FAILURES
```

---

## Creating Custom Tasks

Tasks encapsulate business-level actions:

```java
public class SearchFor implements Task {

    private final String searchTerm;

    public SearchFor(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    @Step("{0} searches for '#searchTerm'")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Enter.theValue(searchTerm).into("#search"),
            Click.on("#search-button")
        );
    }

    public static SearchFor term(String searchTerm) {
        return new SearchFor(searchTerm);
    }
}

// Usage
actor.attemptsTo(SearchFor.term("Widget"));
```

---

## Creating Custom Questions

Questions query the application state:

```java
public class TheSearchResults implements Question<List<String>> {

    @Override
    @Step("{0} checks the search results")
    public List<String> answeredBy(Actor actor) {
        var page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        return page.locator(".result-item")
                   .allTextContents();
    }

    public static TheSearchResults displayed() {
        return new TheSearchResults();
    }
}

// Usage
List<String> results = actor.asksFor(TheSearchResults.displayed());
```

---

## Example Project Structure

```
src/test/
├── java/
│   └── yourpackage/
│       ├── cucumber/
│       │   ├── CucumberTestSuite.java
│       │   ├── PlaywrightHooks.java
│       │   └── StepDefinitions.java
│       └── screenplay/
│           ├── tasks/
│           │   ├── SearchFor.java
│           │   └── AddToCart.java
│           ├── questions/
│           │   ├── TheSearchResults.java
│           │   └── TheCartTotal.java
│           └── ui/
│               └── SearchPage.java  (Target definitions)
└── resources/
    ├── features/
    │   └── search.feature
    ├── serenity.properties
    └── cucumber.properties
```

---

## Troubleshooting

### Browser Not Found

If you see "Browser not found" errors, install Playwright browsers:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### State Leakage Between Tests

If tests interfere with each other:

1. Ensure `net.serenitybdd.cucumber.actors` is in your Cucumber glue path
2. Verify `OnStage.setTheStage(new PlaywrightCast())` runs in `@Before` with `order = 0`
3. For apps using localStorage, consider clearing it in your navigation task:

```java
public class OpenTheApplication implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.url(APP_URL));

        // Clear localStorage if needed
        var page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("() => localStorage.clear()");
        page.reload();
    }
}
```

### Screenshots Not Captured

Set the screenshot strategy in `serenity.properties`:

```properties
serenity.take.screenshots=FOR_EACH_ACTION
```

---

## Further Reading

- [Serenity BDD Documentation](https://serenity-bdd.github.io/)
- [Screenplay Pattern Guide](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)
- [Playwright Java Documentation](https://playwright.dev/java/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
