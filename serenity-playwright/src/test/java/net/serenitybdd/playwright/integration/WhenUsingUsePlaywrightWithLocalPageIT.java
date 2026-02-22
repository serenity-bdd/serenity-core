package net.serenitybdd.playwright.integration;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.junit5.SerenityPlaywrightExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test using local HTML content with {@code @UsePlaywright} and Serenity.
 * <p>
 * This test demonstrates that Playwright manages the browser lifecycle via
 * {@code @UsePlaywright} while Serenity captures screenshots at step boundaries.
 * Uses inline HTML so no network access is required.
 * </p>
 * <p>
 * Run with: mvn verify -pl serenity-playwright -Dit.test=WhenUsingUsePlaywrightWithLocalPageIT
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(WhenUsingUsePlaywrightWithLocalPageIT.HeadlessOptions.class)
@Tag("integration")
class WhenUsingUsePlaywrightWithLocalPageIT {

    @Steps
    LocalSteps localSteps;

    public static class HeadlessOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options()
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-gpu"))
                    )
                    .setHeadless(true);
        }
    }

    private static final String SAMPLE_HTML = """
        <!DOCTYPE html>
        <html>
        <head><title>UsePlaywright Test</title></head>
        <body>
            <h1>Hello from UsePlaywright</h1>
            <input type="text" id="name" placeholder="Enter name">
            <button id="greetBtn" onclick="document.getElementById('result').textContent = 'Hello, ' + document.getElementById('name').value + '!'">Greet</button>
            <div id="result"></div>
        </body>
        </html>
        """;

    @BeforeEach
    void loadPage(Page page) {
        // Page is injected by @UsePlaywright and auto-registered with Serenity
        page.setContent(SAMPLE_HTML);
    }

    @Test
    @DisplayName("Page should be registered with Serenity after @BeforeEach")
    void pageShouldBeRegisteredAfterBeforeEach(Page page) {
        assertThat(PlaywrightSerenity.getCurrentPage()).isSameAs(page);
    }

    @Test
    @DisplayName("Should capture screenshots during step execution")
    void shouldCaptureScreenshotsDuringStepExecution(Page page) {
        localSteps.verifyHeading(page, "Hello from UsePlaywright");
        localSteps.enterName(page, "Serenity");
        localSteps.clickGreet(page);
        localSteps.verifyGreeting(page, "Hello, Serenity!");
    }

    @Test
    @DisplayName("Step libraries should access page via getCurrentPage()")
    void stepLibrariesShouldAccessPageViaGetCurrentPage(Page page) {
        localSteps.verifyHeadingUsingCurrentPage("Hello from UsePlaywright");
    }

    public static class LocalSteps {

        @Step("Verify heading contains '{1}'")
        public void verifyHeading(Page page, String expected) {
            assertThat(page.locator("h1").textContent()).contains(expected);
        }

        @Step("Verify heading contains '{0}' using getCurrentPage()")
        public void verifyHeadingUsingCurrentPage(String expected) {
            Page page = PlaywrightSerenity.getCurrentPage();
            assertThat(page.locator("h1").textContent()).contains(expected);
        }

        @Step("Enter name: {1}")
        public void enterName(Page page, String name) {
            page.fill("#name", name);
        }

        @Step("Click the greet button")
        public void clickGreet(Page page) {
            page.click("#greetBtn");
        }

        @Step("Verify greeting is '{1}'")
        public void verifyGreeting(Page page, String expected) {
            assertThat(page.locator("#result").textContent()).isEqualTo(expected);
        }
    }
}
