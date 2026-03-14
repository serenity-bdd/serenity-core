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

## UI Element Factories

The `ui` package provides semantic factories for locating common UI elements using Playwright's powerful selectors:

```java
import net.serenitybdd.screenplay.playwright.ui.*;

// Buttons
Target submitBtn = Button.withText("Submit");
Target loginBtn = Button.withNameOrId("login-btn");
Target saveBtn = Button.withAriaLabel("Save changes");
Target actionBtn = Button.containingText("Action");

// Input Fields
Target emailField = InputField.withNameOrId("email");
Target searchField = InputField.withPlaceholder("Search...");
Target usernameField = InputField.withLabel("Username");

// Links
Target homeLink = Link.withText("Home");
Target learnMore = Link.containingText("Learn more");

// Checkboxes & Radio Buttons
Target termsCheckbox = Checkbox.withLabel("I agree to the terms");
Target genderMale = RadioButton.withValue("male");

// Dropdowns
Target countryDropdown = Dropdown.withLabel("Country");
Target stateDropdown = Dropdown.withNameOrId("state");

// Labels & Images
Target emailLabel = Label.forFieldId("email");
Target logo = Image.withAltText("Company Logo");
```

---

## Available Interactions

### Navigation
- `Open.url(String url)` - Navigate to a URL
- `Navigate.back()` - Go back in history
- `Navigate.forward()` - Go forward in history
- `Navigate.refresh()` - Refresh the page

### Input
- `Enter.theValue(String).into(Target/String)` - Type text into an input
- `Click.on(Target/String)` - Click an element
- `DoubleClick.on(Target/String)` - Double-click an element
- `RightClick.on(Target/String)` - Right-click an element
- `Check.checkbox(Target/String)` - Check a checkbox
- `Uncheck.checkbox(Target/String)` - Uncheck a checkbox
- `Clear.field(Target/String)` - Clear an input field
- `Press.keys(String)` - Send keyboard keys
- `Upload.file(Path).to(Target/String)` - Upload a file
- `Focus.on(Target/String)` - Focus an element
- `Hover.over(Target/String)` - Hover over an element

### Select/Deselect Options
```java
// Select options
SelectFromOptions.byVisibleText("Option 1").from("#dropdown")
SelectFromOptions.byValue("opt1", "opt2").from(DROPDOWN)
SelectFromOptions.byIndex(0).from("#select")

// Deselect options (for multi-select)
DeselectFromOptions.byVisibleText("Option 1").from("#multi-select")
DeselectFromOptions.byValue("opt1").from(DROPDOWN)
DeselectFromOptions.byIndex(0).from("#multi-select")
DeselectFromOptions.all().from("#multi-select")  // Clear all selections
```

### Drag and Drop
```java
// Standard syntax
Drag.from("#source").to("#target")
Drag.from(SOURCE_ELEMENT).to(TARGET_ELEMENT)

// Alternative fluent syntax
Drag.the("#source").onto("#target")
Drag.the(SOURCE_ELEMENT).onto(TARGET_ELEMENT)
```

### Scrolling
```java
// Scroll to element
Scroll.to("#footer")
Scroll.to(FOOTER_ELEMENT).andAlignToTop()
Scroll.to(ELEMENT).andAlignToCenter()
Scroll.to(ELEMENT).andAlignToBottom()

// Page-level scrolling
Scroll.toTop()
Scroll.toBottom()
Scroll.by(0, 500)           // Scroll by delta
Scroll.toPosition(0, 1000)  // Scroll to absolute position

// Simple element scroll
ScrollIntoView.element("#target")
```

### Waiting
- `WaitFor.the(Target/String).toBeVisible()` - Wait for element visibility
- `WaitFor.the(Target/String).toBeHidden()` - Wait for element to be hidden
- `WaitFor.the(Target/String).toBeEnabled()` - Wait for element to be enabled

### Dialogs/Alerts
```java
HandleDialog.byAccepting()
HandleDialog.byDismissing()
HandleDialog.byEntering("text").andAccepting()
```

---

## Available Questions

### Text & Attributes
- `Text.of(Target/String)` - Get element text
- `TextOfAll.of(Target/String)` - Get text of all matching elements
- `Attribute.of(Target/String).named(String)` - Get attribute value
- `Value.of(Target/String)` - Get input value
- `InnerHTML.of(Target/String)` - Get inner HTML
- `OuterHTML.of(Target/String)` - Get outer HTML

