package net.serenitybdd.screenplay.playwright.reporting;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.TestLocalEnvironmentVariables;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that HTML source is recorded for failed tests when SERENITY_STORE_HTML is FAILURES.
 * <p>
 * This test does NOT use SerenityJUnit5Extension because it needs to simulate a test failure
 * on the TestOutcome without the extension rethrowing the simulated failure.
 * </p>
 */
public class StoreHtmlOnFailureTest {

    private BaseStepListener baseStepListener;
    private BrowseTheWebWithPlaywright ability;

    @BeforeEach
    void setup() throws Exception {
        TestLocalEnvironmentVariables.setProperty("serenity.store.html", "FAILURES");

        File outputDir = Files.createTempDirectory("serenity-test").toFile();
        baseStepListener = new BaseStepListener(outputDir);
        StepEventBus.getParallelEventBus().registerListener(baseStepListener);
        StepEventBus.getParallelEventBus().testStarted("store_html_on_failure_test");

        ability = BrowseTheWebWithPlaywright.usingTheDefaultConfiguration();
        Actor alice = Actor.named("Alice").whoCan(ability);
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );
    }

    @AfterEach
    void teardown() {
        try {
            ability.tearDown();
        } catch (Exception e) {
            // ignore cleanup errors
        }
        StepEventBus.getParallelEventBus().testFinished();
        StepEventBus.getParallelEventBus().dropListener(baseStepListener);
        TestLocalEnvironmentVariables.clear();
    }

    @Test
    @DisplayName("HTML source should be recorded for failed tests when SERENITY_STORE_HTML is FAILURES")
    void html_source_should_be_recorded_for_failed_tests() {
        // Simulate a test failure so that currentTestFailed() returns true
        TestOutcome currentOutcome = baseStepListener.getCurrentTestOutcome();
        currentOutcome.determineTestFailureCause(new RuntimeException("simulated failure"));

        ScreenshotAndHtmlSource screenshotAndSource = ability.takeScreenShot();

        assertThat(screenshotAndSource.getScreenshot()).isNotNull();
        assertThat(screenshotAndSource.getHtmlSource()).isPresent();
        assertThat(screenshotAndSource.getHtmlSource().get().exists()).isTrue();
    }
}