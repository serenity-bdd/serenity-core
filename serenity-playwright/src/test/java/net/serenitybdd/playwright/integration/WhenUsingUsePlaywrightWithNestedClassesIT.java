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
 * Integration test verifying that {@code @UsePlaywright} on an outer class works correctly
 * with JUnit 5 {@code @Nested} inner classes.
 * <p>
 * This ensures that {@link SerenityPlaywrightExtension} correctly defers Page parameter
 * resolution to Playwright's PageExtension when {@code @UsePlaywright} is declared on an
 * enclosing class rather than the direct test class.
 * </p>
 * <p>
 * Run with: mvn verify -pl serenity-playwright -Dit.test=WhenUsingUsePlaywrightWithNestedClassesIT
 * </p>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
@UsePlaywright(WhenUsingUsePlaywrightWithNestedClassesIT.HeadlessOptions.class)
@Tag("integration")
class WhenUsingUsePlaywrightWithNestedClassesIT {

    @Steps
    NestedSteps steps;

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
        <head><title>Nested Test</title></head>
        <body>
            <h1>Hello from Nested Test</h1>
            <p id="status">ready</p>
        </body>
        </html>
        """;

    @BeforeEach
    void loadPage(Page page) {
        page.setContent(SAMPLE_HTML);
    }

    @Nested
    @DisplayName("When running tests in a nested class")
    class WhenRunningInNestedClass {

        @Test
        @DisplayName("Page should be resolved without parameter conflict")
        void pageShouldBeResolvedWithoutConflict(Page page) {
            assertThat(page).isNotNull();
            assertThat(page.title()).isEqualTo("Nested Test");
        }

        @Test
        @DisplayName("Page should be registered with Serenity")
        void pageShouldBeRegisteredWithSerenity(Page page) {
            assertThat(PlaywrightSerenity.getCurrentPage()).isSameAs(page);
        }

        @Test
        @DisplayName("Step libraries should work in nested classes")
        void stepLibrariesShouldWork(Page page) {
            steps.verifyHeading(page, "Hello from Nested Test");
            steps.verifyStatus(page, "ready");
        }
    }

    @Nested
    @DisplayName("When running tests in another nested class")
    class WhenRunningInAnotherNestedClass {

        @Test
        @DisplayName("Page should also work in sibling nested classes")
        void pageShouldAlsoWork(Page page) {
            assertThat(page).isNotNull();
            steps.verifyHeading(page, "Hello from Nested Test");
        }
    }

    public static class NestedSteps {

        @Step("Verify heading contains '{1}'")
        public void verifyHeading(Page page, String expected) {
            assertThat(page.locator("h1").textContent()).contains(expected);
        }

        @Step("Verify status is '{1}'")
        public void verifyStatus(Page page, String expected) {
            assertThat(page.locator("#status").textContent()).isEqualTo(expected);
        }
    }
}