### Element State
- `Visibility.of(Target/String)` - Check if element is visible
- `Presence.of(Target/String)` - Check if element exists in DOM
- `Absence.of(Target/String)` - Check if element does NOT exist in DOM
- `Enabled.of(Target/String)` - Check if element is enabled
- `Disabled.of(Target/String)` - Check if element is disabled
- `Checked.of(Target/String)` - Check if checkbox/radio is checked
- `Focused.of(Target/String)` - Check if element has focus
- `Editable.of(Target/String)` - Check if element is editable
- `Count.of(Target/String)` - Count matching elements

### Select Options
- `SelectOptions.of(Target/String)` - Get all select options
- `SelectedOptionText.of(Target/String)` - Get selected option text
- `AllOptionTexts.of(Target/String)` - Get all option texts

### Styling
- `CSSValue.of(Target/String).named(String)` - Get CSS property value
- `CSSClasses.of(Target/String)` - Get element CSS classes
- `BoundingBox.of(Target/String)` - Get element bounding box

### Page Information
- `TheWebPage.title()` - Get page title
- `TheWebPage.url()` - Get page URL
- `TheWebPage.content()` - Get page HTML content

### JavaScript
```java
JavaScript.result("return document.title")
JavaScript.result("return arguments[0].value", TARGET)
```

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

## Network Interception

Intercept, mock, or modify network requests:

```java
// Block requests (e.g., ads, analytics)
actor.attemptsTo(
    InterceptNetwork.forUrl("**/*.png").andAbort(),
    InterceptNetwork.forUrl("**/analytics/**").andAbort()
);

// Mock API responses
actor.attemptsTo(
    InterceptNetwork.forUrl("**/api/users")
        .andRespondWith("{\"users\": []}")
        .withContentType("application/json")
        .withStatus(200)
);

// Mock with JSON object (auto-serialized)
actor.attemptsTo(
    InterceptNetwork.forUrl("**/api/products")
        .andRespondWithJson(new Product("Widget", 19.99))
        .withStatus(200)
);

// Add headers to requests
actor.attemptsTo(
    InterceptNetwork.forUrl("**/api/**")
        .andContinueWithHeader("Authorization", "Bearer token123")
);

// Custom route handler
actor.attemptsTo(
    InterceptNetwork.forUrl("**/api/**").andHandle(route -> {
        if (route.request().method().equals("POST")) {
            route.fulfill(new Route.FulfillOptions().setBody("{}"));
        } else {
            route.resume();
        }
    })
);

// Remove route handlers
actor.attemptsTo(RemoveRoutes.all());
actor.attemptsTo(RemoveRoutes.forUrl("**/api/**"));

// Wait for network response
actor.attemptsTo(
    WaitForResponse.matching("**/api/search**")
        .whilePerforming(Click.on("#search-btn"))
        .withTimeout(5000)
);
```

---

## Tracing

Capture detailed traces for debugging (viewable with Playwright Trace Viewer):

```java
// Start tracing manually
actor.attemptsTo(
    StartTracing.withName("checkout-flow")
        .withScreenshots()
        .withSnapshots()
        .withSources()
);

// ... perform test actions ...

// Stop and save trace
actor.attemptsTo(
    StopTracing.andSaveAs("checkout-flow")  // Saves to target/playwright/traces/
);

// Or save to specific path
actor.attemptsTo(
    StopTracing.andSaveTo(Paths.get("traces/my-trace.zip"))
);
```

View traces with: `npx playwright show-trace target/playwright/traces/checkout-flow.zip`

---

## Accessibility Testing

Query accessibility information for compliance testing:

```java
// Get accessibility snapshot of the page
String snapshot = actor.asksFor(AccessibilitySnapshot.ofThePage());

// Get accessibility snapshot of specific element
String navSnapshot = actor.asksFor(AccessibilitySnapshot.of("#navigation"));
String headerSnapshot = actor.asksFor(AccessibilitySnapshot.of(HEADER));

// Find all elements with specific ARIA role
List<String> buttonNames = actor.asksFor(
    AccessibilitySnapshot.allWithRole(AriaRole.BUTTON)
);
List<String> linkNames = actor.asksFor(
    AccessibilitySnapshot.allWithRole(AriaRole.LINK)
);
```

