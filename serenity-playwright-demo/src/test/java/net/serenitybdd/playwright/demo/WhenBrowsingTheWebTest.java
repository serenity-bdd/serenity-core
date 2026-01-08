package net.serenitybdd.playwright.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.demo.steps.NavigationSteps;
import net.serenitybdd.playwright.demo.steps.SearchSteps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Demonstrates basic Serenity Playwright integration using programmatic registration.
 * <p>
 * This approach gives you full control over Playwright browser and page lifecycle,
 * while Serenity handles reporting and screenshot capture automatically.
 * </p>
 * <p>
 * Key points:
 * <ul>
 *   <li>Use @ExtendWith(SerenityJUnit5Extension.class) for Serenity integration</li>
 *   <li>Call PlaywrightSerenity.registerPage(page) to enable screenshot capture</li>
 *   <li>Call PlaywrightSerenity.unregisterPage(page) in cleanup</li>
 *   <li>Use @Steps to inject step libraries</li>
 * </ul>
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("When browsing the web")
class WhenBrowsingTheWebTest {

    // Step libraries are injected by Serenity
    @Steps
    NavigationSteps navigate;

    @Steps
    SearchSteps search;

    // Playwright resources - shared across tests for efficiency
    private static Playwright playwright;
    private static Browser browser;
    private Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)  // Run headless for CI
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
        // Register the page with Serenity for automatic screenshot capture
        PlaywrightSerenity.registerPage(page);
    }

    @AfterEach
    void closePage() {
        // Unregister before closing to ensure proper cleanup
        PlaywrightSerenity.unregisterPage(page);
        if (page != null) page.close();
    }

    @Test
    @DisplayName("Should capture screenshots during navigation")
    void shouldCaptureScreenshotsDuringNavigation() {
        // Each step will generate a screenshot in the report
        navigate.navigateTo(page, "https://www.example.com");
        navigate.waitForPageLoad(page);

        search.verifyTitleContains(page, "Example Domain");
    }

    @Test
    @DisplayName("Should navigate to Wikipedia and verify content")
    void shouldNavigateToWikipediaAndVerifyContent() {
        navigate.navigateTo(page, "https://en.wikipedia.org");
        navigate.waitForPageLoad(page);

        search.verifyTitleContains(page, "Wikipedia");
    }

    @Test
    @DisplayName("Should support manual screenshot capture")
    void shouldSupportManualScreenshotCapture() {
        navigate.navigateTo(page, "https://www.example.com");

        // You can also manually trigger screenshot capture at any point
        PlaywrightSerenity.takeScreenshot();

        search.verifyTitleContains(page, "Example");
    }
}
