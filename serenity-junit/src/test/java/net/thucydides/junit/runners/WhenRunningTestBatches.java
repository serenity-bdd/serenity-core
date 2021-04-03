package net.thucydides.junit.runners;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.samples.SamplePassingScenario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRunningTestBatches extends AbstractTestStepRunnerTest {

    @Mock
    FirefoxDriver firefoxDriver;

    MockEnvironmentVariables environmentVariables;

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    WebDriverFactory webDriverFactory;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(environmentVariables);
        StepEventBus.getEventBus().clear();

    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        SerenityRunner runner = new SerenityRunner(SamplePassingScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTestSteps().size(), is(4));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_1").getTestSteps().size(), is(3));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_2").getTestSteps().size(), is(2));
    }

}