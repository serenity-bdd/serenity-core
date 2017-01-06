package net.serenitybdd.junit.runners;

import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.batches.BatchManagerProvider;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.samples.SampleCSVDataDrivenScenarioWithDelays;
import net.thucydides.samples.SampleDataDrivenScenarioWithDelays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * User: YamStranger
 * Date: 11/16/15
 * Time: 10:10 PM
 */
public class WhenRunningADataDrivenTestScenarioToCheckDuration {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    protected SerenityParameterizedRunner getStubbedTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        BatchManager batchManager = new BatchManagerProvider(configuration).get();
        return new SerenityParameterizedRunner(testClass, configuration, factory, batchManager);
    }

    @Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes() throws Throwable  {
        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing(SampleDataDrivenScenarioWithDelays.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();

        for (TestOutcome test : aggregatedScenarios) {
            long duration = 0;
            for (TestStep step : test.getTestSteps()) {
                duration += step.getDuration();
            }
            assertThat("Duration of tests is invalid", test.getDuration() > 0, is(true));
            assertThat("Duration of tests invalid not propagated", test.getDuration(), is(duration));
            assertThat("Duration in seconds of tests invalid", test.getDurationInSeconds() > 0, is(true));
        }
    }
    @Test
    public void a_data_driven_test_driver_should_aggregate_test_outcomes2() throws Throwable  {
        SerenityParameterizedRunner runner = getStubbedTestRunnerUsing( SampleCSVDataDrivenScenarioWithDelays.class);
        runner.run(new RunNotifier());

        List<TestOutcome> aggregatedScenarios = ParameterizedTestsOutcomeAggregator.from(runner).aggregateTestOutcomesByTestMethods();

        for (TestOutcome test : aggregatedScenarios) {
            long duration = 0;
            for (TestStep step : test.getTestSteps()) {
                duration += step.getDuration();
            }
            assertThat("Duration of tests is invalid", test.getDuration() > 0, is(true));
            assertThat("Duration of tests invalid not propagated", test.getDuration(), is(duration));
            assertThat("Duration in seconds of tests invalid", test.getDurationInSeconds() > 0, is(true));
        }
    }
}
