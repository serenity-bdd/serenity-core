package net.serenitybdd.playwright.integration;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test demonstrating Playwright's {@code @UsePlaywright} annotation
 * combined with Serenity BDD.
 * <p>
 * This pattern allows Playwright to manage the full browser lifecycle
 * (Playwright, Browser, BrowserContext, Page) while Serenity handles
 * reporting and screenshot capture at step boundaries.
 * </p>
 * <p>
 * Run with: mvn verify -pl serenity-playwright -Dit.test=WhenUsingUsePlaywrightWithSerenityIT
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(WhenUsingUsePlaywrightWithSerenityIT.HeadlessChromiumOptions.class)
@Tag("integration")
class WhenUsingUsePlaywrightWithSerenityIT {

    @Steps
    SamplePlaywrightSteps steps;

    /**
     * Custom OptionsFactory for headless Chromium with sandbox disabled.
     */
    public static class HeadlessChromiumOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options()
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
                    )
                    .setHeadless(true);
        }
    }

    @Test
    @DisplayName("Should automatically register Playwright-managed Page with Serenity")
    void shouldAutoRegisterPlaywrightManagedPage(Page page) {
        // Page is created and injected by @UsePlaywright
        // SerenityPlaywrightExtension intercepts and registers it with Serenity
        assertThat(PlaywrightSerenity.getCurrentPage()).isSameAs(page);
    }

    @Test
    @DisplayName("Should capture screenshots during step execution with @UsePlaywright")
    void shouldCaptureScreenshotsDuringStepExecution(Page page) {
        // Navigate using a @Step method - screenshots are captured automatically
        steps.navigateTo(page, "https://example.com");

        // Verify the page loaded
        steps.verifyPageLoaded(page);

        // Verify the title
        steps.verifyTitleContains(page, "Example Domain");
    }

    @Test
    @DisplayName("Should support getCurrentPage() in step libraries with @UsePlaywright")
    void shouldSupportGetCurrentPageInStepLibraries(Page page) {
        // Step libraries can use PlaywrightSerenity.getCurrentPage()
        // instead of receiving the page as a parameter
        steps.navigateTo("https://example.com");

        String title = page.title();
        assertThat(title).contains("Example");
    }
}
