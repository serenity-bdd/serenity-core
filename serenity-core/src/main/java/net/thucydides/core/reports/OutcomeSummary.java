package net.thucydides.core.reports;

import net.thucydides.core.model.TestResult;

public class OutcomeSummary {
    private final TestResult outcome;

    public OutcomeSummary(TestResult outcome) {
        this.outcome = outcome;
    }

    public static OutcomeSummary forOutcome(TestResult outcome) {
        return new OutcomeSummary(outcome);
    }

    public String withCount(int count) {
        return String.format("%s COUNT: %d", outcome.name(), count);
    }
}