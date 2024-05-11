package net.serenitybdd.junit.runners;

import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepPublisher;
import org.junit.runners.model.Statement;

import java.lang.reflect.InvocationTargetException;

/**
 * A JUnit statement that runs a Serenity-enabled test and then publishes the results via JUnit.
 */
class SerenityStatement extends Statement {

    private final Statement statement;
    private final StepPublisher publisher;

    SerenityStatement(final Statement statement, final StepPublisher publisher) {
        this.statement = statement;
        this.publisher = publisher;
    }

    StepEventBus stepEventBus() {
        if (publisher instanceof BaseStepListener) {
            return ((BaseStepListener) publisher).getEventBus();
        }
        return StepEventBus.getParallelEventBus();
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            updateCurrentEventBusFrom(publisher);
            statement.evaluate();
        } catch (AssertionError assertionError) {
            if (!stepEventBus().aStepInTheCurrentTestHasFailed()) {
                throw assertionError;
            }
        } catch (Throwable throwable) {
            if (throwable.getClass().getSimpleName().endsWith("AssumptionViolatedException")) {
                stepEventBus().assumptionViolated(throwable.getMessage());
            }
        }
        checkForStepFailures();
        checkForAssumptionViolations();
    }

    private void updateCurrentEventBusFrom(StepPublisher publisher) {
        if (StepEventBus.getParallelEventBus() != stepEventBus()) {
            StepEventBus.overrideEventBusWith(stepEventBus());
        }
    }

    private void checkForStepFailures() throws Throwable {
        if (publisher.aStepHasFailed()) {
            if (publisher.getTestFailureCause() != null) {
                throw publisher.getTestFailureCause().toException();
            } else {
                if (publisher.firstFailingStep().isPresent()) {
                    throw publisher.firstFailingStep().get().getException().asException();
                }
            }
        }
    }

    private void checkForAssumptionViolations() {
        if (stepEventBus().assumptionViolated()) {
            try {
                Class<?> assumptionViolatedException = Class.forName("org.junit.AssumptionViolatedException");
                throw ((RuntimeException) assumptionViolatedException.getConstructor(String.class).newInstance(stepEventBus().getAssumptionViolatedMessage()));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
