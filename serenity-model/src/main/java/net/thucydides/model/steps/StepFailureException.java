package net.thucydides.model.steps;

/**
 * Used to indicate a failing step.
 */
public class StepFailureException extends RuntimeException {
    public StepFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
