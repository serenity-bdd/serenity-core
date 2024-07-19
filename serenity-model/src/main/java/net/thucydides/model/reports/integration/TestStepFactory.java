package net.thucydides.model.reports.integration;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;

import static net.thucydides.model.domain.TestResult.*;

public class TestStepFactory {

    public static TestStep successfulTestStepCalled(String description) {
        return createNewTestStep(description, SUCCESS);
    }

    public static TestStep failingTestStepCalled(String description) {
        return createNewTestStep(description, FAILURE);
    }

    public static TestStep errorTestStepCalled(String description) {
        return createNewTestStep(description, ERROR);
    }

    public static TestStep skippedTestStepCalled(String description) {
        return createNewTestStep(description, SKIPPED);
    }

    public static TestStep ignoredTestStepCalled(String description) {
        return createNewTestStep(description, IGNORED);
    }

    public static TestStep pendingTestStepCalled(String description) {
        return createNewTestStep(description, PENDING);
    }

    public static TestStep flakyTestStepCalled(String description) {
        return createNewFlakyTestStep(description);
    }

    private static TestStep createNewTestStep(String description, TestResult result) {
        TestStep step = new TestStep(description);
        step.setResult(result);
        return step;
    }

    private static TestStep createNewFlakyTestStep(String description) {
//        return TestStep.forStepCalled(description).withResult(TestResult.IGNORED);
        return TestStep.forStepCalled(description).withResult(TestResult.UNDEFINED);
    }

}
