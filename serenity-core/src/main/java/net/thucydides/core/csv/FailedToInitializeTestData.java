package net.thucydides.core.csv;

/**
 * Error that occurs if the test data could not be instanciated for some reason.
 */
public class FailedToInitializeTestData extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FailedToInitializeTestData(final String message) {
        super(message);
    }

    public FailedToInitializeTestData(final String message, final Exception cause) {
        super(message, cause);
    }
}
