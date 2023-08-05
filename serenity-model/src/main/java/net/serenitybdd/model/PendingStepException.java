package net.serenitybdd.model;

/**
 * Exception thrown to indicate that a test cannot proceed and should be considered 'pending'.
 * This will result in the test being marked as 'pending', as opposed to 'failing'.
 */
public class PendingStepException extends RuntimeException {
    public PendingStepException(String message) {
        super(message);
    }
}
