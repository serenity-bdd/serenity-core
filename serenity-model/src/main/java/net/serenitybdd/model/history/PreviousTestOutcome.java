package net.serenitybdd.model.history;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;

public class PreviousTestOutcome {
    private final String id;
    private final String name;
    private final TestResult result;
    private final String testFailureSummary;

    protected PreviousTestOutcome(String id, String name, TestResult result, String testFailureSummary) {
        this.id = id;
        this.name = name;
        this.result = result;
        this.testFailureSummary = testFailureSummary;
    }

    public static PreviousTestOutcome from(TestOutcome testOutcome) {
        return new PreviousTestOutcome(testOutcome.getId(),
                                       testOutcome.getName(),
                                       testOutcome.getResult(),
                                       testOutcome.getTestFailureSummary());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TestResult getResult() {
        return result;
    }

    public String getTestFailureSummary() {
        return testFailureSummary;
    }
}
