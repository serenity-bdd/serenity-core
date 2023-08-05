package net.serenitybdd.junit.runners;

import net.thucydides.core.configuration.WebDriverConfiguration;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.core.webdriver.DriverConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
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
