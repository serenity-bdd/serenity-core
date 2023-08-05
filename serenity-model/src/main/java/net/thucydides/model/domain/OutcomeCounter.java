package net.thucydides.model.domain;

import net.thucydides.model.reports.TestOutcomeCounter;
import net.thucydides.model.reports.TestOutcomes;

public class OutcomeCounter extends TestOutcomeCounter {

    private final TestOutcomes outcomes;

    public OutcomeCounter(TestType testType, TestOutcomes outcomes) {
        super(testType);
        this.outcomes = outcomes.ofType(testType);
    }

    public int withResult(String expectedResult) {
        return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
    }

    public int percentageWithResult(String expectedResult) {
        return percentageWithResult(TestResult.valueOf(expectedResult.toUpperCase()));
    }

    public int withResult(TestResult expectedResult) {
        int totalResults = 0;
        for(TestOutcome outcome : outcomes.getOutcomes()) {
            totalResults += outcome.countResults(expectedResult);
        }
        return totalResults;
    }

    public int percentageWithResult(TestResult expectedResult) {
        int totalResults = withResult(expectedResult);
        int totalAllResults = getTotal();

        return (totalAllResults == 0) ? 0 : (totalResults * 100) / totalAllResults;
    }

    public int getTotal() {
        return outcomes.getTotal();
    }

    public int withIndeterminateResult() {
        return outcomes.getTotal() - withResult(TestResult.SUCCESS)
                - withResult(TestResult.FAILURE)
                - withResult(TestResult.ERROR)
                - withResult(TestResult.COMPROMISED);
    }

    public int withFailureOrError() {
        return withResult(TestResult.FAILURE) + withResult(TestResult.ERROR) + withResult(TestResult.COMPROMISED);
    }

    public int withCompromisedResults() {
        return withResult(TestResult.COMPROMISED);
    }

    public int withAnyResult() {
        return outcomes.getTotal();
    }
}
