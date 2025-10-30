package net.serenitybdd.junit5.extensions;

import net.serenitybdd.junit5.AbstractTestStepRunnerTest;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WhenRunningTestBatches extends AbstractTestStepRunnerTest {

    @Mock
    FirefoxDriver firefoxDriver;

    MockEnvironmentVariables environmentVariables;


    WebDriverFactory webDriverFactory;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(environmentVariables);
        StepEventBus.getParallelEventBus().clear();
    }

    /*@Test TODO clarify
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        runTestForClass(SamplePassingScenario.class);

        List<TestOutcome> executedSteps = StepEventBus.getParallelEventBus().getBaseStepListener().getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTestSteps().size(), is(4));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("edge_case_1").getTestSteps().size(), is(3));
        assertThat(inTheTestOutcomes(executedSteps).theOutcomeFor("edge_case_2").getTestSteps().size(), is(2));
    }*/


}
