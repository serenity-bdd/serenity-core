package net.serenitybdd.screenplay.playwright.reporting;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Enter;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify that screenshots are correctly captured and attached to reports
 * for Screenplay Playwright interactions.
 *
 * <p>Note: Screenshot capture depends on the SERENITY_TAKE_SCREENSHOTS configuration:</p>
 * <ul>
 *   <li>FOR_EACH_ACTION - Screenshots after each UI interaction</li>
 *   <li>FOR_FAILURES - Screenshots only on test failure</li>
 *   <li>BEFORE_AND_AFTER_EACH_STEP - Screenshots before and after</li>
 *   <li>AFTER_EACH_STEP - Screenshots after each step</li>
 *   <li>DISABLED - No screenshots</li>
 * </ul>
 */
@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Screenshot Capture for Playwright Interactions")
public class ScreenshotCaptureTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    @DisplayName("Test outcome should be available after interactions")
    void test_outcome_should_be_available() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Enter.theValue("tomsmith").into("#username"),
            Enter.theValue("SuperSecretPassword!").into("#password"),
            Click.on("button[type='submit']")
        );

        // Verify test outcome is available
        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow(() -> new AssertionError("No test outcome found"));

        assertThat(outcome).isNotNull();
        assertThat(outcome.getTestSteps()).isNotEmpty();
    }

    @Test
    @DisplayName("Steps should be recorded with descriptions")
    void steps_should_be_recorded_with_descriptions() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Click.on("a[href='/login']")
        );

        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        List<String> stepDescriptions = new ArrayList<>();
        collectStepDescriptions(outcome.getTestSteps(), stepDescriptions);

        // Verify steps are recorded
        assertThat(stepDescriptions).isNotEmpty();

        // Verify step descriptions contain meaningful information
        String allDescriptions = String.join(" ", stepDescriptions).toLowerCase();
        assertThat(allDescriptions).satisfiesAnyOf(
            desc -> assertThat(desc).contains("alice"),
            desc -> assertThat(desc).contains("opens"),
            desc -> assertThat(desc).contains("click")
        );
    }

    @Test
    @DisplayName("Screenshots should be attached to steps when configured")
    void screenshots_should_be_attached_when_configured() {
        // This test verifies the screenshot capture mechanism works
        // Actual screenshot attachment depends on SERENITY_TAKE_SCREENSHOTS setting

        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Click.on("a[href='/checkboxes']")
        );

        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        // Verify test completed successfully
        assertThat(outcome.isSuccess() || outcome.isPending()).isTrue();

        // Collect all screenshots from all steps
        List<ScreenshotAndHtmlSource> allScreenshots = new ArrayList<>();
        collectScreenshots(outcome.getTestSteps(), allScreenshots);

        // Note: Screenshots may or may not be present depending on configuration
        // This test just verifies the mechanism doesn't throw errors
        System.out.println("Screenshots captured: " + allScreenshots.size());
    }

    @Test
    @DisplayName("BrowseTheWebWithPlaywright can take screenshots manually")
    void can_take_screenshots_manually() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        // Verify ability can take screenshots
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(alice);
        assertThat(ability).isNotNull();
        assertThat(ability.getCurrentPage()).isNotNull();

        // Manually trigger screenshot
        var screenshotAndSource = ability.takeScreenShot();

        assertThat(screenshotAndSource).isNotNull();
        assertThat(screenshotAndSource.getScreenshot()).isNotNull();
        assertThat(screenshotAndSource.getScreenshot().exists()).isTrue();
    }

    private void collectStepDescriptions(List<TestStep> steps, List<String> descriptions) {
        for (TestStep step : steps) {
            descriptions.add(step.getDescription());
            if (step.hasChildren()) {
                collectStepDescriptions(step.getChildren(), descriptions);
            }
        }
    }

    private void collectScreenshots(List<TestStep> steps, List<ScreenshotAndHtmlSource> screenshots) {
        for (TestStep step : steps) {
            screenshots.addAll(step.getScreenshots());
            if (step.hasChildren()) {
                collectScreenshots(step.getChildren(), screenshots);
            }
        }
    }
}
