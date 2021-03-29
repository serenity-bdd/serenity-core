package net.thucydides.junit.runners;

import net.serenitybdd.junit.runners.SerenityRunner;
import serenitycore.net.thucydides.core.configuration.WebDriverConfiguration;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.model.TestResult;
import serenitymodel.net.thucydides.core.util.MockEnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.DriverConfiguration;
import serenitycore.net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.Before;
import org.junit.runners.model.InitializationError;

import java.util.List;

public abstract class AbstractTestStepRunnerTest {

    protected MockEnvironmentVariables environmentVariables;

    public AbstractTestStepRunnerTest() {
        super();
    }

    @Before
    public void initEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
    }

    protected SerenityRunner getTestRunnerUsing(Class<?> testClass) throws InitializationError {
        DriverConfiguration configuration = new WebDriverConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        return new SerenityRunner(testClass, factory, configuration);
    }

    public TestOutcomeChecker inTheTesOutcomes(List<TestOutcome> testOutcomes) {
        return new TestOutcomeChecker(testOutcomes);
    }

    public class TestOutcomeChecker {
        private final List<TestOutcome> testOutcomes;

        public TestOutcomeChecker(List<TestOutcome> testOutcomes) {
            this.testOutcomes = testOutcomes;
        }


        public TestOutcome theOutcomeFor(String methodName) {
            return matchingTestOutcomeCalled(methodName);
        }

        public TestResult theResultFor(String methodName) {
            return matchingTestOutcomeCalled(methodName).getResult();
        }

        private TestOutcome matchingTestOutcomeCalled(String methodName) {
            for (TestOutcome testOutcome : testOutcomes) {
                if (testOutcome.getName().equals(methodName)) {
                    return testOutcome;
                }
            }
            throw new AssertionError("No matching test method called " + methodName);
        }
    }
}
