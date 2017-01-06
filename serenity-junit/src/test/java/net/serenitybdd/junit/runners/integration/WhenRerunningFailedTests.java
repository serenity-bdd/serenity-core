package net.serenitybdd.junit.runners.integration;


import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.samples.SampleNonWebScenarioWithError;
import net.thucydides.samples.SampleNonWebScenarioWithErrorThatEventuallyWorks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenRerunningFailedTests {

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

    @Test
    public void the_test_runner_reruns_the_failing_tests_with_multiple_failures() throws Throwable {

        environmentVariables.setProperty(ThucydidesSystemProperty.TEST_RETRY_COUNT.getPropertyName(), "3");
        SerenityRunner runner = getNormalTestRunnerUsing(SampleNonWebScenarioWithError.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(3));
    }

    @Test
    public void the_test_runner_reruns_the_failing_tests_and_eventually_succeeds() throws Throwable {

        environmentVariables.setProperty(ThucydidesSystemProperty.TEST_RETRY_COUNT.getPropertyName(), "2");
        SerenityRunner runner = getNormalTestRunnerUsing(SampleNonWebScenarioWithErrorThatEventuallyWorks.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(1));

        assertThat(executedScenarios.get(0).getResult(), is(TestResult.SUCCESS));

        assertThat(executedScenarios.get(0).getStepCount(), is(4));
        assertThat(executedScenarios.get(0).getTestSteps().get(3).getDescription(), containsString("UNSTABLE TEST"));
        assertThat(executedScenarios.get(0).getTestSteps().get(3).getDescription(), containsString("A step that fails on odd tries"));
        assertThat(executedScenarios.get(0).getTestSteps().get(3).getDescription(), containsString("expected:<[tru]e> but was:<[fals]e>"));

        assertThat(executedScenarios.get(0).getTags(), hasItem(TestTag.withName("Retries: 1").andType("unstable test")));
    }

    protected SerenityRunner getNormalTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new SerenityRunner(testClass, factory, configuration);
    }

}
