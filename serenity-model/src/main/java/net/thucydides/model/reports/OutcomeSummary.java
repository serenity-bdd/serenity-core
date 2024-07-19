package net.thucydides.model.reports;

import net.thucydides.model.domain.TestResult;

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
