package net.serenitybdd.junit.runners.integration;


import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * It makes sure that the steps called in the
 */
public class WhenRunningStepsInCleanupMethod {

    MockEnvironmentVariables environmentVariables;

    public static boolean cleanupStepExecuted = false;

    @Before
    public void init() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void step_contained_in_after_method_is_not_skipped() throws Exception {
        SerenityRunner runner = new SerenityRunner(SampleTest.class,
                new WebDriverFactory(environmentVariables),
                new SystemPropertiesConfiguration(environmentVariables));

        CapturingNotifier notifier =  new CapturingNotifier();
        runner.run(notifier);
        assertThat(notifier.failed, is(true));
        assertThat(cleanupStepExecuted, is(true));
    }



    public static class SampleTest {

        @Steps
        TestSteps steps;

        @After
        public void cleanUp() throws IOException {
            steps.cleanupStep();
        }

        @Test
        public void shouldFailExecutingThisTest() {
            steps.exceptionThrowingStep();
        }
    }

    public static class TestSteps {

        @Step
        public void exceptionThrowingStep() {
            throw new RuntimeException();
        }

        @Step
        public void cleanupStep() {
            cleanupStepExecuted = true;
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
