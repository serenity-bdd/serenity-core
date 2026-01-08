# Serenity Playwright Demo

This project demonstrates how to use **Serenity BDD** with **Playwright** for browser automation testing with rich, detailed reports.

## Overview

The `serenity-playwright` module provides seamless integration between Serenity BDD's reporting capabilities and Playwright's powerful browser automation. This combination gives you:

- **Automatic screenshot capture** at each step completion
- **Rich HTML reports** with step-by-step documentation
- **Page source capture** for debugging
- **Step libraries** with `@Step` annotations for clean, reusable test code

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Playwright browsers (installed automatically on first run)

## Running the Tests

```bash
# Run all tests and generate reports
mvn clean verify

# Run a specific test class
mvn clean verify -Dit.test=WhenBrowsingTheWebTest

# Run tests with headed browser (visible)
mvn clean verify -Dplaywright.headless=false
```

## Viewing Reports

After running the tests, open the Serenity report:

```bash
open target/site/serenity/index.html
```

## Project Structure

```
src/test/java/net/serenitybdd/playwright/demo/
├── pages/                           # Page Objects (encapsulate locators)
│   ├── WikipediaHomePage.java       # Wikipedia page interactions
│   ├── DuckDuckGoSearchPage.java    # DuckDuckGo search page
│   └── ExampleDomainPage.java       # Simple example.com page
├── steps/                           # Step libraries (Serenity reporting)
│   ├── WikipediaSteps.java          # Wikipedia steps using Page Objects
│   ├── DuckDuckGoSteps.java         # DuckDuckGo steps
│   ├── ExampleDomainSteps.java      # Example.com steps
│   ├── NavigationSteps.java         # Generic navigation actions
│   ├── SearchSteps.java             # Generic search actions
│   ├── FormSteps.java               # Generic form interactions
│   └── PageAwareSteps.java          # Steps using getCurrentPage()
├── WhenUsingPageObjectsTest.java    # Page Object pattern demo (recommended)
├── WhenBrowsingTheWebTest.java      # Basic navigation tests
├── WhenUsingSerenityPlaywrightAnnotationTest.java  # @SerenityPlaywright demo
├── WhenInteractingWithFormsTest.java # Form interaction tests
└── WhenUsingPageAwareStepsTest.java  # Page-aware step demo
```

## Usage Patterns

### 1. Page Object Pattern (Recommended)

The recommended approach separates concerns into three layers:

```
Test (business scenarios)
  └── Step Library (@Step methods for Serenity reporting)
        └── Page Object (encapsulates locators and page interactions)
```

**Page Object** - Knows locators, hides implementation details:

```java
public class WikipediaHomePage {
    private final Page page;

    public WikipediaHomePage(Page page) {
        this.page = page;
    }

    // Locators are private - only this class knows how to find elements
    private Locator searchBox() {
        return page.getByRole(AriaRole.SEARCHBOX,
                new Page.GetByRoleOptions().setName("Search Wikipedia"));
    }

    // Actions are public - expose meaningful interactions
    public void searchFor(String term) {
        searchBox().fill(term);
        searchBox().press("Enter");
    }

    public String getTitle() {
        return page.title();
    }
}
```

**Step Library** - Provides Serenity reporting, uses Page Objects:

```java
public class WikipediaSteps {
    private WikipediaHomePage homePage;

    @Step("Search Wikipedia for '{1}'")
    public void searchFor(Page page, String term) {
        if (homePage == null) homePage = new WikipediaHomePage(page);
        homePage.searchFor(term);
    }
}
```

**Test** - Reads like business requirements, no technical details:

```java
@ExtendWith(SerenityJUnit5Extension.class)
class WikipediaTest {
    @Steps WikipediaSteps wikipedia;

    @Test
    void shouldSearchForATerm() {
        wikipedia.openWikipedia(page);
        wikipedia.searchFor(page, "Playwright");
        wikipedia.verifyTitleContains(page, "Playwright");
    }
}
```

See `WhenUsingPageObjectsTest.java` for complete examples.

### 2. Programmatic Registration

```java
@ExtendWith(SerenityJUnit5Extension.class)
class MyTest {
    @Steps NavigationSteps navigate;

    private Page page;

    @BeforeEach
    void setup() {
        page = browser.newPage();
        PlaywrightSerenity.registerPage(page);  // Enable screenshot capture
    }

    @AfterEach
    void cleanup() {
        PlaywrightSerenity.unregisterPage(page);
        page.close();
    }

    @Test
    void myTest() {
        navigate.navigateTo(page, "https://example.com");
        // Screenshots captured automatically after each step!
    }
}
```

### 3. Using @SerenityPlaywright Annotation

```java
@SerenityPlaywright  // Combines SerenityJUnit5Extension + SerenityPlaywrightExtension
class MyTest {
    @Steps NavigationSteps navigate;

    private Page page;

    @BeforeEach
    void setup() {
        page = browser.newPage();
        PlaywrightSerenity.registerPage(page);
    }

    @Test
    void myTest() {
        navigate.navigateTo(page, "https://example.com");
    }
    // Cleanup handled automatically by the extension
}
```

### 4. Page-Aware Steps (No page parameter needed)

```java
public class MySteps {
    @Step("Open {0}")
    public void openUrl(String url) {
        // Access page via getCurrentPage() - no parameter needed!
        PlaywrightSerenity.getCurrentPage().navigate(url);
    }
}
```

## Writing Step Libraries

Step libraries use the `@Step` annotation to create reportable actions:

```java
public class MySteps {

    @Step("Navigate to {1}")
    public void navigateTo(Page page, String url) {
        page.navigate(url);
    }

    @Step("Verify title contains '{1}'")
    public void verifyTitleContains(Page page, String expected) {
        assertThat(page.title()).contains(expected);
    }
}
```

**Key points:**
- Each `@Step` method appears in the Serenity report
- Screenshots are captured automatically at step completion
- Parameters can be referenced in the step description using `{0}`, `{1}`, etc.
- Inject step libraries using `@Steps` annotation in tests

## Manual Screenshot Capture

You can manually trigger screenshot capture at any point:

```java
PlaywrightSerenity.takeScreenshot();
```

## Configuration

Edit `src/test/resources/serenity.conf` to customize:

```hocon
serenity {
  project.name = "My Project"
  take.screenshots = FOR_EACH_ACTION  # or BEFORE_AND_AFTER_EACH_STEP, FOR_FAILURES
}
```

## Best Practices

1. **Share browser instances** across tests in the same class using `@BeforeAll`/`@AfterAll`
2. **Create new pages** for each test using `@BeforeEach`/`@AfterEach`
3. **Use headless mode** for CI/CD pipelines
4. **Organize steps** into focused step libraries (navigation, forms, assertions)
5. **Keep step methods small** - one action per step for better reporting

## Troubleshooting

### Playwright browsers not installed
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Screenshots not appearing in reports
- Ensure `PlaywrightSerenity.registerPage(page)` is called before test execution
- Ensure `PlaywrightSerenity.unregisterPage(page)` is called in cleanup

### Tests running slowly
- Use headless mode: `new BrowserType.LaunchOptions().setHeadless(true)`
- Share browser instances across tests in the same class
