package net.thucydides.core.model;

import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;

public class ScenarioOutcomeCounter extends TestOutcomeCounter {

    private final TestOutcomes outcomes;

    public ScenarioOutcomeCounter(TestType testType, TestOutcomes outcomes) {
        super(testType);
        this.outcomes = outcomes.ofType(testType);
    }

    public int withResult(String expectedResult) {
        return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
    }

    public int withResult(TestResult expectedResult) {
        int totalResults = 0;
        for(TestOutcome outcome : outcomes.getOutcomes()) {
            totalResults += (outcome.getResult() == expectedResult)  ? 1 : 0;
        }
        return totalResults;
    }

    public int getTotal() {
        return outcomes.getTotalTestScenarios();
    }

    public int withIndeterminateResult() {
        return getTotal() - withResult(TestResult.SUCCESS)
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