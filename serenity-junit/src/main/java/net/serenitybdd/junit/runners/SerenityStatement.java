package net.serenitybdd.junit.runners;

import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepPublisher;
import org.junit.AssumptionViolatedException;
import org.junit.runners.model.Statement;

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
        return StepEventBus.getEventBus();
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            updateCurrentEventBusFrom(publisher);
            statement.evaluate();
        } catch (AssumptionViolatedException assumptionViolated) {
            stepEventBus().assumptionViolated(assumptionViolated.getMessage());
        } catch (AssertionError assertionError) {
            if (!stepEventBus().aStepInTheCurrentTestHasFailed()) {
                throw assertionError;
            }
        }
        checkForStepFailures();
        checkForAssumptionViolations();
    }

    private void updateCurrentEventBusFrom(StepPublisher publisher) {
        if (StepEventBus.getEventBus() != stepEventBus()) {
            StepEventBus.overrideEventBusWith(stepEventBus());
        }
    }

    private void checkForStepFailures() throws Throwable {
        if (publisher.aStepHasFailed()) {
            throw publisher.getTestFailureCause().toException();
        }
    }

    private void checkForAssumptionViolations() {
        if (stepEventBus().assumptionViolated()) {
            throw new AssumptionViolatedException(stepEventBus().getAssumptionViolatedMessage());
        }
    }
}
