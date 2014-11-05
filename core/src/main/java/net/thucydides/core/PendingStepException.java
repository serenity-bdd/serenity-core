package net.thucydides.core;

/**
 * Exception thrown to indicate that a test cannot proceed and should be considered 'skipped'.
 * This will result in the test being marked as 'skipped', as opposed to 'failing'.
 */
public class PendingStepException extends RuntimeException {
    public PendingStepException(String message) {
        super(message);
    }
}
