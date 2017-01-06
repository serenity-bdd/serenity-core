package net.serenitybdd.junit.runners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
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
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
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
