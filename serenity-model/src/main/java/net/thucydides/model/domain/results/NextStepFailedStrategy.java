package net.thucydides.model.domain.results;

import net.thucydides.model.domain.TestResult;

public class NextStepFailedStrategy implements StepResultMergeStragegy {
    private final TestResult nextStepResult;

    public NextStepFailedStrategy(TestResult nextStepResult) {
        this.nextStepResult = nextStepResult;
    }

    @Override
    public TestResult with(TestResult previousResult) {
        return (previousResult.isMoreSevereThan(nextStepResult)) ? previousResult : nextStepResult;
    }
}
