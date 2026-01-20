package net.serenitybdd.junit5;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.environment.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.Arrays;
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

    public TestOutcomeChecker inTheTestOutcomes(List<TestOutcome> testOutcomes) {
        return new TestOutcomeChecker(testOutcomes);
    }

    public static class TestOutcomeChecker {
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

    public void runTestForClass(Class<?>... testClass){
        List<ClassSelector> selectors = Arrays.stream(testClass)
                .map(DiscoverySelectors::selectClass)
                .toList();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectors)
                .build();

        LauncherFactory.create().execute(request);
    }

    public static TestOutcome getTestOutcomeFor(String testName) {
        return StepEventBus.eventBusForTest(testName).get().getBaseStepListener().getTestOutcomes().get(0);
    }
}
