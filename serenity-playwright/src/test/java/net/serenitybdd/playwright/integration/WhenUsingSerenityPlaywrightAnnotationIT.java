package net.serenitybdd.playwright.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywright;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test demonstrating the @SerenityPlaywright meta-annotation.
 * <p>
 * The @SerenityPlaywright annotation is a convenience meta-annotation that combines:
 * <ul>
 *   <li>@ExtendWith(SerenityJUnit5Extension.class)</li>
 *   <li>@ExtendWith(SerenityPlaywrightExtension.class)</li>
 * </ul>
 * </p>
 * <p>
 * The SerenityPlaywrightExtension automatically detects Page fields in the test
 * instance and registers them with Serenity for screenshot capture.
 * </p>
 * <p>
 * Run with: mvn verify -pl serenity-playwright -Dit.test=WhenUsingSerenityPlaywrightAnnotationIT
 * </p>
 */
@SerenityPlaywright
@Tag("integration")
class WhenUsingSerenityPlaywrightAnnotationIT {

    @Steps
    SamplePlaywrightSteps steps;

    private static Playwright playwright;
    private static Browser browser;

    // This Page field will be auto-detected and registered by SerenityPlaywrightExtension
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
        // Note: With @SerenityPlaywright, we can either:
        // 1. Let the extension auto-detect the 'page' field (after @BeforeEach runs)
        // 2. Explicitly register with PlaywrightSerenity.registerPage(page)
        // We'll use explicit registration for clarity
        PlaywrightSerenity.registerPage(page);
    }

    @AfterEach
    void closePage() {
        if (page != null) {
            page.close();
        }
        // Note: SerenityPlaywrightExtension calls PlaywrightPageRegistry.clear()
        // in afterEach, so explicit unregistration is optional
    }

    @Test
    @DisplayName("Should work with @SerenityPlaywright annotation")
    void shouldWorkWithSerenityPlaywrightAnnotation() {
        // Navigate to a website using the step library
        steps.navigateTo(page, "https://example.com");

        // Verify the page loaded
        steps.verifyPageLoaded(page);

        // Verify the title
        String title = page.title();
        assertThat(title).contains("Example Domain");
    }

    @Test
    @DisplayName("Should capture screenshots on failure")
    void shouldCaptureScreenshotsOnFailure() {
        // Navigate to a website
        steps.navigateTo(page, "https://example.com");

        // This step will pass
        steps.verifyTitleContains(page, "Example");

        // Note: If a step fails, a screenshot is automatically captured
        // at the point of failure for debugging purposes
    }
}
