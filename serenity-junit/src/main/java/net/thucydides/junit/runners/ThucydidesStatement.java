package net.thucydides.junit.runners;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepPublisher;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.model.Statement;

/**
 * A JUnit statement that runs a Thucydides-enabled test and then publishes the results via JUnit.
 */
public class ThucydidesStatement extends Statement {

    private final Statement statement;
    private final StepPublisher publisher;

    public ThucydidesStatement(final Statement statement, final StepPublisher publisher) {
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
            System.out.println( publisher.getTestFailureCause().toException());
            throw publisher.getTestFailureCause().toException();
        }
    }

    private void checkForAssumptionViolations() {
        if (StepEventBus.getEventBus().assumptionViolated()) {
            throw new AssumptionViolatedException(StepEventBus.getEventBus().getAssumptionViolatedMessage());
        }
    }
}
