package net.thucydides.core.steps;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;

public class CurrentTestResult {
    public static TestResult forTestOutcome(TestOutcome testOutcome, int currentExample) {
        if (isCucumber(testOutcome) && testOutcome.isDataDriven() && !testOutcome.getTestSteps().isEmpty()) {
            if (currentExample > 0) {
                if (testOutcome.getTestSteps().size() >= currentExample) {
                    return testOutcome.fromStep(currentExample - 1).getResult();
                } else {
                    return TestResult.UNDEFINED;
                }
            } else {
                return testOutcome.fromStep(0).getResult();
            }
        } else {
            return testOutcome.getResult();
        }
    }

    public static boolean isCucumber(TestOutcome testOutcome) {
        return "Cucumber".equalsIgnoreCase(testOutcome.getTestSource());
    }
}
