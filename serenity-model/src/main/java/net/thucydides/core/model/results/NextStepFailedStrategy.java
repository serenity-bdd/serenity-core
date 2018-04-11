package net.thucydides.core.model.results;

import net.thucydides.core.model.TestResult;

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
