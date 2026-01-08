package net.serenitybdd.playwright.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.demo.steps.NavigationSteps;
import net.serenitybdd.playwright.demo.steps.SearchSteps;
import net.serenitybdd.playwright.junit5.SerenityPlaywright;
import org.junit.jupiter.api.*;

/**
 * Demonstrates the @SerenityPlaywright meta-annotation approach.
 * <p>
 * The @SerenityPlaywright annotation is a convenience meta-annotation that combines:
 * <ul>
 *   <li>@ExtendWith(SerenityJUnit5Extension.class) - Core Serenity support</li>
 *   <li>@ExtendWith(SerenityPlaywrightExtension.class) - Playwright integration</li>
 * </ul>
 * </p>
 * <p>
 * The SerenityPlaywrightExtension can auto-detect Page fields in your test class,
 * but you can also explicitly register pages for more control.
 * </p>
 */
@SerenityPlaywright
@DisplayName("When using the @SerenityPlaywright annotation")
class WhenUsingSerenityPlaywrightAnnotationTest {

    @Steps
    NavigationSteps navigate;

    @Steps
    SearchSteps search;

    private static Playwright playwright;
    private static Browser browser;

    // This Page field can be auto-detected by SerenityPlaywrightExtension
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
        // Explicitly register - you can also let the extension auto-detect it
        PlaywrightSerenity.registerPage(page);
    }

    @AfterEach
    void closePage() {
        if (page != null) page.close();
        // Note: SerenityPlaywrightExtension automatically calls clear() in afterEach
    }

    @Test
    @DisplayName("Should work with the convenience annotation")
    void shouldWorkWithConvenienceAnnotation() {
        navigate.navigateTo(page, "https://www.example.com");
        navigate.waitForPageLoad(page);
        search.verifyTitleContains(page, "Example Domain");
    }

    @Test
    @DisplayName("Should capture screenshots for each step")
    void shouldCaptureScreenshotsForEachStep() {
        // Step 1: Navigate
        navigate.navigateTo(page, "https://en.wikipedia.org");

        // Step 2: Wait for load
        navigate.waitForPageLoad(page);

        // Step 3: Verify title
        search.verifyTitleContains(page, "Wikipedia");

        // Each of these steps will have a screenshot in the report!
    }
}
