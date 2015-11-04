package net.thucydides.core.model.results;

import net.thucydides.core.model.TestResult;

public class NextStepCompromisedStrategy implements StepResultMergeStragegy {
    private final TestResult nextStepResult;

    public NextStepCompromisedStrategy(TestResult nextStepResult) {
        this.nextStepResult = nextStepResult;
    }

    @Override
    public TestResult with(TestResult previousResult) {
        return (previousResult.isMoreSevereThan(nextStepResult)) ? previousResult : nextStepResult;
    }
}
