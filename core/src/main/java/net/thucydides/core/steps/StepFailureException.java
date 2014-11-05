package net.thucydides.core.steps;

/**
 * Used to indicate a failing step.
 */
public class StepFailureException extends RuntimeException {
    public StepFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