---

## Download Handling

Handle file downloads during tests:

```java
// Wait for download while clicking
actor.attemptsTo(
    WaitForDownload.whilePerforming(Click.on("#download-btn"))
        .andSaveAs("report.pdf")
);

// Save to specific path
actor.attemptsTo(
    WaitForDownload.whilePerforming(Click.on(EXPORT_LINK))
        .andSaveTo(Paths.get("downloads/data.xlsx"))
        .withTimeout(30000)
);

// Query download information
String filename = actor.asksFor(DownloadedFile.suggestedFilename());
String url = actor.asksFor(DownloadedFile.url());
Path path = actor.asksFor(DownloadedFile.path());
String error = actor.asksFor(DownloadedFile.failure());  // null if successful
```

---

## Visual Comparison

Perform visual regression testing:

```java
// Compare full page screenshot to baseline
actor.attemptsTo(
    CompareScreenshot.ofPage().toBaseline("homepage")
);

// Compare element screenshot
actor.attemptsTo(
    CompareScreenshot.of("#header").toBaseline("header-component")
);
actor.attemptsTo(
    CompareScreenshot.of(NAVIGATION).toBaseline("nav-bar")
);

// With custom threshold (0.0 = exact match, 1.0 = any difference allowed)
actor.attemptsTo(
    CompareScreenshot.ofPage()
        .toBaseline("dashboard")
        .withThreshold(0.05)  // Allow 5% difference
);

// Update baseline images
actor.attemptsTo(
    CompareScreenshot.ofPage()
        .toBaseline("homepage")
        .updatingBaseline()
);
```

- Baselines stored in: `src/test/resources/visual-baselines/`
- Actual screenshots: `target/visual-comparisons/actual/`
- Diff images: `target/visual-comparisons/diff/`

---

## Geolocation & Permissions

Test location-based features and permission-dependent functionality:

```java
// Grant permissions
actor.attemptsTo(
    GrantPermissions.forGeolocation(),
    GrantPermissions.forNotifications(),
    GrantPermissions.forMediaDevices(),  // camera + microphone
    GrantPermissions.forClipboard()
);

// Grant specific permissions
actor.attemptsTo(
    GrantPermissions.for_("geolocation", "notifications")
        .onOrigin("https://example.com")
);

// Set geolocation
actor.attemptsTo(
    SetGeolocation.to(51.5074, -0.1278)  // London coordinates
        .withAccuracy(100)
);

// Use predefined locations
actor.attemptsTo(SetGeolocation.toNewYork());
actor.attemptsTo(SetGeolocation.toLondon());
actor.attemptsTo(SetGeolocation.toTokyo());
actor.attemptsTo(SetGeolocation.toSanFrancisco());
actor.attemptsTo(SetGeolocation.toSydney());
actor.attemptsTo(SetGeolocation.toParis());

// Clear geolocation and permissions
actor.attemptsTo(SetGeolocation.clear());
actor.attemptsTo(ClearPermissions.all());
```

---

## Device Emulation

Test responsive designs with device emulation:

```java
// Mobile devices
actor.attemptsTo(EmulateDevice.iPhone12());
actor.attemptsTo(EmulateDevice.iPhone14Pro());
actor.attemptsTo(EmulateDevice.iPhoneSE());
actor.attemptsTo(EmulateDevice.iPad());
actor.attemptsTo(EmulateDevice.iPadPro());
actor.attemptsTo(EmulateDevice.pixel5());
actor.attemptsTo(EmulateDevice.pixel7());
actor.attemptsTo(EmulateDevice.galaxyS21());
actor.attemptsTo(EmulateDevice.galaxyTabS7());

// Desktop viewports
actor.attemptsTo(EmulateDevice.desktop());      // 1280x720
actor.attemptsTo(EmulateDevice.desktopHD());    // 1920x1080
actor.attemptsTo(EmulateDevice.desktop4K());    // 3840x2160
actor.attemptsTo(EmulateDevice.laptop());       // 1366x768
actor.attemptsTo(EmulateDevice.macBookPro14()); // 1512x982

// Custom viewport
actor.attemptsTo(
    EmulateDevice.withViewport(1440, 900)
        .withDeviceScaleFactor(2)
        .asMobile()
);
```

