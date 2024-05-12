package net.serenitybdd.junit.runners;

import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepPublisher;
import org.junit.runners.model.Statement;

import java.lang.reflect.Constructor;
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
        } catch (Throwable throwable) {
            // Attempt to check if the caught throwable is an instance of AssumptionViolatedException
            if (isAssumptionViolatedException(throwable)) {
                stepEventBus().assumptionViolated(throwable.getMessage());
            } else if (throwable instanceof AssertionError) {
                if (!stepEventBus().aStepInTheCurrentTestHasFailed()) {
                    throw throwable;
                }
            } else {
                // Re-throw if it's neither an AssumptionViolatedException nor an AssertionError
                throw throwable;
            }
        }

        checkForStepFailures();
        checkForAssumptionViolations();
    }

    private boolean isAssumptionViolatedException(Throwable throwable) {
        try {
            // Load the AssumptionViolatedException class using reflection
            Class<?> clazz = Class.forName("org.junit.AssumptionViolatedException");
            // Check if the throwable is an instance of AssumptionViolatedException
            return clazz.isInstance(throwable);
        } catch (ClassNotFoundException e) {
            // Class not found, meaning it's not an AssumptionViolatedException
            return false;
        }
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
//            throw new AssumptionViolatedException(stepEventBus().getAssumptionViolatedMessage());
            throw createAssumptionViolatedException(stepEventBus().getAssumptionViolatedMessage());
        }
    }

    private RuntimeException createAssumptionViolatedException(String message) {
        try {
            // Load the AssumptionViolatedException class using reflection
            Class<?> clazz = Class.forName("org.junit.AssumptionViolatedException");
            // Get the constructor that takes a String as an argument
            Constructor<?> constructor = clazz.getConstructor(String.class);
            // Create a new instance of AssumptionViolatedException with the provided message
            return (RuntimeException) constructor.newInstance(message);
        } catch (ClassNotFoundException e) {
            // Handle the case where the AssumptionViolatedException class is not found
            System.err.println("AssumptionViolatedException class not found: " + e.getMessage());
            return new RuntimeException("Assumption violation occurred, but no handler class found.");
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            // Handle other exceptions related to reflection and instantiation
            System.err.println("Error instantiating AssumptionViolatedException: " + e.getMessage());
            return new RuntimeException("Error during assumption violation exception handling.");
        }
    }
}
