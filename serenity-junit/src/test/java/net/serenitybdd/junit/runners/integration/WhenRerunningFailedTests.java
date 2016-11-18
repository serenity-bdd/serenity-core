package net.serenitybdd.junit.runners.integration;


import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.samples.SampleNonWebScenarioWithError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
    public void the_test_runner_reruns_the_failing_tests_1() throws Throwable {

        environmentVariables.setProperty(ThucydidesSystemProperty.TEST_RETRY_COUNT.getPropertyName(), "3");
        SerenityRunner runner = getNormalTestRunnerUsing(SampleNonWebScenarioWithError.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(6));
    }

    @Test
    public void the_test_runner_reruns_the_failing_tests_2() throws Throwable {

        environmentVariables.setProperty(ThucydidesSystemProperty.TEST_RETRY_COUNT.getPropertyName(), "4");
        SerenityRunner runner = getNormalTestRunnerUsing(SampleNonWebScenarioWithError.class);

        runner.run(new RunNotifier());

        List<TestOutcome> executedScenarios = runner.getTestOutcomes();
        assertThat(executedScenarios.size(), is(7));
    }

    protected SerenityRunner getNormalTestRunnerUsing(Class<?> testClass) throws Throwable {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new SerenityRunner(testClass, factory, configuration);
    }

}
