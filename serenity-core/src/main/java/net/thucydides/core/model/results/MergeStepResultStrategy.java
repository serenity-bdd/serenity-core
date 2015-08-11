package net.thucydides.core.model.results;

import net.thucydides.core.model.TestResult;

/**
 * Created by john on 9/08/2015.
 */
public class MergeStepResultStrategy {
    public static StepResultMergeStragegy whenNextStepResultis(TestResult nextStepResult) {
        switch (nextStepResult) {
            case SKIPPED: return new NextStepWasSkippedStrategy(nextStepResult);
            case FAILURE: return new NextStepFailedStrategy(nextStepResult);
            default: return new NextStepStatusTakesPriorityStrategy(nextStepResult);
        }
    }
}
