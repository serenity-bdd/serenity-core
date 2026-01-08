package net.serenitybdd.playwright.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.demo.steps.DuckDuckGoSteps;
import net.serenitybdd.playwright.demo.steps.ExampleDomainSteps;
import net.serenitybdd.playwright.demo.steps.WikipediaSteps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Demonstrates the recommended Page Object pattern with Serenity Playwright.
 * <p>
 * This test class shows the proper separation of concerns:
 * <pre>
 * Test (this class)
 *   └── Step Library (@Step methods for reporting)
 *         └── Page Object (encapsulates locators and page actions)
 * </pre>
 * </p>
 * <p>
 * Notice that:
 * <ul>
 *   <li>Tests read like business requirements - no technical details</li>
 *   <li>No CSS selectors, XPath, or locator strategies visible here</li>
 *   <li>Step methods describe user intentions, not implementation</li>
 *   <li>Page Objects are hidden behind the step libraries</li>
 * </ul>
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("When using Page Objects with Playwright")
class WhenUsingPageObjectsTest {

    @Steps
    WikipediaSteps wikipedia;

    @Steps
    DuckDuckGoSteps duckDuckGo;

    @Steps
    ExampleDomainSteps exampleDomain;

    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setupPage() {
        page = browser.newPage();
        PlaywrightSerenity.registerPage(page);
    }

    @AfterEach
    void closePage() {
        PlaywrightSerenity.unregisterPage(page);
        if (page != null) page.close();
    }

    // ========== Wikipedia Tests ==========

    @Test
    @DisplayName("Should search Wikipedia for a term")
    void shouldSearchWikipediaForATerm() {
        // Notice: no selectors, no locators - just business language
        wikipedia.openWikipedia(page);
        wikipedia.searchFor(page, "Playwright automation");
        wikipedia.verifyTitleContains(page, "Playwright");
    }

    @Test
    @DisplayName("Should display Wikipedia home page correctly")
    void shouldDisplayWikipediaHomePage() {
        wikipedia.openWikipedia(page);
        wikipedia.verifyHomePageIsDisplayed(page);
        wikipedia.verifyTitleContains(page, "Wikipedia");
    }

    // ========== DuckDuckGo Tests ==========

    @Test
    @DisplayName("Should search DuckDuckGo and see results")
    void shouldSearchDuckDuckGoAndSeeResults() {
        duckDuckGo.openDuckDuckGo(page);
        duckDuckGo.searchFor(page, "Serenity BDD");
        duckDuckGo.verifyResultsAreDisplayed(page);
        duckDuckGo.verifyResultsContain(page, "Serenity");
    }

    // ========== Example Domain Tests ==========

    @Test
    @DisplayName("Should display example.com correctly")
    void shouldDisplayExampleDomain() {
        exampleDomain.openExampleDomain(page);
        exampleDomain.verifyPageIsDisplayed(page);
        exampleDomain.verifyHeadingIsVisible(page);
    }

    @Test
    @DisplayName("Should verify example.com page title")
    void shouldVerifyExampleDomainTitle() {
        exampleDomain.openExampleDomain(page);
        exampleDomain.verifyTitleContains(page, "Example");
    }
}
