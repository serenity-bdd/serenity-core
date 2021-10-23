package net.thucydides.core.model.results;

import net.thucydides.core.model.TestResult;

public class MergeStepResultStrategy {
    public static StepResultMergeStragegy whenNextStepResultIs(TestResult nextStepResult) {
        switch (nextStepResult) {
            case SKIPPED: return new NextStepWasSkippedStrategy(nextStepResult);
            case FAILURE: return new NextStepFailedStrategy(nextStepResult);
            case ABORTED: return new NextStepCompromisedStrategy(nextStepResult);
            case COMPROMISED: return new NextStepCompromisedStrategy(nextStepResult);
            default: return new NextStepStatusTakesPriorityStrategy(nextStepResult);
        }
    }
}
