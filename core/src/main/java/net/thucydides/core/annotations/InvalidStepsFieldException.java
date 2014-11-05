package net.thucydides.core.annotations;

/**
 * Thrown if no suitable WebDriver field with the @Managed annotation is found in a test.
 * 
 * @author johnsmart
 *
 */
public class InvalidStepsFieldException extends RuntimeException {

    private static final long serialVersionUID = -7552399074205295160L;

    public InvalidStepsFieldException(final String message) {
        super(message);
    }

    public InvalidStepsFieldException(final String message, final IllegalAccessException cause) {
        super(message, cause);
    }
}
