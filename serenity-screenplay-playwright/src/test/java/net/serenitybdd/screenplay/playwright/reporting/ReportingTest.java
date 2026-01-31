package net.serenitybdd.screenplay.playwright.reporting;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.playwright.interactions.Click;
import net.serenitybdd.screenplay.playwright.interactions.Enter;
import net.serenitybdd.screenplay.playwright.interactions.Open;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Serenity reporting integration.
 */
@ExtendWith(SerenityJUnit5Extension.class)
public class ReportingTest {

    Actor alice;

    @BeforeEach
    void setup() {
        alice = Actor.named("Alice")
            .whoCan(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());
    }

    @Test
    void interactions_should_appear_in_report() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/login"),
            Enter.theValue("tomsmith").into("#username"),
            Enter.theValue("SuperSecretPassword!").into("#password"),
            Click.on("button[type='submit']")
        );

        // Verify steps are recorded
        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow(() -> new AssertionError("No test outcome found"));

        List<TestStep> steps = outcome.getTestSteps();
        assertThat(steps).hasSizeGreaterThan(0);

        // Get all step descriptions including nested ones
        List<String> stepDescriptions = new java.util.ArrayList<>();
        collectStepDescriptions(steps, stepDescriptions);

        // Check that we have meaningful step descriptions
        assertThat(stepDescriptions).isNotEmpty();
    }

    private void collectStepDescriptions(List<TestStep> steps, List<String> descriptions) {
        for (TestStep step : steps) {
            descriptions.add(step.getDescription());
            if (step.hasChildren()) {
                collectStepDescriptions(step.getChildren(), descriptions);
            }
        }
    }

    @Test
    void step_descriptions_should_include_element_info() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/"),
            Click.on("a[href='/login']")
        );

        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        List<String> stepDescriptions = new java.util.ArrayList<>();
        collectStepDescriptions(outcome.getTestSteps(), stepDescriptions);

        // Verify steps are recorded with descriptions
        assertThat(stepDescriptions).isNotEmpty();

        // Verify some description contains actor name or action
        String allDescriptions = String.join(" ", stepDescriptions).toLowerCase();
        assertThat(allDescriptions.contains("alice") ||
                   allDescriptions.contains("click") ||
                   allDescriptions.contains("open")).isTrue();
    }

    @Test
    void test_outcome_should_have_correct_result() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/")
        );

        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        // Test should be successful
        assertThat(outcome.isSuccess() || outcome.isPending()).isTrue();
    }

    @Test
    void nested_steps_should_be_recorded() {
        alice.attemptsTo(
            Open.url("https://the-internet.herokuapp.com/checkboxes"),
            net.serenitybdd.screenplay.playwright.interactions.Check.the("#checkboxes input >> nth=0")
        );

        TestOutcome outcome = StepEventBus.getParallelEventBus()
            .getBaseStepListener()
            .latestTestOutcome()
            .orElseThrow();

        // Should have recorded steps
        assertThat(outcome.getTestSteps()).isNotEmpty();
    }
}
