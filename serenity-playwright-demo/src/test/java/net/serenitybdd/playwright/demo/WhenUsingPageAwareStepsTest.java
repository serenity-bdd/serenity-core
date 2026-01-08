package net.serenitybdd.playwright.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.demo.steps.PageAwareSteps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Demonstrates using step libraries that access the page via PlaywrightSerenity.getCurrentPage().
 * <p>
 * This pattern is useful when you want cleaner step method signatures without
 * passing the Page object to every method.
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("When using page-aware steps")
class WhenUsingPageAwareStepsTest {

    @Steps
    PageAwareSteps steps;

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

    @Test
    @DisplayName("Should access page without passing it as parameter")
    void shouldAccessPageWithoutPassingAsParameter() {
        // Notice: no page parameter needed in step methods!
        steps.openUrl("https://www.example.com");
        steps.verifyPageTitleContains("Example");
        steps.verifyUrlContains("example.com");
    }

    @Test
    @DisplayName("Should support manual screenshots in steps")
    void shouldSupportManualScreenshotsInSteps() {
        steps.openUrl("https://www.example.com");

        // This step manually triggers a screenshot
        steps.takeScreenshotNow();

        steps.verifyPageTitleContains("Example");
    }
}