---

## Clock Control

Test time-dependent features by controlling the browser's clock:

```java
import java.time.LocalDateTime;

// Freeze time at specific moment
actor.attemptsTo(
    ControlClock.freezeAt(LocalDateTime.of(2024, 12, 25, 10, 30))
);

// Install fake timers at specific time (time will advance)
actor.attemptsTo(
    ControlClock.installAt(LocalDateTime.of(2024, 1, 1, 0, 0))
);

// Fast-forward time
actor.attemptsTo(ControlClock.fastForward(5000));        // 5 seconds
actor.attemptsTo(ControlClock.fastForwardSeconds(30));
actor.attemptsTo(ControlClock.fastForwardMinutes(5));
actor.attemptsTo(ControlClock.fastForwardHours(2));

// Pause and resume
actor.attemptsTo(ControlClock.pause());
actor.attemptsTo(ControlClock.resume());

// Set system time (without fake timers)
actor.attemptsTo(
    ControlClock.setSystemTimeTo(LocalDateTime.of(2024, 6, 15, 14, 0))
);
```

---

## Console Messages

Capture and analyze browser console output:

```java
// Start capturing console messages
actor.attemptsTo(CaptureConsoleMessages.duringTest());

// ... perform actions ...

// Query captured messages
List<String> allMessages = actor.asksFor(ConsoleMessages.all());
List<String> errors = actor.asksFor(ConsoleMessages.errors());
List<String> warnings = actor.asksFor(ConsoleMessages.warnings());
List<String> logs = actor.asksFor(ConsoleMessages.logs());
List<String> info = actor.asksFor(ConsoleMessages.info());

// Filter by content
List<String> apiErrors = actor.asksFor(
    ConsoleMessages.containing("API error")
);

// Get message counts
Integer totalCount = actor.asksFor(ConsoleMessages.count());
Integer errorCount = actor.asksFor(ConsoleMessages.errorCount());

// Get full message objects (with type, text, location)
List<CapturedConsoleMessage> messages = actor.asksFor(
    ConsoleMessages.allCaptured()
);

// Clear captured messages
actor.attemptsTo(CaptureConsoleMessages.clear());
```

---

## PDF Generation

Generate PDF documents from pages (Chromium headless only):

```java
// Basic PDF generation
actor.attemptsTo(
    GeneratePDF.andSaveAs("report.pdf")  // Saves to target/pdfs/
);

// Save to specific path
actor.attemptsTo(
    GeneratePDF.andSaveTo(Paths.get("output/invoice.pdf"))
);

// With formatting options
actor.attemptsTo(
    GeneratePDF.andSaveAs("document.pdf")
        .withFormat("A4")
        .withMargins("1cm", "1cm", "1cm", "1cm")
        .inLandscape()
        .withBackground()  // Include background graphics
        .withScale(0.8)
);

// With headers and footers
actor.attemptsTo(
    GeneratePDF.andSaveAs("report.pdf")
        .withHeaderTemplate("<div style='font-size:10px'>Company Report</div>")
        .withFooterTemplate("<div style='font-size:10px'>Page <span class='pageNumber'></span> of <span class='totalPages'></span></div>")
        .withPageRanges("1-5, 8")
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
serenity.take.screenshots=FOR_EACH_ACTION

# Downloads directory
playwright.downloads.path=target/downloads
```

### Configuration via Context Options

For advanced configuration (device emulation, geolocation, permissions), use browser context options:

```java
Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
    .setViewportSize(375, 667)
    .setDeviceScaleFactor(2)
    .setIsMobile(true)
    .setHasTouch(true)
    .setLocale("en-US")
    .setTimezoneId("America/New_York")
    .setGeolocation(40.7128, -74.0060)
    .setPermissions(Arrays.asList("geolocation"));

OnStage.setTheStage(
    new PlaywrightCast().withContextOptions(contextOptions)
);
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

## Working with Multiple Pages and Frames

### Switching Between Pages (Tabs)

The `SwitchTo` class provides methods for managing multiple browser pages (tabs) in Playwright:

#### Opening New Pages

```java
// Open a new blank page
actor.attemptsTo(
    SwitchTo.newPage()
);

