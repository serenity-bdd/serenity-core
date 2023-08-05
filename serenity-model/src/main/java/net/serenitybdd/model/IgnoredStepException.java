package net.serenitybdd.model;

/**
 * Exception thrown to indicate that a test cannot proceed and should be considered 'skipped'.
 * This will result in the test being marked as 'skipped', as opposed to 'failing'.
 */
public class IgnoredStepException extends RuntimeException {
    public IgnoredStepException(String message) {
        super(message);
    }
}
