package net.thucydides.core.annotations;

/**
 * Thrown if no suitable WebDriver field with the @Managed annotation is found in a test.
 * 
 * @author johnsmart
 *
 */
public class InvalidManagedWebDriverFieldException extends RuntimeException {

    private static final long serialVersionUID = -7552399074205295160L;

    public InvalidManagedWebDriverFieldException(final String message) {
        super(message);
    }

    public InvalidManagedWebDriverFieldException(final String message, final Throwable e) {
        super(message, e);
    }
}
