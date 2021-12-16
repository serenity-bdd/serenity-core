package net.serenitybdd.cucumber.web;

import net.serenitybdd.cucumber.integration.*;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.assertj.core.util.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner;
import static org.assertj.core.api.Assertions.assertThat;

public class WhenRunningUIScenarios {

    File outputDirectory;

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Before
    public void setup() {
        outputDirectory = Files.newTemporaryFolder();
        environmentVariables.setProperty("webdriver.driver", "chrome");
        environmentVariables.setProperty("headless.mode", "true");
    }

    @Test
    public void shouldWorkWithTableDrivenScenarios() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumScenario.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        TestOutcome testOutcome = recordedTestOutcomes.get(0);

        // Then
        assertThat(testOutcome.getTitle()).isEqualTo("A scenario that uses selenium");
        assertThat(testOutcome.getStepCount()).isEqualTo(2);
    }

    @Test
    public void aFailingScenarioShouldRecordTheFailure() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingScenario.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        TestOutcome testOutcome = recordedTestOutcomes.get(0);

        // Then
        assertThat(testOutcome.getTitle()).isEqualTo("A failing scenario that uses selenium");
        assertThat(testOutcome.isFailure()).isTrue();
        assertThat(testOutcome.getStepCount()).isEqualTo(2);
    }

    @Test
    public void shouldUseADifferentBrowserIfRequested() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumDifferentBrowserScenario.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        TestOutcome testOutcome = recordedTestOutcomes.get(0);

        // Then
        assertThat(testOutcome.getResult()).isEqualTo(TestResult.SUCCESS);
    }

    @Test
    public void shouldBeAbleToUsePageObjectsDirectly() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumPageObjects.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        TestOutcome testOutcome = recordedTestOutcomes.get(0);

        // Then
        assertThat(testOutcome.getResult()).isEqualTo(TestResult.SUCCESS);
    }

    @Test
    public void shouldRunSubsequentScenariosAfteeAScenarioFails() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingAndPassingScenario.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON)
                .loadFrom(outputDirectory);

        recordedTestOutcomes.sort(Comparator.comparing(TestOutcome::getStartTime));

        // Then
        assertThat(recordedTestOutcomes.get(0).getResult()).isEqualTo(TestResult.FAILURE);
        assertThat(recordedTestOutcomes.get(1).getResult()).isEqualTo(TestResult.SUCCESS);
    }

    @Test
    public void shouldBeAbleToSpecifyTheBrowserInABaseClass() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleSeleniumFailingAndPassingScenario.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON)
                .loadFrom(outputDirectory);

        recordedTestOutcomes.sort(Comparator.comparing(TestOutcome::getStartTime));

        // Then
        assertThat(recordedTestOutcomes.get(0).getResult()).isEqualTo(TestResult.FAILURE);
        assertThat(recordedTestOutcomes.get(1).getResult()).isEqualTo(TestResult.SUCCESS);
    }

    @Test
    public void shouldGenerateATestOutcomeForEachScenario() {
        // Given
        io.cucumber.core.runtime.Runtime runtime = serenityRunnerForCucumberTestRunner(SimpleScenario.class, outputDirectory, environmentVariables);

        // When
        runtime.run();

        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory);
        TestOutcome testOutcome = recordedTestOutcomes.get(0);

        assertThat(testOutcome.getTitle()).isEqualTo("A simple scenario");

        assertThat(testOutcome.getStepCount()).isEqualTo(4);

        List<String> steps = testOutcome.getTestSteps().stream().map(TestStep::getDescription).collect(Collectors.toList());
        assertThat(steps).contains(
                "Given I want to purchase 2 widgets",
                "And a widget costs $5",
                "When I buy the widgets",
                "Then I should be billed $10");
    }
}
