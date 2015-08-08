package net.serenitybdd.screenplay;

import net.serenitybdd.screenplay.executionstrategies.*;
import net.serenitybdd.screenplay.state.TestStateEngine;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;

public class Actor {

    private final String name;

    public Actor(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public <T extends Task> void has(T... todos) {
        attemtpsTo(todos);
    }

    public <T extends Task> void attemtpsTo(T... todos) {
        for (Task todo : todos) {
            perform(todo);
        }
    }

    private void perform(Task todo) {
        try {
            todo.performAs(this);
        } catch (Throwable e) {
            StepEventBus.getEventBus().testFailed(e);
        }
    }

    private void ensureAllStepsAppearInTheTestOutcome() {
    }

    private boolean theTaskHasFailed() {
        return (getCurrentOutcome() == TestResult.FAILURE || getCurrentOutcome() == TestResult.ERROR);
    }

    private void completeTestOutcomeStructure() {
        StepEventBus.getEventBus().getCurrentStep();
    }

    private boolean newOutcomeIsDifferentTo(TestResult previousOutcome) {
        return previousOutcome != getCurrentOutcome();
    }

    private void insertError(Throwable e) {
    }

    private void replaySteps() {
    }

    private void windBackSteps() {
    }

    @Step("Then #this should see that {0}")
    public <ANSWER> ANSWER seesThat(Question<ANSWER> question) {
        return question.answeredBy(this);
    }

    public static Actor named(String name) {
        return new Actor(name);
    }

    private TaskExecutionStrategy usingCurrentExecutionStrategy() {
        switch (TestStateEngine.currentState()) {
            case RUNNABLE:
                return new RunnableTaskStrategy(this);
            case DRY_RUN:
                return new DryRunTaskStrategy(this);
            case PENDING:
                return new PendingTaskStrategy(this);
            case FAILED:
                return new PostFailureTaskStrategy(this);
            default:
                return new PostFailureTaskStrategy(this);
        }
    }

    public TestResult getCurrentOutcome() {
        return StepEventBus.getEventBus().resultSoFar().or(TestResult.UNDEFINED);
    }
}
