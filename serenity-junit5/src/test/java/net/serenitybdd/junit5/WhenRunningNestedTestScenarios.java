package net.serenitybdd.junit5;

import net.serenitybdd.junit5.samples.integration.JUnit5NestedExample;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRunningNestedTestScenarios extends AbstractTestStepRunnerTest {

    MockEnvironmentVariables environmentVariables;

    WebDriverFactory webDriverFactory;

    @BeforeEach
    public void createATestableDriverFactory() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(environmentVariables);
        StepEventBus.getParallelEventBus().clear();
        StepEventBus.setNoCleanupForStickyBuses(true);
    }

    @Test
    public void should_run_top_level_tests() {

        runTestForClass(JUnit5NestedExample.class);

        StepEventBus stepEventBus = StepEventBus.eventBusForTest("sampleTestForMethodAOuterClass").get();
        TestOutcome outcome = stepEventBus.getBaseStepListener().getTestOutcomes().get(0);

        assertThat(outcome.getResult(), is(TestResult.SUCCESS));
    }

    @Test
    public void should_run_nested_tests() {

        runTestForClass(JUnit5NestedExample.class);

        StepEventBus stepEventBus = StepEventBus.eventBusForTest("sampleTestForMethodA").get();
        TestOutcome outcome = stepEventBus.getBaseStepListener().getTestOutcomes().get(0);

        assertThat(outcome.getResult(), is(TestResult.SUCCESS));
    }

}
