package net.thucydides.junit.runners.integration;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.List;

import static net.thucydides.core.webdriver.SystemPropertiesConfiguration.MAX_RETRIES;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class WhenRetryingFailedTests {

    MockEnvironmentVariables environmentVariables;

    @Before
    public void init() {
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty(MAX_RETRIES, "0");
    }

    @Test
    public void result_is_a_pass_despite_initial_failure() throws Exception {
        environmentVariables.setProperty(MAX_RETRIES, "5");
        ThucydidesRunner runner = new ThucydidesRunner(FailThenPassSample.class,
                                                       new WebDriverFactory(environmentVariables),
                                                       new SystemPropertiesConfiguration(environmentVariables));

        CapturingNotifier notifier = new CapturingNotifier();
        runner.run(notifier);
        List<TestOutcome> outcomes = runner.getTestOutcomes();

        assertThat(outcomes.size(), is(1));
        assertThat(outcomes.get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(notifier.failed, is(false));
    }

    public static class FailThenPassSample {

        private static int failureCount;

        @Steps
        public FailThenPassSteps failThenPassSteps;

        @BeforeClass
        public static void initCounter() {
            failureCount = 0;
        }

        @Test
        public void fail_twice_then_pass() {
            failThenPassSteps.attemptSomething((failureCount++ < 2));
        }
    }

    public static class FailThenPassSteps {

        @Step
        public void attemptSomething(boolean shouldFail) {
            if (shouldFail) {
                fail();
            }
        }
    }

    static class CapturingNotifier extends RunNotifier {

        public boolean failed = false;

        @Override
        public void fireTestFailure(Failure failure) {
            failed = true;
            super.fireTestFailure(failure);
        }
    }
}
