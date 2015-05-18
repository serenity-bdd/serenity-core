package net.thucydides.core.model;

import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

public class OutcomeCounter extends TestOutcomeCounter {

    private final TestOutcomes outcomes;

    public OutcomeCounter(TestType testType, TestOutcomes outcomes) {
        super(testType);
        this.outcomes = outcomes.ofType(testType);
    }

    public int withResult(String expectedResult) {
        return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
    }

    public int withResult(TestResult expectedResult) {
        return sum(outcomes.getOutcomes(), on(TestOutcome.class).countResults(expectedResult));
    }

    public int getTotal() {
        return outcomes.getTotal();
    }

    public int withIndeterminateResult() {
        return outcomes.getTotal() - withResult(TestResult.SUCCESS)
                - withResult(TestResult.FAILURE)
                - withResult(TestResult.ERROR);
    }

    public int withFailureOrError() {
        return withResult(TestResult.FAILURE) + withResult(TestResult.ERROR);
    }

    public int withAnyResult() {
        return outcomes.getTotal();
    }
}