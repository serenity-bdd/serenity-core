package net.thucydides.model.domain.results;

import net.thucydides.model.domain.TestResult;

/**
 * Created by john on 9/08/2015.
 */
public class NextStepStatusTakesPriorityStrategy implements StepResultMergeStragegy {
    private final TestResult nextStepResult;

    public NextStepStatusTakesPriorityStrategy(TestResult nextStepResult) {
        this.nextStepResult = nextStepResult;
    }

    @Override
    public TestResult with(TestResult previousResult) {
        return nextStepResult;
    }
}
