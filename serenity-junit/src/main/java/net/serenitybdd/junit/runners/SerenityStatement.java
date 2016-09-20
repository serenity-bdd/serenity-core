package net.serenitybdd.junit.runners;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepPublisher;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

/**
 * A JUnit statement that runs a Serenity-enabled test and then publishes the results via JUnit.
 */
public class SerenityStatement extends Statement {

    private final Statement statement;
    private final StepPublisher publisher;

    public SerenityStatement(final Statement statement, final StepPublisher publisher) {
        this.statement = statement;
        this.publisher = publisher;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException assumptionViolated) {
            StepEventBus.getEventBus().assumptionViolated(assumptionViolated.getMessage());
        } catch (AssertionError assertionError) {
            if (!StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed()) {
                throw assertionError;
            }
        }
        checkForStepFailures();
        checkForAssumptionViolations();
    }

    private void checkForStepFailures() throws Throwable {
        if (publisher.aStepHasFailed()) {
            String message = publisher.getTestFailureCause().getErrorType() + ": " + publisher.getTestFailureCause().getMessage();
            throw publisher.getTestFailureCause().toException();
        }
    }

    private void checkForAssumptionViolations() {
        if (StepEventBus.getEventBus().assumptionViolated()) {
            throw new AssumptionViolatedException(StepEventBus.getEventBus().getAssumptionViolatedMessage());
        }
    }
}
