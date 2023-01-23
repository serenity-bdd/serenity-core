package net.serenitybdd.screenplay;

import net.thucydides.core.model.stacktrace.FailureCause;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.events.*;
import net.thucydides.core.steps.session.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class EventBusInterface {


    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusInterface.class);

    public static void castActor(String name) {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            return;
        }
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getEventBus().castActor(name);
        }  else {
            TestSession.addEvent(new CastActorEvent(name));
        }

    }

    public void reportStepFailureFor(Performable todo, Throwable e) {
        ExecutedStepDescription taskDescription = ExecutedStepDescription.of(todo.getClass(), "attemptsTo");
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getEventBus().stepFailed(new StepFailure(taskDescription, e));
        }  else {
            TestSession.addEvent(new StepFailedEvent(new StepFailure(taskDescription, e)));
        }
    }

    public <T> void reportStepFailureFor(Consequence<T> consequence, Throwable e) {
        ExecutedStepDescription consequenceDescription = ExecutedStepDescription.withTitle(consequence.toString());
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getEventBus().stepFailed(new StepFailure(consequenceDescription, e));
        }  else {
            TestSession.addEvent(new StepFailedEvent(new StepFailure(consequenceDescription, e)));
        }
    }

    public int getRunningStepCount() {
        return StepEventBus.getEventBus().getBaseStepListener().getRunningStepCount();
    }

    public void mergePreviousStep() {
        StepEventBus.getEventBus().mergePreviousStep();
    }

    public void updateOverallResult() {
        if (StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            if(!TestSession.isSessionStarted()) {
                StepEventBus.getEventBus().updateOverallResults();
            } else {
                TestSession.addEvent(new UpdateOverallResultsEvent());
            }
        }
    }

    public void startQuestion(String title) {
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(title).asAQuestion());
        } else {
            TestSession.addEvent(new StepStartedEvent(ExecutedStepDescription.withTitle(title).asAQuestion()));
        }
    }

    public void finishQuestion() {
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getEventBus().stepFinished();
        } else {
            TestSession.addEvent(new StepFinishedEvent());
        }
    }

    public void reportStepFinished() {
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getEventBus().stepFinished();
        } else {
            TestSession.addEvent(new StepFinishedEvent());
        }

    }

    public void reportStepIgnored() {
        StepEventBus.getEventBus().stepIgnored();
    }

    public void reportStepSkippedFor(Performable todo) {
        ExecutedStepDescription taskDescription = ExecutedStepDescription.of(todo.getClass(), "performAs");
        StepEventBus.getEventBus().stepStarted(taskDescription);
        StepEventBus.getEventBus().stepIgnored();
    }

    public boolean isBaseStepListenerRegistered() {
        return StepEventBus.getEventBus().isBaseStepListenerRegistered();
    }

    public boolean aStepHasFailed() {
        return isBaseStepListenerRegistered() && StepEventBus.getEventBus().getBaseStepListener().aStepHasFailed();
    }

    public boolean aStepHasFailedInTheCurrentExample() {
        return isBaseStepListenerRegistered() && StepEventBus.getEventBus().getBaseStepListener().aStepHasFailedInTheCurrentExample();
    }

    public FailureCause getFailureCause() {
        return StepEventBus.getEventBus().getBaseStepListener().getCurrentTestOutcome().getTestFailureCause();
    }

    public Optional<FailureCause> failureCause() {
        if ((StepEventBus.getEventBus() == null)
                || (!StepEventBus.getEventBus().isBaseStepListenerRegistered())
                || (StepEventBus.getEventBus().getBaseStepListener().getCurrentTestOutcome() == null)) {
            return Optional.empty();
        }
        if (StepEventBus.getEventBus().getBaseStepListener().aStepHasFailed()) {
            if (StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome().isPresent()) {
                return Optional.ofNullable(StepEventBus.getEventBus().getBaseStepListener().getCurrentTestOutcome().getTestFailureCause());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public boolean shouldIgnoreConsequences() {
        if (StepEventBus.getEventBus().isDryRun()) {
            return true;
        }

        if (StepEventBus.getEventBus().softAssertsActive() && !StepEventBus.getEventBus().currentTestIsSuspended()) {
            return false;
        }
        return (StepEventBus.getEventBus().currentTestIsSuspended() || StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed());
    }

    public void enableSoftAsserts() {
        StepEventBus.getEventBus().enableSoftAsserts();
    }

    public void disableSoftAsserts() {
        StepEventBus.getEventBus().disableSoftAsserts();
    }


    public void assignFactToActor(Actor actor, String fact) {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            return;
        }
        StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.assignFact(actor.getName(), fact)
        );
    }

    public void assignAbilityToActor(Actor actor, String ability) {
        if (!StepEventBus.getEventBus().isBaseStepListenerRegistered()) {
            return;
        }
        if (StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome() == null) {
            return;
        }

        StepEventBus.getEventBus().getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.assignAbility(actor.getName(), ability)
        );
    }

}