// Open a new page and navigate to a URL
actor.attemptsTo(
    SwitchTo.newPageAt("https://example.com/page2")
);
```

#### Switching Between Existing Pages

```java
// Switch by index (0-based)
actor.attemptsTo(
    SwitchTo.pageNumber(0)  // Switch to first page
);

// Switch by title (partial match)
actor.attemptsTo(
    SwitchTo.pageWithTitle("Dashboard")
);

// Switch by URL (partial match)
actor.attemptsTo(
    SwitchTo.pageWithUrl("/admin")
);
```

#### Closing Pages

```java
// Close current page and switch to previous one
actor.attemptsTo(
    SwitchTo.previousPageAfterClosingCurrent()
);
```

#### Complete Example

```java
@Test
void shouldWorkWithMultiplePages() {
    actor.attemptsTo(
        // Open main page
        Open.url("https://example.com"),
        Click.on("#external-link"),  // Opens new tab
        
        // Switch to the new page
        SwitchTo.pageWithTitle("External Page"),
        Ensure.that("#content").isVisible(),
        
        // Switch back to original page
        SwitchTo.pageNumber(0),
        Ensure.that("#main").isVisible(),
        
        // Close the second page
        SwitchTo.pageNumber(1),
        SwitchTo.previousPageAfterClosingCurrent()
    );
}
```

### Working with IFrames

The `WithinFrame` class allows you to interact with elements inside iframes without manually switching contexts:

#### Basic IFrame Interactions

```java
// Click an element within an iframe
actor.attemptsTo(
    WithinFrame.locatedBy("#my-iframe")
        .click("#button-inside")
);

// Fill an input within an iframe
actor.attemptsTo(
    WithinFrame.locatedBy("iframe[name='content']")
        .fill("#email", "user@example.com")
);

// Clear an input within an iframe
actor.attemptsTo(
    WithinFrame.locatedBy("#editor-frame")
        .clear("#text-field")
);

// Type text character by character
actor.attemptsTo(
    WithinFrame.locatedBy("#editor-frame")
        .type("#text-field", "Hello World")
);
```

#### Custom Actions Within IFrames

For more complex interactions, use the `perform()` method with a lambda:

```java
actor.attemptsTo(
    WithinFrame.locatedBy("#my-iframe")
        .perform(frame -> {
            // Perform multiple actions within the frame
            frame.locator("#username").fill("testuser");
            frame.locator("#password").fill("password123");
            frame.locator("#remember-me").check();
            frame.locator("#login-button").click();
        })
);
```

#### Nested IFrames

For nested iframes, you can use `Target.inFrame()` or chain multiple `WithinFrame` calls:

```java
// Using Target for nested frames
Target nestedElement = Target.the("nested element")
    .inFrame("iframe#outer")
    .inFrame("iframe#inner")
    .locatedBy("#content");

actor.attemptsTo(
    Click.on(nestedElement)
);

// Or query content from nested frames
String text = actor.asksFor(Text.of(nestedElement));
```

#### Complete IFrame Example

```java
@Test
void shouldInteractWithIFrame() {
    actor.attemptsTo(
        // Navigate to page with iframe
        Open.url("https://example.com/iframe-demo"),
        
        // Fill form within iframe
        WithinFrame.locatedBy("#contact-form-iframe")
            .fill("#name", "John Doe"),
        
        WithinFrame.locatedBy("#contact-form-iframe")
            .fill("#email", "john@example.com"),
        
        WithinFrame.locatedBy("#contact-form-iframe")
            .click("#submit"),
        
        // Verify success message (also in iframe)
        Ensure.that(
            Target.the("success message")
                .inFrame("#contact-form-iframe")
                .locatedBy(".success")
        ).isVisible()
    );
}
```

### Checking Current Page State

You can query information about the current page or open pages:

```java
// Get count of open pages
int pageCount = actor.asksFor(PageCount.inTheBrowser());

// Get current page URL
String url = actor.asksFor(TheWebPage.currentUrl());

