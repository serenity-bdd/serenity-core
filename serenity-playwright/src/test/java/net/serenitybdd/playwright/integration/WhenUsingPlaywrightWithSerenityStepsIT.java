package net.serenitybdd.playwright.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test demonstrating Playwright integration with Serenity step libraries.
 * <p>
 * This test shows the recommended pattern for using Playwright with Serenity:
 * <ul>
 *   <li>Programmatic Playwright lifecycle management</li>
 *   <li>Page registration with PlaywrightSerenity</li>
 *   <li>@Step annotated methods for automatic screenshot capture</li>
 * </ul>
 * </p>
 * <p>
 * Run with: mvn verify -pl serenity-playwright -Dit.test=WhenUsingPlaywrightWithSerenityStepsIT
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@Tag("integration")
class WhenUsingPlaywrightWithSerenityStepsIT {

    @Steps
    SamplePlaywrightSteps steps;

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
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setupPage() {
        page = browser.newPage();
        // Register the page with Serenity for automatic screenshot capture
        PlaywrightSerenity.registerPage(page);
    }

    @AfterEach
    void closePage() {
        // Clean up registration
        PlaywrightSerenity.unregisterPage(page);
        if (page != null) {
            page.close();
        }
    }

    @Test
    @DisplayName("Should capture screenshots during step execution")
    void shouldCaptureScreenshotsDuringStepExecution() {
        // Given I navigate to a website
        steps.navigateTo(page, "https://example.com");

        // Then the page should load
        steps.verifyPageLoaded(page);

        // And the title should be correct
        steps.verifyTitleContains(page, "Example Domain");

        // Screenshots are automatically captured after each @Step method
    }

    @Test
    @DisplayName("Should work with getCurrentPage() in step library")
    void shouldWorkWithGetCurrentPage() {
        // The step library can use PlaywrightSerenity.getCurrentPage()
        // instead of receiving the page as a parameter
        steps.navigateTo("https://example.com");

        // Verify the page loaded correctly
        String title = page.title();
        assertThat(title).contains("Example");
    }

    @Test
    @DisplayName("Should support manual screenshot capture")
    void shouldSupportManualScreenshotCapture() {
        steps.navigateTo(page, "https://example.com");

        // Take a manual screenshot at a specific point
        steps.takeScreenshot();

        steps.verifyPageLoaded(page);
    }
}