// Get current page title
String title = actor.asksFor(TheWebPage.title());
```

---

## API Reference

### Interactions (Actions)

| Class | Description |
|-------|-------------|
| `Open` | Navigate to URLs |
| `Navigate` | Browser navigation (back, forward, refresh) |
| `Click` | Click elements |
| `DoubleClick` | Double-click elements |
| `RightClick` | Right-click elements |
| `Enter` | Type text into inputs |
| `Clear` | Clear input fields |
| `Press` | Send keyboard keys |
| `Check` / `Uncheck` | Toggle checkboxes |
| `SelectFromOptions` | Select dropdown options |
| `DeselectFromOptions` | Deselect multi-select options |
| `Drag` | Drag and drop |
| `Scroll` | Page and element scrolling |
| `ScrollIntoView` | Scroll element into view |
| `Focus` | Focus elements |
| `Hover` | Hover over elements |
| `Upload` | Upload files |
| `HandleDialog` | Handle alerts/dialogs |
| `WaitFor` | Wait for element states |
| `ExecuteJavaScript` | Execute JavaScript |
| `TakeScreenshot` | Capture screenshots |
| `InterceptNetwork` | Mock/intercept network requests |
| `RemoveRoutes` | Remove network interceptions |
| `WaitForResponse` | Wait for network responses |
| `WaitForDownload` | Handle file downloads |
| `StartTracing` / `StopTracing` | Control Playwright tracing |
| `SetGeolocation` | Set browser geolocation |
| `GrantPermissions` / `ClearPermissions` | Manage browser permissions |
| `EmulateDevice` | Emulate device viewports |
| `ControlClock` | Control browser time |
| `CaptureConsoleMessages` | Capture console output |
| `GeneratePDF` | Generate PDF from page |
| `CompareScreenshot` | Visual regression testing |
| `ManageCookies` | Cookie operations |
| `ManageStorage` | Local/session storage operations |
| `SwitchTo` | Switch pages/tabs |
| `WithinFrame` | Interact within iframes |

### Questions (Queries)

| Class | Description |
|-------|-------------|
| `Text` / `TextOfAll` | Get element text |
| `Attribute` / `AttributeOfAll` | Get attribute values |
| `Value` | Get input values |
| `InnerHTML` / `OuterHTML` | Get HTML content |
| `Visibility` | Check visibility |
| `Presence` / `Absence` | Check DOM presence |
| `Enabled` / `Disabled` | Check enabled state |
| `Checked` | Check checkbox state |
| `Focused` | Check focus state |
| `Editable` | Check editable state |
| `Count` | Count elements |
| `CSSValue` / `CSSClasses` | Get styles |
| `BoundingBox` / `ElementBounds` | Get dimensions |
| `SelectOptions` / `SelectedOptionText` | Get select options |
| `TheWebPage` | Page title, URL, content |
| `JavaScript` | Execute JavaScript queries |
| `AccessibilitySnapshot` | Get accessibility tree |
| `DownloadedFile` | Get download information |
| `ConsoleMessages` | Get captured console messages |
| `CookieValue` | Get cookie values |
| `StorageValue` | Get storage values |
| `PageCount` | Count open pages |

### UI Element Factories

| Class | Methods |
|-------|---------|
| `Button` | `withText()`, `withNameOrId()`, `withAriaLabel()`, `containingText()`, `locatedBy()` |
| `InputField` | `withNameOrId()`, `withPlaceholder()`, `withLabel()`, `withAriaLabel()`, `locatedBy()` |
| `Link` | `withText()`, `containingText()`, `withTitle()`, `locatedBy()` |
| `Checkbox` | `withLabel()`, `withNameOrId()`, `withValue()`, `locatedBy()` |
| `RadioButton` | `withLabel()`, `withNameOrId()`, `withValue()`, `locatedBy()` |
| `Dropdown` | `withLabel()`, `withNameOrId()`, `locatedBy()` |
| `Label` | `withText()`, `withExactText()`, `forFieldId()`, `locatedBy()` |
| `Image` | `withAltText()`, `withSrc()`, `withSrcContaining()`, `locatedBy()` |

---

## Further Reading

- [Serenity BDD Documentation](https://serenity-bdd.github.io/)
- [Screenplay Pattern Guide](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)
- [Playwright Java Documentation](https://playwright.dev/java/)
- [Playwright Trace Viewer](https://playwright.dev/java/docs/trace-viewer)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
